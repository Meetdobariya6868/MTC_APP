package com.example.mtc_app.customer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.mtc_app.R;
import com.example.mtc_app.customer.fragments.CustomerHomeFragment;
import com.example.mtc_app.customer.fragments.CustomerProfileFragment;
import com.example.mtc_app.customer.fragments.MakeOrderFragment;

public class CustomerHomePageActivity extends AppCompatActivity {

    private TextView pageTitle;
    private FrameLayout loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        // Initialize the page title and loading container
        pageTitle = findViewById(R.id.pageTitle);
        loadingContainer = findViewById(R.id.loadingContainer);

        // Remove the backArrow initialization completely since it's commented out in XML

        // Set default fragment (HomeFragment)
        setFragment(new CustomerHomeFragment(), "Home Page");

        // Navigation bar items click listeners
        findViewById(R.id.nav_home).setOnClickListener(v ->
                setFragment(new CustomerHomeFragment(), "Home Page")
        );

        // Add the progress bar functionality for Make Order button
        findViewById(R.id.nav_request_order).setOnClickListener(v -> {
            showProgressBar(); // Show the progress bar before switching fragment
            setFragment(new MakeOrderFragment(), "Make Orders");
        });

        findViewById(R.id.nav_profile).setOnClickListener(v ->
                setFragment(new CustomerProfileFragment(), "Profile")
        );
    }

    private void setFragment(Fragment fragment, String title) {
        // Update the page title
        pageTitle.setText(title);

        // Show the progress bar for the Make Order Fragment
        if (fragment instanceof MakeOrderFragment) {
            showProgressBar();
        }

        // Replace the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Show the progress bar
    private void showProgressBar() {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(FrameLayout.VISIBLE);
        }
    }

    // Hide the progress bar
    public void hideProgressBar() {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(FrameLayout.GONE);
        }
    }
}