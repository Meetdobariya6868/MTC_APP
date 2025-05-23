package com.example.mtc_app.customer.fragments;

import android.content.Context;
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
import com.example.mtc_app.customer.adapter.CustomerOrderAdapter;
import com.example.mtc_app.customer.model.CustomerHomePageOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerHomeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private CustomerOrderAdapter customerOrderAdapter;
    private View progressBar;
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
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customerOrderAdapter = new CustomerOrderAdapter(getContext(), filteredOrderList); // removed listener
        recyclerView.setAdapter(customerOrderAdapter);

        recyclerView.setVisibility(View.GONE); // Hide initially
        progressBar.setVisibility(View.VISIBLE); // Show progress initially


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
                                parts[0], parts[1], parts[2], parts[3],parts[4], Integer.parseInt(parts[5])
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

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);  // Hide data while loading

        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("Total Orders")
                .whereEqualTo("Email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<CustomerHomePageOrder> fetchedList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String orderId = document.getId();
                            String segment = document.getString("LabJobNumber");
                            String status = document.getString("Status");
                            String createdAt = document.getString("Created At");
                            int totalPrice = document.getLong("Total Price") != null
                                    ? document.getLong("Total Price").intValue()
                                    : 0;

                            String dispatchMode = "";
                            Map<String, Object> radioSelections = (Map<String, Object>) document.get("Radio Selections");
                            if (radioSelections != null && radioSelections.containsKey("Mode of Dispatch")) {
                                dispatchMode = String.valueOf(radioSelections.get("Mode of Dispatch"));
                            }

<<<<<<< HEAD
                            CustomerHomePageOrder order = new CustomerHomePageOrder(
                                    orderId,
                                    status,
                                    segment,
                                    dispatchMode,
                                    createdAt,
                                    totalPrice
                            );

                            fetchedList.add(order);
=======
                            fetchedList.add(new CustomerHomePageOrder(orderId, status, dispatchMode, createdAt, totalPrice));
>>>>>>> 8584525ed397b64e440fecd43f7c236c482a6c15
                        }

                        orderList.clear();
                        orderList.addAll(fetchedList);
                        filteredOrderList.clear();
                        filteredOrderList.addAll(orderList);
                        customerOrderAdapter.notifyDataSetChanged();

                        cacheOrders(orderList);

                        recyclerView.setVisibility(View.VISIBLE);  // ✅ Show data only after it's ready
                        progressBar.setVisibility(View.GONE);      // ✅ Hide loader
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
