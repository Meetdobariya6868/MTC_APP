package com.example.mtc_app.customerRepresentative;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.example.mtc_app.customer.orders.CustomerOrderDetails;
import com.example.mtc_app.customerRepresentative.CustomerOrder;
import com.google.android.material.button.MaterialButton;

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
        holder.segment.setText("Segment: " + order.getSegment());
        holder.dispatchMode.setText("Dispatch: " + order.getDispatchMode());
        holder.orderDate.setText("Date: " + order.getOrderDate());
        holder.orderDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetail.class);  // Corrected class name
            intent.putExtra("orderId", orderIds.get(position));           // Passing document ID
            context.startActivity(intent);
        });

        // Optionally set the spinner value to match order.getStatus()
        // Spinner dropdown may need adapter setup if not using static XML entries
        String[] statuses = context.getResources().getStringArray(R.array.status_options);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(order.getStatus())) {
                holder.statusDropdown.setSelection(i);
                break;
            }
        }

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
}
