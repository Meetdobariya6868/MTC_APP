package com.example.mtc_app.customer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
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
    private ImageView profilePicture, editProfileIcon;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final String CLOUDINARY_FOLDER_NAME = "profile_images";
    private static final String PREFS_NAME = "UserProfilePrefs";
    private ProgressBar profileImageLoader;


    public CustomerProfileFragment() {}

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
        editProfileIcon = view.findViewById(R.id.edit_icon);
        profileImageLoader = view.findViewById(R.id.profile_image_loader);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadCachedUserDetails();
        fetchUserDetails();

        editProfileIcon.setOnClickListener(v -> showImagePickerDialog());
        editProfileButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        logOutButton.setOnClickListener(v -> showLogoutConfirmation());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserDetails();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Choose an option")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) pickImageFromGallery();
                    else captureImageFromCamera();
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
        if (!isAdded() || resultCode != requireActivity().RESULT_OK) return;

        if (requestCode == GALLERY_REQUEST_CODE && data != null) {
            imageUri = data.getData();
            uploadImageToCloudinary();
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            uploadImageToCloudinary();
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

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateProfileImageUrl(imageUrl));
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateProfileImageUrl(String imageUrl) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        firestore.collection("users").document(user.getUid())
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profilePicture);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    private void performLogout() {
        auth.signOut();

        requireActivity().getSharedPreferences("MyAppPrefs", 0).edit().clear().apply();
        requireActivity().getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(requireActivity(), CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }, 500);
    }

    private void loadCachedUserDetails() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        usernameText.setText(prefs.getString("name", "Loading..."));
        userHandleText.setText(prefs.getString("role", "Loading..."));
        emailValueText.setText(prefs.getString("email", ""));
        addressValueText.setText(prefs.getString("address", ""));
        phoneValueText.setText(prefs.getString("phone", ""));
    }

    private void fetchUserDetails() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        firestore.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(this::updateUserDetails)
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show());
    }

    private void updateUserDetails(DocumentSnapshot doc) {
        if (!isAdded() || getContext() == null || getActivity() == null || !doc.exists()) return;

        String name = doc.getString("name");
        String role = doc.getString("role");
        String email = doc.getString("email");
        String address = doc.getString("address");
        String phone = doc.getString("phone");
        String imageUrl = doc.getString("profileImageUrl");

        String displayRole;
        switch (role != null ? role : "") {
            case "cr": displayRole = "Customer Representative"; break;
            case "staff": displayRole = "Segment Head"; break;
            case "admin": displayRole = "Administration"; break;
            default: displayRole = "Customer";
        }

        usernameText.setText(name);
        userHandleText.setText(displayRole);
        emailValueText.setText(email);
        addressValueText.setText(address);
        phoneValueText.setText(phone);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            profileImageLoader.setVisibility(View.VISIBLE);
            profilePicture.setVisibility(View.INVISIBLE); // Hide image view until it's ready

            Glide.with(getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.25f)
                    .into(new ImageViewTarget<Drawable>(profilePicture) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            if (resource != null) {
                                profilePicture.setImageDrawable(resource);
                                profilePicture.setVisibility(View.VISIBLE);
                            }
                            profileImageLoader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            profileImageLoader.setVisibility(View.GONE);
                            profilePicture.setVisibility(View.VISIBLE); // Optional: fallback to showing old image
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If no image URL exists, hide loader and optionally show placeholder
            profileImageLoader.setVisibility(View.GONE);
            profilePicture.setImageResource(R.drawable.ic_profile); // Optional default
            profilePicture.setVisibility(View.VISIBLE);
        }


        SharedPreferences.Editor editor = getActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit();

        editor.putString("name", name);
        editor.putString("role", displayRole);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.putString("phone", phone);
        editor.apply();
    }
}
