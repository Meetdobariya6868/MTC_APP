package com.example.mtc_app.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.example.mtc_app.customer.model.CustomerHomePageOrder;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.OrderViewHolder> {

    private List<CustomerHomePageOrder> orderList;
    private Context context;

    public CustomerOrderAdapter(Context context, List<CustomerHomePageOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CustomerHomePageOrder order = orderList.get(position);

        holder.segment.setText("Job ID : " + order.getSegment());
        holder.dispatchMode.setText("Dispatch : " + order.getDispatchMode());
        holder.orderDate.setText("Date : " + order.getDate());
        holder.price.setText("₹ " + order.getPrice());

        String status = order.getStatus() != null ? order.getStatus().trim() : "";
        holder.orderStatus.setText(status.isEmpty() ? "Unknown" : status);

        int color;
        if (status.equalsIgnoreCase("Reported")) {
            color = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
        } else if (status.equalsIgnoreCase("Open")) {
            color = ContextCompat.getColor(context, android.R.color.holo_green_dark);
        } else {
            color = ContextCompat.getColor(context, android.R.color.darker_gray);
        }

        holder.orderStatus.setTextColor(color);

        Drawable dot = ContextCompat.getDrawable(context, android.R.drawable.presence_online);
        if (dot != null) {
            dot = dot.mutate(); // required for tint to work on each item separately
            dot.setTint(color);
            holder.orderStatus.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
        } else {
            holder.orderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderStatus, dispatchMode, orderDate, segment, price;
        MaterialButton orderDetailsButton;
        CardView cardView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.orderCard);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            dispatchMode = itemView.findViewById(R.id.dispatchMode);
            orderDate = itemView.findViewById(R.id.orderDate);
            segment = itemView.findViewById(R.id.segment);
            price = itemView.findViewById(R.id.price);
            orderDetailsButton = itemView.findViewById(R.id.orderDetailsButton);
        }
    }
}
