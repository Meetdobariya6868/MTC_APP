package com.example.mtc_app.staff.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mtc_app.R;
import com.example.mtc_app.staff.staff_detailed_page;

import java.util.List;

public class adapter_home extends RecyclerView.Adapter<adapter_home.HomeViewHolder> {
    private List<ItemData> dataItems;
    private Context context;

    public adapter_home(List<ItemData> dataItems, Context context) {
        this.dataItems = dataItems;
        this.context = context;
    }

    public void updateList(List<ItemData> newList) {
        dataItems.clear();
        dataItems.addAll(newList);
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
        holder.itemTitle.setText(item.getTitle());
        holder.itemSubtitle.setText(item.getSubtitle());
        holder.itemIcon.setImageResource(item.getIconResId());
        holder.testSummary.setText(item.getTestSummary()); // Ensure testSummary is displayed
        holder.itemActionIcon.setImageResource(R.drawable.ic_chevron_right);
        holder.orderStatus.setText(item.getOrderStatus());

//        holder.cardView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, staff_detailed_page.class);
//            intent.putExtra("customerName", item.getTitle());
//            intent.putExtra("dispatchAddress", item.getSubtitle());
//            intent.putExtra("email", item.getCategory());
//            intent.putExtra("testSummary", item.getTestSummary()); // Pass testSummary to details page
//            context.startActivity(intent);
//        });

        // Show popup with full test selection details on click
        holder.cardView.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            builder.setTitle("Test Selections");

            // Format testSummary with line breaks if needed
            String formattedTestSummary = item.getTestSummary().replace(",", ",\n").replace("{", "").replace("}", "").replace("[", "").replace("]", "");

            builder.setMessage(formattedTestSummary);

            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemSubtitle, testSummary, orderStatus; // Added testSummary
        ImageView itemIcon, itemActionIcon;
        CardView cardView;

        public HomeViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemSubtitle = itemView.findViewById(R.id.itemSubtitle);
            testSummary = itemView.findViewById(R.id.itemSample); // Ensure this exists in XML
            itemIcon = itemView.findViewById(R.id.itemIcon);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            itemActionIcon = itemView.findViewById(R.id.itemActionIcon);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
