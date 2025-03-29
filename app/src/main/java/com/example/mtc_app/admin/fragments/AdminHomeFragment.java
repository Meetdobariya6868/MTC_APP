package com.example.mtc_app.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private LinearLayout orderContainer;
    private FirebaseFirestore db;
    private EditText searchView;
    private List<DocumentSnapshot> allOrders = new ArrayList<>(); // Store all fetched orders

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find Views
        orderContainer = rootView.findViewById(R.id.orderContainer);
        searchView = rootView.findViewById(R.id.searchView); // Get reference to EditText

        // Fetch orders dynamically
        loadOrdersFromFirestore();

        // Add search listener
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString()); // Filter as user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void loadOrdersFromFirestore() {
        db.collection("Total Orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allOrders = task.getResult().getDocuments(); // Store all orders globally
                        displayOrders(allOrders); // Show all orders initially
                    }
                });
    }

    private void filterOrders(String query) {
        List<DocumentSnapshot> filteredOrders = new ArrayList<>();

        for (DocumentSnapshot order : allOrders) {
            String name = order.getString("Customer Name");
            String phone = order.getString("Mobile Number");

            if ((name != null && name.toLowerCase().contains(query.toLowerCase())) ||
                    (phone != null && phone.contains(query))) {
                filteredOrders.add(order);
            }
        }

        displayOrders(filteredOrders); // Refresh the UI with filtered results
    }

    private void displayOrders(List<DocumentSnapshot> orders) {
        orderContainer.removeAllViews();

        for (DocumentSnapshot order : orders) {
            View orderView = getLayoutInflater().inflate(R.layout.order_card, orderContainer, false);
            TextView orderTitle = orderView.findViewById(R.id.orderTitle);
            TextView customerName = orderView.findViewById(R.id.customerName);

            // Fetch data dynamically
            String orderId = order.getId();
            String name = order.getString("Customer Name");
            String phone = order.getString("Mobile Number");

            // Set data to UI
            orderTitle.setText("Order ID: " + orderId);
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
        intent.putExtra("name", order.getString("Customer Name"));
        intent.putExtra("address", order.getString("Dispatch Address"));
        intent.putExtra("phone", order.getString("Mobile Number"));
        startActivity(intent);
    }
}
