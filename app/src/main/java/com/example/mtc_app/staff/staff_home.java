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
    private List<String> itemDocumentIds, filteredDocumentIds; // 🔥 Both lists for consistency
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String staffCategory;
    private ImageView profileIcon;
    private EditText searchBar;
    private String passedCategory = null;

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
        itemDocumentIds = new ArrayList<>();
        filteredDocumentIds = new ArrayList<>();

        adapter = new adapter_home(filteredList, this, filteredDocumentIds); // 🔥 Updated adapter call
        recyclerView.setAdapter(adapter);

        profileIcon = findViewById(R.id.profileIcon);
        searchBar = findViewById(R.id.searchBar);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(staff_home.this, staff_profile_page.class);
            startActivity(intent);
        });

        passedCategory = getIntent().getStringExtra("category");

        if (passedCategory != null && !passedCategory.isEmpty()) {
            Toast.makeText(this, "Category: " + passedCategory, Toast.LENGTH_SHORT).show();
            loadProducts(passedCategory);
        } else {
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

        if ("aggregate".equalsIgnoreCase(category)) {
            productCategories.add("Aggregate Coarse");
            productCategories.add("Aggregate Fine");
        } else if ("cement".equalsIgnoreCase(category)) {
            productCategories.add("Cement");
            productCategories.add("FlyAsh");
        } else if ("cccube".equalsIgnoreCase(category)) {
            productCategories.add("CementCube");
        } else if ("soil".equalsIgnoreCase(category)) {
            productCategories.add("Soil");
        } else if ("steel".equalsIgnoreCase(category)) {
            productCategories.add("Steel");
        } else if("brick".equalsIgnoreCase(category)) {
            productCategories.add("Brick");
            productCategories.add("AacBlock");
            productCategories.add("PowerBlock");
        } else if ("water".equalsIgnoreCase(category)) {
            productCategories.add("ConstWater");
            productCategories.add("WasteWater");
        } else if ("ndt".equalsIgnoreCase(category)) {
            productCategories.add(("NDT"));
        } else if ("mixdesign".equalsIgnoreCase(category)) {
            productCategories.add("MixDesign");
        } else if ("bitumen".equalsIgnoreCase(category)) {
            productCategories.add("Bitumen");
        }

        if (productCategories.isEmpty()) {
            Toast.makeText(this, "No products assigned to this category", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Total Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    filteredList.clear();
                    itemDocumentIds.clear();
                    filteredDocumentIds.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> testSelections = (Map<String, Object>) doc.get("Test Selections");
                        Map<String, Object> categoryQuantities = (Map<String, Object>) doc.get("Category Quantities");
                        String status = doc.getString("Status");

                        if (testSelections != null && !"Reported".equalsIgnoreCase(status)) {
                            Map<String, Object> filteredTestSelections = new HashMap<>();
                            StringBuilder quantityInfo = new StringBuilder();

                            for (String key : testSelections.keySet()) {
                                if (productCategories.contains(key)) {
                                    filteredTestSelections.put(key, testSelections.get(key));

                                    // Get quantity for this specific category
                                    if (categoryQuantities != null && categoryQuantities.containsKey(key)) {
                                        String quantity = (String) categoryQuantities.get(key);
                                        if (quantity != null && !quantity.isEmpty()) {
                                            if (quantityInfo.length() > 0) {
                                                quantityInfo.append(", ");
                                            }
                                            quantityInfo.append(key).append(": ").append(quantity);
                                        }
                                    }
                                }
                            }

                            if (!filteredTestSelections.isEmpty()) {
                                String title = doc.getString("LabNumber");
                                String subtitle = doc.getString("Created At");
                                String categoryItem = doc.getString("Email");
                                String dueDate = doc.getString("Due Date");
                                String testSummary = filteredTestSelections.toString();
                                String documentId = doc.getId();

                                if (quantityInfo.length() > 0) {
                                    testSummary = "Tests: " + testSummary + "\nQuantities: " + quantityInfo.toString();
                                }

                                ItemData item = new ItemData(title, subtitle, R.drawable.ic_placeholder, categoryItem, testSummary, status, documentId, dueDate);
                                itemList.add(item);
                                itemDocumentIds.add(documentId);
                            }
                        }
                    }

                    filteredList.addAll(itemList);
                    filteredDocumentIds.addAll(itemDocumentIds);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching orders", e));
    }

    private void setupSearchListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterSearch(String query) {
        filteredList.clear();
        filteredDocumentIds.clear();

        if (query.isEmpty()) {
            filteredList.addAll(itemList);
            filteredDocumentIds.addAll(itemDocumentIds);
        } else {
            for (int i = 0; i < itemList.size(); i++) {
                ItemData item = itemList.get(i);
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                        item.getTestSummary().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                    filteredDocumentIds.add(itemDocumentIds.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
