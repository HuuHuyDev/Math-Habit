package com.kidsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class để quản lý SharedPreferences
 * Giúp lưu và đọc dữ liệu người dùng một cách dễ dàng
 */
public class SharedPreferencesHelper {
    
    private static final String PREF_NAME = "KidsAppPrefs";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    // Keys cho dữ liệu bé
    public static final String KEY_CHILD_NAME = "child_name";
    public static final String KEY_CHILD_AGE = "child_age";
    public static final String KEY_CHILD_BIRTHDAY = "child_birthday";
    public static final String KEY_CHILD_GENDER = "child_gender";
    public static final String KEY_CHILD_GRADE = "child_grade";
    public static final String KEY_CHILD_XP = "child_xp";
    public static final String KEY_CHILD_COINS = "child_coins";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_TOKEN = "user_token";
    
    /**
     * Constructor
     * @param context Context của ứng dụng
     */
    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    /**
     * Lưu thông tin bé vào SharedPreferences
     */
    public void saveChildInfo(String name, int age, String birthday, String gender, String grade) {
        editor.putString(KEY_CHILD_NAME, name);
        editor.putInt(KEY_CHILD_AGE, age);
        editor.putString(KEY_CHILD_BIRTHDAY, birthday);
        editor.putString(KEY_CHILD_GENDER, gender);
        editor.putString(KEY_CHILD_GRADE, grade);
        editor.apply();
    }
    
    /**
     * Lưu điểm XP và Coins
     */
    public void savePoints(int xp, int coins) {
        editor.putInt(KEY_CHILD_XP, xp);
        editor.putInt(KEY_CHILD_COINS, coins);
        editor.apply();
    }
    
    /**
     * Lưu trạng thái đăng nhập
     */
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
    
    /**
     * Kiểm tra xem đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Lưu token đăng nhập
     */
    public void saveToken(String token) {
        editor.putString(KEY_USER_TOKEN, token);
        editor.apply();
    }
    
    /**
     * Lấy token đăng nhập
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }
    
    /**
     * Lấy tên bé
     */
    public String getChildName() {
        return sharedPreferences.getString(KEY_CHILD_NAME, "Bé");
    }
    
    /**
     * Lấy tuổi bé
     */
    public int getChildAge() {
        return sharedPreferences.getInt(KEY_CHILD_AGE, 0);
    }
    
    /**
     * Lấy XP
     */
    public int getXP() {
        return sharedPreferences.getInt(KEY_CHILD_XP, 0);
    }
    
    /**
     * Lấy Coins
     */
    public int getCoins() {
        return sharedPreferences.getInt(KEY_CHILD_COINS, 0);
    }
    
    /**
     * Xóa tất cả dữ liệu (dùng khi đăng xuất)
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Xóa chỉ dữ liệu đăng nhập (giữ lại thông tin bé)
     */
    public void clearLoginData() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_TOKEN);
        editor.apply();
    }
}
