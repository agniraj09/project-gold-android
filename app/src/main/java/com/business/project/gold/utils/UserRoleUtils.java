package com.business.project.gold.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserRoleUtils {

    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_USER_ID = "username";
    private static final String KEY_USER_ROLE = "role";
    public static final String IS_SETUP_COMPLETED = "isSetupCompleted";

    // Save user details
    public static void saveUserDetails(Context context, String userId, String userRole) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.putBoolean(IS_SETUP_COMPLETED, true);
        editor.apply();
    }

    // Get user details
    public static String getUserDetails(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null); // Returns null if the key doesn't exist
    }

    // Clear user details
    public static void clearUserDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean isSetupCompleted(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_SETUP_COMPLETED, false); // Returns null if the key doesn't exist
    }
}
