package com.business.project.gold.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.OrderCancellationRequest;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdText, orderDate, functionDate, orderType, status, referrer, manager, advanceAmount, totalAmount, damageRepairCost, deliveryChanges, cancellationCharges,
            netIncome, participantShare, managerShare, referrerShare, customerName, customerMobileNumber;;

    Button editOrderButton, settleOrderButton, cancelOrderButton, generateBillButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setTitle("Order Details");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assignElements();
        long orderId = getIntent().getLongExtra("orderId", 0);
        boolean fromNewOrderPage = getIntent().getBooleanExtra("fromNewOrderPage", false);
        boolean fromViewOrdersPage = getIntent().getBooleanExtra("fromViewOrdersPage", false);

        getOrderDetailsAndPopulateLayouts(orderId);
        handleOrderCancellationOnClick();
        handleOrderSettlementOnClick();
        handleEditOrderOnClick(fromNewOrderPage, fromViewOrdersPage);
        handleGenerateBillOnClick();

        // Register a callback for back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fromNewOrderPage) {
                    Intent intent = new Intent(OrderDetailActivity.this, HomeScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (fromViewOrdersPage) {
                    Intent intent = new Intent(OrderDetailActivity.this, ViewOrdersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
        });

    }

    private void handleGenerateBillOnClick() {
        // Button to trigger the capture process (Optional)
        generateBillButton.setOnClickListener(v -> generateBill());
    }

    private void handleEditOrderOnClick(boolean fromNewOrderPage, boolean fromViewOrdersPage) {
        editOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, EditOrderActivity.class);
            intent.putExtra("orderId", Long.parseLong(orderIdText.getText().toString()));
            intent.putExtra("fromNewOrderPage", fromNewOrderPage);
            intent.putExtra("fromViewOrdersPage", fromViewOrdersPage);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void getOrderDetailsAndPopulateLayouts(long orderId) {
        Call<OrderDetailsWithUserDetailsDTO> call = RetrofitConfig.getApiService().getAnOrder(orderId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    var order = response.body();
                    assignValues(order);
                } else {
                    System.out.println("Failed to load data");
                    Toast.makeText(OrderDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleOrderCancellationOnClick() {
        cancelOrderButton.setOnClickListener(v -> showCancellationPopupAndHandleOrderCancellation(OrderDetailActivity.this, Long.valueOf(orderIdText.getText().toString())));
    }

    private void handleOrderSettlementOnClick() {
        settleOrderButton.setOnClickListener(v -> showSettlementConfirmationDialog(OrderDetailActivity.this, Long.valueOf(orderIdText.getText().toString())));
    }

    public void showSettlementConfirmationDialog(Context context, Long orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Settlement");
        builder.setMessage("Are you sure you want to settle this order?");

        // "Yes" Button
        builder.setPositiveButton("Yes", (dialog, which) -> {
            proceedWithOrderSettlement(orderId);
            dialog.dismiss();
        });

        // "Cancel" Button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void proceedWithOrderSettlement(Long orderId) {
        Call<OrderDetailsWithUserDetailsDTO> call = RetrofitConfig.getApiService().settleOrder(orderId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reAssignValuesAfterSettlement(response);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to settle the order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showCancellationPopupAndHandleOrderCancellation(Context context, Long orderId) {
        // Show popup
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.order_cancellation_dialog, null);
        builder.setView(popupView);
        builder.setTitle("Cancellation Details");
        AlertDialog dialog = builder.create();
        dialog.show();

        handleOrderCancellationPopupOnClick(orderId, popupView, dialog);
    }

    private void handleOrderCancellationPopupOnClick(Long orderId, View popupView, AlertDialog dialog) {
        EditText cancellationChargesEditText = popupView.findViewById(R.id.cancellationCharges);
        CheckBox isAdvanceReturnedCheckBox = popupView.findViewById(R.id.isAdvanceReturned);
        Button submitButton = popupView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String cancellationCharegString = cancellationChargesEditText.getText().toString();
            BigDecimal cancellationCharges = cancellationCharegString.isBlank() ? BigDecimal.ZERO : BigDecimal.valueOf(Long.parseLong(cancellationCharegString));
            boolean isAdvanceReturned = isAdvanceReturnedCheckBox.isChecked();

            var request = new OrderCancellationRequest(orderId, cancellationCharges, isAdvanceReturned);

            Call<OrderDetailsWithUserDetailsDTO> call = RetrofitConfig.getApiService().cancelOrder(request);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        reAssignValuesAfterCancellation(response);
                    } else {
                        System.out.println("Failed to load data");
                        Toast.makeText(OrderDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OrderDetailsWithUserDetailsDTO> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                    Toast.makeText(OrderDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            dialog.dismiss();
        });
    }

    private void reAssignValuesAfterCancellation(@NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
        Toast.makeText(getApplicationContext(), "Order cancelled successfully", Toast.LENGTH_LONG).show();
        assignValues(response.body());
    }

    private void reAssignValuesAfterSettlement(@NonNull Response<OrderDetailsWithUserDetailsDTO> response) {
        Toast.makeText(OrderDetailActivity.this, "Order Settled Successfully", Toast.LENGTH_LONG).show();
        assignValues(response.body());
    }

    private void assignValues(OrderDetailsWithUserDetailsDTO order) {
        orderIdText.setText(String.valueOf(order.id()));
        orderDate.setText(order.orderDate());
        orderType.setText(order.type());
        functionDate.setText(order.functionDate());
        status.setText(order.status());
        referrer.setText(order.referrer().firstName());
        manager.setText(order.manager().firstName());
        advanceAmount.setText(String.valueOf(order.advanceAmount()));
        totalAmount.setText(String.valueOf(order.totalAmount()));
        damageRepairCost.setText(String.valueOf(order.damageRepairCost()));
        deliveryChanges.setText(String.valueOf(order.deliveryCharges()));
        cancellationCharges.setText(String.valueOf(order.cancellationCharges()));
        netIncome.setText(String.valueOf(order.netIncome()));
        participantShare.setText(String.valueOf(order.participantsShare()));
        managerShare.setText(String.valueOf(order.managerShare()));
        referrerShare.setText(String.valueOf(order.referrerShare()));
        customerName.setText(order.customer().getCustomerName());
        customerMobileNumber.setText(order.customer().getMobileNumber());

        if ("CANCELLED".equalsIgnoreCase(order.status())) {
            orderIdText.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            orderIdText.setTextColor(getColor(android.R.color.white));
            makeButtonsInvisible();
        } else if ("SETTLED".equalsIgnoreCase(order.status())) {
            orderIdText.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            orderIdText.setTextColor(getColor(android.R.color.white));
            makeButtonsInvisible();
        }
    }

    private void makeButtonsInvisible() {
        editOrderButton.setVisibility(View.GONE);
        settleOrderButton.setVisibility(View.GONE);
        cancelOrderButton.setVisibility(View.GONE);
        generateBillButton.setVisibility(View.VISIBLE);
    }

    private void assignElements() {
        orderIdText = findViewById(R.id.order_id);
        orderDate = findViewById(R.id.orderDate);
        functionDate = findViewById(R.id.functionDate);
        orderType = findViewById(R.id.orderType);
        status = findViewById(R.id.status);
        referrer = findViewById(R.id.referrer);
        manager = findViewById(R.id.manager);
        advanceAmount = findViewById(R.id.advanceAmount);
        totalAmount = findViewById(R.id.totalAmount);
        damageRepairCost = findViewById(R.id.damageRepairCost);
        deliveryChanges = findViewById(R.id.deliveryChanges);
        cancellationCharges = findViewById(R.id.cancellationCharges);
        netIncome = findViewById(R.id.netIncome);
        participantShare = findViewById(R.id.participantShare);
        managerShare = findViewById(R.id.managerShare);
        referrerShare = findViewById(R.id.referrerShare);
        editOrderButton = findViewById(R.id.edit_order);
        settleOrderButton = findViewById(R.id.settle_order);
        cancelOrderButton = findViewById(R.id.cancel_order);
        generateBillButton = findViewById(R.id.generate_bill);
        customerName = findViewById(R.id.customer_name);
        customerMobileNumber = findViewById(R.id.customer_mobile);
    }

    private void generateBill() {
        try {
            var billCardView = prepareAndExportBill();
            exportBill(billCardView);
        } catch (Exception e) {
            Log.e("ShareError", "Error sharing screenshot", e);
        }
    }

    private View prepareAndExportBill() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View billLayout = inflater.inflate(R.layout.bill_layout, null); // Replace with the correct layout name

        // Access and modify the views in the inflated layout
        TextView orderId = billLayout.findViewById(R.id.order_id);
        TextView invoiceDate = billLayout.findViewById(R.id.functionDate);
        TextView customerName = billLayout.findViewById(R.id.customer_name);
        TextView customerMobile = billLayout.findViewById(R.id.customer_mobile);
        TextView manager = billLayout.findViewById(R.id.manager);
        TextView advanceAmount = billLayout.findViewById(R.id.advanceAmount);
        TextView packageAmount = billLayout.findViewById(R.id.packageAmount);
        TextView deliveryChanges = billLayout.findViewById(R.id.deliveryChanges);
        TextView cancellationCharges = billLayout.findViewById(R.id.cancellationCharges);
        TextView totalAmount = billLayout.findViewById(R.id.totalAmount);


        // Assign values dynamically
        orderId.setText(this.orderIdText.getText().toString());
        invoiceDate.setText(this.customerName.getText().toString());
        customerName.setText(this.customerName.getText().toString());
        customerMobile.setText(this.customerMobileNumber.getText().toString());
        manager.setText(this.manager.getText().toString());
        advanceAmount.setText(this.advanceAmount.getText().toString());
        packageAmount.setText(this.totalAmount.getText().toString());
        deliveryChanges.setText(this.deliveryChanges.getText().toString());
        cancellationCharges.setText(this.cancellationCharges.getText().toString());

        if ("CANCELLED".equalsIgnoreCase(this.status.getText().toString())) {
            totalAmount.setText(this.cancellationCharges.getText().toString());
        } else if ("SETTLED".equalsIgnoreCase(this.status.getText().toString())) {
            var total = Long.parseLong(this.totalAmount.getText().toString()) + Long.parseLong(this.deliveryChanges.getText().toString());
            totalAmount.setText(String.valueOf(total));
        }



        // Call the method to generate and export the image
        return billLayout.findViewById(R.id.bill_card);
    }


    private void exportBill(View billView) throws IOException {
        // Measure and layout the view
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(
                getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.EXACTLY);
        int desiredHeight = View.MeasureSpec.makeMeasureSpec(
                getResources().getDisplayMetrics().heightPixels, View.MeasureSpec.AT_MOST);

        billView.measure(desiredWidth, desiredHeight);
        billView.layout(0, 0, billView.getMeasuredWidth(), billView.getMeasuredHeight());

        // Create a bitmap from the layout
        Bitmap bitmap = Bitmap.createBitmap(billView.getMeasuredWidth(), billView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        billView.draw(canvas);

        // Save the screenshot to an app-specific directory
        File file = new File(getExternalFilesDir(null), "invoice.png");
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        // Share the screenshot
        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                file
        );
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Coupon Via"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean fromNewOrderPage = getIntent().getBooleanExtra("fromNewOrderPage", false);
        boolean fromViewOrdersPage = getIntent().getBooleanExtra("fromViewOrdersPage", false);
        if (fromNewOrderPage) {
            Intent intent = new Intent(OrderDetailActivity.this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (fromViewOrdersPage) {
            Intent intent = new Intent(OrderDetailActivity.this, ViewOrdersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onSupportNavigateUp();
    }




}
