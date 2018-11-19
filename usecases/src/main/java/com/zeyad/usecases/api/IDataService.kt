package com.zeyad.usecases.api

import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.File

interface IDataService {

    /**
     * Gets list from getRequest.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> getList(getListRequest: GetRequest): Flowable<List<M>>

    /**
     * Gets object from getRequest.
     *
     * @param getRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> getObject(getRequest: GetRequest): Flowable<M>

    /**
     * Gets object from getRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> patchObject(postRequest: PostRequest): Single<M>

    /**
     * Post Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Single with the Object.
     */
    fun <M> postObject(postRequest: PostRequest): Single<M>

    /**
     * Post list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Single with the list.
     */
    fun <M> postList(postRequest: PostRequest): Single<M>

    /**
     * Put Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Single with the Object.
     */
    fun <M> putObject(postRequest: PostRequest): Single<M>

    /**
     * Put list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Single with the list.
     */
    fun <M> putList(postRequest: PostRequest): Single<M>

    /**
     * Deletes item from postRequest.
     *
     * @param request contains the attributes of the request.
     * @return Single with the list.
     */
    fun <M> deleteItemById(request: PostRequest): Single<M>

    /**
     * Deletes list from postRequest.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Single with the list.
     */
    fun <M> deleteCollectionByIds(deleteRequest: PostRequest): Single<M>

    /**
     * Deletes All.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Completable with the list.
     */
    fun deleteAll(deleteRequest: PostRequest): Single<Boolean>

    /**
     * Get list of items according to the query passed.
     *
     * @param realmQueryProvider query tp select list of item(s).
     * @return
     */
    fun <M> queryDisk(query: String, clazz: Class<M>): Single<List<M>>

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return [<] with the data.
     */
    fun <M> getListOffLineFirst(getRequest: GetRequest): Flowable<List<M>>

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return [&gt;][Single] with the data.
     */
    fun <M> getObjectOffLineFirst(getRequest: GetRequest): Flowable<M>

    /**
     * Uploads a file to a url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Single with the Object response.
     */
    fun <M> uploadFile(fileIORequest: FileIORequest): Single<M>

    /**
     * Downloads file from the give url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Single with the ResponseBody
     */
    fun downloadFile(fileIORequest: FileIORequest): Single<File>
}
