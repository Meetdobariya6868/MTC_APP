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
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private List<DocumentSnapshot> allOrders = new ArrayList<>();
    private boolean isFragmentVisible = false;  // 🔥 Track fragment visibility

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        db = FirebaseFirestore.getInstance();
        orderContainer = rootView.findViewById(R.id.orderContainer);
        searchView = rootView.findViewById(R.id.searchView);
        progressBar = rootView.findViewById(R.id.progressBar);

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

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;  // 🔥 Mark fragment as visible
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;  // 🔥 Mark fragment as not visible
    }

    private void listenToOrderChanges() {
        progressBar.setVisibility(View.VISIBLE); // Show loading indicator

        db.collection("Total Orders")
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        // Log or handle error
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
                });
    }

    private void filterOrders(String query) {
        List<DocumentSnapshot> filteredOrders = new ArrayList<>();

        for (DocumentSnapshot order : allOrders) {
            String customerName = order.getString("Customer Name");
            String phone = order.getString("Mobile Number");
            String jobId = order.getString("LabJobNumber");

            if ((customerName != null && customerName.toLowerCase().contains(query.toLowerCase())) ||
                    (phone != null && phone.contains(query)) ||
                    (jobId != null && jobId.toLowerCase().contains(query.toLowerCase()))) {
                // 🔥 Exclude "Reported" orders from filtered results
                String status = order.getString("Status");
                if (status == null || !status.equalsIgnoreCase("Reported")) {
                    filteredOrders.add(order);
                }
            }
        }

        displayOrders(filteredOrders);
    }

    private void displayOrders(List<DocumentSnapshot> orders) {
        if (!isFragmentVisible || getContext() == null || getView() == null) {
            // 🔥 Fragment not attached or visible, skip UI updates
            return;
        }

        orderContainer.removeAllViews();

        for (DocumentSnapshot order : orders) {
            String status = order.getString("Status");
            if (status != null && status.equalsIgnoreCase("Reported")) {
                continue;  // 🔥 Skip displaying Reported orders
            }

            View orderView = LayoutInflater.from(getContext()).inflate(R.layout.order_card, orderContainer, false);

            TextView jobIdTextView = orderView.findViewById(R.id.jobId);
            TextView customerNameTextView = orderView.findViewById(R.id.customerName);

            String jobId = order.getString("LabJobNumber");
            String customerName = order.getString("Customer Name");

            jobIdTextView.setText("Job ID: " + (jobId != null ? jobId : "N/A"));
            customerNameTextView.setText("Customer: " + (customerName != null ? customerName : "N/A"));

            orderView.setOnClickListener(v -> openOrderDetail(order));
            orderContainer.addView(orderView);
        }
    }

    private void openOrderDetail(DocumentSnapshot order) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), AdminOrderDetail.class);
        intent.putExtra("orderId", order.getId());
        intent.putExtra("name", order.getString("Customer Name"));
        intent.putExtra("address", order.getString("Dispatch Address"));
        intent.putExtra("phone", order.getString("Mobile Number"));
        intent.putExtra("jobId", order.getString("LabJobNumber"));
        startActivity(intent);
    }
}
