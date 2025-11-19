package com.kidsapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.kidsapp.utils.Constants;

/**
 * SharedPreferences helper class
 */
public class SharedPref {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Auth Token
    public void saveAuthToken(String token) {
        editor.putString(Constants.KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(Constants.KEY_AUTH_TOKEN, null);
    }

    // User ID
    public void saveUserId(String userId) {
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.KEY_USER_ID, null);
    }

    // User Name
    public void saveUserName(String name) {
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null);
    }

    // User Email
    public void saveUserEmail(String email) {
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
    }

    // User Role
    public void saveUserRole(String role) {
        editor.putString(Constants.KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, null);
    }

    // Login Status
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    // Clear all data
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}

