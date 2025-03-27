package com.example.mtc_app.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminHomePageActivity;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField, phoneField;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize all views
        initializeViews();

        // Set up Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        phoneField = findViewById(R.id.phoneField);
        registerButton = findViewById(R.id.registerButton);

        // Add a null check for progressBar
        progressBar = findViewById(R.id.progressBar);
        if (progressBar == null) {
            // Log an error or show a toast if progressBar is not found
            Toast.makeText(this, "Progress bar not found in layout", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // Redirect to Login/Admin Home click listener
        TextView redirectToLogin = findViewById(R.id.tv_redirect_to_login);
        redirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, CustomerLoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Register button click listener
        registerButton.setOnClickListener(view -> registerUser());
    }

    private boolean validateInputs(String name, String email, String password, String phone) {
        boolean isValid = true;

        // Name validation: Only letters, minimum 2 characters
        if (!Pattern.matches("^[a-zA-Z ]{2,}$", name)) {
            nameField.setError("Name must contain only letters and be at least 2 characters long");
            isValid = false;
        }

        // Email validation using a more comprehensive regex
        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", email)) {
            emailField.setError("Invalid email address");
            isValid = false;
        }

        // Password validation: Minimum 6 characters
        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        // Phone number validation: Exactly 10 digits
        if (!Pattern.matches("^[0-9]{10}$", phone)) {
            phoneField.setError("Phone number must be 10 digits");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        // Add null check for progressBar before using it
        if (progressBar == null) {
            Toast.makeText(this, "Progress bar is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();

        // Set role as "customer" for all registrations
        String role = "customer";

        // Validate inputs before proceeding
        if (!validateInputs(name, email, password, phone)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Ensure progressBar is not null before hiding
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserDetailsToFirestore(userId, name, email, phone, role);
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetailsToFirestore(String userId, String name, String email, String phone, String role) {
        // Get current date and time
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create user data map
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("role", role);  // Role is set as "customer" during registration
        user.put("created_at", createdAt);

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, CustomerLoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}