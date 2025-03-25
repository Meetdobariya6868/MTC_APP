package com.example.mtc_app.customer.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.profile.EditProfileActivity;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerProfileFragment extends Fragment {

    private TextView usernameText, userHandleText, emailValueText, addressValueText, phoneValueText;
    private Button editProfileButton, logOutButton;
    private ProgressBar loadingProgress;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private static final String PREFS_NAME = "UserProfilePrefs";
    private boolean isFirstLoad = true;

    public CustomerProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_customer_profile_fragment, container, false);

        usernameText = view.findViewById(R.id.username);
        userHandleText = view.findViewById(R.id.user_handle);
        emailValueText = view.findViewById(R.id.email_value);
        addressValueText = view.findViewById(R.id.address_value);
        phoneValueText = view.findViewById(R.id.phone_value);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        logOutButton = view.findViewById(R.id.logOut);
        loadingProgress = view.findViewById(R.id.loading_progress);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadCachedUserDetails();
        loadUserDetails(false); // Load without showing loading progress initially

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        logOutButton.setOnClickListener(v -> showLogoutConfirmation());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserDetails(true); // Reload on resume without showing progress bar
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logoutUser() {
        auth.signOut();
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        preferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), CustomerLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadCachedUserDetails() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        usernameText.setText(preferences.getString("name", ""));
        userHandleText.setText(preferences.getString("role", ""));
        emailValueText.setText(preferences.getString("email", ""));
        addressValueText.setText(preferences.getString("address", ""));
        phoneValueText.setText(preferences.getString("phone", ""));
    }

    private void loadUserDetails(boolean isResumed) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (isFirstLoad) {
                loadingProgress.setVisibility(View.VISIBLE);
                isFirstLoad = false;
            }

            String userId = currentUser.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(this::populateUserDetails)
                    .addOnFailureListener(e -> {
                        loadingProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Failed to load user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void populateUserDetails(DocumentSnapshot document) {
        if (document.exists()) {
            String name = document.getString("name");
            String role = document.getString("role");
            String email = document.getString("email");
            String address = document.getString("address");
            String phone = document.getString("phone");

            usernameText.setText(name);
            userHandleText.setText(role);
            emailValueText.setText(email);
            addressValueText.setText(address);
            phoneValueText.setText(phone);

            SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", name);
            editor.putString("role", role);
            editor.putString("email", email);
            editor.putString("address", address);
            editor.putString("phone", phone);
            editor.apply();
        } else {
            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
        }

        loadingProgress.setVisibility(View.GONE);
    }
}
