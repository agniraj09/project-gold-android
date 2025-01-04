package com.business.project.gold.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.adapter.ArtifactAvailabilityAdapter;
import com.business.project.gold.adapter.ArtifactNameAdapter;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.ArtifactDTO;
import com.business.project.gold.domain.ArtifactGroup;
import com.business.project.gold.domain.CouponCodeDetailsDto;
import com.business.project.gold.domain.CouponCodeRedeemResponse;
import com.business.project.gold.utils.NetworkUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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

        inventoryButton.setOnClickListener(v -> showInventoryOptionsPopup());
        couponCodeButton.setOnClickListener(v -> showCouponCodeOptionsPopup());

        if (!NetworkUtils.isNetworkConnected(this)) {
            NetworkUtils.showNetworkDialog(this);
        }
    }

    private void showInventoryOptionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_inventory_options, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        // Set up the buttons
        Button collectionsButton = dialogView.findViewById(R.id.collectionsButton);
        Button addNewArtifactButton = dialogView.findViewById(R.id.addNewArtifactButton);
        Button editArtifactButton = dialogView.findViewById(R.id.editArtifactButton);
        Button checkAvailabilityButton = dialogView.findViewById(R.id.checkAvailabilityButton);

        collectionsButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(HomeScreenActivity.this, CollectionDetailsActivity.class);
            startActivity(intent);
        });

        checkAvailabilityButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            showCheckAvailabilityDialog();
        });

        addNewArtifactButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            openAddArtifactBottomSheet();
        });

        editArtifactButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            openEditArtifactBottomSheet();
        });

        alertDialog.show();
    }

    private void openEditArtifactBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_artifact, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText groupNameInput = dialogView.findViewById(R.id.group_name_input);
        RecyclerView artifactsRecyclerView = dialogView.findViewById(R.id.artifactsRecyclerView);
        Button addArtifactButton = dialogView.findViewById(R.id.addArtifactButton);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        // Setup RecyclerView for dynamic artifact list
        List<String> artifacts = new ArrayList<>();
        ArtifactNameAdapter artifactAdapter = new ArtifactNameAdapter(artifacts);
        artifactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        artifactsRecyclerView.setAdapter(artifactAdapter);

        // Add Artifact
        addArtifactButton.setOnClickListener(v -> {
            artifacts.add("");
            artifactAdapter.notifyItemInserted(artifacts.size() - 1);
        });

        // Submit Button
        submitButton.setOnClickListener(v -> {
            String selectedGroupName = groupNameInput.getText().toString();
            List<String> enteredArtifacts = artifactAdapter.getArtifacts();

            // Check if the group name is empty
            if (selectedGroupName.trim().isEmpty()) {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there are any artifacts
            if (enteredArtifacts.isEmpty()) {
                Toast.makeText(this, "Please add at least one artifact", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate each artifact name
            boolean hasEmptyArtifact = enteredArtifacts.stream()
                    .map(String::trim)
                    .anyMatch(String::isEmpty);
            if (hasEmptyArtifact) {
                Toast.makeText(this, "Artifact name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            submitArtifacts(selectedGroupName, enteredArtifacts, bottomSheetDialog);
        });

        bottomSheetDialog.show();
    }

    private void openAddArtifactBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_artifact, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText groupNameInput = dialogView.findViewById(R.id.group_name_input);
        RecyclerView artifactsRecyclerView = dialogView.findViewById(R.id.artifactsRecyclerView);
        Button addArtifactButton = dialogView.findViewById(R.id.addArtifactButton);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        // Setup RecyclerView for dynamic artifact list
        List<String> artifacts = new ArrayList<>();
        ArtifactNameAdapter artifactAdapter = new ArtifactNameAdapter(artifacts);
        artifactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        artifactsRecyclerView.setAdapter(artifactAdapter);

        // Add Artifact
        addArtifactButton.setOnClickListener(v -> {
            artifacts.add("");
            artifactAdapter.notifyItemInserted(artifacts.size() - 1);
        });

        // Submit Button
        submitButton.setOnClickListener(v -> {
            String selectedGroupName = groupNameInput.getText().toString();
            List<String> enteredArtifacts = artifactAdapter.getArtifacts();

            // Check if the group name is empty
            if (selectedGroupName.trim().isEmpty()) {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there are any artifacts
            if (enteredArtifacts.isEmpty()) {
                Toast.makeText(this, "Please add at least one artifact", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate each artifact name
            boolean hasEmptyArtifact = enteredArtifacts.stream()
                    .map(String::trim)
                    .anyMatch(String::isEmpty);
            if (hasEmptyArtifact) {
                Toast.makeText(this, "Artifact name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            submitArtifacts(selectedGroupName, enteredArtifacts, bottomSheetDialog);
        });

        bottomSheetDialog.show();
    }

    // Submit artifacts to API
    private void submitArtifacts(String groupName, List<String> artifacts, BottomSheetDialog dialog) {
        ArtifactGroup artifactGroup = new ArtifactGroup().setArtifactGroup(groupName);
        artifactGroup.setArtifacts(artifacts.stream().map(artifact -> new ArtifactDTO().setArtifact(artifact)).collect(Collectors.toList()));

        Call<Void> call = RetrofitConfig.getApiService().addNewArtifact(artifactGroup);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Artifacts added successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add artifacts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCheckAvailabilityDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_check_availability, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText inputDate = dialogView.findViewById(R.id.inputDate);
        Spinner inputGroupSpinner = dialogView.findViewById(R.id.inputGroupSpinner);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        TextView resultsLabel = dialogView.findViewById(R.id.resultsLabel);
        RecyclerView resultsRecyclerView = dialogView.findViewById(R.id.resultsRecyclerView);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup date picker
        inputDate.setOnClickListener(v -> showDatePickerDialog(inputDate));

        // Populate group spinner
        fetchGroups(inputGroupSpinner);

        btnSubmit.setOnClickListener(v -> {
            String date = inputDate.getText().toString().trim();
            String groupName = inputGroupSpinner.getSelectedItem().toString();

            if (date.isEmpty() || groupName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                checkAvailability(date, groupName, resultsLabel, resultsRecyclerView);
            }
        });

        bottomSheetDialog.show();
    }

    private void showDatePickerDialog(EditText inputDate) {
        LocalDate currentDate = LocalDate.now();
        int yearGiven = currentDate.getYear();
        int month = currentDate.getMonthValue() - 1; // Month is 0-indexed
        int day = currentDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    inputDate.setText(selectedDate.toString());
                },
                yearGiven,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void fetchGroups(Spinner inputGroupSpinner) {
        Call<List<String>> call = RetrofitConfig.getApiService().getAllGroups();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> groups = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeScreenActivity.this,
                            android.R.layout.simple_spinner_item, groups);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    inputGroupSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                Toast.makeText(HomeScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAvailability(String date, String groupName, TextView resultsLabel, RecyclerView resultsRecyclerView) {
        Call<Map<String, String>> call = RetrofitConfig.getApiService().checkAvailability(date, groupName);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> results = response.body();

                    if (!results.isEmpty()) {
                        resultsLabel.setVisibility(View.VISIBLE);
                        resultsRecyclerView.setVisibility(View.VISIBLE);

                        // Clear existing data and update the adapter with new data
                        ArtifactAvailabilityAdapter adapter = (ArtifactAvailabilityAdapter) resultsRecyclerView.getAdapter();
                        if (adapter == null) {
                            adapter = new ArtifactAvailabilityAdapter(results);
                            resultsRecyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateData(results);
                        }
                    } else {
                        Toast.makeText(HomeScreenActivity.this, "No results available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Failed to fetch availability", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                Toast.makeText(HomeScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showCouponCodeOptionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_coupon_options, null);
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
        View dialogView = inflater.inflate(R.layout.dialog_generate_coupon, null);

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
        View dialogView = inflater.inflate(R.layout.dialog_redeem_coupon, null);

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