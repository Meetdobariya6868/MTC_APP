package com.example.mtc_app.customerRepresentative;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.example.mtc_app.customer.orders.CustomerOrderDetails;
import com.example.mtc_app.customerRepresentative.CustomerOrder;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.OrderViewHolder> {

    private final List<CustomerOrder> orderList;
    private final Context context;
    // Add a field for storing document IDs
    private List<String> orderIds;

    public CustomerOrderAdapter(Context context, List<CustomerOrder> orders, List<String> orderIds) {
        this.context = context;
        this.orderList = orders;
        this.orderIds = orderIds;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cr_order_card, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CustomerOrder order = orderList.get(position);

        holder.orderStatus.setText(order.getStatus());
        holder.price.setText("₹ " + order.getPrice());
        holder.segment.setText("Job ID : " + order.getSegment());
        holder.dispatchMode.setText("Dispatch : " + order.getDispatchMode());
        holder.orderDate.setText("Date : " + order.getOrderDate());

        // View Order Details
        holder.orderDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetail.class);
            intent.putExtra("orderId", orderIds.get(position));
            context.startActivity(intent);
        });

        // Setup Spinner
        String[] statuses = context.getResources().getStringArray(R.array.status_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusDropdown.setAdapter(adapter);

        // Set current status in dropdown
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(order.getStatus())) {
                holder.statusDropdown.setSelection(i);
                break;
            }
        }

        // Handle dropdown status change with confirmation
        setupStatusDropdown(holder.statusDropdown, holder.orderStatus, orderIds.get(position), order.getStatus(), position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderStatus, price, segment, dispatchMode, orderDate;
        Spinner statusDropdown;
        MaterialButton orderDetailsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            price = itemView.findViewById(R.id.price);
            segment = itemView.findViewById(R.id.segment);
            dispatchMode = itemView.findViewById(R.id.dispatchMode);
            orderDate = itemView.findViewById(R.id.orderDate);
            statusDropdown = itemView.findViewById(R.id.statusDropdown);
            orderDetailsButton = itemView.findViewById(R.id.orderDetailsButton);
        }
    }

    private void setupStatusDropdown(Spinner spinner, TextView statusText, String orderId, String currentStatus, int position) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                String newStatus = parent.getItemAtPosition(pos).toString();

                new AlertDialog.Builder(context)
                        .setTitle("Change Status")
                        .setMessage("Are you sure you want to update status to \"" + newStatus + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            statusText.setText(newStatus);

                            int color = newStatus.equalsIgnoreCase("Open")
                                    ? context.getResources().getColor(android.R.color.holo_green_dark)
                                    : context.getResources().getColor(android.R.color.holo_orange_dark);

                            statusText.setTextColor(color);

                            Drawable dot = context.getResources()
                                    .getDrawable(android.R.drawable.presence_online)
                                    .mutate();
                            dot.setTint(color);
                            statusText.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);

                            // Firestore status update
                            FirebaseFirestore.getInstance()
                                    .collection("Total Orders")
                                    .document(orderId)
                                    .update("Status", newStatus)
                                    .addOnSuccessListener(unused -> {
                                        if (newStatus.equalsIgnoreCase("Reported")) {
                                            // Remove from current list and notify
                                            orderList.remove(position);
                                            orderIds.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, orderList.size());
                                        }
                                    });

                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Revert spinner if cancelled without triggering selection again
                            String[] statuses = context.getResources().getStringArray(R.array.status_options);
                            for (int i = 0; i < statuses.length; i++) {
                                if (statuses[i].equalsIgnoreCase(currentStatus)) {
                                    spinner.setOnItemSelectedListener(null);
                                    spinner.setSelection(i, false); // don't trigger listener
                                    setupStatusDropdown(spinner, statusText, orderId, currentStatus, position);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        })
                        .show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



}
