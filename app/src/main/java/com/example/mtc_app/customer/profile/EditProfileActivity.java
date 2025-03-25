package com.example.mtc_app.customer.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.edit_name);
        phoneEditText = findViewById(R.id.edit_phone);
        addressEditText = findViewById(R.id.edit_address);
        saveButton = findViewById(R.id.save_button);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (user != null) {
            fetchUserData();
        }

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void fetchUserData() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String phone = documentSnapshot.getString("phone");
                String address = documentSnapshot.getString("address");

                // Save in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.putString("phone", phone);
                editor.putString("address", address);
                editor.apply();

                // Display data
                nameEditText.setText(name);
                phoneEditText.setText(phone);
                addressEditText.setText(address);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(EditProfileActivity.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("address", address);

        db.collection("users").document(user.getUid()).update(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", name);
                        editor.putString("phone", phone);
                        editor.putString("address", address);
                        editor.apply();

                        Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
