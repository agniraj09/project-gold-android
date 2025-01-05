package com.business.project.gold.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.decoration.EventDecorator;
import com.business.project.gold.domain.OrderIDAndDate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Toolbar toolbar = findViewById(R.id.toolbar_calendarview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendarView = findViewById(R.id.calendarView);
        fetchOrdersWithDates();
    }

    private void fetchOrdersWithDates() {
        Call<List<OrderIDAndDate>> call = RetrofitConfig.getApiService().getAllOrdersWithOrderIDAndDate();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderIDAndDate>> call, @NonNull Response<List<OrderIDAndDate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderIDAndDate> groups = response.body();

                    List<CalendarDay> datesWithDots = groups.stream().map(order -> localDateToCalendarDay(order.getFunctionDate())).distinct().collect(Collectors.toList());

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

    private CalendarDay localDateToCalendarDay(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return CalendarDay.from(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
    }
}