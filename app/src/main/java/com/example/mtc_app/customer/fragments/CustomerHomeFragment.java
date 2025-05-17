package com.example.mtc_app.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private EditText searchInput;

    private CustomerOrderAdapter customerOrderAdapter;
    private final List<CustomerHomePageOrder> orderList = new ArrayList<>();
    private final List<CustomerHomePageOrder> filteredOrderList = new ArrayList<>();

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
        searchInput = view.findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customerOrderAdapter = new CustomerOrderAdapter(getContext(), filteredOrderList, this::onOrderClick);
        recyclerView.setAdapter(customerOrderAdapter);

        loadCachedOrders();
        setupSearchFunctionality();
        recyclerView.post(this::fetchOrdersFromFirestore);
    }

    private void loadCachedOrders() {
        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("order_cache", Context.MODE_PRIVATE);
        String cachedData = prefs.getString("orders", null);

        if (cachedData != null) {
            try {
                orderList.clear();
                filteredOrderList.clear();

                for (String row : cachedData.split(";;")) {
                    String[] parts = row.split("\\|\\|");
                    if (parts.length == 5) {
                        orderList.add(new CustomerHomePageOrder(
                                parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4])
                        ));
                    }
                }

                filteredOrderList.addAll(orderList);
                customerOrderAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchOrdersFromFirestore() {
        if (!isAdded() || auth.getCurrentUser() == null) return;

        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("Total Orders")
                .whereEqualTo("Email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<CustomerHomePageOrder> fetchedList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CustomerHomePageOrder order = new CustomerHomePageOrder(
                                    document.getId(),
                                    document.getString("Status"),
                                    document.getString("Mode of Dispatch"),
                                    document.getString("Created At"),
                                    document.getLong("Total Price") != null ? document.getLong("Total Price").intValue() : 0
                            );
                            fetchedList.add(order);
                        }

                        orderList.clear();
                        orderList.addAll(fetchedList);
                        filteredOrderList.clear();
                        filteredOrderList.addAll(orderList);
                        customerOrderAdapter.notifyDataSetChanged();

                        if (isAdded()) {
                            cacheOrders(orderList);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cacheOrders(List<CustomerHomePageOrder> orders) {
        Context context = getContext();
        if (context == null) return;

        StringBuilder builder = new StringBuilder();
        for (CustomerHomePageOrder order : orders) {
            builder.append(order.getOrderId()).append("||")
                    .append(order.getStatus() != null ? order.getStatus() : "").append("||")
                    .append(order.getDispatchMode() != null ? order.getDispatchMode() : "").append("||")
                    .append(order.getDate() != null ? order.getDate() : "").append("||")
                    .append(order.getPrice()).append(";;");
        }

        SharedPreferences prefs = context.getSharedPreferences("order_cache", Context.MODE_PRIVATE);
        prefs.edit().putString("orders", builder.toString()).apply();
    }

    private void setupSearchFunctionality() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterOrders(String query) {
        filteredOrderList.clear();
        if (query.isEmpty()) {
            filteredOrderList.addAll(orderList);
        } else {
            String lower = query.toLowerCase();
            for (CustomerHomePageOrder order : orderList) {
                if ((order.getOrderId() != null && order.getOrderId().toLowerCase().contains(lower)) ||
                        (order.getStatus() != null && order.getStatus().toLowerCase().contains(lower)) ||
                        (order.getDispatchMode() != null && order.getDispatchMode().toLowerCase().contains(lower))) {
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
