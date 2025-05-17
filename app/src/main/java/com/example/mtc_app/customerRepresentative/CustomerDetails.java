package com.example.mtc_app.customerRepresentative;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mtc_app.R;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerDetails extends Fragment {

    private FirebaseFirestore db;
    private TextView userName, userPhone, userEmail;
    private String customerPhone;

    public CustomerDetails() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        userName = view.findViewById(R.id.userName);
        userPhone = view.findViewById(R.id.userPhone);
        userEmail = view.findViewById(R.id.userEmail);

        Bundle args = getArguments();
        if (args != null) {
            customerPhone = args.getString("customer_phone");
            userPhone.setText(customerPhone != null ? customerPhone : "N/A");
            if (customerPhone != null && !customerPhone.equals("N/A")) {
                fetchCustomerDetails(customerPhone);
            }
        }

        setButtonHandlers(view);
    }

    private void setButtonHandlers(View view) {
        MaterialButton orderDetailsButton = view.findViewById(R.id.orderDetailsButton3);
        MaterialButton editButton = view.findViewById(R.id.editButton);
        MaterialButton addOrderButton = view.findViewById(R.id.addOrderButton);
        MaterialButton loginButton = view.findViewById(R.id.loginButton);

        if (orderDetailsButton != null) {
            orderDetailsButton.setOnClickListener(v -> openFragment(new OrderDetails()));
        }

        if (editButton != null) {
            editButton.setOnClickListener(v -> openFragment(new EditCustomer()));
        }

        if (addOrderButton != null) {
            addOrderButton.setOnClickListener(v -> openFragment(new AddOrder()));
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> loginAsThisUser());
        }
    }

    private void loginAsThisUser() {
        if (customerPhone == null || customerPhone.equals("N/A")) {
            Toast.makeText(requireContext(), "Invalid customer phone", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("phone", customerPhone)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        String uid = doc.getId();
                        String role = doc.getString("role");

                        getCustomTokenFromBackend(uid, token -> {
                            FirebaseAuth.getInstance().signInWithCustomToken(token)
                                    .addOnSuccessListener(authResult -> {
                                        saveLoginState(role);
                                        startActivity(new Intent(requireContext(), CustomerHomePageActivity.class));
                                        requireActivity().finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LoginAsUser", "signInWithCustomToken failed", e);
                                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "User fetch failed", e);
                    Toast.makeText(requireContext(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveLoginState(String role) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean("isLoggedIn", true)
                .putString("userRole", role != null ? role : "customer")
                .apply();
    }

    private Bundle getCustomerBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("customer_name", userName != null ? userName.getText().toString() : "");
        bundle.putString("customer_phone", userPhone != null ? userPhone.getText().toString() : "");
        bundle.putString("customer_email", userEmail != null ? userEmail.getText().toString() : "");
        return bundle;
    }


    private void openFragment(Fragment fragment) {
        fragment.setArguments(getCustomerBundle());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchCustomerDetails(String customerPhone) {
        db.collection("users")
                .whereEqualTo("phone", customerPhone)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        String email = doc.getString("email");

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                userName.setText(name != null ? name : "No Name Found");
                                userPhone.setText(phone != null ? phone : "No Phone Found");
                                userEmail.setText(email != null ? email : "No Email Found");
                            });
                        }
                    } else {
                        Log.e("FirestoreData", "No user found with phone: " + customerPhone);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching data", e));
    }

    private void getCustomTokenFromBackend(String uid, OnTokenReceivedListener listener) {
        String url = "https://mtcnotify.onrender.com/impersonate";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uid", uid);
        } catch (JSONException e) {
            Log.e("TokenRequest", "JSON error", e);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String token = response.getString("token");
                        listener.onTokenReceived(token);
                    } catch (JSONException e) {
                        Log.e("TokenParse", "Error parsing token", e);
                        Toast.makeText(requireContext(), "Token parsing failed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("TokenFetchError", "Backend request failed", error);
                    Toast.makeText(requireContext(), "Failed to get token", Toast.LENGTH_SHORT).show();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                1,
                1.0f
        ));

        Volley.newRequestQueue(requireContext()).add(request);
    }



    interface OnTokenReceivedListener {
        void onTokenReceived(String token);
    }
}
