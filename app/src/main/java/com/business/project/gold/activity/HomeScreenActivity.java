package com.business.project.gold.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.CouponCodeDetailsDto;
import com.business.project.gold.domain.CouponCodeRedeemResponse;
import com.business.project.gold.utils.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        Button newOrderButton = findViewById(R.id.newOrderButton);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        Button revenueButton = findViewById(R.id.revenueButton);
        Button inventoryButton = findViewById(R.id.inventoryButton);
        Button couponCodeButton = findViewById(R.id.couponCodeButton);

        // Set click listeners for each button
        newOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, NewOrderSubmissionActivity.class);
            startActivity(intent);
        });

        viewOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, ViewOrdersActivity.class);
            startActivity(intent);
        });

        revenueButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, RevenueDetailsActivity.class);
            startActivity(intent);
        });

        couponCodeButton.setOnClickListener(v -> showCouponCodeOptionsPopup());

        if (!NetworkUtils.isNetworkConnected(this)) {
            NetworkUtils.showNetworkDialog(this);
        }
    }

    private void showCouponCodeOptionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.coupon_options, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        // Set up the buttons
        Button generateButton = dialogView.findViewById(R.id.generateCouponButton);
        Button redeemButton = dialogView.findViewById(R.id.redeemCouponButton);

        generateButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            openGenerateCouponDialog(); // Open the Generate Coupon Dialog
        });

        redeemButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            openRedeemCouponDialog(); // Redeem coupon code
        });

        alertDialog.show();
    }

    private void openGenerateCouponDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.generate_coupon_popup, null);

        // Initialize dialog views
        EditText etDiscount = dialogView.findViewById(R.id.discountPercentageEditText);
        RadioGroup rgValidity = dialogView.findViewById(R.id.rgValidity);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        // Create the AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);

        // Set up the submit button
        btnSubmit.setOnClickListener(v -> {
            String discount = etDiscount.getText().toString();
            int selectedId = rgValidity.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

            if (discount.isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            var couponCode = "VBJ" + generateRandomNumber(1000000000, 1999999999);
            int validityValue = Integer.parseInt((String) selectedRadioButton.getTag());
            var validity = LocalDate.now().plusMonths(validityValue).toString();
            alertDialog.dismiss();


            var request = new CouponCodeDetailsDto(couponCode, validity, discount);
            Call<Void> call = RetrofitConfig.getApiService().createCouponCode(request);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(HomeScreenActivity.this, CouponActivity.class);
                        intent.putExtra("couponCode", couponCode);
                        intent.putExtra("validity", validity);
                        intent.putExtra("discount", discount);
                        startActivity(intent);
                    } else {
                        System.out.println("Failed to load data");
                        Toast.makeText(HomeScreenActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                    Toast.makeText(HomeScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        alertDialog.show();
    }

    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    private void openRedeemCouponDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.redeem_coupon_dialog, null);

        // Initialize views
        EditText etCouponCode = dialogView.findViewById(R.id.coupon_code_input);
        Button btnRedeem = dialogView.findViewById(R.id.redeem_button);
        TextView tvResult = dialogView.findViewById(R.id.validation_result);

        // Create the AlertDialog
        AlertDialog redeemDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        redeemDialog.setCanceledOnTouchOutside(false);

        // Set redeem button click listener
        btnRedeem.setOnClickListener(v -> {
            String couponCode = etCouponCode.getText().toString().trim();
            if (couponCode.isEmpty()) {
                Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make API call to redeem the coupon
            redeemCoupon(couponCode, tvResult, redeemDialog);
        });

        redeemDialog.show();
    }

    private void redeemCoupon(String couponCode, TextView tvResult, AlertDialog dialog) {
        Call<CouponCodeRedeemResponse> call = RetrofitConfig.getApiService().redeemCouponCode(couponCode);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CouponCodeRedeemResponse> call, @NonNull Response<CouponCodeRedeemResponse> response) {
                tvResult.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    tvResult.setText(response.body().message());
                    tvResult.setTextColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.green));
                } else {
                    try {
                        String errorMessage = new Gson().fromJson(response.errorBody().string(), CouponCodeRedeemResponse.class).message();
                        tvResult.setText(errorMessage);
                        tvResult.setTextColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.red));
                    } catch (Exception e) {
                        Log.e("RedeemError", "Error parsing error response", e);
                        tvResult.setText("Error: Unable to fetch error message.");
                        tvResult.setTextColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.red));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CouponCodeRedeemResponse> call, @NonNull Throwable t) {
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText("Error: Unable to redeem coupon.");
                tvResult.setTextColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.red));
            }
        });
    }





}
