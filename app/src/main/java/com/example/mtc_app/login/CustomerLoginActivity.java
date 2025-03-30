package com.example.mtc_app.login;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.example.mtc_app.register.RegisterActivity;
import com.example.mtc_app.staff.staff_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView registerTextView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Redirect to the appropriate role-based page without showing the login form
            String userRole = sharedPreferences.getString("userRole", "");
            redirectToRoleBasedPage(userRole);
            return;
        }

        // Set up login button click listener
        loginButton.setOnClickListener(view -> loginUser());

        // Set up register text view click listener
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to registration activity
                Intent intent = new Intent(CustomerLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            if (role != null) {
                                saveUserRoleInPreferences(role);
                                redirectToRoleBasedPage(role);
                            } else {
                                Toast.makeText(this, "Role is missing for this user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User role not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserRoleInPreferences(String role) {
        // Save login status and user role to SharedPreferences
        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", true)
                .putString("userRole", role)
                .apply();
    }

    private void redirectToRoleBasedPage(String role) {
        if (role == null) {
            Toast.makeText(this, "Invalid role.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        switch (role.toLowerCase()) {
            case "admin":
                intent = new Intent(this, AdminHomePageActivity.class);
                break;
            case "customer":
                intent = new Intent(this, CustomerHomePageActivity.class);
                break;
            case "staff":
                intent = new Intent(this, staff_home.class);
                break;
            case "cr":
                intent = new Intent(this, CrMain.class);
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }
}