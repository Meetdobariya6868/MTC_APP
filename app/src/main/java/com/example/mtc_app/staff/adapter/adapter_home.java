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
        holder.itemActionIcon.setImageResource(R.drawable.ic_chevron_right);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, staff_detailed_page.class);
            intent.putExtra("customerName", item.getTitle());
            intent.putExtra("dispatchAddress", item.getSubtitle());
            intent.putExtra("email", item.getCategory());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }




    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemSubtitle;
        ImageView itemIcon, itemActionIcon;
        CardView cardView;

        public HomeViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemSubtitle = itemView.findViewById(R.id.itemSubtitle);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            itemActionIcon = itemView.findViewById(R.id.itemActionIcon);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}