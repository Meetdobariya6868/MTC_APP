package com.example.mtc_app.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.adapters.CustomerOrderAdapter;
import com.example.mtc_app.customer.models.CustomerHomePageOrder;
import com.example.mtc_app.customer.orders.CustomerOrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView searchView;
    private CustomerOrderAdapter customerOrderAdapter;
    private List<CustomerHomePageOrder> orderList = new ArrayList<>();
    private List<CustomerHomePageOrder> filteredOrderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_customer_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize with filteredOrderList instead of orderList
        customerOrderAdapter = new CustomerOrderAdapter(getContext(), filteredOrderList, this::onOrderClick);
        recyclerView.setAdapter(customerOrderAdapter);

        fetchOrders();
        setupSearchFunctionality();
    }

    private void fetchOrders() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = auth.getCurrentUser().getEmail();
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Total Orders")
                .whereEqualTo("Email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        orderList.clear();
                        filteredOrderList.clear();

                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CustomerHomePageOrder order = new CustomerHomePageOrder(
                                        document.getId(), // Store order ID
                                        document.getString("status"),
                                        document.getString("Mode of Dispatch"),
                                        document.getString("Created At"),
                                        document.getLong("Total Price") != null ? document.getLong("Total Price").intValue() : 0
                                );
                                orderList.add(order);
                            }
                            // Add all orders to filteredOrderList initially
                            filteredOrderList.addAll(orderList);
                            customerOrderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearchFunctionality() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOrders(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return true;
            }
        });
    }

    private void filterOrders(String query) {
        filteredOrderList.clear();
        if (query.isEmpty()) {
            filteredOrderList.addAll(orderList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (CustomerHomePageOrder order : orderList) {
                // Search by order ID, status, or dispatch mode
                if (order.getOrderId().toLowerCase().contains(lowerCaseQuery) ||
                        (order.getStatus() != null && order.getStatus().toLowerCase().contains(lowerCaseQuery)) ||
                        (order.getDispatchMode() != null && order.getDispatchMode().toLowerCase().contains(lowerCaseQuery))) {
                    filteredOrderList.add(order);
                }
            }
        }
        customerOrderAdapter.notifyDataSetChanged();
    }

    private void onOrderClick(String orderId) {
        Intent intent = new Intent(getContext(), CustomerOrderDetails.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }
}