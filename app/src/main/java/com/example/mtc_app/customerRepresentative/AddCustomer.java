package com.example.mtc_app.customerRepresentative;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCustomer extends Fragment {

    private TextInputEditText editTextName, editTextMobile, editTextEmail, editTextPassword, editTextAddress;
    private Button addCustomerButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextName = view.findViewById(R.id.editTextName);
        editTextMobile = view.findViewById(R.id.editTextMobile);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        addCustomerButton = view.findViewById(R.id.addCustomerButton);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addCustomerButton.setOnClickListener(v -> addCustomer());
    }

    private void addCustomer() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextMobile.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String role = "customer";

        if (name.isEmpty() || !name.matches("^[a-zA-Z ]+$")) {
            editTextName.setError("Enter a valid name (letters and spaces only)");
            return;
        }

        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            editTextMobile.setError("Enter a valid 10-digit mobile number");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long");
            return;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Address is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(phoneQuery -> {
                    if (!phoneQuery.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        editTextMobile.setError("This mobile number is already registered");
                        return;
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String userId = auth.getCurrentUser().getUid();
                                    saveCustomerToFirestore(userId, name, phone, email, address, role);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to validate mobile number", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveCustomerToFirestore(String userId, String name, String phone, String email, String address, String role) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> customer = new HashMap<>();
        customer.put("name", name);
        customer.put("phone", phone);
        customer.put("email", email);
        customer.put("address", address);
        customer.put("role", role);
        customer.put("created_at", createdAt);

        firestore.collection("users").document(userId)
                .set(customer)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Customer added successfully", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error saving customer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputFields() {
        editTextName.setText("");
        editTextMobile.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextAddress.setText("");
    }
}
