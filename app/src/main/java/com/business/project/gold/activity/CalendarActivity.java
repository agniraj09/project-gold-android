package com.business.project.gold.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.adapter.OrderAdapter;
import com.business.project.gold.adapter.OrderIDListAdapter;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.decoration.EventDecorator;
import com.business.project.gold.domain.OrderIDAndDate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView ordersRecyclerView;

    private TextView ordersTitleLabel;
    private OrderIDListAdapter orderAdapter;
    private List<OrderIDAndDate> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Toolbar toolbar = findViewById(R.id.toolbar_calendarview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendarView = findViewById(R.id.calendarView);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersTitleLabel = findViewById(R.id.orders_title_label);

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchOrdersWithDates();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                displayOrdersForSelectedDate(date);
            }
        });
    }

    private void fetchOrdersWithDates() {
        Call<List<OrderIDAndDate>> call = RetrofitConfig.getApiService().getAllOrdersWithOrderIDAndDate();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderIDAndDate>> call, @NonNull Response<List<OrderIDAndDate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders = response.body(); // Save orders for later use

                    List<CalendarDay> datesWithDots = orders.stream()
                            .map(order -> localDateToCalendarDay(order.getFunctionDate()))
                            .distinct()
                            .collect(Collectors.toList());

                    EventDecorator eventDecorator = new EventDecorator(Color.RED, datesWithDots);
                    calendarView.addDecorator(eventDecorator);
                } else {
                    Toast.makeText(CalendarActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderIDAndDate>> call, @NonNull Throwable t) {
                Toast.makeText(CalendarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrdersForSelectedDate(CalendarDay date) {
        LocalDate selectedDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());

        // Filter orders for the selected date
        List<OrderIDAndDate> selectedOrders = orders.stream()
                .filter(order -> LocalDate.parse(order.getFunctionDate()).equals(selectedDate))
                .collect(Collectors.toList());

        String title = "Orders for " + selectedDate.toString();
        ordersTitleLabel.setText(title);
        if (selectedOrders.isEmpty()) {
            Toast.makeText(this, "No orders for this date", Toast.LENGTH_SHORT).show();
            orderAdapter = new OrderIDListAdapter(this, new ArrayList<>());
            ordersRecyclerView.setAdapter(orderAdapter);
        } else {
            orderAdapter = new OrderIDListAdapter(this, selectedOrders);
            ordersRecyclerView.setAdapter(orderAdapter);
        }
    }

    private CalendarDay localDateToCalendarDay(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return CalendarDay.from(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
    }
}
