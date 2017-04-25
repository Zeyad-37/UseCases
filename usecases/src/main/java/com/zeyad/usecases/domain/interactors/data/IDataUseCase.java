package com.zeyad.usecases.domain.interactors.data;

import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;

import java.util.List;

import rx.Observable;

public interface IDataUseCase {

    /**
     * Gets list from getRequest.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<List> getList(GetRequest getListRequest);

    /**
     * Gets object from getRequest.
     *
     * @param getRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable getObject(GetRequest getRequest);

    /**
     * Gets object from getRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable patchObject(PostRequest postRequest);

    /**
     * Post Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable postObject(PostRequest postRequest);

    /**
     * Post list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<?> postList(PostRequest postRequest);

    /**
     * Put Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable putObject(PostRequest postRequest);

    /**
     * Put list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable putList(PostRequest postRequest);

    /**
     * Deletes list from postRequest.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable deleteCollection(PostRequest deleteRequest);

    /**
     * Deletes All.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<Boolean> deleteAll(PostRequest deleteRequest);

    /**
     * Get list of items according to the query passed.
     *
     * @param getRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<List> queryDisk(GetRequest getRequest);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Observable<List>} with the data.
     */
    Observable<List> getListOffLineFirst(GetRequest getRequest);

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return {@link Observable>} with the data.
     */
    Observable getObjectOffLineFirst(GetRequest getRequest);
}
