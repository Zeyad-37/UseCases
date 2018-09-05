package com.zeyad.usecases.app.screens.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zeyad.usecases.app.screens.user.list.UserListActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(UserListActivity.getCallingIntent(this))
        finish()
    }
}
