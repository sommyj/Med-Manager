package com.sommy.android.med_manager.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by somto on 4/14/18.
 * This class contains utility methods which update name and email address in SharedPreferences
 */
public class PreferenceUtilities {

    private static final String KEY_DISPLAY_NAME = "display-name";
    private static final String KEY_EMAIL_ADDRESS = "email-address";
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first-name";
    private static final String KEY_LAST_NAME = "last-name";

    private static final String DEFAULT_TEXT = "";

    synchronized public static void setDisplayName(Context context, String displayName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.apply();
    }

    public static String getDisplayName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_DISPLAY_NAME, DEFAULT_TEXT);
    }

    synchronized public static void setEmailAddress(Context context, String emailAddress) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL_ADDRESS, emailAddress);
        editor.apply();
    }

    public static String getEmailAddress(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_EMAIL_ADDRESS, DEFAULT_TEXT);
    }

    synchronized public static void setId(Context context, String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ID, id);
        editor.apply();
    }

    public static String getId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_ID, DEFAULT_TEXT);
    }

    synchronized public static void setFirstName(Context context, String firstName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.apply();
    }

    public static String getFirstName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_FIRST_NAME, DEFAULT_TEXT);
    }

    synchronized public static void setLastName(Context context, String lastName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_NAME, lastName);
        editor.apply();
    }

    public static String getLastName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_LAST_NAME, DEFAULT_TEXT);
    }

}
