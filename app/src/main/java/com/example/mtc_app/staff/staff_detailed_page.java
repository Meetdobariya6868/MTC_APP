package com.example.mtc_app.staff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mtc_app.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class staff_detailed_page extends AppCompatActivity {

    private TextView orderDate, discussionDetails, dispatchAddress, testSelections, totalPrice;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detailed_page);

        // Initialize TextViews for required fields only
        orderDate = findViewById(R.id.date);
        discussionDetails = findViewById(R.id.customerName);
        dispatchAddress = findViewById(R.id.dispatchMode);
        testSelections = findViewById(R.id.testSelections);
        totalPrice = findViewById(R.id.email_staff);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email") && intent.hasExtra("customerName")) {
            String emailValue = intent.getStringExtra("email");
            String customerNameValue = intent.getStringExtra("customerName");
            fetchOrderDetails(emailValue, customerNameValue);
        } else {
            Toast.makeText(this, "No Order details provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOrderDetails(String email, String customerName) {
        db.collection("Total Orders")
                .whereEqualTo("Customer Name", customerName)
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            orderDate.setText(doc.getString("Created At")); // Fetch Order Date
                            dispatchAddress.setText(doc.getString("Dispatch Address")); // Fetch Dispatch Address
                            discussionDetails.setText(doc.getString("Discussion Details")); // Fetch Discussion Details
                            totalPrice.setText(doc.getString("Email")); // Fetch Total Price

                            // Fetch Test Selections (Stored as a Map in Firestore)
                            Map<String, Object> testSelectionsMap = (Map<String, Object>) doc.get("Test Selections");
                            if (testSelectionsMap != null) {
                                testSelections.setText(testSelectionsMap.toString()); // Convert Map to String for display
                            } else {
                                testSelections.setText("No test selections available");
                            }

                            Log.d("Firestore", "Order details loaded successfully.");
                        }
                    } else {
                        Toast.makeText(this, "No order found for the provided details", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching order details", e);
                    Toast.makeText(this, "Error loading order data", Toast.LENGTH_SHORT).show();
                });
    }
}
