package com.zeyad.usecases.integration;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.ImmediateSchedulersRule;
import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RequestsVerifier;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.mockwebserver.MockResponse;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;

/**
 * @author by ZIaDo on 6/17/17.
 */
@RunWith(AndroidRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25, application = TestApplication.class)
public class APIIntegrationTest {

    private final static String userResponse = "{\"login\": \"Zeyad-37\", \"id\": 5938141, \"avatar_url\": " +
            "\"https://avatars2.githubusercontent.com/u/5938141?v=3\"}",
            userListResponse = "[" + userResponse + ", " + userResponse + "]",
            SUCCESS = "{\"success\": \"true\"}";
    private final static Success success = new Success(true);
    @Rule
    public final ImmediateSchedulersRule testSchedulerRule = new ImmediateSchedulersRule();
    private IDataService dataService;
    private User testUser;
    private List<User> users;

    @Before
    public void setUp() {
        RESTMockServer.reset();
        new DataServiceFactory(new DataServiceConfig.Builder(RuntimeEnvironment.application)
                .baseUrl(RESTMockServer.getUrl())
//                .withRealm() Todo Fix
                //                .withCache(3, TimeUnit.MINUTES)
                .build());
        dataService = DataServiceFactory.Companion.getDataService();
        users = new ArrayList<>(2);
        testUser = new User();
        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
        testUser.setId(5938141);
        testUser.setLogin("Zeyad-37");
        users.add(testUser);
        users.add(testUser);
    }

    @Test
    public void testValidUser() {
        final String getUserPath = "users/Zeyad-37";
        RESTMockServer.whenGET(pathContains(getUserPath))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(userResponse));

        final TestSubscriber<User> testSubscriber = new TestSubscriber<>();
        dataService.<User>getObject(new GetRequest.Builder(User.class, false)
                .url(getUserPath)
                .id("Zeyad-37", User.LOGIN)
                //                .cache(User.LOGIN)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyGET(pathContains(getUserPath)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(testUser)
                .assertComplete();
    }

    @Test
    public void testValidUserList() {
        String getUserListPath = "users?since=0";
        RESTMockServer.whenGET(pathContains(getUserListPath))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(userListResponse));

        TestSubscriber<List<User>> testSubscriber = new TestSubscriber<>();
        dataService.<User>getList(new GetRequest.Builder(User.class, false)
                .url(getUserListPath)
                .id("Zeyad-37", User.LOGIN)
                //                .cache(User.LOGIN)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyGET(pathContains(getUserListPath)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(users)
                .assertComplete();
    }

    @Test
    public void testPatchObject() {
        String path = "patch/user";
        RESTMockServer.whenPATCH(pathContains(path))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(SUCCESS));

        TestObserver<Success> testSubscriber = new TestObserver<>();
        dataService.<Success>patchObject(new PostRequest.Builder(User.class, false)
                .url(path)
                .payLoad(testUser)
                .responseType(Success.class)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyPATCH(pathContains(path)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(success)
                .assertComplete();
    }

    @Test
    public void testPostObject() {
        String path = "postObject/user";
        RESTMockServer.whenPOST(pathContains(path))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(SUCCESS));

        TestObserver<Success> testSubscriber = new TestObserver<>();
        dataService.<Success>postObject(new PostRequest.Builder(User.class, false)
                .url(path)
                .payLoad(testUser)
                .responseType(Success.class)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyPOST(pathContains(path)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(success)
                .assertComplete();
    }

    @Test
    public void testPostList() {
        String path = "postList/users";
        RESTMockServer.whenPOST(pathContains(path))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(SUCCESS));

        TestObserver<Success> testSubscriber = new TestObserver<>();
        dataService.<Success>postList(new PostRequest.Builder(User.class, false)
                .url(path)
                .payLoad(userListResponse)
                .responseType(Success.class)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyPOST(pathContains(path)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(success)
                .assertComplete();
    }

    @Test
    public void testPutObject() {
        String path = "putObject/user";
        RESTMockServer.whenPUT(pathContains(path))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(SUCCESS));

        TestObserver<Success> testSubscriber = new TestObserver<>();
        dataService.<Success>putObject(new PostRequest.Builder(User.class, false)
                .url(path)
                .payLoad(testUser)
                .responseType(Success.class)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyPUT(pathContains(path)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(success)
                .assertComplete();
    }

    @Test
    public void testPutList() {
        String path = "putList/users";
        RESTMockServer.whenPUT(pathContains(path))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(SUCCESS));

        TestObserver<Success> testSubscriber = new TestObserver<>();
        dataService.<Success>putList(new PostRequest.Builder(User.class, false)
                .url(path)
                .payLoad(userListResponse)
                .responseType(Success.class)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyPUT(pathContains(path)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(success)
                .assertComplete();
    }

//    @Test
//    public void testDeleteItemById() {
//        String path = "deleteObject/user";
//        RESTMockServer.whenDELETE(pathContains(path))
//                .thenReturn(new MockResponse()
//                        .setResponseCode(HttpURLConnection.HTTP_OK)
//                        .setBody(SUCCESS));
//
//        TestObserver<Success> testSubscriber = new TestObserver<>();
//        dataService.<Success>deleteItemById(new PostRequest.Builder(User.class, false)
//                .url(path)
//                .payLoad("{\"id\": \"Zeyad-37\"}")
//                .idColumnName(User.LOGIN, String.class)
//                .responseType(Success.class)
//                .build())
//                .subscribe(testSubscriber);
//
//        testSubscriber.awaitTerminalEvent();
//
//        RequestsVerifier.verifyDELETE(pathContains(path)).invoked();
//
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(success)
//                .assertComplete();
//    }
//
//    @Test
//    public void testDeleteCollectionByIds() {
//        String path = "deleteListById/user";
//        RESTMockServer.whenDELETE(pathContains(path))
//                .thenReturn(new MockResponse()
//                        .setResponseCode(HttpURLConnection.HTTP_OK)
//                        .setBody(SUCCESS));
//        List<String> payload = new ArrayList<>(2);
//        payload.add("Zeyad-37");
//        payload.add("Zeyad-37");
//        TestObserver<Success> testSubscriber = new TestObserver<>();
//        dataService.<Success>deleteCollectionByIds(new PostRequest.Builder(User.class, false)
//                .url(path)
////                .payLoad(Arrays.array("Zeyad-37", "Zeyad-37"))
//                .payLoad(payload)
//                .idColumnName(User.LOGIN, String.class)
//                .responseType(Success.class)
//                .build())
//                .subscribe(testSubscriber);
//
//        testSubscriber.awaitTerminalEvent();
//
//        RequestsVerifier.verifyDELETE(pathContains(path)).invoked();
//
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(success)
//                .assertComplete();
//    }

//    @Test
//    public void testDeleteAll() {
//        TestObserver<Boolean> testObserver = new TestObserver<>();
//        dataService.deleteAll(new PostRequest.Builder(User.class, false)
//                .responseType(Boolean.class)
//                .build())
//                .subscribe(testObserver);
//
//        testObserver.awaitTerminalEvent();
//
//        testObserver.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(true)
//                .assertComplete();
//    }

//    @Test
//    public void testQueryDisk() {
//        TestSubscriber<List<User>> testSubscriber = new TestSubscriber<>();
//        dataService.queryDisk(realm -> realm.where(User.class))
//                .subscribe(testSubscriber);
//
//        testSubscriber.awaitTerminalEvent();
//
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(users)
//                .assertComplete();
//    }

//    @Test
//    public void testUploadFile() {
//        String path = "upload/user";
//        RESTMockServer.whenPOST(pathContains(path))
//                .thenReturn(new MockResponse()
//                        .setResponseCode(HttpURLConnection.HTTP_OK)
//                        .setBody(SUCCESS));
//
//        File file = new File(RuntimeEnvironment.application.getCacheDir().getPath(), "test");
//        file.mkdir();
//        TestObserver<Success> testSubscriber = new TestObserver<>();
//
//        HashMap<String, File> hashMap = new HashMap<>(1);
//        hashMap.put("image", file);
//        dataService.<Success>uploadFile(new FileIORequest.Builder(path, file)
//                .keyFileMapToUpload(hashMap)
//                .payLoad(new HashMap<>())
//                .responseType(Success.class)
//                .build())
//                .subscribe(testSubscriber);
//
//        testSubscriber.awaitTerminalEvent();
//
//        RequestsVerifier.verifyPOST(pathContains(path)).invoked();
//
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(success)
//                .assertComplete();
//    }
//
//    @Test
//    public void testDownloadFile() {
//        String path = "download/user";
//        RESTMockServer.whenRequested(pathContains(path))
//                .thenReturn(new MockResponse()
//                        .setResponseCode(HttpURLConnection.HTTP_OK)
//                        .setBody(SUCCESS));
//
//        File file = new File(RuntimeEnvironment.application.getCacheDir().getPath(), "test");
//        file.mkdir();
//
//        TestObserver<File> testSubscriber = new TestObserver<>();
//        dataService.downloadFile(new FileIORequest.Builder(path, file)
//                .payLoad(new HashMap<>())
//                .requestType(Object.class)
//                .build())
//                .subscribe(testSubscriber);
//
//        testSubscriber.awaitTerminalEvent();
//
//        RequestsVerifier.verifyRequest(pathContains(path)).invoked();
//
//        testSubscriber.assertSubscribed()
//                .assertNoErrors()
//                .assertValueCount(1)
//                .assertValue(file)
//                .assertComplete();
//    }

    @Test
    public void testGetObjectOffLineFirst() {
        String getUserPath = "users/Zeyad-37";
        RESTMockServer.whenGET(pathContains(getUserPath))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(userResponse));

        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
        dataService.<User>getObject(new GetRequest.Builder(User.class, false)
                .url(getUserPath)
                .id("Zeyad-37", User.LOGIN)
                //                .cache(User.LOGIN)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyGET(pathContains(getUserPath)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(testUser)
                .assertComplete();
    }

    @Test
    public void testGetListOffLineFirst() {
        String getUserListPath = "users?since=0";
        RESTMockServer.whenGET(pathContains(getUserListPath))
                .thenReturn(new MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(userListResponse));

        TestSubscriber<List<User>> testSubscriber = new TestSubscriber<>();
        dataService.<User>getList(new GetRequest.Builder(User.class, false)
                .url(getUserListPath)
                .id("Zeyad-37", User.LOGIN)
                //                .cache(User.LOGIN)
                .build())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        RequestsVerifier.verifyGET(pathContains(getUserListPath)).invoked();

        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(users)
                .assertComplete();
    }
}
