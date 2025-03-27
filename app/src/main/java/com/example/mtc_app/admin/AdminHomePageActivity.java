package com.example.mtc_app.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.fragments.AdminCRFragment;
import com.example.mtc_app.admin.fragments.AdminHomeFragment;
import com.example.mtc_app.admin.fragments.AdminStaffFragment;
import com.example.mtc_app.auth.AuthUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomePageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private TextView menuItem1, menuItem2, menuItem3, menuItem4;
    private CardView customer1, customer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        auth = FirebaseAuth.getInstance();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Initialize Cards for Order Details
        customer1 = findViewById(R.id.Customer1);
        customer2 = findViewById(R.id.Customer2);

        // Card Click Listeners (Opens Order Detail)
        customer1.setOnClickListener(view -> openOrderDetail("Order 1", "John Doe", "123 Main St", "9876543210", "john@example.com"));
        customer2.setOnClickListener(view -> openOrderDetail("Order 2", "Jane Smith", "456 Market St", "9876543211", "jane@example.com"));

        // Setup Navigation Drawer Items
        menuItem1 = navigationView.findViewById(R.id.menuItem1);
        menuItem2 = navigationView.findViewById(R.id.menuItem2);
        menuItem3 = navigationView.findViewById(R.id.menuItem3);
        menuItem4 = navigationView.findViewById(R.id.menuItem4);

        menuItem1.setOnClickListener(v -> navigateToFragment(new AdminHomeFragment(), "Home"));
        menuItem2.setOnClickListener(v -> navigateToFragment(new AdminCRFragment(), "CR Section"));
        menuItem3.setOnClickListener(v -> navigateToFragment(new AdminStaffFragment(), "Staff Section"));
        menuItem4.setOnClickListener(v -> showLogoutConfirmation());

        // Setup Navigation Drawer Toggle
        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default Fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new AdminHomeFragment(), false);
        }
    }

    // Handles Navigation Drawer Fragment Switching
    private void navigateToFragment(Fragment fragment, String tag) {
        drawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            loadFragment(fragment, true);
        }
    }

    // Load Fragment into FrameLayout
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    // Open Order Detail Activity
    private void openOrderDetail(String order, String name, String address, String phone, String email) {
        Intent intent = new Intent(this, AdminOrderDetail.class);
        intent.putExtra("order", order);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    // Show Logout Confirmation Dialog
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Logout Method
    private void logout() {
        AuthUtils.logout(this);
    }

    // Handle Back Press (Closes Drawer Instead of Exiting)
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}