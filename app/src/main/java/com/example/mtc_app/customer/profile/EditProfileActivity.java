package com.example.mtc_app.customer.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailEditText, addressEditText, phoneEditText, nameEditText;
    private Button saveButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.edit_name);
        addressEditText = findViewById(R.id.edit_address);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.save_button);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchData();

        saveButton.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            String newAddress = addressEditText.getText().toString().trim();
            String newPhone = phoneEditText.getText().toString().trim();
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
        String name = documentSnapshot.getString("name");
        String address = documentSnapshot.getString("address");
        String phone = documentSnapshot.getString("phone");

        nameEditText.setText(name);
        addressEditText.setText(address);
        phoneEditText.setText(phone);
    }

    private void updateProfile(String userId, String newAddress, String newPhone, String newName) {
        firestore.collection("users").document(userId)
                .update("address", newAddress, "phone", newPhone, "name",newName)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
