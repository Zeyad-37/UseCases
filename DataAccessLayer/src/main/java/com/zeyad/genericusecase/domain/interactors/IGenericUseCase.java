package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;

import io.realm.RealmQuery;
import rx.Observable;

public interface IGenericUseCase {

    @SuppressWarnings("unchecked")
    Observable getList(@NonNull GetListRequest genericUseCaseRequest);

    @SuppressWarnings("unchecked")
    Observable getObject(@NonNull GetObjectRequest getObjectRequest);

    Observable postObject(@NonNull PostRequest postRequest);

    Observable postList(@NonNull PostRequest postRequest);

    @SuppressWarnings("unchecked")
    Observable executeSearch(String query, String column, @NonNull Class presentationClass, Class dataClass);

    @SuppressWarnings("unchecked")
    Observable executeSearch(RealmQuery realmQuery, @NonNull Class presentationClass);

    Observable deleteCollection(@NonNull PostRequest deleteRequest);

    Observable putObject(@NonNull PostRequest postRequest);

    Observable uploadFile(@NonNull FileIORequest fileIORequest);

    Observable downloadFile(@NonNull FileIORequest fileIORequest);

    Observable putList(@NonNull PostRequest postRequest);

    Observable<Boolean> deleteAll(@NonNull PostRequest postRequest);

    Observable readFromResource(String filePath);

    @NonNull
    Observable<String> readFromFile(String fullFilePath);

    Observable<Boolean> saveToFile(String fullFilePath, String data);

    Observable<Boolean> saveToFile(String fullFilePath, byte[] data);

    @NonNull
    <T> Observable.Transformer<T, T> applySchedulers();
}
