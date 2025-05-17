package com.example.mtc_app.customer.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mtc_app.R;
import com.example.mtc_app.customer.profile.EditProfileActivity;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.example.mtc_app.utils.CloudinaryManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomerProfileFragment extends Fragment {

    private TextView usernameText, userHandleText, emailValueText, addressValueText, phoneValueText;
    private Button editProfileButton, logOutButton;
    private ProgressBar loadingProgress;

    private ImageView profilePicture, editProfileIcon;
    private FirebaseFirestore db;
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final String CLOUDINARY_FOLDER_NAME = "profile_images";
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
        profilePicture = view.findViewById(R.id.profile_image);
        userHandleText = view.findViewById(R.id.user_handle);
        emailValueText = view.findViewById(R.id.email_value);
        addressValueText = view.findViewById(R.id.address_value);
        phoneValueText = view.findViewById(R.id.phone_value);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        logOutButton = view.findViewById(R.id.logOut);
        loadingProgress = view.findViewById(R.id.loading_progress);
        editProfileIcon = view.findViewById(R.id.edit_icon);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Load cached data instantly while Firebase loads in the background
        loadCachedUserDetails();
        fetchUserDetails(false);

        editProfileIcon.setOnClickListener(v -> showImagePickerDialog());

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

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        pickImageFromGallery();
                    } else {
                        captureImageFromCamera();
                    }
                })
                .show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(requireContext().getExternalFilesDir(null), "profile_pic.jpg");
        imageUri = FileProvider.getUriForFile(requireContext(), "com.example.mtc_app.fileprovider", imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isAdded() && resultCode == requireActivity().RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                imageUri = data.getData();
                uploadImageToCloudinary();
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                uploadImageToCloudinary();
            }
        }
    }

    private void uploadImageToCloudinary() {
        if (imageUri == null) return;

        new Thread(() -> {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("folder", CLOUDINARY_FOLDER_NAME);

                Map uploadResult = CloudinaryManager.getInstance().uploader().upload(inputStream, uploadParams);
                String imageUrl = (String) uploadResult.get("secure_url");

//                requireActivity().runOnUiThread(() -> updateProfileImageUrl(imageUrl));

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateProfileImageUrl(imageUrl));
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void updateProfileImageUrl(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    Glide.with(requireContext()).load(imageUrl).into(profilePicture);
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
    private void performLogout() {
        // Show loading indicator
        loadingProgress.setVisibility(View.VISIBLE);

        // Sign out from Firebase Authentication
        auth.signOut();

        // Clear all shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", 0);
        sharedPreferences.edit().clear().apply();

        SharedPreferences profilePrefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        profilePrefs.edit().clear().apply();

        // Redirect to login screen after a short delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(requireActivity(), CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish(); // Ensures activity is fully closed
        }, 500); // Smooth transition
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
