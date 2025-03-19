package com.example.mtc_app.customerRepresentative;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditCustomer extends Fragment {

    private FirebaseFirestore db;
    private EditText editTextName, editTextMobile, editTextEmail, editTextPassword, editTextAddress;
    private Button saveButton;
    private String oldPhone, documentId;

    public EditCustomer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editTextName = view.findViewById(R.id.editTextName2);
        editTextMobile = view.findViewById(R.id.editTextMobile2);
        editTextEmail = view.findViewById(R.id.editTextEmail2);
        editTextPassword = view.findViewById(R.id.editTextPassword2);
        editTextAddress = view.findViewById(R.id.editTextAddress2);
        saveButton = view.findViewById(R.id.addCustomerButton2);

        // Retrieve phone number from arguments
        Bundle args = getArguments();
        if (args != null) {
            oldPhone = args.getString("customer_phone");
            editTextMobile.setText(oldPhone);
            fetchCustomerDetails(oldPhone);
        }

        // Save button click listener
        saveButton.setOnClickListener(v -> updateCustomerDetails());
    }

    // Fetch customer details from Firestore
    private void fetchCustomerDetails(String phone) {
        db.collection("users")
                .whereEqualTo("phone", phone)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        documentId = document.getId(); // Store Firestore document ID

                        // Populate UI fields
                        editTextName.setText(document.getString("name"));
                        editTextEmail.setText(document.getString("email"));
                        editTextPassword.setText(document.getString("password"));
                        editTextAddress.setText(document.getString("address"));
                    } else {
                        Toast.makeText(getContext(), "Customer not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching details", Toast.LENGTH_SHORT).show());
    }

    // Update customer details in Firestore
    private void updateCustomerDetails() {
        if (TextUtils.isEmpty(documentId)) {
            Toast.makeText(getContext(), "No customer to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = editTextName.getText().toString().trim();
        String newPhone = editTextMobile.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();
        String newPassword = editTextPassword.getText().toString().trim();
        String newAddress = editTextAddress.getText().toString().trim();

        if (newName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newAddress.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // If the phone number was changed, check if it's already in use
        if (!newPhone.equals(oldPhone)) {
            db.collection("users")
                    .whereEqualTo("phone", newPhone)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getContext(), "Phone number already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            updateFirestore(newName, newPhone, newEmail, newPassword, newAddress);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error checking phone number", Toast.LENGTH_SHORT).show());
        } else {
            updateFirestore(newName, newPhone, newEmail, newPassword, newAddress);
        }
    }

    // Perform the update operation in Firestore
    private void updateFirestore(String name, String phone, String email, String password, String address) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("phone", phone);
        updatedData.put("email", email);
        updatedData.put("password", password);
        updatedData.put("address", address);

        db.collection("users").document(documentId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Customer updated successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();  // Go back to previous fragment
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
    }
}
