package com.example.mtc_app.staff;

import com.example.mtc_app.profile.EditProfileActivity;
import com.example.mtc_app.utils.CloudinaryManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.mtc_app.R;
import com.example.mtc_app.auth.AuthUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class staff_profile_page extends AppCompatActivity {

    private static final String TAG = "staff_profile_page";
    private ImageView profilePicture;
    private TextView profileName, profileEmail, profilePhone, addressValue;
    private Button btnLogout, btnEditProfile;
    private FirebaseFirestore db;
    private Uri imageUri;

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final String CLOUDINARY_FOLDER_NAME = "profile_images";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile_page);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize UI components
        initializeViews();

        if (currentUser != null) {
            // Initial data load
            fetchProfileData(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing views");
        profilePicture = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.username);
        profileEmail = findViewById(R.id.emailValue);
        profilePhone = findViewById(R.id.phoneValue);
        addressValue = findViewById(R.id.addressValue);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.edit_profile_button);

        // Clear default text immediately to avoid "Staff_name" showing
        profileName.setText("");
    }

    private void setupClickListeners() {
        ImageView editProfileIcon = findViewById(R.id.edit_icon);
        editProfileIcon.setOnClickListener(v -> showImagePickerDialog());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - refreshing profile data");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Force a fresh fetch from the server, not cache
            fetchProfileDataFromServer(user.getUid());
        }
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(this)
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
        File imageFile = new File(getExternalFilesDir(null), "profile_pic.jpg");
        imageUri = FileProvider.getUriForFile(this, "com.example.mtc_app.fileprovider", imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
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
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("folder", CLOUDINARY_FOLDER_NAME);

                Map uploadResult = CloudinaryManager.getInstance().uploader().upload(inputStream, uploadParams);
                String imageUrl = (String) uploadResult.get("secure_url");

                runOnUiThread(() -> updateProfileImageUrl(imageUrl));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void updateProfileImageUrl(String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    Glide.with(this).load(imageUrl).into(profilePicture);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logout() {
        AuthUtils.logout(this);
    }

    private void fetchProfileData(String userId) {
        Log.d(TAG, "Regular fetch profile data for user: " + userId);
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(this::processProfileData)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching profile data", e);
                    Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchProfileDataFromServer(String userId) {
        Log.d(TAG, "FORCE SERVER fetch profile data for user: " + userId);
        // Use Source.SERVER to force getting fresh data
        db.collection("users")
                .document(userId)
                .get(Source.SERVER)
                .addOnSuccessListener(this::processProfileData)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching profile data from server", e);
                    Toast.makeText(this, "Failed to refresh profile.", Toast.LENGTH_SHORT).show();
                });
    }

    private void processProfileData(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            // Log all fields for debugging
            Log.d(TAG, "Document data: " + documentSnapshot.getData());

            // Check field names - the problem might be in how the field is named
            for (String field : documentSnapshot.getData().keySet()) {
                Log.d(TAG, "Field found: " + field + " = " + documentSnapshot.get(field));
            }

            // Correctly identify the field name for the user's name
            // Try different variations of the name field
            String name = null;
            if (documentSnapshot.contains("name")) {
                name = documentSnapshot.getString("name");
            } else if (documentSnapshot.contains("fullName")) {
                name = documentSnapshot.getString("fullName");
            } else if (documentSnapshot.contains("displayName")) {
                name = documentSnapshot.getString("displayName");
            } else if (documentSnapshot.contains("userName")) {
                name = documentSnapshot.getString("userName");
            }

            Log.d(TAG, "Name found in document: " + name);

            if (name != null && !name.trim().isEmpty()) {
                Log.d(TAG, "Setting profileName TextView to: " + name);
                profileName.setText(name);
            } else {
                Log.d(TAG, "No valid name found in document");
            }

            // Process other fields
            String email = documentSnapshot.getString("email");
            if (email != null) {
                profileEmail.setText(email);
            }

            String address = documentSnapshot.getString("address");
            if (address != null) {
                addressValue.setText(address);
            }

            String phone = documentSnapshot.getString("phone");
            if (phone != null) {
                profilePhone.setText(phone);
            }

            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(staff_profile_page.this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.cust_profile)
                        .into(profilePicture);
            }
        } else {
            Log.d(TAG, "Document does not exist");
            Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
        }
    }
}