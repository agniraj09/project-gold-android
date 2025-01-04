package com.business.project.gold.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.domain.InvestorRevenueDetails;

import java.util.List;

public class InvestorRevenueAdapter extends RecyclerView.Adapter<InvestorRevenueAdapter.ViewHolder> {

    private final List<InvestorRevenueDetails> investorList;

    // Constructor to pass data to the adapter
    public InvestorRevenueAdapter(List<InvestorRevenueDetails> investorList) {
        this.investorList = investorList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the row layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind the data to the ViewHolder
        InvestorRevenueDetails investor = investorList.get(position);
        
        // Set the data to the views
        holder.name.setText(investor.firstName() + " " + investor.lastName().substring(0, 1));
        holder.totalOrders.setText(String.valueOf(investor.totalOrders()));
        holder.totalIncome.setText(String.format("₹ %.2f", investor.totalIncome()));
    }

    @Override
    public int getItemCount() {
        return investorList.size();
    }

    // ViewHolder class that holds the views for each row
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, totalOrders, totalIncome;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            totalOrders = itemView.findViewById(R.id.totalOrders);
            totalIncome = itemView.findViewById(R.id.totalIncome);
        }
    }
}
