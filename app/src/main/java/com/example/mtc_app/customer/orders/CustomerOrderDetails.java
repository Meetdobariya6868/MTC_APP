// ✅ Final Optimized: CustomerOrderDetails.java
package com.example.mtc_app.customer.orders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CustomerOrderDetails extends AppCompatActivity {

    private TextView orderStatus, orderDate, customerName, mobileNumber, email,
            dispatchAddress, modeOfDispatch, totalPrice, complianceStatement,
            deviationDetails, discussionDetails, standardDeviation, reviewRemarks,
            sampleCondition, termsAndConditions, testingRequirements;
    private MaterialButton backButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String orderId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);

        db = FirebaseFirestore.getInstance();

        orderStatus = findViewById(R.id.orderStatus);
        orderDate = findViewById(R.id.orderDate);
        customerName = findViewById(R.id.customerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        dispatchAddress = findViewById(R.id.dispatchAddress);
        modeOfDispatch = findViewById(R.id.modeOfDispatch);
        totalPrice = findViewById(R.id.totalPrice);
        complianceStatement = findViewById(R.id.complianceStatement);
        deviationDetails = findViewById(R.id.deviationDetails);
        discussionDetails = findViewById(R.id.discussionDetails);
        standardDeviation = findViewById(R.id.standardDeviation);
        reviewRemarks = findViewById(R.id.reviewRemarks);
        sampleCondition = findViewById(R.id.sampleCondition);
        termsAndConditions = findViewById(R.id.termsAndConditions);
        testingRequirements = findViewById(R.id.testingRequirements);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.orderLoadingProgress);

        orderId = getIntent().getStringExtra("orderId");

        if (orderId != null && !orderId.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetails(String orderId) {
        db.collection("Total Orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        setTextView(orderStatus, "Status: ", documentSnapshot.getString("Status"));
                        setTextView(orderDate, "Order Date: ", documentSnapshot.getString("Created At"));
                        setTextView(customerName, "Customer Name: ", documentSnapshot.getString("Customer Name"));
                        setTextView(mobileNumber, "Mobile Number: ", documentSnapshot.getString("Mobile Number"));
                        setTextView(email, "Email: ", documentSnapshot.getString("Email"));
                        setTextView(dispatchAddress, "Dispatch Address: ", documentSnapshot.getString("Dispatch Address"));
                        setTextView(modeOfDispatch, "Mode of Dispatch: ", getNestedValue(documentSnapshot, "Radio Selections.Mode of Dispatch"));
                        setTextView(complianceStatement, "Compliance Statement: ", getNestedValue(documentSnapshot, "Radio Selections.Compliance Statement"));
                        setTextView(deviationDetails, "Deviation Details: ", documentSnapshot.getString("Deviation Details"));
                        setTextView(discussionDetails, "Discussion Details: ", documentSnapshot.getString("Discussion Details"));
                        setTextView(standardDeviation, "Standard Deviation: ", getNestedValue(documentSnapshot, "Radio Selections.Standard Deviation"));
                        setTextView(sampleCondition, "Sample Condition: ", getNestedValue(documentSnapshot, "Radio Selections.Sample Condition"));
                        setTextView(termsAndConditions, "Terms & Conditions: ", documentSnapshot.getString("Terms And Conditions"));

                        if (documentSnapshot.contains("Total Price")) {
                            Object priceObj = documentSnapshot.get("Total Price");
                            String priceStr = priceObj != null ? String.valueOf(priceObj) : "N/A";
                            setTextView(totalPrice, "Total Price: ₹", priceStr);
                        } else {
                            setTextView(totalPrice, "Total Price: ₹", "N/A");
                        }

                        if (documentSnapshot.contains("Review Remarks")) {
                            Map<String, String> reviewMap = (Map<String, String>) documentSnapshot.get("Review Remarks");
                            if (reviewMap != null) {
                                StringBuilder reviewDetails = new StringBuilder("Review Remarks:\n");
                                for (Map.Entry<String, String> entry : reviewMap.entrySet()) {
                                    reviewDetails.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                                }
                                setTextView(reviewRemarks, "", reviewDetails.toString());
                            }
                        }

                        if (documentSnapshot.contains("Test Selections")) {
                            Map<String, Object> testSelections = (Map<String, Object>) documentSnapshot.get("Test Selections");
                            if (testSelections != null) {
                                StringBuilder testDetails = new StringBuilder("Testing Requirements:\n");
                                for (Map.Entry<String, Object> entry : testSelections.entrySet()) {
                                    if (entry.getValue() instanceof List) {
                                        List<String> tests = (List<String>) entry.getValue();
                                        testDetails.append(entry.getKey()).append(": ").append(tests.toString()).append("\n");
                                    }
                                }
                                setTextView(testingRequirements, "", testDetails.toString());
                            }
                        }
                    } else {
                        Toast.makeText(this, "Order details not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error fetching order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CustomerOrderDetails", "Firestore fetch error", e);
                });
    }

    private String getNestedValue(DocumentSnapshot doc, String key) {
        try {
            String[] path = key.split("\\.");
            Object value = doc.get(path[0]);
            for (int i = 1; i < path.length; i++) {
                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).get(path[i]);
                } else {
                    return "N/A";
                }
            }
            return value != null ? value.toString() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void setTextView(TextView textView, String label, String value) {
        if (textView != null) {
            textView.setText(label + (value != null ? value : "N/A"));
        }
    }
}
