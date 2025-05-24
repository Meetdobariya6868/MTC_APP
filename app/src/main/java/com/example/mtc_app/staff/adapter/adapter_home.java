package com.example.mtc_app.staff.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
    private List<ItemData> dataItems;
    private Context context;
    private List<String> documentIds; // 🔥 Maintain filteredDocumentIds

    public adapter_home(List<ItemData> dataItems, Context context, List<String> documentIds) {
        this.dataItems = dataItems;
        this.context = context;
        this.documentIds = documentIds; // 🔥 Keep document IDs list
    }

    public void updateList(List<ItemData> newList, List<String> newDocIds) {
        dataItems.clear();
        dataItems.addAll(newList);
        documentIds.clear();
        documentIds.addAll(newDocIds); // 🔥 Update doc IDs list with data
        notifyDataSetChanged();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_list, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        ItemData item = dataItems.get(position);
        String documentId = documentIds.get(position); // 🔥 Map doc ID to item

        holder.itemTitle.setText(item.getTitle());
        holder.itemSubtitle.setText(item.getSubtitle());
        if (holder.itemIcon != null) {
            holder.itemIcon.setImageResource(item.getIconResId());
        }
        holder.testSummary.setText(item.getTestSummary());
        holder.itemActionIcon.setImageResource(R.drawable.ic_chevron_right);
        holder.orderStatus.setText(item.getOrderStatus());

        String[] statuses = context.getResources().getStringArray(R.array.status_options);
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
                            FirebaseFirestore.getInstance()
                                    .collection("Total Orders")
                                    .document(documentId) // 🔥 Use mapped doc ID
                                    .update("Status", newStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
                                        holder.orderStatus.setText(newStatus);

                                        if (newStatus.equalsIgnoreCase("Reported")) {
                                            int removedPos = holder.getAdapterPosition();
                                            if (removedPos != RecyclerView.NO_POSITION) {
                                                dataItems.remove(removedPos);
                                                documentIds.remove(removedPos); // 🔥 Remove doc ID from list
                                                notifyItemRemoved(removedPos);
                                                notifyItemRangeChanged(removedPos, dataItems.size());
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", (dialog, which) -> holder.statusDropdown.setSelection(selectedPos))
                        .show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.cardView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Test Selections");
            String formattedTestSummary = item.getTestSummary()
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

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    private int getPositionForStatus(String[] statuses, String status) {
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
            itemIcon = itemView.findViewById(R.id.itemIcon);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            itemActionIcon = itemView.findViewById(R.id.itemActionIcon);
            cardView = itemView.findViewById(R.id.cardView);
            statusDropdown = itemView.findViewById(R.id.statusDropdown);
        }
    }
}
