package com.example.mtc_app.customerRepresentative;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mtc_app.R;
import com.example.mtc_app.auth.AuthUtils;
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

public class CRProfile extends Fragment {

    private ImageView profilePicture, editProfileIcon;
    private TextView profileName, profileEmail, profilePhone, addressValue,username;
    private Button btnLogout, btnEditProfile;
    private FirebaseFirestore db;
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private static final String CLOUDINARY_FOLDER_NAME = "profile_images";

    public CRProfile() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cr_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize UI components
        username = view.findViewById(R.id.username);
        profilePicture = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.username);
        profileEmail = view.findViewById(R.id.emailValue);
        profilePhone = view.findViewById(R.id.phoneValue);
        addressValue = view.findViewById(R.id.addressValue);
        btnLogout = view.findViewById(R.id.logOut);
        btnEditProfile = view.findViewById(R.id.edit_profile_button);
        editProfileIcon = view.findViewById(R.id.edit_icon);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchProfileData(userId);
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        editProfileIcon.setOnClickListener(v -> showImagePickerDialog());
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
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

    private void showLogoutConfirmation() {
        if (isAdded()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> logout())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void logout() {
        AuthUtils.logout(requireContext());
    }

    private void fetchProfileData(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        username.setText(documentSnapshot.getString("name"));
                        profileEmail.setText(documentSnapshot.getString("email"));
                        addressValue.setText(documentSnapshot.getString("address"));
                        profilePhone.setText(documentSnapshot.getString("phone"));

                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            if (isAdded()) {
                                Glide.with(requireContext())
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.cust_profile)
                                        .into(profilePicture);
                            }

                        }
                    } else {
                        Toast.makeText(requireContext(), "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching profile data", e);
                    Toast.makeText(requireContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
                });
    }
}