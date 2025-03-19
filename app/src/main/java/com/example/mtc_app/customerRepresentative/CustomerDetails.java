package com.example.mtc_app.customerRepresentative;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mtc_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CustomerDetails extends Fragment {

    private FirebaseFirestore db;
    private TextView  userName, userPhone, userEmail;
    private String customerPhone; // Store customer phone for Firestore lookup

    public CustomerDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements

        userName = view.findViewById(R.id.userName);
        userPhone = view.findViewById(R.id.userPhone);
        userEmail = view.findViewById(R.id.userEmail);

        // Retrieve data from bundle
        Bundle args = getArguments();
        if (args != null) {
            customerPhone = args.getString("customer_phone"); // Get phone number

            userPhone.setText(customerPhone != null ? customerPhone : "N/A");

            // Fetch customer details using phone number
            if (customerPhone != null && !customerPhone.equals("N/A")) {
                fetchCustomerDetails(customerPhone);
            }
        }

        // Handle "Order Details" button click
        MaterialButton orderDetailsButton = view.findViewById(R.id.orderDetailsButton3);
        if (orderDetailsButton != null) {
            orderDetailsButton.setOnClickListener(v -> {
                Fragment orderDetailsFragment = new OrderDetails();
                orderDetailsFragment.setArguments(getCustomerBundle());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, orderDetailsFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Handle "Edit" button click
        MaterialButton editButton = view.findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setOnClickListener(v -> {
                Fragment editCustomerFragment = new EditCustomer();
                editCustomerFragment.setArguments(getCustomerBundle());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, editCustomerFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Handle "Add Order" button click
        MaterialButton addOrderButton = view.findViewById(R.id.addOrderButton);
        if (addOrderButton != null) {
            addOrderButton.setOnClickListener(v -> {
                Fragment addOrderFragment = new AddOrder();
                addOrderFragment.setArguments(getCustomerBundle());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, addOrderFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    // Fetch customer details from Firestore using phone number
    private void fetchCustomerDetails(String customerPhone) {
        db.collection("users")
                .whereEqualTo("phone", customerPhone) // Query Firestore by phone number
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Get Firestore document ID and user number

                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");

                        Log.d("FirestoreData", "Found user: " + documentSnapshot.getData());

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // Show "user_number" if available, else fallback to customerId

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


    // Helper method to pass customer details to another fragment
    private Bundle getCustomerBundle() {
        Bundle bundle = new Bundle();

        if (userName != null && userPhone != null && userEmail != null) {

            bundle.putString("customer_name", userName.getText().toString());
            bundle.putString("customer_phone", userPhone.getText().toString());
            bundle.putString("customer_email", userEmail.getText().toString());
        }

        return bundle;
    }
}
