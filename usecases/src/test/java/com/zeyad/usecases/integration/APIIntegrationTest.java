package com.zeyad.usecases.integration;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.api.IDataService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import io.appflate.restmock.RESTMockServer;
import okhttp3.mockwebserver.MockWebServer;

/**
 * @author by ZIaDo on 6/17/17.
 */
@RunWith(AndroidSampleRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25, application = TestApplication.class)
public class APIIntegrationTest {
    private final static String userResponse = "\"{\\n  \\\"login\\\": \\\"Zeyad-37\\\",\\n  \\\"id\\\": 5938141,\\n  \\\"avatar_url\\\": \\\"https://avatars2.githubusercontent.com/u/5938141?v=3\\\",\\n}\"";
    private IDataService dataService;
    private String url;
    private MockWebServer mockWebServer;

    @Before
    public void setUp() {
        RESTMockServer.reset();

        // Be sure to reset the server before each test
//        RESTMockServerStarter.startSync(new JVMFileParser());
//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(RESTMockServer.getUrl())
//                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
//                .build());
//        dataService = DataServiceFactory.getInstance();
    }

    @Test
    public void testValidUser() throws Exception {
//        RESTMockServerStarter.startSync(new JVMFileParser());

//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(RESTMockServer.getUrl())
////                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
//                .build());
//        dataService = DataServiceFactory.getInstance();
//
//        String path = String.format("users/%s", "Zeyad-37");
//        RESTMockServer.whenGET(pathContains(path))
//                .thenReturnString(200, userResponse);
//
//        User testUser = new User();
//        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
//        testUser.setId(5938141);
//        testUser.setLogin("Zeyad-37");
//
//        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
//        dataService.<User>getObject(new GetRequest.Builder(User.class, false)
//                .url(path)
//                .id("Zeyad-37", User.LOGIN, String.class)
////                .cache(User.LOGIN)
//                .build())
//                .subscribe(testSubscriber);
//
////        RequestsVerifier.verifyGET(pathContains(path)).invoked();
//
////        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(testUser)
//                .assertComplete();
    }

    @Test
    public void testValidUser2() throws Exception {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//
//        mockWebServer.enqueue(new MockResponse()
//                .setResponseCode(HttpURLConnection.HTTP_OK)
//                .setBody(userResponse));
//
//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(mockWebServer.url("/").toString())
////                .withCache(3, TimeUnit.MINUTES)
////                .withRealm()
//                .build());
//        dataService = DataServiceFactory.getInstance();
//
//        User testUser = new User();
//        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
//        testUser.setId(5938141);
//        testUser.setLogin("Zeyad-37");
//
//        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
//        dataService.<User>getObject(new GetRequest.Builder(User.class, false)
//                .url(String.format("users/%s", "Zeyad-37"))
//                .id("Zeyad-37", User.LOGIN, String.class)
////                .cache(User.LOGIN)
//                .build());
////                .subscribe(testSubscriber);
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("/Zeyad-37", request.getPath());
//        assertEquals("GET", request.getMethod());
//
////        testSubscriber.awaitTerminalEvent();
////        testSubscriber.assertSubscribed()
////                .assertNoErrors()
////                .assertValueCount(1)
////                .assertValue(testUser)
////                .assertComplete();
//
//        mockWebServer.shutdown();
    }
}
