package com.example.mtc_app.customer;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        pageTitle = findViewById(R.id.pageTitle);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                setFragment(new CustomerHomeFragment(), "Home Page");
                return true;
//            } else if (itemId == R.id.nav_request_order) {
//                setFragment(new MakeOrderFragment(), "Make Orders");
//                return true;
            } else if (itemId == R.id.nav_profile) {
                setFragment(new CustomerProfileFragment(), "Profile");
                return true;
            } else {
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void setFragment(Fragment fragment, String title) {
        pageTitle.setText(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }
}
