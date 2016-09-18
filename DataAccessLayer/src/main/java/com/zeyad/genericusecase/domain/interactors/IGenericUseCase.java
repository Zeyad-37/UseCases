package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;

public interface IGenericUseCase {

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicGetList(@NonNull Subscriber UseCaseSubscriber, String url, @NonNull Class presentationClass,
                               Class domainClass, Class dataClass, boolean persist);

    @SuppressWarnings("unchecked")
    void executeDynamicGetList(@NonNull GetListRequest genericUseCaseRequest) throws Exception;

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicGetList(@NonNull Subscriber UseCaseSubscriber, String url, @NonNull Class presentationClass,
                               Class domainClass, Class dataClass, boolean persist, boolean shouldCache);

    @SuppressWarnings("unchecked")
    Observable getList(@NonNull GetListRequest genericUseCaseRequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeGetObject(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId,
                          @NonNull Class presentationClass, Class domainClass, Class dataClass, boolean persist);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeGetObject(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId,
                          @NonNull Class presentationClass, Class domainClass, Class dataClass, boolean persist,
                          boolean shouldCache);

    @SuppressWarnings("unchecked")
    void executeGetObject(@NonNull GetObjectRequest getObjectRequest);

    @SuppressWarnings("unchecked")
    Observable getObject(@NonNull GetObjectRequest getObjectRequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicPostObject(@NonNull Subscriber UseCaseSubscriber, String url,
                                  String idColumnName, HashMap<String, Object> keyValuePairs,
                                  @NonNull Class presentationClass, Class domainClass,
                                  Class dataClass, boolean persist);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicPostObject(@NonNull Subscriber UseCaseSubscriber, String idColumnName, String url, JSONObject keyValuePairs,
                                  @NonNull Class presentationClass, Class domainClass, Class dataClass,
                                  boolean persist);

    @SuppressWarnings("unchecked")
    void executeDynamicPostObject(@NonNull PostRequest postRequest);

    Observable postObject(@NonNull PostRequest postRequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicPostList(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, JSONArray jsonArray,
                                Class domainClass, Class dataClass, boolean persist);

    @SuppressWarnings("unchecked")
    void executeDynamicPostList(@NonNull PostRequest postRequest);

    Observable postList(@NonNull PostRequest postRequest);

    @SuppressWarnings("unchecked")
    Observable executeSearch(String query, String column, @NonNull Class presentationClass, Class dataClass);

    @SuppressWarnings("unchecked")
    Observable executeSearch(RealmQuery realmQuery, @NonNull Class presentationClass);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDeleteCollection(@NonNull Subscriber UseCaseSubscriber, String url, HashMap<String,
            Object> keyValuePairs, Class domainClass, Class dataClass, boolean persist);

    @SuppressWarnings("unchecked")
    void executeDeleteCollection(@NonNull PostRequest deleteRequest);

    Observable deleteCollection(@NonNull PostRequest deleteRequest);

    @SuppressWarnings("unchecked")
    void executeDynamicPutObject(@NonNull PostRequest postRequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicPutObject(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, HashMap<String,
            Object> keyValuePairs, @NonNull Class presentationClass, Class domainClass, Class dataClass,
                                 boolean persist);

    Observable putObject(@NonNull PostRequest postRequest);

    Observable uploadFile(@NonNull FileIORequest fileIORequest);

    Observable downloadFile(@NonNull FileIORequest fileIORequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicPutList(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, HashMap<String,
            Object> keyValuePairs, @NonNull Class presentationClass, Class domainClass, Class dataClass,
                               boolean persist);

    void executeDynamicPutList(@NonNull PostRequest postRequest);

    Observable putList(@NonNull PostRequest postRequest);

    @SuppressWarnings("unchecked")
    @Deprecated
    void executeDynamicDeleteAll(@NonNull Subscriber UseCaseSubscriber, String url, Class dataClass,
                                 boolean persist);

    @SuppressWarnings("unchecked")
    void executeDynamicDeleteAll(@NonNull PostRequest postRequest);

    Observable<Boolean> deleteAll(@NonNull PostRequest postRequest);

    Observable readFromResource(String filePath);

    @NonNull
    Observable<String> readFromFile(String fullFilePath);

    Observable<Boolean> saveToFile(String fullFilePath, String data);

    Observable<Boolean> saveToFile(String fullFilePath, byte[] data);

    void unsubscribe();

    @NonNull
    <T> Observable.Transformer<T, T> applySchedulers();
}
