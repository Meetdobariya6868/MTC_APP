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
    private List<DocumentSnapshot> allStaffUsers = new ArrayList<>(); // Store all fetched CR users
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

        soil = view.findViewById(R.id.Customer1);
        cement = view.findViewById(R.id.Customer2);
        steel = view.findViewById(R.id.Customer3);

        soil.setOnClickListener(v -> openStaffModule("soil"));
        cement.setOnClickListener(v -> openStaffModule("cement"));
        steel.setOnClickListener(v -> openStaffModule("steel"));


        // Add search listener
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCRUsers(s.toString()); // Filter as user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        fetchCRUsers();
        return view;
    }

    private void openStaffModule(String category) {
        Intent intent = new Intent(requireContext(), staff_home.class); // Replace StaffActivity with your actual activity
        intent.putExtra("category", category);
        startActivity(intent);
    }


    private void filterCRUsers(String query) {
        List<DocumentSnapshot> filteredCRs = new ArrayList<>();

        for (DocumentSnapshot cr : allStaffUsers) {
            String name = cr.getString("name");  // Fetching from Firestore

            if ((name != null && name.toLowerCase().contains(query.toLowerCase()))) {
                filteredCRs.add(cr);
            }
        }

        displayCRUsers(filteredCRs); // Refresh UI with filtered results
    }

    private void displayCRUsers(List<DocumentSnapshot> crUsers) {
        crContainer.removeAllViews();

        for (DocumentSnapshot cr : crUsers) {
            String name = cr.getString("name");
            String phone = cr.getString("phone");
            addCRCard(name, phone);
        }
    }

    private void addCRCard(String name, String phone) {
        View cardView = getLayoutInflater().inflate(R.layout.order_card, crContainer, false);

        TextView nameTextView = cardView.findViewById(R.id.orderTitle);
        TextView phoneTextView = cardView.findViewById(R.id.customerName);

        nameTextView.setText(name);
        phoneTextView.setText(phone);

        crContainer.addView(cardView);
    }
}