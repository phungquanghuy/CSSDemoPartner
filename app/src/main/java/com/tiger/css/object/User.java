package com.tiger.css.object;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    public String name;
    public String email;
    public String username;


    public User() {
        this.username = "null";
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, Context context) {
        this.name = name;
        this.email = email;
        SharedPreferences mSharedPreferences = context.getSharedPreferences("username",Context.MODE_PRIVATE);
        this.username = mSharedPreferences.getString("username","null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
