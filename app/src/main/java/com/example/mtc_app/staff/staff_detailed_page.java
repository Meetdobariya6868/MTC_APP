package com.example.mtc_app.staff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class staff_detailed_page extends AppCompatActivity {

    private TextView customerName, dispatchAddress, email1, totalPrice, complianceStatement, modeOfDispatch, mobileNumber;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detailed_page);

        customerName  = findViewById(R.id.customerName);
        dispatchAddress  = findViewById(R.id.quantity);
        email1  = findViewById(R.id.email_staff);
        totalPrice = findViewById(R.id.price);
        complianceStatement = findViewById(R.id.testsPerformed);
        modeOfDispatch = findViewById(R.id.dispatchMode);
        mobileNumber = findViewById(R.id.phoneNo);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("email")) {
            String emailValue = intent.getStringExtra("email");
            fetchOrderDetails(emailValue);
        } else {
            Toast.makeText(this, "No Order ID provided", Toast.LENGTH_SHORT).show();
        }

//        if (intent != null) {
//            customerName.setText(intent.getStringExtra("customerName"));
//            dispatchAddress.setText(intent.getStringExtra("dispatchAddress"));
//            email.setText(intent.getStringExtra("email"));
//        }
    }

    private void fetchOrderDetails(String email) {
        db.collection("Total Orders")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            customerName.setText(doc.getString("customer Name"));
                            dispatchAddress.setText(doc.getString("dispatch Address"));
                            email1.setText(doc.getString("email"));
//                            totalPrice.setText(String.valueOf(doc.getDouble("Total Price")));
                            complianceStatement.setText(doc.getString("compliance Statement"));
                            modeOfDispatch.setText(doc.getString("mode Of Dispatch"));
                            mobileNumber.setText(doc.getString("mobile Number"));
                        }
                    } else {
                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching order details", e);
                    Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
    }
}
