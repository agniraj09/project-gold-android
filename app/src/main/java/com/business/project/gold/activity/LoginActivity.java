package com.business.project.gold.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.business.project.gold.R;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.UserDetails;
import com.business.project.gold.utils.NetworkUtils;
import com.business.project.gold.utils.UserRoleUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView textViewWelcome;
    EditText usernameText;
    Button buttonFetchRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        usernameText = findViewById(R.id.editTextUserID);
        buttonFetchRole = findViewById(R.id.buttonFetchRole);

        // Check if setup is completed
        boolean isSetupCompleted = UserRoleUtils.isSetupCompleted(getApplicationContext());

        buttonFetchRole.setOnClickListener(v -> {
            hideKeyboard();
            String username = usernameText.getText().toString().trim();
            if (!username.isEmpty()) {
                fetchRoleDetails(username);
            } else {
                usernameText.setError("Please enter a valid Username");
            }
        });

        if (isSetupCompleted) {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if (!NetworkUtils.isNetworkConnected(this)) {
                NetworkUtils.showNetworkDialog(this);
            }
        }
    }

    private void fetchRoleDetails(String username) {
        Call<UserDetails> userDetails = RetrofitConfig.getApiService().getAUser(username);

        userDetails.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDetails user = response.body();
                    UserRoleUtils.saveUserDetails(getApplicationContext(), username, user.getRole());
                    usernameText.setVisibility(View.GONE);
                    buttonFetchRole.setVisibility(View.GONE);

                    Animation fadeZoomIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_zoom_in);
                    String welcomeMessage = "Welcome\n" + user.getFullname() + " 😎";
                    textViewWelcome.setText(welcomeMessage);
                    textViewWelcome.setVisibility(View.VISIBLE);
                    textViewWelcome.startAnimation(fadeZoomIn);

                    // Delay for 2 seconds, then move to the next activity
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        finish();
                    }, 2500); // 2000 milliseconds = 2 seconds
                } else {
                    usernameText.setError("Invalid User");
                    Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                usernameText.setError("Invalid User");
                Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
