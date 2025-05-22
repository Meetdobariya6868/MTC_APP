package com.example.mtc_app.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userRole = sharedPreferences.getString("userRole", "");
            redirectToRoleBasedPage(userRole);
            return;
        }

        loginButton.setOnClickListener(view -> loginUser());

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            isValid = false;
        }

        if (!isValid) return;

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
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage;
                            String errorCode = "";

                            if (exception instanceof com.google.firebase.auth.FirebaseAuthException) {
                                errorCode = ((com.google.firebase.auth.FirebaseAuthException) exception).getErrorCode();
                            }

                            switch (errorCode) {
                                case "ERROR_USER_NOT_FOUND":
                                    errorMessage = "This email is not registered";
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    errorMessage = "Incorrect password";
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    errorMessage = "Invalid email format";
                                    break;
                                case "ERROR_USER_DISABLED":
                                    errorMessage = "This account has been disabled";
                                    break;
                                default:
                                    errorMessage = "Login failed. Please check your credentials.";
                                    break;
                            }

                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
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
