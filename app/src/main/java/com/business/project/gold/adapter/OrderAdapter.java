package com.business.project.gold.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;
import com.business.project.gold.service.OrderCardClickListener;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderDetailsDTOViewHolder> {
    private List<OrderDetailsWithUserDetailsDTO> orders;
    private final OrderCardClickListener cardClickListener;

    public OrderAdapter(List<OrderDetailsWithUserDetailsDTO> orders, OrderCardClickListener cardClickListener) {
        this.orders = orders;
        this.cardClickListener = cardClickListener;
    }

    @NonNull
    @Override
    public OrderDetailsDTOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent, false);
        return new OrderDetailsDTOViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsDTOViewHolder holder, int position) {
        OrderDetailsWithUserDetailsDTO order = orders.get(position);

        holder.orderId.setText(String.valueOf(order.id()));
        holder.orderDate.setText(order.orderDate());
        holder.functionDate.setText(order.functionDate());
        holder.manager.setText(order.manager().firstName());
        holder.orderType.setText(order.type());
        holder.advanceAmount.setText(String.valueOf(order.advanceAmount()));
        holder.totalAmount.setText(String.valueOf(order.totalAmount()));
        holder.statusLabel.setText(order.status());
        holder.statusLabel.setBackgroundColor(getColor(order.status(), holder));
        holder.itemView.setOnClickListener(v -> cardClickListener.onCardClick(order.id()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private int getColor(String status, RecyclerView.ViewHolder holder) {
        Context context = holder.itemView.getContext();
        if ("NEW".equalsIgnoreCase(status)) {
            return ContextCompat.getColor(context, R.color.blue);
        } else if ("SETTLED".equalsIgnoreCase(status)) {
            return ContextCompat.getColor(context, R.color.green);
        } else {
            return ContextCompat.getColor(context, R.color.red);
        }

    }

    public static class OrderDetailsDTOViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, functionDate, statusLabel, manager,  orderType, advanceAmount, totalAmount;
        Button editOrder, cancelOrder, settleOrder;

        public OrderDetailsDTOViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.orderDate);
            functionDate = itemView.findViewById(R.id.functionDate);
            manager = itemView.findViewById(R.id.manager);
            orderType = itemView.findViewById(R.id.orderType);
            advanceAmount = itemView.findViewById(R.id.advanceAmount);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            editOrder = itemView.findViewById(R.id.edit_order);
            cancelOrder = itemView.findViewById(R.id.cancel_order);
            settleOrder = itemView.findViewById(R.id.settle_order);
            statusLabel = itemView.findViewById(R.id.status_label);
        }
    }
}
