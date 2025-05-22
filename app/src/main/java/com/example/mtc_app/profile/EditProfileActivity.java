package com.example.mtc_app.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.fragments.CustomerProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText addressEditText, phoneEditText, nameEditText;
    private Button saveButton, tryAgainButton, okButton;
    private MaterialButton cancelButton;
    private ImageView profileImage;
    private View buttonsLayout;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        addressEditText = findViewById(R.id.edit_address);
        nameEditText = findViewById(R.id.edit_name);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_text);
//        tryAgainButton = findViewById(R.id.tryAgainButton);
//        okButton = findViewById(R.id.okButton);
        profileImage = findViewById(R.id.profile_image);
//        buttonsLayout = findViewById(R.id.buttonsLayout);

        // Add null checks before setting click listeners
        if (profileImage != null) {
            profileImage.setOnClickListener(v -> showImageOptions());
        }
        if (tryAgainButton != null) {
            tryAgainButton.setOnClickListener(v -> showImageOptions());
        }
//        if (okButton != null) {
//            okButton.setOnClickListener(v -> uploadImageToStorage());
//        }

        // Cancel button click listener
        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> {
                // Simply finish the activity to go back to the previous screen (profile page)
                finish();
            });
        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        fetchData();

        saveButton.setOnClickListener(v -> {
            String newAddress = addressEditText.getText().toString().trim();
            String newPhone = phoneEditText.getText().toString().trim();
            String newName = nameEditText.getText().toString().trim();

            String userId = auth.getCurrentUser().getUid();
            updateProfile(userId, newAddress, newPhone, newName);
        });
    }

    private void fetchData() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        populateUserData(documentSnapshot);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void populateUserData(DocumentSnapshot documentSnapshot) {
        String address = documentSnapshot.getString("address");
        String phone = documentSnapshot.getString("phone");
        String name = documentSnapshot.getString("name");
        String imageUrl = documentSnapshot.getString("image");

        addressEditText.setText(address);
        phoneEditText.setText(phone);
        nameEditText.setText(name);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load the image using a library like Glide or Picasso
            // Glide.with(this).load(imageUrl).into(profileImage);
        }
    }

    private void showImageOptions() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(pickPhotoIntent, "Select Option");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
        startActivityForResult(chooser, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                updateProfileImage(imageBitmap, null);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    updateProfileImage(imageBitmap, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateProfileImage(Bitmap imageBitmap, Uri uri) {
        if (imageBitmap != null && profileImage != null) {
            profileImage.setImageBitmap(imageBitmap);
            if (buttonsLayout != null) {
                buttonsLayout.setVisibility(View.VISIBLE);
            }
            imageUri = uri;
        }
    }

    private void updateProfile(String userId, String newAddress, String newPhone, String newName) {
        firestore.collection("users").document(userId)
                .update("address", newAddress, "phone", newPhone, "name", newName)
                .addOnSuccessListener(aVoid -> {
                    // Show success toast
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    // Set result to indicate successful update
                    setResult(RESULT_OK);

                    // Finish this activity
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}