package com.zeyad.usecases.data.network;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    private static RestApiImpl sRestAPI;
    private final IApiConnection mApiConnection;

    /**
     * Creates a new instance.
     */
    private RestApiImpl() {
        mApiConnection = ApiConnectionFactory.getInstance();
    }

    RestApiImpl(IApiConnection apiConnection) {
        mApiConnection = apiConnection;
    }

    public static void init() {
        sRestAPI = new RestApiImpl();
    }

    public static RestApiImpl getInstance() {
        if (sRestAPI == null)
            init();
        return sRestAPI;
    }

    /**
     * Downloads file from the give url.
     *
     * @param url address of file to be downloaded.
     * @return Observable with the ResponseBody
     */
    @Override
    public Observable<ResponseBody> dynamicDownload(@Url String url) {
        return mApiConnection.dynamicDownload(url);
    }

    /**
     * Uploads a file to a url.
     *
     * @param url     destination address.
     * @param partMap request body contains the file to be uploaded.
     * @return Observable with the Object response.
     */
    @Override
    public Observable<Object> upload(@Url String url, @PartMap Map<String, RequestBody> partMap, @Part MultipartBody.Part file) {
        return mApiConnection.upload(url, partMap, file);
    }

    /**
     * Gets Object from full url.
     *
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicGetObject(@Url String url) {
        return mApiConnection.dynamicGetObject(url);
    }

    /**
     * Gets Object from full url.
     *
     * @param shouldCache should retrofit cache the response.
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicGetObject(@Url String url, boolean shouldCache) {
        return mApiConnection.dynamicGetObject(url, shouldCache);
    }

    /**
     * Gets list from full url.
     *
     * @return Observable with the list.
     */
    @Override
    public Observable<List> dynamicGetList(@Url String url) {
        return mApiConnection.dynamicGetList(url);
    }

    /**
     * Gets list from full url.
     *
     * @param shouldCache should retrofit cache the response.
     * @return Observable with the list.
     */
    @Override
    public Observable<List> dynamicGetList(@Url String url, boolean shouldCache) {
        return mApiConnection.dynamicGetList(url, shouldCache);
    }

    /**
     * Post Object to full url.
     *
     * @param body payload to send.
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicPost(@Url String url, RequestBody body) {
        return mApiConnection.dynamicPost(url, body);
    }

    /**
     * Post Object to full url.
     *
     * @param body payload to send.
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicPatch(@Url String url, RequestBody body) {
        return mApiConnection.dynamicPatch(url, body);
    }

    /**
     * Put Object to full url.
     *
     * @param body payload to send.
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicPut(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicPut(url, body);
    }

    /**
     * Delete Object from full url.
     *
     * @param body payload to send.
     * @return Observable with the Object.
     */
    @Override
    public Observable<Object> dynamicDelete(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicDelete(url, body);
    }
}
