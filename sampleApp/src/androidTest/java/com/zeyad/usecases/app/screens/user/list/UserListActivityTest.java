package com.zeyad.usecases.app.screens.user.list;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.zeyad.usecases.app.OkHttpIdlingResourceRule;
import com.zeyad.usecases.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RequestsVerifier;
import okhttp3.mockwebserver.MockResponse;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.zeyad.usecases.app.screens.RecyclerViewItemCountAssertion.withItemCount;
import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;
import static io.appflate.restmock.utils.RequestMatchers.pathStartsWith;
import static org.hamcrest.Matchers.allOf;

/**
 * @author by ZIaDo on 6/15/17.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
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

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                parentMatcher.describeTo(description.appendText("Child at position " + position + " in parent "));
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Before
    public void before() {
        RESTMockServer.reset();
    }

    @Test
    public void userListActivityTest() {
        ViewInteraction recyclerView = onView(allOf(withId(R.id.user_list),
                childAtPosition(childAtPosition(withId(R.id.frameLayout), 0), 0), isDisplayed()));
        recyclerView.check(matches(isDisplayed()));
        onView(withId(R.id.user_list)).check(withItemCount(30));
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