package com.example.mtc_app.splashScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminHomePageActivity;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.example.mtc_app.staff.staff_home;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen); // logo layout shown for 500ms

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String userRole = sharedPreferences.getString("userRole", "");

            if (isLoggedIn && auth.getCurrentUser() != null) {
                navigateToHome(userRole);
            } else {
                startActivity(new Intent(this, CustomerLoginActivity.class));
            }

            finish();
        }, 500); // Delay splash for 500ms
    }

    private void navigateToHome(String userRole) {
        Intent intent;
        switch (userRole.toLowerCase()) {
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
                Toast.makeText(this, "Unknown role: " + userRole, Toast.LENGTH_SHORT).show();
                intent = new Intent(this, CustomerLoginActivity.class);
                break;
        }
        startActivity(intent);
    }
}
