package com.example.mtc_app.customer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.fragments.CustomerHomeFragment;
import com.example.mtc_app.customer.fragments.CustomerProfileFragment;
import com.example.mtc_app.customer.fragments.MakeOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerHomePageActivity extends AppCompatActivity {

    private TextView pageTitle;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        // Initialize UI elements
        pageTitle = findViewById(R.id.pageTitle);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        setFragment(new CustomerHomeFragment(), "Home Page");

        // Bottom navigation handling
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadWithProgress(new CustomerHomeFragment(), "Home Page");
                return true;
            } else if (itemId == R.id.nav_request_order) {
                loadWithProgress(new MakeOrderFragment(), "Make Orders");
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadWithProgress(new CustomerProfileFragment(), "Profile");
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadWithProgress(Fragment fragment, String title) {
        showProgressBar();
        handler.postDelayed(() -> {
            setFragment(fragment, title);
            hideProgressBar();
        }, 500); // Adjust delay as needed (in milliseconds)
    }

    private void setFragment(Fragment fragment, String title) {
        pageTitle.setText(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }
}
