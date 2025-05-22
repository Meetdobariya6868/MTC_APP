package com.example.mtc_app.admin.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mtc_app.R;
import com.example.mtc_app.staff.staff_home;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminStaffFragment extends Fragment {

    private FirebaseFirestore db;
    CardView soil, cement, steel;
    private LinearLayout crContainer;
    private List<DocumentSnapshot> allStaffUsers = new ArrayList<>();
    private EditText searchView;

    public AdminStaffFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_staff, container, false);
        crContainer = view.findViewById(R.id.staffContainer);
        db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchView);

        soil = view.findViewById(R.id.soil);
        cement = view.findViewById(R.id.cement);
        steel = view.findViewById(R.id.steel);

        List<CardView> categoryCards = new ArrayList<>();
        if (soil != null) {
            soil.setTag("soil");
            categoryCards.add(soil);
        }
        if (cement != null) {
            cement.setTag("cement");
            categoryCards.add(cement);
        }
        if (steel != null) {
            steel.setTag("steel");
            categoryCards.add(steel);
        }

        soil.setOnClickListener(v -> openStaffModule("soil"));
        cement.setOnClickListener(v -> openStaffModule("cement"));
        steel.setOnClickListener(v -> openStaffModule("steel"));

        // Fetch staff users from Firestore
        fetchStaffUsers();

        // Add search listener
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();

                // Filter staff users
                filterStaffUsers(query);

                // Filter category cards
                for (CardView card : categoryCards) {
                    String tag = (String) card.getTag();
                    if (tag != null && tag.toLowerCase().contains(query)) {
                        card.setVisibility(View.VISIBLE);
                    } else {
                        card.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchStaffUsers() {
        db.collection("users")
                .whereEqualTo("role", "staff") // Assuming staff role exists
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allStaffUsers.clear();
                        allStaffUsers.addAll(task.getResult().getDocuments());
                        displayStaffUsers(allStaffUsers);
                    }
                });
    }

    private void openStaffModule(String category) {
        Intent intent = new Intent(requireContext(), staff_home.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void filterStaffUsers(String query) {
        List<DocumentSnapshot> filteredList = new ArrayList<>();
        for (DocumentSnapshot staff : allStaffUsers) {
            String name = staff.getString("name");
            String phone = staff.getString("phone");
            if ((name != null && name.toLowerCase().contains(query.toLowerCase())) ||
                    (phone != null && phone.contains(query))) {
                filteredList.add(staff);
            }
        }
        displayStaffUsers(filteredList);
    }

    private void displayStaffUsers(List<DocumentSnapshot> staffList) {
        crContainer.removeAllViews();
        for (DocumentSnapshot staff : staffList) {
            String name = staff.getString("name");
            String phone = staff.getString("phone");
            addStaffCard(name, phone);
        }
    }

    private void addStaffCard(String name, String phone) {
        View cardView = getLayoutInflater().inflate(R.layout.order_card, crContainer, false);
        TextView nameTextView = cardView.findViewById(R.id.jobId);
        TextView phoneTextView = cardView.findViewById(R.id.customerName);

        // Display Staff Name and Phone correctly
        nameTextView.setText("Staff Name: " + (name != null ? name : "N/A"));
        phoneTextView.setText("Phone: " + (phone != null ? phone : "N/A"));

        crContainer.addView(cardView);
    }
}