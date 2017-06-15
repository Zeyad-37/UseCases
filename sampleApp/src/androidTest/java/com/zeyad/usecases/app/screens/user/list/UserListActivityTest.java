package com.zeyad.usecases.app.screens.user.list;

import android.support.test.rule.ActivityTestRule;

import com.zeyad.usecases.app.OkHttpIdlingResourceRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RequestsVerifier;
import okhttp3.mockwebserver.MockResponse;

import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;
import static io.appflate.restmock.utils.RequestMatchers.pathStartsWith;

/**
 * @author by ZIaDo on 6/15/17.
 */
public class UserListActivityTest {
    private static final String USER_LIST_BODY = "{ \"login\" : \"octocat\", \"followers\" : 1500 }";
    private final String urlPart = "users?since=0";
    @Rule
    public ActivityTestRule<UserListActivity> activityRule
            = new ActivityTestRule<>(UserListActivity.class, true, false);

    @Rule
    public OkHttpIdlingResourceRule okHttpIdlingResourceRule = new OkHttpIdlingResourceRule();

//    @Rule
//    public MockWebServerRule mockWebServerRule = new MockWebServerRule();

    @Before
    public void before() {
        RESTMockServer.reset();
    }

    @Test
    public void followers() throws IOException, InterruptedException {
        RESTMockServer.whenGET(pathEndsWith(urlPart))
                .thenReturnFile("users/userList.json");

        activityRule.launchActivity(null);

//        onView(withId(R.id.followers))
//                .check(matches(withText("1500")));

        RequestsVerifier.verifyGET(pathStartsWith("/" + urlPart)).invoked();
    }

    @Test
    public void status404() throws IOException {
        RESTMockServer.whenGET(pathEndsWith(urlPart))
                .thenReturnEmpty(404);

        activityRule.launchActivity(null);

//        onView(withId(R.id.followers))
//                .check(matches(withText("404")));
    }

    @Test
    public void malformedJson() throws IOException {
        RESTMockServer.whenGET(pathEndsWith(urlPart)).thenReturn(new MockResponse().setBody("Jason"));

        activityRule.launchActivity(null);

//        onView(withId(R.id.followers))
//                .check(matches(withText("IOException")));
    }

    @Test
    public void timeout() throws IOException {
        RESTMockServer.whenGET(pathEndsWith(urlPart)).thenReturn(
                new MockResponse().setBody(USER_LIST_BODY).throttleBody(1, 1, TimeUnit.SECONDS));

        activityRule.launchActivity(null);

//        onView(withId(R.id.followers))
//                .check(matches(withText("SocketTimeoutException")));
    }
}