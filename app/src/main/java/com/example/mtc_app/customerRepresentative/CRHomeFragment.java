package com.example.mtc_app.customerRepresentative;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.orders.CustomerOrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CRHomeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
//    private CustomerAdapter adapter;
//    private List<Customer> customerList, filteredList;

    private com.example.mtc_app.customer.adapters.CustomerOrderAdapter customerOrderAdapter;
    private List<com.example.mtc_app.customer.models.CustomerHomePageOrder> orderList = new ArrayList<>();
    private EditText searchInput;

    public CRHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cr_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);

        searchInput = view.findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        customerOrderAdapter = new com.example.mtc_app.customer.adapters.CustomerOrderAdapter(getContext(), orderList, this::onOrderClick);
        recyclerView.setAdapter(customerOrderAdapter);

        fetchOrders();

//        customerList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//        adapter = new CustomerAdapter(filteredList, this::openCustomerDetailsFragment);
//        recyclerView.setAdapter(adapter);
//
//        loadCustomerData();
//        setupSearchFunctionality();

        return view;
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
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                com.example.mtc_app.customer.models.CustomerHomePageOrder order = new com.example.mtc_app.customer.models.CustomerHomePageOrder(
                                        document.getId(), // Store order ID
                                        document.getString("status"),
                                        document.getString("Mode of Dispatch"),
                                        document.getString("Created At"),
                                        document.getLong("Total Price") != null ? document.getLong("Total Price").intValue() : 0
                                );
                                orderList.add(order);
                            }
                            customerOrderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void loadCustomerData() {
//        CollectionReference usersRef = db.collection("users");
//
//        usersRef.whereEqualTo("role", "customer")
//                .orderBy("created_at", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    orderList.clear();
////                    filteredList.clear();
//
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//                        com.example.mtc_app.customer.models.CustomerHomePageOrder customer = document.toObject(com.example.mtc_app.customer.models.CustomerHomePageOrder.class);
////                        if (customer != null) {
////                            customer.setId(document.getId()); // Set Firestore document ID manually
////                            customerList.add(customer);
////                        }
//                    }
//
//                    filteredList.addAll(customerList);
//                    adapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching customer data", e));
//    }


//    private void setupSearchFunctionality() {
//        searchInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterCustomers(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//    }

//    private void filterCustomers(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            filteredList.addAll(customerList);
//        } else {
//            for (Customer customer : customerList) {
//                if (customer.getName().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(customer);
//                }
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }

//    private void openCustomerDetailsFragment(Customer customer) {
//        CustomerDetails customerDetailsFragment = new CustomerDetails();
//        Bundle bundle = new Bundle();
//        bundle.putString("customer_phone", customer.getPhone());  // Pass customer phone instead of ID
//        customerDetailsFragment.setArguments(bundle);
//
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, customerDetailsFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    private void onOrderClick(String orderId) {
        Intent intent = new Intent(getContext(), CustomerOrderDetails.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

}
