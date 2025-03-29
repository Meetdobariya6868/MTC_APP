package com.example.mtc_app.admin.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.cardview.widget.CardView;

import java.util.List;

public class AdminHomeFragment extends Fragment {

    private LinearLayout orderContainer; // Container for dynamic order cards
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find the order container layout
        orderContainer = rootView.findViewById(R.id.orderContainer);

        // Fetch orders dynamically
        loadOrdersFromFirestore();

        return rootView;
    }

    private void loadOrdersFromFirestore() {
        db.collection("Total Orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<DocumentSnapshot> orders = querySnapshot.getDocuments();
                            displayOrders(orders);
                        }
                    } else {
                        Log.e("Firestore", "Error fetching orders", task.getException());
                    }
                });
    }

    private void displayOrders(List<DocumentSnapshot> orders) {
        orderContainer.removeAllViews(); // Clear old views before adding new ones

        for (DocumentSnapshot order : orders) {
            View orderView = getLayoutInflater().inflate(R.layout.order_card, orderContainer, false);
            TextView orderTitle = orderView.findViewById(R.id.orderTitle);
            TextView customerName = orderView.findViewById(R.id.customerName);

            // Fetch data dynamically
            String orderId = order.getId();
            String name = order.getString("Customer Name");
            String address = order.getString("Dispatch Address");
            String phone = order.getString("Mobile Number");

            // Set data to UI
            orderTitle.setText("Order ID: " + phone);
            customerName.setText("Customer: " + name);

            // Set click listener to open order details
            orderView.setOnClickListener(v -> openOrderDetail(order));

            // Add dynamic order card to container
            orderContainer.addView(orderView);
        }
    }

    private void openOrderDetail(DocumentSnapshot order) {
        Intent intent = new Intent(getActivity(), AdminOrderDetail.class);
        intent.putExtra("orderId", order.getId());
        intent.putExtra("name", order.getString("Email"));
        intent.putExtra("address", order.getString("Dispatch Address"));
        intent.putExtra("phone", order.getString("Mobile Number"));
        startActivity(intent);
    }
}
