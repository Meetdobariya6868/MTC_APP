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
import java.util.List;

public class staff_home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private adapter_home adapter;
    private List<ItemData> itemList, filteredList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String staffCategory;
    private ImageView profileIcon, filterButton;
    private EditText searchBar;

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
        filterButton = findViewById(R.id.filterButton);
        searchBar = findViewById(R.id.searchBar);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(staff_home.this, staff_profile_page.class);
            startActivity(intent);
        });

        loadUserCategory();
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
        db.collection("Total Orders")
                .whereEqualTo("staffCategory", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    filteredList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String title = doc.getString("customer Name");
                        String subtitle = doc.getString("dispatch Address");
                        String categoryItem = doc.getString("email");
                        ItemData item = new ItemData(title, subtitle, R.drawable.ic_placeholder, categoryItem);
                        itemList.add(item);
                    }
                    filteredList.addAll(itemList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching products", e));
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
                        item.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
