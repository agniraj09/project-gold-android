package com.business.project.gold.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.adapter.InvestorRevenueAdapter;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.CumulativeRevenueDetails;
import com.business.project.gold.domain.InvestorRevenueDetails;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RevenueDetailsActivity extends AppCompatActivity {

    private TextView totalOrdersTextView;
    private TextView totalIncomeTextView;
    private TextView openOrdersTextView;
    private TextView settledOrdersTextView;
    private TextView cancelledOrdersTextView;
    private TextView avgManagerShareTextView;
    private TextView avgReferrerShareTextView;
    private TextView avgParticipantShareTextView;

    private RecyclerView recyclerView;
    private InvestorRevenueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_details);
        setTitle("Revenue Details");

        // Initialize TextViews
        totalOrdersTextView = findViewById(R.id.totalOrders);
        totalIncomeTextView = findViewById(R.id.totalIncome);
        openOrdersTextView = findViewById(R.id.openOrders);
        settledOrdersTextView = findViewById(R.id.settledOrders);
        cancelledOrdersTextView = findViewById(R.id.cancelledOrders);
        avgManagerShareTextView = findViewById(R.id.avgManagerShare);
        avgReferrerShareTextView = findViewById(R.id.avgReferrerShare);
        avgParticipantShareTextView = findViewById(R.id.avgParticipantShare);

        recyclerView = findViewById(R.id.participant_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvestorRevenueAdapter(Collections.emptyList());
        recyclerView.setAdapter(adapter);

        // Fetch the revenue details and update the UI
        fetchCumulativeRevenueDetails();
    }

    private void fetchCumulativeRevenueDetails() {
        // Replace with your actual date range values

        RetrofitConfig.getApiService().getCumulativeRevenueDetails().enqueue(new Callback<CumulativeRevenueDetails>() {
            @Override
            public void onResponse(Call<CumulativeRevenueDetails> call, Response<CumulativeRevenueDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    findViewById(R.id.main_content).setVisibility(View.VISIBLE);
                    CumulativeRevenueDetails cumulativeRevenueDetails = response.body();
                    if (cumulativeRevenueDetails.totalOrders() > 0) {
                        // Update UI with the cumulative revenue details
                        totalOrdersTextView.setText(String.valueOf(cumulativeRevenueDetails.totalOrders()));
                        totalIncomeTextView.setText(String.format("₹ %.2f", cumulativeRevenueDetails.totalIncome()));
                        openOrdersTextView.setText(String.valueOf(cumulativeRevenueDetails.openOrders()));
                        settledOrdersTextView.setText(String.valueOf(cumulativeRevenueDetails.settledOrders()));
                        cancelledOrdersTextView.setText(String.valueOf(cumulativeRevenueDetails.cancelledOrders()));
                        avgManagerShareTextView.setText(String.format("₹ %.2f", cumulativeRevenueDetails.averageManagerShare()));
                        avgReferrerShareTextView.setText(String.format("₹ %.2f", cumulativeRevenueDetails.averageReferrerShare()));
                        avgParticipantShareTextView.setText(String.format("₹ %.2f", cumulativeRevenueDetails.averageParticipantShare()));

                        // Set up the adapter for the RecyclerView with the investor data
                        List<InvestorRevenueDetails> investorList = cumulativeRevenueDetails.investorRevenueDetails();
                        adapter = new InvestorRevenueAdapter(investorList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // No orders, show "No Orders" message and hide main content
                        findViewById(R.id.main_content).setVisibility(View.GONE);
                        findViewById(R.id.noOrdersMessage).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CumulativeRevenueDetails> call, Throwable t) {
                findViewById(R.id.main_content).setVisibility(View.GONE);
                findViewById(R.id.noOrdersMessage).setVisibility(View.VISIBLE);
                System.out.println(t.getMessage());
                Toast.makeText(RevenueDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
