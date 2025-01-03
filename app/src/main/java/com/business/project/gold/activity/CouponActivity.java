package com.business.project.gold.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.business.project.gold.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Random;

public class CouponActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        // Set up the toolbar as the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Discount Coupon");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String validityText = getIntent().getStringExtra("validity");
        String discount = getIntent().getStringExtra("discount");
        String couponCode = getIntent().getStringExtra("couponCode");

        // Initialize TextViews
        TextView couponCodeTextView = findViewById(R.id.couponCode);
        TextView validityTextView = findViewById(R.id.validity);
        TextView discountTextView = findViewById(R.id.discountPercentage);
        FloatingActionButton shareButton = findViewById(R.id.share_coupon_button);

        // Update TextView content
        discount = discount.concat( " %");
        couponCodeTextView.setText(couponCode);
        validityTextView.setText(validityText);
        discountTextView.setText(discount);

        // Set share button functionality
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCoupon(couponCode);
            }
        });
    }


    private void shareCoupon(String couponCode) {
        try {
            // Find the layout containing the coupon
            View couponLayout = findViewById(R.id.couponLayout); // Replace with your coupon layout ID

            // Create a bitmap from the layout
            Bitmap bitmap = Bitmap.createBitmap(couponLayout.getWidth(), couponLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            couponLayout.draw(canvas);

            // Save the screenshot to an app-specific directory
            File file = new File(getExternalFilesDir(null), (couponCode + ".png"));
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
        } catch (Exception e) {
            Log.e("ShareError", "Error sharing screenshot", e);
        }
    }

}
