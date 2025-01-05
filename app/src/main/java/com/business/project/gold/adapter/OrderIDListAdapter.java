package com.business.project.gold.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.activity.OrderDetailActivity;
import com.business.project.gold.domain.OrderIDAndDate;

import java.util.List;

public class OrderIDListAdapter extends RecyclerView.Adapter<OrderIDListAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderIDAndDate> orders;

    public OrderIDListAdapter(Context context, List<OrderIDAndDate> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_id, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderIDAndDate order = orders.get(position);
        holder.orderIdTextView.setText(String.valueOf(order.getOrderId()));

        // Handle item click to navigate to Order Details page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            intent.putExtra("fromTimelinePage", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
        }
    }
}
