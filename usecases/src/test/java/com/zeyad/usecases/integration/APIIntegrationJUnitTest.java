package com.zeyad.usecases.integration;

import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.stores.CloudStore;
import com.zeyad.usecases.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import io.reactivex.subscribers.TestSubscriber;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;
import static junit.framework.Assert.assertEquals;

/**
 * @author by ZIaDo on 6/27/17.
 */
public class APIIntegrationJUnitTest {
    private final static String userResponse = "\"{\\n  \\\"login\\\": \\\"Zeyad-37\\\",\\n  \\\"id\\\": 5938141,\\n  \\\"avatar_url\\\": \\\"https://avatars2.githubusercontent.com/u/5938141?v=3\\\",\\n}\"";
    private IDataService dataService;
    private CloudStore cloudStore;

    @Before
    public void setUp() {
        // Be sure to reset the server before each test
        RESTMockServerStarter.startSync(new JVMFileParser());
        RESTMockServer.reset();
        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
                .baseUrl(RESTMockServer.getUrl())
                .baseUrl("https://api.github.com/")
//                .okHttpBuilder(new OkHttpClient.Builder()
//                        .addNetworkInterceptor(provideMockInterceptor())
//                        .addInterceptor(provideMockInterceptor()))
//                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
                .build());

        cloudStore = new CloudStore(new ApiConnection(ApiConnection.init(null),
                ApiConnection.initWithCache(null, null)), null, new DAOMapper(), null, Utils.getInstance());
    }

    @Test
    public void testValidUser() throws Exception {
        String path = String.format("users/%s", "Zeyad-37");

        RESTMockServer.whenGET(pathContains(path)).thenReturnFile(200, userResponse);

//        RESTMockServerStarter.startSync(new JVMFileParser());

//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(RESTMockServer.getUrl())
////                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
//                .build());
        dataService = DataServiceFactory.getInstance();

        User testUser = new User();
        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
        testUser.setId(5938141);
        testUser.setLogin("Zeyad-37");

//        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
        GetRequest getRequest = new GetRequest.Builder(User.class, false)
                .url(path)
                .id("Zeyad-37", User.LOGIN, String.class)
                .build();
        dataService.<User>getObject(getRequest)
                .subscribe(testSubscriber);
//        cloudStore.<User>dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
//                getRequest.getItemId(), getRequest.getIdType(), getRequest.getDataClass(),
//                getRequest.isPersist(), getRequest.isShouldCache())
//                .subscribe(testSubscriber);
//        RequestsVerifier.verifyGET(pathContains(path)).invoked();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(testUser)
                .assertResult(testUser)
                .assertComplete();
        assertEquals(1, testSubscriber.valueCount());
        assertEquals(testUser, testSubscriber.values().get(0));
    }
}
