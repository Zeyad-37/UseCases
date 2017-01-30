package com.zeyad.usecases.domain.interactors.data;

import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;

import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public interface IDataUseCase {
    /**
     * Gets list from full url.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<List> getList(GetRequest getListRequest);

    /**
     * Gets object from full url.
     *
     * @param getRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable getObject(GetRequest getRequest);

    /**
     * Post Object to full url.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable postObject(PostRequest postRequest);

    /**
     * Post list to full url.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable<?> postList(PostRequest postRequest);

    /**
     * Put Object to full url.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the Object.
     */
    Observable putObject(PostRequest postRequest);

    /**
     * Put list to full url.
     *
     * @param postRequest contains the attributes of the request.
     * @return Observable with the list.
     */
    Observable putList(PostRequest postRequest);

    /**
     * Deletes list from full url.
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
     * @param column            The key used to look for inside the DB.
     * @param query             The query used to look for inside the DB.
     * @param dataClass         Class type of the items to be deleted.
     * @param presentationClass Class type of the items to be returned.
     * @return
     */
    Observable searchDisk(String query, String column, Class presentationClass, Class dataClass);

    /**
     * Get list of items according to the query passed.
     *
     * @param getRequest contains the attributes of the request.
     * @return
     */
    Observable searchDisk(GetRequest getRequest);

    BehaviorSubject getLastObject();

    BehaviorSubject<List> getLastList();

    Observable<List> getListOffLineFirst(GetRequest getRequest);

    Observable getObjectOffLineFirst(GetRequest getRequest);
}
