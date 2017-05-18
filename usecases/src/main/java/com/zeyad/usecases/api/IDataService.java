package com.zeyad.usecases.api;

import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.io.File;
import java.util.List;

import rx.Completable;
import rx.Observable;

public interface IDataService {

    /**
     * Gets list from getRequest.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    <M> Observable<List<M>> getList(GetRequest getListRequest);

    /**
     * Gets object from getRequest.
     *
     * @param getRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    <M> Observable<M> getObject(GetRequest getRequest);

    /**
     * Gets object from getRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    <M> Observable<M> patchObject(PostRequest postRequest);

    /**
     * Post Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    <M> Observable<M> postObject(PostRequest postRequest);

    /**
     * Post list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    <M> Observable<M> postList(PostRequest postRequest);

    /**
     * Put Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    <M> Observable<M> putObject(PostRequest postRequest);

    /**
     * Put list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    <M> Observable<M> putList(PostRequest postRequest);

    /**
     * Deletes item from postRequest.
     *
     * @param request contains the attributes of the request.
     * @return Observable with the list.
     */
    <M> Observable<M> deleteItemById(PostRequest request);

    /**
     * Deletes list from postRequest.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    <M> Observable<M> deleteCollectionByIds(PostRequest deleteRequest);

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
    <M> Observable<List<M>> queryDisk(RealmQueryProvider realmQueryProvider);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Observable<List>} with the data.
     */
    <M> Observable<List<M>> getListOffLineFirst(GetRequest getRequest);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Observable>} with the data.
     */
    <M> Observable<M> getObjectOffLineFirst(GetRequest getRequest);

    /**
     * Uploads a file to a url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Observable with the Object response.
     */
    <M> Observable<M> uploadFile(FileIORequest fileIORequest);

    /**
     * Downloads file from the give url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Observable with the ResponseBody
     */
    Observable<File> downloadFile(FileIORequest fileIORequest);
}
