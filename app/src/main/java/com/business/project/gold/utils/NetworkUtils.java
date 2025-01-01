package com.business.project.gold.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    // Check if the device has an active network connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // For devices running Android 10 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities networkCapabilities =
                            connectivityManager.getNetworkCapabilities(activeNetwork);

                    // Check for network capabilities (like internet connection)
                    if (networkCapabilities != null) {
                        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    }
                }
            }
        }
        return false; // No network connection
    }

    public static void showNetworkDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No network connection. Please check your internet connection and try again.")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Check the network again
                        if (isNetworkConnected(context)) {
                            // Network is available, move forward
                            dialog.dismiss();
                            // Proceed to the next screen
                        } else {
                            // Network is still not available, show the dialog again
                            showNetworkDialog(context);
                        }
                    }
                });
                /*.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Optionally handle cancel action, e.g., close app or disable navigation
                        dialog.dismiss();
                    }
                });*/

        AlertDialog alert = builder.create();
        alert.show();
    }
}
