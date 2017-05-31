package com.zeyad.usecases.app.screens.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zeyad.usecases.app.screens.user.list.UserListActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(UserListActivity.getCallingIntent(this));
        finish();
    }
}
