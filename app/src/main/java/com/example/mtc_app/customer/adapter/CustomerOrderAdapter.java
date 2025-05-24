package com.example.mtc_app.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminOrderDetail;
import com.example.mtc_app.customer.models.CustomerHomePageOrder;
import com.example.mtc_app.customer.orders.CustomerOrderDetails;
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

        holder.orderStatus.setText("Status: " + order.getStatus());
        holder.dispatchMode.setText("Dispatch Mode: " + order.getDispatchMode());
        holder.orderDate.setText("Date: " + order.getDate());
        holder.segment.setText("Segment: " + order.getSegment());
        holder.price.setText("Total Price: ₹" + order.getPrice());
        holder.orderDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerOrderDetails.class);
            intent.putExtra("orderId", order.getOrderId());
            context.startActivity(intent);
        });
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
