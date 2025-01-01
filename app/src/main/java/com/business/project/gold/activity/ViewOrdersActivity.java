package com.business.project.gold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.adapter.InvestorRevenueAdapter;
import com.business.project.gold.adapter.OrderAdapter;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;
import com.business.project.gold.service.OrderCardClickListener;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrdersActivity extends AppCompatActivity implements OrderCardClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
        setTitle("Orders");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(Collections.emptyList(), ViewOrdersActivity.this);
        recyclerView.setAdapter(orderAdapter);


        Call<List<OrderDetailsWithUserDetailsDTO>> call = RetrofitConfig.getApiService().getAllOrders();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetailsWithUserDetailsDTO>> call, @NonNull Response<List<OrderDetailsWithUserDetailsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDetailsWithUserDetailsDTO> orders = response.body();
                    if(!orders.isEmpty()) {
                        orderAdapter = new OrderAdapter(orders, ViewOrdersActivity.this);
                        recyclerView.setAdapter(orderAdapter);
                    }else {
                        showNoOrdersMessage();
                    }
                } else {
                    showNoOrdersMessage();
                    Log.e("getAllOrders", "Failed to load data");
                    Toast.makeText(ViewOrdersActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetailsWithUserDetailsDTO>> call, @NonNull Throwable t) {
                showNoOrdersMessage();
                Log.e("getAllOrders", "Failed to load data");
                Toast.makeText(ViewOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showNoOrdersMessage() {
        // No orders, show "No Orders" message and hide main content
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.noOrdersMessage).setVisibility(View.VISIBLE);
    }


    @Override
    public void onCardClick(Long orderId) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("fromViewOrdersPage", true);
        startActivity(intent);
    }

}
