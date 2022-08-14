package com.example.recipeapp.utils;

import static com.example.recipeapp.utils.Constants.PREF_DATA_NAME;
import static com.example.recipeapp.utils.Constants.PREF_USER;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.recipeapp.models.User;
import com.google.gson.Gson;

public class AppSharedPreference {

    SharedPreferences sharedPreferences;

    public AppSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_DATA_NAME, Context.MODE_PRIVATE);
    }

    public User getUserInfo() {
        Gson gson = new Gson();
        String userString = sharedPreferences.getString(PREF_USER, "");
        if (TextUtils.isEmpty(userString)) {
            return null;
        }
        return gson.fromJson(sharedPreferences.getString(PREF_USER, ""), User.class);
    }

    public void setUserInfo(User userInfo) {
        Gson gson = new Gson();
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if (userInfo == null) {
            myEdit.putString(PREF_USER, "");
        } else {
            myEdit.putString(PREF_USER, gson.toJson(userInfo));
        }
        myEdit.commit();
    }
}
