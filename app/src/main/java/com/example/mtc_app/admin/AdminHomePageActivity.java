//package com.example.mtc_app.admin;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.mtc_app.R;
//import com.example.mtc_app.admin.fragments.AdminCRFragment;
//import com.example.mtc_app.admin.fragments.AdminHomeFragment;
//import com.example.mtc_app.admin.fragments.AdminStaffFragment;
//import com.example.mtc_app.auth.AuthUtils;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import java.util.List;
//
//public class AdminHomePageActivity extends AppCompatActivity {
//
//    private DrawerLayout drawerLayout;
//    private TextView menuItem1, menuItem2, menuItem3, menuItem4;
//    private FirebaseFirestore db;
//    private LinearLayout orderContainer;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_home);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Setup Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // Initialize Drawer Layout
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//
//        // Initialize Order Container
//        orderContainer = findViewById(R.id.orderContainer);
//
//        // Setup Navigation Drawer Items
//        menuItem1 = navigationView.findViewById(R.id.menuItem1);
//        menuItem2 = navigationView.findViewById(R.id.menuItem2);
//        menuItem3 = navigationView.findViewById(R.id.menuItem3);
//        menuItem4 = navigationView.findViewById(R.id.menuItem4);
//
//        menuItem1.setOnClickListener(v -> navigateToFragment(new AdminHomeFragment(), "Home"));
//        menuItem2.setOnClickListener(v -> navigateToFragment(new AdminCRFragment(), "CR Section"));
//        menuItem3.setOnClickListener(v -> navigateToFragment(new AdminStaffFragment(), "Staff Section"));
//        menuItem4.setOnClickListener(v -> showLogoutConfirmation());
//
//        // Setup Navigation Drawer Toggle
//        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
//                this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//
//        // Default Fragment (Home)
//        if (savedInstanceState == null) {
//            loadOrdersFromFirestore();
//        }
//
//    }
//    // Handles Navigation Drawer Fragment Switching
//    private void navigateToFragment(Fragment fragment, String tag) {
//        drawerLayout.closeDrawer(GravityCompat.START);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
//
//        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
//            loadFragment(fragment, true);
//        }
//    }
//
//    // Load Fragment into FrameLayout
//    private void loadFragment(Fragment fragment, boolean addToBackStack) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, fragment);
//        if (addToBackStack) {
//            transaction.addToBackStack(null);
//        }
//        transaction.commit();
//    }
//
//    // Show Logout Confirmation Dialog
//    private void showLogoutConfirmation() {
//        new AlertDialog.Builder(this)
//                .setTitle("Logout")
//                .setMessage("Are you sure you want to logout?")
//                .setPositiveButton("Yes", (dialog, which) -> logout())
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    // Logout Method
//    private void logout() {
//        AuthUtils.logout(this);
//    }
//
//    // Handle Back Press (Closes Drawer Instead of Exiting)
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//    private void loadOrdersFromFirestore() {
//        db.collection("Total Orders")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null) {
//                            List<DocumentSnapshot> orders = querySnapshot.getDocuments();
//                            displayOrders(orders);
//                        }
//                    } else {
//                        Log.e("Firestore", "Error fetching orders", task.getException());
//                    }
//                });
//    }
//
//    private void displayOrders(List<DocumentSnapshot> orders) {
//        orderContainer.removeAllViews();
//        for (DocumentSnapshot order : orders) {
//            View orderView = getLayoutInflater().inflate(R.layout.order_card, orderContainer, false);
//            TextView orderTitle = orderView.findViewById(R.id.orderTitle);
//            TextView customerName = orderView.findViewById(R.id.customerName);
//
//            // Fetch data dynamically
//            String orderId = order.getId();
//            String name = order.getString("Email");
//            String address = order.getString("Dispatch Address");
//            String phone = order.getString("Mobile Number");
//
//            // Set data to UI
//            orderTitle.setText("Order ID: " + orderId);
//            customerName.setText("Customer: " + name);
//
//            // Set click listener
//            orderView.setOnClickListener(v -> openOrderDetail(order));
//
//            orderContainer.addView(orderView);
//        }
//    }
//
//    private void openOrderDetail(DocumentSnapshot order) {
//        Intent intent = new Intent(this, AdminOrderDetail.class);
//        intent.putExtra("orderId", order.getId());
//        intent.putExtra("name", order.getString("Email"));
//        intent.putExtra("address", order.getString("Dispatch Address"));
//        intent.putExtra("phone", order.getString("Mobile Number"));
//        startActivity(intent);
//    }
//}



package com.example.mtc_app.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.mtc_app.customerRepresentative.CRHomeFragment;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.example.mtc_app.staff.staff_home;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomePageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        auth = FirebaseAuth.getInstance();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Drawer Layout
//        drawerLayout = findViewById(R.id.drawer_layout);
//
//        // Setup Navigation Drawer Toggle (still supported)
//        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
//                this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        // Bottom Navigation View setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AdminHomeFragment())
                        .commit();
                return true;
//                selectedFragment = new AdminHomeFragment();
            } else if (itemId == R.id.menu_cr) {
                // Launch CR module
                Intent crIntent = new Intent(this, CrMain.class);
                startActivity(crIntent);
                return true;
//                selectedFragment = new CRHomeFragment();
            } else if (itemId == R.id.menu_staff) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AdminStaffFragment())
                        .commit();
                return true;
//                selectedFragment = new AdminStaffFragment();
            } else if (itemId == R.id.menu_logout) {
                showLogoutConfirmation();
                return true;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, false);
                return true;
            }

            return false;
        });

        // Default Fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home); // triggers default fragment
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
        finishAffinity(); // This will close the app immediately
        super.onBackPressed();
    }
}
