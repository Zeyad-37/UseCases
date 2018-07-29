package com.zeyad.usecases.app.screens.splash;

import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;

@LargeTest
//@RunWith(AndroidJUnit4.class)
public class UserListActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void userListActivityTest() {
    }
}
