package com.zeyad.usecases.api;

import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface IDataService {

    /**
     * Gets list from getRequest.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    <M> Flowable<List<M>> getList(GetRequest getListRequest);

    /**
     * Gets object from getRequest.
     *
     * @param getRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    <M> Flowable<M> getObject(GetRequest getRequest);

    /**
     * Gets object from getRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    <M> Flowable<M> patchObject(PostRequest postRequest);

    /**
     * Post Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    <M> Flowable<M> postObject(PostRequest postRequest);

    /**
     * Post list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    <M> Flowable<M> postList(PostRequest postRequest);

    /**
     * Put Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    <M> Flowable<M> putObject(PostRequest postRequest);

    /**
     * Put list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    <M> Flowable<M> putList(PostRequest postRequest);

    /**
     * Deletes item from postRequest.
     *
     * @param request contains the attributes of the request.
     * @return Flowable with the list.
     */
    <M> Flowable<M> deleteItemById(PostRequest request);

    /**
     * Deletes list from postRequest.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    <M> Flowable<M> deleteCollectionByIds(PostRequest deleteRequest);

    /**
     * Deletes All.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Completable with the list.
     */
    Completable deleteAll(PostRequest deleteRequest);

    /**
     * Get list of items according to the query passed.
     *
     * @param realmQueryProvider query tp select list of item(s).
     * @return
     */
    <M> Flowable<List<M>> queryDisk(RealmQueryProvider realmQueryProvider);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Flowable<List>} with the data.
     */
    <M> Flowable<List<M>> getListOffLineFirst(GetRequest getRequest);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Flowable>} with the data.
     */
    <M> Flowable<M> getObjectOffLineFirst(GetRequest getRequest);

    /**
     * Uploads a file to a url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Flowable with the Object response.
     */
    <M> Flowable<M> uploadFile(FileIORequest fileIORequest);

    /**
     * Downloads file from the give url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Flowable with the ResponseBody
     */
    Flowable<File> downloadFile(FileIORequest fileIORequest);
}
