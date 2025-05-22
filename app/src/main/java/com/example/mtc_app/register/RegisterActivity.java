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
import com.example.mtc_app.login.CustomerLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

        initializeViews();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        setupClickListeners();
    }

    private void initializeViews() {
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        phoneField = findViewById(R.id.phoneField);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        TextView redirectToLogin = findViewById(R.id.tv_redirect_to_login);
        redirectToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, CustomerLoginActivity.class));
            finish();
        });

        registerButton.setOnClickListener(view -> registerUser());
    }

    private boolean validateInputs(String name, String email, String password, String phone) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameField.setError("Name is required");
            isValid = false;
        } else if (!Pattern.matches("^[a-zA-Z ]{2,25}$", name)) {
            nameField.setError("Name must contain only letters (2–25 characters)");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            isValid = false;
        } else if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            emailField.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Phone number is required");
            isValid = false;
        } else if (!Pattern.matches("^[0-9]{10}$", phone)) {
            phoneField.setError("Phone number must be exactly 10 digits");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        if (progressBar == null) {
            Toast.makeText(this, "Progress bar not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String role = "customer";

        if (!validateInputs(name, email, password, phone)) return;

        progressBar.setVisibility(View.VISIBLE);

        // First check if phone is already registered
        firestore.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            phoneField.setError("This phone number is already registered.");
                        } else {
                            // Proceed to check email
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(authTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (authTask.isSuccessful()) {
                                            String userId = auth.getCurrentUser().getUid();
                                            saveUserDetailsToFirestore(userId, name, email, phone, role);
                                        } else {
                                            if (authTask.getException() instanceof FirebaseAuthUserCollisionException) {
                                                emailField.setError("This email address is already registered.");
                                            } else if (authTask.getException() != null && authTask.getException().getMessage().contains("PERMISSION_DENIED")) {
                                                Toast.makeText(this, "Registration failed: Permission denied. Please contact admin.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(this, "Registration failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(this, "Error while checking phone: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Unknown error occurred while checking phone", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserDetailsToFirestore(String userId, String name, String email, String phone, String role) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("role", role);
        user.put("created_at", createdAt);

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, CustomerLoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
