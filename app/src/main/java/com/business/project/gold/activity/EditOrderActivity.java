package com.business.project.gold.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.CustomerDetailsDTO;
import com.business.project.gold.domain.NewOrderRequest;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;
import com.business.project.gold.domain.SpinnerItem;
import com.business.project.gold.domain.UserDetails;
import com.business.project.gold.domain.UserDetailsWithNameDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOrderActivity extends AppCompatActivity {

    private Spinner orderTypeSpinner, referrerSpinner, managerSpinner;
    private TextView functionDateTextView;
    private EditText advanceAmount, totalAmount, damageRepairCost, deliveryCharges, customerName, customerMobileNumber;

    private long orderId;

    private final DateTimeFormatter ddMMMyyyy = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private boolean fromNewOrderPage;
    private boolean fromViewOrdersPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_submission); // You can reuse the same layout
        setTitle("Edit Order");

        orderId = getIntent().getLongExtra("orderId", 0);
        functionDateTextView = findViewById(R.id.functionDate);
        orderTypeSpinner = findViewById(R.id.orderType);
        referrerSpinner = findViewById(R.id.referrer);
        managerSpinner = findViewById(R.id.manager);
        advanceAmount = findViewById(R.id.advanceAmount);
        totalAmount = findViewById(R.id.totalAmount);
        damageRepairCost = findViewById(R.id.damageRepairCost);
        deliveryCharges = findViewById(R.id.deliveryCharges);
        customerName = findViewById(R.id.customer_name);
        customerMobileNumber = findViewById(R.id.customer_mobile);
        functionDateTextView.setOnClickListener(v -> showDatePickerDialog());

        fromNewOrderPage = getIntent().getBooleanExtra("fromNewOrderPage", false);
        fromViewOrdersPage = getIntent().getBooleanExtra("fromViewOrdersPage", false);

        // Populate spinners
        populateOrderTypeSpinner();
        populateReferrerManagerSpinners();

        // Handle the back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackWithResult();
            }
        });
    }

    private void loadOrderDetails(long orderId) {
        // Fetch order details and populate the fields
        Call<OrderDetailsWithUserDetailsDTO> call = RetrofitConfig.getApiService().getAnOrder(orderId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<OrderDetailsWithUserDetailsDTO> call, Response<OrderDetailsWithUserDetailsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetailsWithUserDetailsDTO order = response.body();
                    populateOrderDetails(order);
                } else {
                    Toast.makeText(EditOrderActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsWithUserDetailsDTO> call, Throwable t) {
                Toast.makeText(EditOrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateOrderDetails(OrderDetailsWithUserDetailsDTO order) {
        advanceAmount.setText(String.valueOf(order.advanceAmount()));
        totalAmount.setText(String.valueOf(order.totalAmount()));
        damageRepairCost.setText(String.valueOf(order.damageRepairCost()));
        deliveryCharges.setText(String.valueOf(order.deliveryCharges()));
        functionDateTextView.setText(ddMMMyyyy.format(LocalDate.parse(order.functionDate())));
        customerName.setText(order.customer().getCustomerName());
        customerMobileNumber.setText(order.customer().getMobileNumber());

        // Set selected order type, referrer, and manager
        if (orderTypeSpinner.getAdapter() != null) {
            setSpinnerSelection(orderTypeSpinner, order.type());
        }
        if (referrerSpinner.getAdapter() != null) {
            setSpinnerSelection(referrerSpinner, order.referrer());
        }
        if (managerSpinner.getAdapter() != null) {
            setSpinnerSelection(managerSpinner, order.manager());
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, UserDetailsWithNameDTO user) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                SpinnerItem item = (SpinnerItem) adapter.getItem(i);
                if (item.getValue().equalsIgnoreCase(user.fullName())) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void populateReferrerManagerSpinners() {
        // Fetch users for referrer and manager spinners
        Call<List<UserDetails>> call = RetrofitConfig.getApiService().getUsers();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserDetails>> call, Response<List<UserDetails>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserDetails> userList = response.body();
                    List<SpinnerItem> referrers = new ArrayList<>();
                    List<SpinnerItem> managers = new ArrayList<>();
                    userList
                            .forEach(user -> referrers.add(new SpinnerItem().setKey(user.getId()).setValue(user.getFullname())));

                    userList.stream()
                            .filter(user -> "MGR".equalsIgnoreCase(user.getRole()))
                            .forEach(user -> managers.add(new SpinnerItem().setKey(user.getId()).setValue(user.getFullname())));

                    populateReferrerSpinner(referrers);
                    populateManagerSpinner(managers);

                    loadOrderDetails(orderId);
                } else {
                    Toast.makeText(EditOrderActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDetails>> call, Throwable t) {
                Toast.makeText(EditOrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateOrderTypeSpinner() {
        String[] orderTypeOptions = getResources().getStringArray(R.array.order_type_list);
        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderTypeOptions);
        orderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderTypeSpinner.setAdapter(orderTypeAdapter);
        orderTypeSpinner.setSelection(0);
    }

    private void populateManagerSpinner(List<SpinnerItem> managers) {
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, managers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managerSpinner.setAdapter(adapter);
    }

    private void populateReferrerSpinner(List<SpinnerItem> referrers) {
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, referrers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        referrerSpinner.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            if (isFormValid()) {
                submitData();
            } else {
                Toast.makeText(getApplicationContext(), "Fill all mandatory fields", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFormValid() {
        return (!totalAmount.getText().toString().isBlank()
                && !customerName.getText().toString().isBlank()
                && !customerMobileNumber.getText().toString().isBlank());
    }

    private void submitData() {

        var request = new NewOrderRequest()
                .setFunctionDate(LocalDate.parse(functionDateTextView.getText().toString(), ddMMMyyyy).toString())
                .setType(orderTypeSpinner.getSelectedItem().toString())
                .setAdvanceAmount(convertToBigDecimal(advanceAmount))
                .setTotalAmount(convertToBigDecimal(totalAmount))
                .setDamageRepairCost(convertToBigDecimal(damageRepairCost))
                .setDeliveryCharges(convertToBigDecimal(deliveryCharges))
                .setCancellationCharge(BigDecimal.ZERO)
                .setManager(((SpinnerItem) managerSpinner.getSelectedItem()).getKey())
                .setReferrer(((SpinnerItem) referrerSpinner.getSelectedItem()).getKey())
                .setCustomerDetails(new CustomerDetailsDTO().setCustomerName(customerName.getText().toString()).setMobileNumber(customerMobileNumber.getText().toString()));

        Call<OrderDetailsWithUserDetailsDTO> call = RetrofitConfig.getApiService().editOrder(orderId, request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(EditOrderActivity.this, OrderDetailActivity.class);
                    intent.putExtra("orderId", response.body().id());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("fromNewOrderPage", fromNewOrderPage);
                    intent.putExtra("fromViewOrdersPage", fromViewOrdersPage);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Order updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("Failed to load data");
                    Toast.makeText(EditOrderActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(EditOrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private BigDecimal convertToBigDecimal(EditText editText) {
        var value = editText.getText().toString();
        return value.isBlank() ? BigDecimal.ZERO : BigDecimal.valueOf(Long.parseLong(value));
    }

    private void showDatePickerDialog() {
        functionDateTextView = findViewById(R.id.functionDate);
        LocalDate currentDate = LocalDate.now();
        int yearGiven = currentDate.getYear();
        int month = currentDate.getMonthValue() - 1; // Month is 0-indexed
        int day = currentDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    String formattedDate = selectedDate.format(ddMMMyyyy);
                    functionDateTextView.setText(formattedDate);
                },
                yearGiven,
                month,
                day
        );
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_order_submission_menu, menu);
        return true;
    }

    // Handle the ActionBar back button press
    @Override
    public boolean onSupportNavigateUp() {
        navigateBackWithResult();
        return true; // Indicate the event was handled
    }

    private void navigateBackWithResult() {
        // Pass the orderId back to the previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("orderId", orderId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}
