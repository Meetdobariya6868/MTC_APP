package com.example.mtc_app.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminOrderDetail extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView nameText, addressText, phoneText, emailText, discussionDetailsText, testSelectionText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        db = FirebaseFirestore.getInstance();

        // Initialize Views
        nameText = findViewById(R.id.nameText);
        addressText = findViewById(R.id.addressText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        discussionDetailsText = findViewById(R.id.sampleText);
        testSelectionText = findViewById(R.id.segmentsText);

        // Get order ID from Intent
        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            fetchOrderDetails(orderId);
        }
    }

    private void fetchOrderDetails(String orderId) {
        db.collection("Total Orders").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Display customer name at the top instead of email
                        String customerName = documentSnapshot.getString("Customer Name");
                        if (customerName == null || customerName.isEmpty()) {
                            // Fallback to using name from email if customer name is not available
                            String email = documentSnapshot.getString("Email");
                            if (email != null && email.contains("@")) {
                                customerName = email.substring(0, email.indexOf('@'));
                            } else {
                                customerName = "Customer";
                            }
                        }
                        nameText.setText(customerName);

                        // Set other fields
                        addressText.setText(documentSnapshot.getString("Dispatch Address"));
                        phoneText.setText(documentSnapshot.getString("Mobile Number"));
                        emailText.setText(documentSnapshot.getString("Email"));
                        discussionDetailsText.setText(documentSnapshot.getString("Discussion Details"));

                        // Fetch "Test Selection" (Assuming it's stored as a List<String>)
                        List<String> testSelectionList = (List<String>) documentSnapshot.get("Test Selection");

                        if (testSelectionList != null && !testSelectionList.isEmpty()) {
                            StringBuilder testSelectionTextBuilder = new StringBuilder();
                            for (String test : testSelectionList) {
                                testSelectionTextBuilder.append("• ").append(test).append("\n");
                            }
                            testSelectionText.setText(testSelectionTextBuilder.toString().trim());
                        } else {
                            testSelectionText.setText("No Test Selection available");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Error
                });
    }
}