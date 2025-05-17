package com.example.mtc_app.admin.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.mtc_app.R;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminCRFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout crContainer;
    private List<DocumentSnapshot> allCRUsers = new ArrayList<>(); // Store all fetched CR users
    private EditText searchView;

    public AdminCRFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_cr_page, container, false);
        crContainer = view.findViewById(R.id.crContainer);
        db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchView);

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

        fetchCRUsers();
        return view;
    }

    private void fetchCRUsers() {
        db.collection("users")
                .whereEqualTo("role", "cr")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allCRUsers.clear();
                        allCRUsers.addAll(task.getResult().getDocuments()); // Store all CR users
                        displayCRUsers(allCRUsers); // Display all initially
                    }
                });
    }

    private void filterCRUsers(String query) {
        List<DocumentSnapshot> filteredCRs = new ArrayList<>();

        for (DocumentSnapshot cr : allCRUsers) {
            String name = cr.getString("name");  // Fetching from Firestore
            String phone = cr.getString("phone");

            if ((name != null && name.toLowerCase().contains(query.toLowerCase())) ||
                    (phone != null && phone.contains(query))) {
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

        cardView.setOnClickListener(v -> {
            for (DocumentSnapshot cr : allCRUsers) {
                if (name.equals(cr.getString("name")) && phone.equals(cr.getString("phone"))) {
                    String crUid = cr.getId(); // assuming doc ID is UID
                    openCRHome(crUid, name);
                    break;
                }
            }
        });

        crContainer.addView(cardView);
    }

    private void openCRHome(String crUid, String crName) {
        Intent intent = new Intent(getContext(), CrMain.class);
        intent.putExtra("cr_uid", crUid);
        intent.putExtra("cr_name", crName);
        startActivity(intent);
    }

}
