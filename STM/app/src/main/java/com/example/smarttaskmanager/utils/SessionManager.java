package com.example.smarttaskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smarttaskmanager.dto.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME   = "stm_session";
    private static final String KEY_USER    = "logged_in_user";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        prefs.edit()
                .putString(KEY_USER, gson.toJson(user))
                .apply();
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, User.class);
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_USER);
    }

    public void clearSession() {
        prefs.edit().remove(KEY_USER).apply();
    }
}