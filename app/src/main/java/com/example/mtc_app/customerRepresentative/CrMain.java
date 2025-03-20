//package com.example.mtc_app.customerRepresentative;
//import com.example.mtc_app.R;
//
//import android.os.Bundle;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.navigation.NavigationView;
//
//public class CrMain extends AppCompatActivity {
//
//    FloatingActionButton fab;
//    DrawerLayout drawerLayout;
//    BottomNavigationView bottomNavigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main); // Ensure the layout file is correct
//
//        // Initialize views
//        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        fab = findViewById(R.id.fab);
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//
//        // Set up Toolbar
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // Load the default fragment on first run
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CRHomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
//
//        replaceFragment(new CRHomeFragment());
//
//        // Set up BottomNavigationView
//        bottomNavigationView.setBackground(null); // Remove background for FAB overlap
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.home) {
//                replaceFragment(new CRHomeFragment());
////            } else if (item.getItemId() == R.id.addcustomer) {
////                replaceFragment(new AddCustomer());
////            } else if (item.getItemId() == R.id.customerdetails) {
////                replaceFragment(new CustomerDetails());
//            } else if (item.getItemId() == R.id.profile) {
//                replaceFragment(new CRProfile());
//            } else {
//                return false;
//            }
//            return true;
//        });
//
//        // FAB click listener
//        fab.setOnClickListener(view -> replaceFragment(new AddCustomer())); // Open AddCustomer fragment
//    }
//
//    // Replace the current fragment
//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
//}


package com.example.mtc_app.customerRepresentative;
import com.example.mtc_app.R;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class CrMain extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure the layout file is correct

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab);
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set up Toolbar
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // Load the default fragment on first run
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CRHomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }

        replaceFragment(new CRHomeFragment());

        // Set up BottomNavigationView
        bottomNavigationView.setBackground(null); // Remove background for FAB overlap
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new CRHomeFragment());
//            } else if (item.getItemId() == R.id.addcustomer) {
//                replaceFragment(new AddCustomer());
            } else if (item.getItemId() == R.id.nav_profile) {
                replaceFragment(new CRProfile());
            } else {
                return false;
            }
            return true;
        });

        // FAB click listener
        fab.setOnClickListener(view -> replaceFragment(new AddCustomer())); // Open AddCustomer fragment
    }

    // Replace the current fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}

//
//
//package com.example.mtc_app.customerRepresentative;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.mtc_app.R;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//public class CrMain extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        // Handle bottom navigation item clicks
//
////                bottomNavigationView.setOnItemSelectedListener(item -> {
////                    int itemId = item.getItemId();
////                    if (itemId == R.id.nav_home) {
////                        Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
////                        return true;
////                    } else if (itemId == R.id.nav_profile) {
////                        Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
////                        return true;
////                    } else {
////                        return false;
////                    }
////                });
//
//
//
//
//                // Handle FAB click
////        fab.setOnClickListener(v -> Toast.makeText(this, "Add Button Clicked", Toast.LENGTH_SHORT).show());
//                drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//
//        // Set up Toolbar
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // Load the default fragment on first run
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CRHomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
//
//        replaceFragment(new CRHomeFragment());
//
//        // Set up BottomNavigationView
//        bottomNavigationView.setBackground(null); // Remove background for FAB overlap
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.home) {
//                replaceFragment(new CRHomeFragment());
////            } else if (item.getItemId() == R.id.addcustomer) {
////                replaceFragment(new AddCustomer());
////            } else if (item.getItemId() == R.id.customerdetails) {
////                replaceFragment(new CustomerDetails());
//            } else if (item.getItemId() == R.id.profile) {
//                replaceFragment(new CRProfile());
//            } else {
//                return false;
//            }
//            return true;
//        });
//
//        // FAB click listener
//        fab.setOnClickListener(view -> replaceFragment(new AddCustomer())); // Open AddCustomer fragment
//    }
//
//    // Replace the current fragment
//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
//
//    }
