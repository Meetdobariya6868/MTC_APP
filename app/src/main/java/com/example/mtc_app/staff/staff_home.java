package com.example.mtc_app.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mtc_app.R;
import com.example.mtc_app.staff.adapter.ItemData;
import com.example.mtc_app.staff.adapter.adapter_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class staff_home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private adapter_home adapter;
    private List<ItemData> itemList, filteredList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String staffCategory;
    private ImageView profileIcon, filterButton;
    private EditText searchBar;
    private String passedCategory = null; // for Intent override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new adapter_home(filteredList, this);
        recyclerView.setAdapter(adapter);

        profileIcon = findViewById(R.id.profileIcon);
        searchBar = findViewById(R.id.searchBar);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(staff_home.this, staff_profile_page.class);
            startActivity(intent);
        });

        // Get the optional category passed from AdminStaffFragment
        passedCategory = getIntent().getStringExtra("category");

//        loadUserCategory();

        if (passedCategory != null && !passedCategory.isEmpty()) {
            // If category passed from intent, use it directly
            Toast.makeText(this, "Category: " + passedCategory, Toast.LENGTH_SHORT).show();
            loadProducts(passedCategory);
        } else {
            // Otherwise, use Firestore to get logged-in user's category
            loadUserCategory();
        }
        setupSearchListener();
    }

    private void loadUserCategory() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        staffCategory = documentSnapshot.getString("staffCategory");
                        loadProducts(staffCategory);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user data", e));
    }

    private void loadProducts(String category) {
        List<String> productCategories = new ArrayList<>();

        // Define product categories based on staff category
        if ("aggregate".equalsIgnoreCase(category)) {
            productCategories.add("Aggregate Coarse");
            productCategories.add("Aggregate Fine");
            productCategories.add("Paver Block");
        } else if ("cement".equalsIgnoreCase(category)) {
            productCategories.add("Cement");
            productCategories.add("Hardend Concrete");
        } else if ("soil".equalsIgnoreCase(category)) {
            productCategories.add("Soil");
        }

        if (productCategories.isEmpty()) {
            Toast.makeText(this, "No products assigned to this category", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Firestore", "Fetching data for categories: " + productCategories);

        db.collection("Total Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    filteredList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> testSelections = (Map<String, Object>) doc.get("Test Selections");

                        if (testSelections != null) {
                            // Create a filtered map containing only allowed product categories
                            Map<String, Object> filteredTestSelections = new HashMap<>();

                            for (String key : testSelections.keySet()) {
                                if (productCategories.contains(key)) {
                                    filteredTestSelections.put(key, testSelections.get(key));
                                }
                            }

                            // If filteredTestSelections is empty, it means this order has no relevant tests
                            if (!filteredTestSelections.isEmpty()) {
                                String title = doc.getString("Customer Name");
                                String subtitle = doc.getString("Dispatch Address");
                                String categoryItem = doc.getString("Email");
                                String testSummary = filteredTestSelections.toString(); // Convert filtered Map to String

                                // Log fetched data
                                Log.d("Firestore", "Filtered Item: " + title + " | " + subtitle + " | " + categoryItem + " | " + testSummary);

                                // Create ItemData object
                                ItemData item = new ItemData(title, subtitle, R.drawable.ic_placeholder, categoryItem, testSummary);
                                itemList.add(item);
                            }
                        }
                    }
                    filteredList.addAll(itemList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching orders", e));
    }


    private void setupSearchListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filterSearch(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(itemList);
        } else {
            for (ItemData item : itemList) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                        item.getTestSummary().toLowerCase().contains(query.toLowerCase())) { // Added testSummary filter
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
