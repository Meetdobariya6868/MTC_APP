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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private LinearLayout orderContainer;
    private FirebaseFirestore db;
    private EditText searchView;
    private List<DocumentSnapshot> allOrders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        db = FirebaseFirestore.getInstance();
        orderContainer = rootView.findViewById(R.id.orderContainer);
        searchView = rootView.findViewById(R.id.searchView);

        listenToOrderChanges(); // Start listening for real-time updates

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void listenToOrderChanges() {
        db.collection("Total Orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        if (value != null) {
                            allOrders = value.getDocuments();
                            String query = searchView.getText().toString();
                            if (!query.isEmpty()) {
                                filterOrders(query);
                            } else {
                                displayOrders(allOrders);
                            }
                        }
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

        displayOrders(filteredOrders);
    }

    private void displayOrders(List<DocumentSnapshot> orders) {
        orderContainer.removeAllViews();

        for (DocumentSnapshot order : orders) {
            View orderView = getLayoutInflater().inflate(R.layout.order_card, orderContainer, false);
            TextView orderTitle = orderView.findViewById(R.id.orderTitle);
            TextView customerName = orderView.findViewById(R.id.customerName);

            String orderId = order.getId();
            String name = order.getString("Customer Name");
            String phone = order.getString("Mobile Number");

            orderTitle.setText("Order ID: " + name);
            customerName.setText("Customer: " + phone);

            orderView.setOnClickListener(v -> openOrderDetail(order));
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
