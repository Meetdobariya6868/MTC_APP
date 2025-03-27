package com.example.mtc_app.admin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mtc_app.R;

public class AdminOrderDetail extends AppCompatActivity {

    private TextView nameText, addressText, phoneText, emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        // Initialize views
        initializeViews();

        // Populate views with intent data
        populateViewsFromIntent();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.nameText);
        addressText = findViewById(R.id.addressText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
    }

    private void populateViewsFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameText.setText(extras.getString("name", "N/A"));
            addressText.setText(extras.getString("address", "N/A"));
            phoneText.setText(extras.getString("phone", "N/A"));
            emailText.setText(extras.getString("email", "N/A"));
        }
    }
}