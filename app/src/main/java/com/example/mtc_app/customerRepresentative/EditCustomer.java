package com.example.mtc_app.customerRepresentative;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashMap;
import java.util.Map;

public class EditCustomer extends Fragment {

    private FirebaseFirestore db;
    private EditText editTextName, editTextMobile, editTextEmail, editTextPassword, editTextAddress;
    private Button saveButton;
    private String oldPhone, documentId;

    public EditCustomer() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        editTextName = view.findViewById(R.id.editTextName2);
        editTextMobile = view.findViewById(R.id.editTextMobile2);
        editTextEmail = view.findViewById(R.id.editTextEmail2);
        editTextPassword = view.findViewById(R.id.editTextPassword2);
        editTextAddress = view.findViewById(R.id.editTextAddress2);
        saveButton = view.findViewById(R.id.addCustomerButton2);

        Bundle args = getArguments();
        if (args != null) {
            oldPhone = args.getString("customer_phone");
            editTextMobile.setText(oldPhone);
            fetchCustomerDetails(oldPhone);
        }

        saveButton.setOnClickListener(v -> updateCustomerDetails());
    }

    private void fetchCustomerDetails(String phone) {
        db.collection("users")
                .whereEqualTo("phone", phone)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        documentId = document.getId();

                        String name = document.getString("name");
                        String email = document.getString("email");
                        String password = document.getString("password");
                        String address = document.getString("address");

                        editTextName.setText(name);
                        editTextEmail.setText(email);
                        editTextPassword.setText(password);
                        editTextAddress.setText(address);

                        // ✅ Save fetched customer data to a separate preferences file (optional)
                        SharedPreferences prefs = requireContext().getSharedPreferences("edit_customer_temp", Context.MODE_PRIVATE);
                        prefs.edit()
                                .putString("name", name)
                                .putString("email", email)
                                .putString("password", password)
                                .putString("address", address)
                                .putString("phone", phone)
                                .apply();
                    } else {
                        Toast.makeText(getContext(), "Customer not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching details", Toast.LENGTH_SHORT).show());
    }

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

        // Validation
        if (newName.isEmpty()) {
            editTextName.setError("Name is required");
            return;
        } else if (!newName.matches("^[a-zA-Z ]+$")) {
            editTextName.setError("Name must contain only letters and spaces");
            return;
        }

        if (newPhone.isEmpty()) {
            editTextMobile.setError("Mobile number is required");
            return;
        } else if (!newPhone.matches("\\d{10}")) {
            editTextMobile.setError("Enter a valid 10-digit mobile number");
            return;
        }

        if (newEmail.isEmpty()) {
            editTextEmail.setError("Email is required");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTextEmail.setError("Enter a valid email address");
            return;
        }

        if (newPassword.isEmpty()) {
            editTextPassword.setError("Password is required");
            return;
        } else if (newPassword.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long");
            return;
        }

        if (newAddress.isEmpty()) {
            editTextAddress.setError("Address is required");
            return;
        }

        updateFirestore(newName, newPhone, newEmail, newPassword, newAddress);
    }

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

                    // ✅ Optional: Clear temporary customer data cache after update
                    SharedPreferences prefs = requireContext().getSharedPreferences("edit_customer_temp", Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
    }
}
