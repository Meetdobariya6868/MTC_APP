package com.example.mtc_app.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mtc_app.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminEditOrderActivity extends AppCompatActivity {

    private EditText nameEditText, addressEditText, phoneEditText, emailEditText, discussionEditText, priceEditText;
    private Button updateButton;

    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_order);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get order ID from Intent
        orderId = getIntent().getStringExtra("orderId");

        // Initialize UI Elements
        nameEditText = findViewById(R.id.editCustomerName);
        addressEditText = findViewById(R.id.editDispatchAddress);
        phoneEditText = findViewById(R.id.editMobileNumber);
        emailEditText = findViewById(R.id.editEmail);
        discussionEditText = findViewById(R.id.editDiscussionDetails);
        priceEditText = findViewById(R.id.editTotalPrice);
        updateButton = findViewById(R.id.buttonUpdate);

        // Fetch and pre-fill data
        fetchOrderData();

        updateButton.setOnClickListener(v -> updateOrderData());
    }

    private void fetchOrderData() {
        if (orderId == null) return;

        db.collection("Total Orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameEditText.setText(documentSnapshot.getString("Customer Name"));
                        addressEditText.setText(documentSnapshot.getString("Dispatch Address"));
                        phoneEditText.setText(documentSnapshot.getString("Mobile Number"));
                        emailEditText.setText(documentSnapshot.getString("Email"));
                        discussionEditText.setText(documentSnapshot.getString("Discussion Details"));

                        Number price = documentSnapshot.getDouble("Total Price");
                        if (price != null) {
                            priceEditText.setText(String.valueOf(price));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load order data", Toast.LENGTH_SHORT).show());
    }

    private void updateOrderData() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String discussion = discussionEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Customer Name", name);
        updatedData.put("Dispatch Address", address);
        updatedData.put("Mobile Number", phone);
        updatedData.put("Email", email);
        updatedData.put("Discussion Details", discussion);
        updatedData.put("Total Price", price);

        db.collection("Total Orders").document(orderId)
                .update(updatedData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish this activity and return to the previous screen
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
