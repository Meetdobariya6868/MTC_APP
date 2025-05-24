package com.example.mtc_app.staff.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mtc_app.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class adapter_home extends RecyclerView.Adapter<adapter_home.HomeViewHolder> {

    private final List<ItemData> dataItems;
    private final Context context;
    private final List<String> documentIds;

    public adapter_home(List<ItemData> dataItems, Context context, List<String> documentIds) {
        this.dataItems = dataItems;
        this.context = context;
        this.documentIds = documentIds;
    }

    public void updateList(List<ItemData> newList, List<String> newDocIds) {
        dataItems.clear();
        dataItems.addAll(newList);
        documentIds.clear();
        documentIds.addAll(newDocIds);
        notifyDataSetChanged();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_list, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        if (position < 0 || position >= dataItems.size()) return;

        ItemData item = dataItems.get(position);
        String documentId = (position < documentIds.size()) ? documentIds.get(position) : null;

        // Safely bind views
        if (holder.itemTitle != null) {
            String jobId = item.getTitle();
            holder.itemTitle.setText("Job ID: " + (jobId != null ? jobId : "N/A"));
        }
        if (holder.itemSubtitle != null) holder.itemSubtitle.setText(item.getSubtitle() != null ? item.getSubtitle() : "N/A");
        if (holder.testSummary != null) holder.testSummary.setText(item.getTestSummary() != null ? item.getTestSummary() : "N/A");
        if (holder.orderStatus != null) holder.orderStatus.setText(item.getOrderStatus() != null ? item.getOrderStatus() : "N/A");

        // Safe icon handling (only if the layout includes it)
        if (holder.itemActionIcon != null) {
            holder.itemActionIcon.setImageResource(R.drawable.ic_chevron_right);
        }

        // Spinner setup with fallback handling
        String[] statuses = context.getResources().getStringArray(R.array.status_options);
        if (holder.statusDropdown != null) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statuses);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.statusDropdown.setAdapter(spinnerAdapter);

            int selectedPos = getPositionForStatus(statuses, item.getOrderStatus());
            holder.statusDropdown.setSelection(selectedPos);

            holder.statusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean isFirst = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (isFirst) {
                        isFirst = false;
                        return;
                    }

                    String newStatus = parent.getItemAtPosition(pos).toString();
                    new AlertDialog.Builder(context)
                            .setTitle("Change Status")
                            .setMessage("Are you sure you want to update status to \"" + newStatus + "\"?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (documentId != null) {
                                    FirebaseFirestore.getInstance()
                                            .collection("Total Orders")
                                            .document(documentId)
                                            .update("Status", newStatus)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
                                                if (holder.orderStatus != null) holder.orderStatus.setText(newStatus);

                                                if (newStatus.equalsIgnoreCase("Reported")) {
                                                    int removedPos = holder.getAdapterPosition();
                                                    if (removedPos != RecyclerView.NO_POSITION) {
                                                        dataItems.remove(removedPos);
                                                        documentIds.remove(removedPos);
                                                        notifyItemRemoved(removedPos);

                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show());
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> holder.statusDropdown.setSelection(selectedPos))
                            .show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        // Card click handling
        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Test Selections");
                String formattedTestSummary = (item.getTestSummary() != null ? item.getTestSummary() : "N/A")
                        .replace(",", ",\n")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("[", "")
                        .replace("]", "");
                builder.setMessage(formattedTestSummary);
                builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataItems != null ? dataItems.size() : 0;
    }

    private int getPositionForStatus(String[] statuses, String status) {
        if (status == null) return 0;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(status)) return i;
        }
        return 0;
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemSubtitle, testSummary, orderStatus;
        ImageView itemIcon, itemActionIcon;
        CardView cardView;
        Spinner statusDropdown;

        public HomeViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemSubtitle = itemView.findViewById(R.id.itemSubtitle);
            testSummary = itemView.findViewById(R.id.itemSample);
            itemIcon = itemView.findViewById(R.id.itemIcon); // Safe even if unused
            itemActionIcon = itemView.findViewById(R.id.itemActionIcon);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            cardView = itemView.findViewById(R.id.cardView);
            statusDropdown = itemView.findViewById(R.id.statusDropdown);
        }
    }
}
