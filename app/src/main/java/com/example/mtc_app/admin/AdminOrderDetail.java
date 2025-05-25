package com.example.mtc_app.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class AdminOrderDetail extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView nameText, addressText, phoneText, emailText, discussionDetailsText,
            testSelectionText, totalPriceText, deviationDetailText, discussionDetailText, dateText;
    private TextView radioSelectionsText, reviewRemarksText, selectedPointsText, testSelectionsText,categoryQuantityText;
    private Button editButton, deleteButton;
    private ProgressBar progressBar;

    private String orderId;

    private DocumentReference orderRef;

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
        categoryQuantityText = findViewById(R.id.categoryQuantity);
        progressBar = findViewById(R.id.progressBar);

        orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            orderRef = db.collection("Total Orders").document(orderId);
            listenToOrderChanges(); // Real-time listener
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminOrderDetail.this, AdminEditOrderActivity.class);
            intent.putExtra("orderId", orderId);
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
    }

    private void listenToOrderChanges() {
        progressBar.setVisibility(View.VISIBLE);

        orderRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null || documentSnapshot == null || !documentSnapshot.exists()) {
                progressBar.setVisibility(View.GONE); // Hide loader on error
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.GONE);

            // Update UI with latest data
            String customerName = documentSnapshot.getString("Customer Name");
            if (customerName == null || customerName.isEmpty()) {
                String email = documentSnapshot.getString("Email");
                customerName = (email != null && email.contains("@")) ? email.substring(0, email.indexOf('@')) : "Customer";
            }
            nameText.setText(customerName);

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

            Number totalPriceNumber = documentSnapshot.getDouble("Total Price");
            totalPriceText.setText(totalPriceNumber != null ?
                    String.format("₹%,.2f", totalPriceNumber.doubleValue()) : "Not Available");

            List<String> testSelectionList = (List<String>) documentSnapshot.get("Test Selection");
            if (testSelectionList != null && !testSelectionList.isEmpty()) {
                StringBuilder testBuilder = new StringBuilder();
                for (String test : testSelectionList) {
                    testBuilder.append("• ").append(test).append("\n");
                }
                testSelectionText.setText(testBuilder.toString().trim());
            } else {
                testSelectionText.setText("No Test Selection available");
            }

            String labJobNumber = documentSnapshot.getString("LabJobNumber");
            TextView labJobNumberText = findViewById(R.id.labJobNumberText);
            if (labJobNumber != null && !labJobNumber.isEmpty()) {
                labJobNumberText.setText("Job ID: " + labJobNumber);
            } else {
                labJobNumberText.setText("Job ID: Not Available");
            }


            Map<String, Object> radioSelections = (Map<String, Object>) documentSnapshot.get("Radio Selections");
            radioSelectionsText.setText(buildBulletList(radioSelections, "No Radio Selections"));

            Map<String, Object> reviewRemarks = (Map<String, Object>) documentSnapshot.get("Review Remarks");
            reviewRemarksText.setText(buildBulletList(reviewRemarks, "No Review Remarks"));

            Map<String, Object> categoryQuantity = (Map<String, Object>) documentSnapshot.get("Category Quantities");
            categoryQuantityText.setText(buildBulletList(categoryQuantity, "No category Quantity"));

            Map<String, Object> selectedPoints = (Map<String, Object>) documentSnapshot.get("Selected Points");
            selectedPointsText.setText(buildBulletList(selectedPoints, "No Selected Points"));

            Map<String, Object> testSelectionsMap = (Map<String, Object>) documentSnapshot.get("Test Selections");
            if (testSelectionsMap != null) {
                StringBuilder selectionsBuilder = new StringBuilder();
                for (Map.Entry<String, Object> entry : testSelectionsMap.entrySet()) {
                    selectionsBuilder.append(entry.getKey()).append(":\n");
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        for (Object test : list) {
                            selectionsBuilder.append("   • ").append(String.valueOf(test)).append("\n");
                        }
                    } else {
                        selectionsBuilder.append("   (No tests found)\n");
                    }
                    selectionsBuilder.append("\n");
                }
                testSelectionsText.setText(selectionsBuilder.toString().trim());
            } else {
                testSelectionsText.setText("No Test Selections available.");
            }

        });
    }

    private void deleteOrder() {
        if (orderId != null) {
            db.collection("Total Orders").document(orderId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete order", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private String buildBulletList(Map<String, Object> map, String defaultText) {
        if (map == null || map.isEmpty()) return defaultText;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return builder.toString().trim();
    }
}
