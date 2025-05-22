package com.example.mtc_app.customerRepresentative;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mtc_app.R;
import com.example.mtc_app.customer.fragments.MakeOrderFragment;
import com.example.mtc_app.customerRepresentative.CustomerOrderAdapter;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.example.mtc_app.customerRepresentative.CustomerOrder;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerDetails extends Fragment {

    private FirebaseFirestore db;
    private TextView userName, userPhone, userEmail;
    private String customerPhone;
    private List<String> orderIds = new ArrayList<>();

    private RecyclerView recyclerView;
    private CustomerOrderAdapter adapter;
    private List<CustomerOrder> orderList;

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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderList = new ArrayList<>();
        adapter = new CustomerOrderAdapter(requireContext(), orderList, orderIds);
        recyclerView.setAdapter(adapter);



        Bundle args = getArguments();
        if (args != null) {
            customerPhone = args.getString("customer_phone");
            userPhone.setText(customerPhone != null ? customerPhone : "N/A");
            if (customerPhone != null && !customerPhone.equals("N/A")) {
                fetchCustomerDetails(customerPhone);
                fetchCustomerOrders(customerPhone);
            }
        }

        setButtonHandlers(view);
    }


    private void fetchCustomerOrders(String phone) {
        db.collection("Total Orders")
                .whereEqualTo("Mobile Number", phone)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    orderList.clear();
                    orderIds.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    List<Pair<CustomerOrder, String>> tempList = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshots.getDocuments()) {
                        String status = doc.getString("Status");
                        if (status == null || !status.equalsIgnoreCase("Open")) continue;

                        String segment = doc.getString("LabJobNumber");
                        String orderDate = doc.getString("Created At");
                        String price = String.valueOf(doc.get("Total Price"));
                        Map<String, Object> radioSelections = (Map<String, Object>) doc.get("Radio Selections");
                        String dispatchMode = "";

                        if (radioSelections != null && radioSelections.containsKey("Mode of Dispatch")) {
                            dispatchMode = String.valueOf(radioSelections.get("Mode of Dispatch"));
                        }

                        CustomerOrder order = new CustomerOrder(segment, dispatchMode, orderDate, price, status);
                        orderIds.add(doc.getId());
                        tempList.add(new Pair<>(order, orderDate));
                    }

                    // Sort by date descending
                    Collections.sort(tempList, (o1, o2) -> {
                        try {
                            Date d1 = sdf.parse(o1.second);
                            Date d2 = sdf.parse(o2.second);
                            return d2.compareTo(d1); // Newest first
                        } catch (ParseException | java.text.ParseException e) {
                            return 0;
                        }
                    });

                    for (Pair<CustomerOrder, String> pair : tempList) {
                        orderList.add(pair.first);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderFetch", "Error fetching orders", e);
                    Toast.makeText(requireContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                });
    }

    private void setButtonHandlers(View view) {
        MaterialButton editButton = view.findViewById(R.id.editButton);
        MaterialButton deleteButton = view.findViewById(R.id.deleteButton);
        MaterialButton addOrderButton = view.findViewById(R.id.addOrderButton);
        MaterialButton loginButton = view.findViewById(R.id.loginButton);

        if (editButton != null) {
            editButton.setOnClickListener(v -> openFragment(new EditCustomer()));
        }

        if (addOrderButton != null) {
            addOrderButton.setOnClickListener(v -> {
                Fragment makeOrderFragment = new MakeOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putString("customer_phone", customerPhone); // pass phone
                makeOrderFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, makeOrderFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }



        if (loginButton != null) {
            loginButton.setOnClickListener(v -> loginAsThisUser());
        }

        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer and all associated orders?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteCustomerAndOrders())
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        MaterialButton emailButton = view.findViewById(R.id.emailButton);
        if (emailButton != null) {
            emailButton.setOnClickListener(v -> {
                String toEmail = userEmail != null && userEmail.getText() != null
                        ? userEmail.getText().toString()
                        : "";

                if (toEmail.isEmpty()) {
                    Toast.makeText(requireContext(), "Email not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.setPackage("com.google.android.gm"); // Force Gmail
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Your Material Testing Order");
                intent.putExtra(Intent.EXTRA_TEXT, "Dear Customer,\n\n");

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(requireContext(), "Gmail app not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteCustomerAndOrders() {
        if (customerPhone == null || customerPhone.isEmpty()) {
            Toast.makeText(requireContext(), "Customer phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Delete from 'users'
        db.collection("users")
                .whereEqualTo("phone", customerPhone)
                .get()
                .addOnSuccessListener(userSnapshots -> {
                    for (DocumentSnapshot userDoc : userSnapshots.getDocuments()) {
                        db.collection("users").document(userDoc.getId()).delete();
                    }

                    // Step 2: Delete matching orders
                    db.collection("Total Orders")
                            .whereEqualTo("Mobile Number", customerPhone)
                            .get()
                            .addOnSuccessListener(orderSnapshots -> {
                                for (DocumentSnapshot orderDoc : orderSnapshots.getDocuments()) {
                                    db.collection("Total Orders").document(orderDoc.getId()).delete();
                                }

                                Toast.makeText(requireContext(), "Customer and all orders deleted", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack(); // Optional: navigate back
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to delete orders", Toast.LENGTH_SHORT).show();
                                Log.e("DeleteOrders", "Error deleting orders", e);
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
                    Log.e("DeleteCustomer", "Error deleting customer", e);
                });
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
}
