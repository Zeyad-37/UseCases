package com.zeyad.genericusecase.domain.interactors.generic;

import com.zeyad.genericusecase.data.requests.FileIORequest;
import com.zeyad.genericusecase.data.requests.GetRequest;
import com.zeyad.genericusecase.data.requests.PostRequest;

import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

public interface IGenericUseCase {
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
     * @param realmQuery        The query used to look for inside the DB.
     * @param presentationClass Class type of the items to be returned.
     * @return
     */
    Observable searchDisk(RealmQuery realmQuery, Class presentationClass);

    /**
     * Uploads a file to a url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Observable with the Object response.
     */
    Observable uploadFile(FileIORequest fileIORequest);

    /**
     * Downloads file from the give url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Observable with the ResponseBody
     */
    Observable downloadFile(FileIORequest fileIORequest);
}
