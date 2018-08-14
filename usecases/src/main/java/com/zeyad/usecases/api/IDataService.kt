package com.zeyad.usecases.api

import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.RealmModel
import java.io.File

interface IDataService {

    /**
     * Gets list from getRequest.
     *
     * @param getListRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> getList(getListRequest: GetRequest<M>): Flowable<List<M>>

    /**
     * Gets object from getRequest.
     *
     * @param getRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> getObject(getRequest: GetRequest<M>): Flowable<M>

    /**
     * Gets object from getRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> patchObject(postRequest: PostRequest): Flowable<M>

    /**
     * Post Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> postObject(postRequest: PostRequest): Flowable<M>

    /**
     * Post list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> postList(postRequest: PostRequest): Flowable<M>

    /**
     * Put Object to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the Object.
     */
    fun <M> putObject(postRequest: PostRequest): Flowable<M>

    /**
     * Put list to postRequest.
     *
     * @param postRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> putList(postRequest: PostRequest): Flowable<M>

    /**
     * Deletes item from postRequest.
     *
     * @param request contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> deleteItemById(request: PostRequest): Flowable<M>

    /**
     * Deletes list from postRequest.
     *
     * @param deleteRequest contains the attributes of the request.
     * @return Flowable with the list.
     */
    fun <M> deleteCollectionByIds(deleteRequest: PostRequest): Flowable<M>

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
    fun <M : RealmModel> queryDisk(realmQueryProvider: RealmQueryProvider<M>): Flowable<List<M>>

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return [<] with the data.
     */
    fun <M> getListOffLineFirst(getRequest: GetRequest<M>): Flowable<List<M>>

    /**
     * Creates a repository pattern with live objects
     *
     * @param getRequest contains the attributes of the request.
     * @return [&gt;][Flowable] with the data.
     */
    fun <M> getObjectOffLineFirst(getRequest: GetRequest<M>): Flowable<M>

    /**
     * Uploads a file to a url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Flowable with the Object response.
     */
    fun <M> uploadFile(fileIORequest: FileIORequest): Flowable<M>

    /**
     * Downloads file from the give url.
     *
     * @param fileIORequest contains the attributes of the request,
     * @return Flowable with the ResponseBody
     */
    fun downloadFile(fileIORequest: FileIORequest): Flowable<File>
}
