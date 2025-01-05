package com.business.project.gold.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.ArtifactDTO;
import com.business.project.gold.domain.ArtifactGroup;
import com.business.project.gold.domain.CustomerDetailsDTO;
import com.business.project.gold.domain.NewOrderRequest;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;
import com.business.project.gold.domain.SpinnerItem;
import com.business.project.gold.domain.UserDetails;
import com.business.project.gold.domain.UserDetailsWithNameDTO;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOrderActivity extends AppCompatActivity {

    private Spinner orderTypeSpinner, referrerSpinner, managerSpinner;
    private TextView functionDateTextView;
    private EditText advanceAmount, totalAmount, damageRepairCost, deliveryCharges, customerName, customerMobileNumber;

    private Button addJewelSetButton;
    private LinearLayout setContainer;
    private long orderId;

    private final DateTimeFormatter ddMMMyyyy = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private boolean fromNewOrderPage, fromViewOrdersPage, fromTimelinePage;

    private List<ArtifactGroup> selectedGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_submission); // You can reuse the same layout

        // Set up the toolbar as the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderId = getIntent().getLongExtra("orderId", 0);
        functionDateTextView = findViewById(R.id.functionDate);
        addJewelSetButton = findViewById(R.id.addJewelSetButton);
        setContainer = findViewById(R.id.setContainer);
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
        addJewelSetButton.setOnClickListener(v -> showBottomSheet());

        fromNewOrderPage = getIntent().getBooleanExtra("fromNewOrderPage", false);
        fromViewOrdersPage = getIntent().getBooleanExtra("fromViewOrdersPage", false);
        fromTimelinePage = getIntent().getBooleanExtra("fromTimelinePage", false);

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

        // Populate artifact groups and pieces
        for (ArtifactGroup group : order.artifactGroups()) {
            addSelectedGroupAndPieces(group.getArtifactGroup(), group.getArtifacts());
        }

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

    public void addSelectedGroupAndPieces(String groupName, List<ArtifactDTO> selectedPieces) {
        ArtifactGroup newGroup = new ArtifactGroup();
        newGroup.setArtifactGroup(groupName);
        newGroup.setArtifacts(new ArrayList<>());

        // Inflate the layout for the item
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_artifact_group, setContainer, false);

        // Find views in the inflated layout
        TextView groupText = itemView.findViewById(R.id.group_name);
        ImageView deleteIcon = itemView.findViewById(R.id.delete_icon);
        ChipGroup chipGroup = itemView.findViewById(R.id.pieces_chip_group);

        // Set group name
        groupText.setText(groupName);

        // Populate the ChipGroup with the selected pieces
        for (ArtifactDTO piece : selectedPieces) {
            Chip chip = new Chip(this);
            chip.setText(piece.getArtifact());
            chip.setCloseIconVisible(false); // No close icon for individual chips
            chipGroup.addView(chip);

            // Add the artifact to the group's artifact list
            newGroup.getArtifacts().add(new ArtifactDTO()
                    .setArtifactId(piece.getArtifactId())
                    .setArtifact(piece.getArtifact()));
        }

        // Set delete icon click listener to remove the item
        deleteIcon.setOnClickListener(v -> {
            // Remove the item from the parent layout
            setContainer.removeView(itemView);
            selectedGroups.removeIf(group -> group.getArtifactGroup().equalsIgnoreCase(newGroup.getArtifactGroup()));
        });

        // Add the item to the container
        setContainer.addView(itemView);

        // Add the new group to the selected groups list
        selectedGroups.add(newGroup);
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
                .setCustomerDetails(new CustomerDetailsDTO().setCustomerName(customerName.getText().toString()).setMobileNumber(customerMobileNumber.getText().toString()))
                .setArtifactGroupList(selectedGroups);;

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
                    intent.putExtra("fromTimelinePage", fromTimelinePage);
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

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_jewel_set, null);

        Spinner groupDropdown = bottomSheetView.findViewById(R.id.groupDropdown);
        ChipGroup pieceChipGroup = bottomSheetView.findViewById(R.id.pieceChipGroup);
        Button confirmSelectionButton = bottomSheetView.findViewById(R.id.confirmSelectionButton);

        Call<List<ArtifactGroup>> call = RetrofitConfig.getApiService().getArtifactGroupsAndArtifacts(LocalDate.parse(functionDateTextView.getText().toString(), ddMMMyyyy).toString());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ArtifactGroup>> call, Response<List<ArtifactGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArtifactGroup> artifactGroups = response.body();

                    List<String> groupNames = new ArrayList<>();
                    Map<String, List<ArtifactDTO>> artifactMap = new HashMap<>();

                    for (ArtifactGroup group : artifactGroups) {
                        groupNames.add(group.getArtifactGroup());
                        artifactMap.put(group.getArtifactGroup(), group.getArtifacts());
                    }

                    ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(EditOrderActivity.this, android.R.layout.simple_spinner_item, groupNames);
                    groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    groupDropdown.setAdapter(groupAdapter);

                    groupDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedGroup = groupNames.get(position);
                            List<ArtifactDTO> artifacts = artifactMap.getOrDefault(selectedGroup, new ArrayList<>());

                            // Clear previous chips
                            pieceChipGroup.removeAllViews();

                            // Populate ChipGroup with artifact options
                            for (ArtifactDTO artifact : artifacts) {
                                Chip chip = new Chip(EditOrderActivity.this);
                                chip.setText(artifact.getArtifact());
                                chip.setCheckable(true);
                                chip.setTag(artifact.getArtifactId()); // Save artifact ID in tag
                                pieceChipGroup.addView(chip);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            pieceChipGroup.removeAllViews();
                        }
                    });
                } else {
                    Toast.makeText(EditOrderActivity.this, "Failed to load artifact groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ArtifactGroup>> call, Throwable t) {
                Toast.makeText(EditOrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        confirmSelectionButton.setOnClickListener(v -> {
            String selectedGroup = groupDropdown.getSelectedItem() != null ? groupDropdown.getSelectedItem().toString() : "None";
            List<ArtifactDTO> selectedPieces = getSelectedArtifacts(pieceChipGroup); // Your method to fetch selected pieces from ChipGroup

            if (selectedPieces.isEmpty()) {
                // Show Toast if no chip is selected
                Toast.makeText(EditOrderActivity.this, "Please select at least one piece before proceeding.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the method to add them to the layout
            addSelectedGroupAndPieces(selectedGroup, selectedPieces);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.show();
    }

    @NonNull
    private static List<ArtifactDTO> getSelectedArtifacts(ChipGroup pieceChipGroup) {
        List<ArtifactDTO> selectedArtifacts = new ArrayList<>();
        for (int i = 0; i < pieceChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) pieceChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedArtifacts.add(new ArtifactDTO().setArtifactId(Long.parseLong(chip.getTag().toString())).setArtifact(chip.getText().toString()));
            }
        }
        return selectedArtifacts;
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
