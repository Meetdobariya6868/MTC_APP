package com.example.mtc_app.customer.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

        // Load cached data instantly while Firebase loads in the background
        loadCachedUserDetails();
        fetchUserDetails(false);

        editProfileButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        logOutButton.setOnClickListener(v -> showLogoutConfirmation());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserDetails(true); // Reload on resume without progress bar
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        // Show loading indicator
        loadingProgress.setVisibility(View.VISIBLE);

        // Sign out from Firebase Authentication
        auth.signOut();

        // Clear all shared preferences
        clearAllPreferences();

        // Delay to show loading and ensure sign out process completes
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Redirect to login screen
            Intent intent = new Intent(requireActivity(), CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Ensure fragment and activity are properly finished
            requireActivity().finish();
        }, 500); // Short delay to ensure smooth transition
    }

    private void clearAllPreferences() {
        // Clear user-related shared preferences
        SharedPreferences profilePrefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        profilePrefs.edit().clear().apply();
    }


    private void loadCachedUserDetails() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        usernameText.setText(preferences.getString("name", "Loading..."));
        userHandleText.setText(preferences.getString("role", "Loading..."));
        emailValueText.setText(preferences.getString("email", ""));
        addressValueText.setText(preferences.getString("address", ""));
        phoneValueText.setText(preferences.getString("phone", ""));
    }

    private void fetchUserDetails(boolean isResumed) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        if (isFirstLoad) {
            loadingProgress.setVisibility(View.VISIBLE);
            isFirstLoad = false;
        }

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(this::updateUserDetails)
                .addOnFailureListener(e -> {
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserDetails(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch data
        String name = document.getString("name");
        String role = document.getString("role");
        String email = document.getString("email");
        String address = document.getString("address");
        String phone = document.getString("phone");

        // Map role values
        String displayRole;
        switch (role) {
            case "customer": displayRole = "Customer"; break;
            case "cr": displayRole = "Customer Representative"; break;
            case "staff": displayRole = "Segment Head"; break;
            case "admin": displayRole = "Administration"; break;
            default: displayRole = "Customer"; // Default role
        }

        // Update UI only if values have changed
        if (!name.equals(usernameText.getText().toString())) usernameText.setText(name);
        if (!displayRole.equals(userHandleText.getText().toString())) userHandleText.setText(displayRole);
        if (!email.equals(emailValueText.getText().toString())) emailValueText.setText(email);
        if (!address.equals(addressValueText.getText().toString())) addressValueText.setText(address);
        if (!phone.equals(phoneValueText.getText().toString())) phoneValueText.setText(phone);

        // Save updated data to cache
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString("name", name);
        editor.putString("role", displayRole);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.putString("phone", phone);
        editor.apply();

        loadingProgress.setVisibility(View.GONE);
    }
}
