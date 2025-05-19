package com.example.mtc_app.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class AdminOrderDetail extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView nameText, addressText, phoneText, emailText, discussionDetailsText, testSelectionText, totalPriceText, deviationDetailText, discussionDetailText, dateText;
    private TextView radioSelectionsText, reviewRemarksText, selectedPointsText, testSelectionsText;

    private Button editButton, deleteButton;

    private String orderId;


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
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        totalPriceText = findViewById(R.id.totalPriceText);
        deviationDetailText = findViewById(R.id.deviationDetailText);
        discussionDetailText = findViewById(R.id.discussionDetailText);
        dateText = findViewById(R.id.dateText);

        radioSelectionsText = findViewById(R.id.radioSelectionsText);
        reviewRemarksText = findViewById(R.id.reviewRemarksText);
        selectedPointsText = findViewById(R.id.selectedPointsText);
        testSelectionsText = findViewById(R.id.testSelectionsText);


        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminOrderDetail.this, AdminEditOrderActivity.class);
            intent.putExtra("orderId", getIntent().getStringExtra("orderId"));
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AdminOrderDetail.this)
                    .setTitle("Delete Order")
                    .setMessage("Are you sure you want to delete this order?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteOrder())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Get order ID from Intent
        orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            fetchOrderDetails(orderId);
        }
    }

    private void deleteOrder() {
        if (orderId != null) {
            db.collection("Total Orders").document(orderId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to previous screen (Admin home)
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete order", Toast.LENGTH_SHORT).show()
                    );
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

                        String deviationDetail = documentSnapshot.getString("Deviation Details");
                        deviationDetailText.setText(deviationDetail != null ? deviationDetail : "Not Available");

                        String discussionDetail = documentSnapshot.getString("Discussion Details");
                        discussionDetailText.setText(discussionDetail != null ? discussionDetail : "Not Available");

                        String orderDate = documentSnapshot.getString("Created At");
                        dateText.setText(orderDate != null ? orderDate : "Not Available");

                        // Fetch and display total price as a formatted number
                        Number totalPriceNumber = documentSnapshot.getDouble("Total Price"); // or use getLong() if it's stored as integer

                        if (totalPriceNumber != null) {
                            // Format to currency-style if needed
                            String formattedPrice = String.format("₹%,.2f", totalPriceNumber.doubleValue());
                            totalPriceText.setText(formattedPrice);
                        } else {
                            totalPriceText.setText("Not Available");
                        }


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

                        // RADIO SELECTIONS
                        Map<String, Object> radioSelections = (Map<String, Object>) documentSnapshot.get("Radio Selections");
                        if (radioSelections != null) {
                            StringBuilder radioText = new StringBuilder();
                            for (Map.Entry<String, Object> entry : radioSelections.entrySet()) {
                                radioText.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                            }
                            radioSelectionsText.setText(radioText.toString().trim());
                        } else {
                            radioSelectionsText.setText("No Radio Selections");
                        }

                        // REVIEW REMARKS
                        Map<String, Object> reviewRemarks = (Map<String, Object>) documentSnapshot.get("Review Remarks");
                        if (reviewRemarks != null) {
                            StringBuilder reviewText = new StringBuilder();
                            for (Map.Entry<String, Object> entry : reviewRemarks.entrySet()) {
                                reviewText.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                            }
                            reviewRemarksText.setText(reviewText.toString().trim());
                        } else {
                            reviewRemarksText.setText("No Review Remarks");
                        }

                        // SELECTED POINTS
                        Map<String, Object> selectedPoints = (Map<String, Object>) documentSnapshot.get("Selected Points");
                        if (selectedPoints != null) {
                            StringBuilder selectedText = new StringBuilder();
                            for (Map.Entry<String, Object> entry : selectedPoints.entrySet()) {
                                selectedText.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                            }
                            selectedPointsText.setText(selectedText.toString().trim());
                        } else {
                            selectedPointsText.setText("No Selected Points");
                        }

                        // TEST SELECTIONS
                        Map<String, Object> testSelectionsMap = (Map<String, Object>) documentSnapshot.get("Test Selections");

                        if (testSelectionsMap != null) {
                            StringBuilder testSelectionsBuilder = new StringBuilder();

                            for (Map.Entry<String, Object> entry : testSelectionsMap.entrySet()) {
                                String category = entry.getKey();
                                Object value = entry.getValue();

                                testSelectionsBuilder.append(category).append(":\n");

                                if (value instanceof List) {
                                    List<?> tests = (List<?>) value;
                                    for (Object test : tests) {
                                        testSelectionsBuilder.append("   • ").append(String.valueOf(test)).append("\n");
                                    }
                                } else {
                                    testSelectionsBuilder.append("   (No tests found)\n");
                                }

                                testSelectionsBuilder.append("\n");
                            }

                            testSelectionsText.setText(testSelectionsBuilder.toString().trim());
                        } else {
                            testSelectionsText.setText("No Test Selections available.");
                        }


                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Error
                });
    }
}