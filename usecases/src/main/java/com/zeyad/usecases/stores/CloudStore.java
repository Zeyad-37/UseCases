package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.exceptions.NetworkConnectionException;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.network.RestApi;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CloudStore implements DataStore {

    private static final String APPLICATION_JSON = "application/json";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String TAG = CloudStore.class.getSimpleName();
    private final DataBaseManager mDataBaseManager;
    @NonNull
    private final DAOMapper mEntityDataMapper;
    private final ApiConnection mApiConnection;
    @NonNull
    private final Utils mUtils;
    private final MemoryStore mMemoryStore;

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param apiConnection   The {@link RestApi} implementation to use.
     * @param dataBaseManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    CloudStore(ApiConnection apiConnection, DataBaseManager dataBaseManager,
               @NonNull DAOMapper entityDataMapper, MemoryStore memoryStore, @NonNull Utils utils) {
        mApiConnection = apiConnection;
        mEntityDataMapper = entityDataMapper;
        mDataBaseManager = dataBaseManager;
        mMemoryStore = memoryStore;
        mUtils = utils;
        Config.setCloudStore(this);
    }

    private <M> Flowable<M> getErrorFlowableNotPersisted() {
        return Flowable.error(new NetworkConnectionException("Could not reach server and could not saveToDisk to queue!"));
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> dynamicGetList(String url, String idColumnName,
                                                @NonNull Class dataClass, boolean saveToDisk,
                                                boolean shouldCache) {
        return mApiConnection.dynamicGetList(url, shouldCache)
                .map(entities -> mEntityDataMapper.<List<M>>mapAllTo(entities, dataClass))
                .doOnNext(list -> {
                    if (mUtils.withDisk(saveToDisk)) {
                        saveAllToDisk(list, dataClass);
                        saveAllToMemory(idColumnName, new JSONArray(gson.toJson(list)), dataClass);
                    }
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicGetObject(String url, String idColumnName, Object itemId, Class itemIdType,
                                            @NonNull Class dataClass, boolean saveToDisk, boolean shouldCache) {
        return mApiConnection.<M>dynamicGetObject(url, shouldCache)
                .doOnNext(m -> saveLocally(idColumnName, itemIdType,
                        new JSONObject(gson.toJson(m)), dataClass, saveToDisk, shouldCache))
                .map(entity -> mEntityDataMapper.<M>mapTo(entity, dataClass));
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return Flowable.error(new IllegalAccessException("Can not search disk in cloud data store!"));
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPatchObject(String url, String idColumnName, Class itemIdType,
                                              @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                              Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            saveLocally(idColumnName, itemIdType, jsonObject, dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPatch(url,
                    RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPostObject(String url, String idColumnName, Class itemIdType,
                                             @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                             Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            saveLocally(idColumnName, itemIdType, jsonObject, dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPostList(String url, String idColumnName, Class itemIdType,
                                           @NonNull JSONArray jsonArray, @NonNull Class dataClass,
                                           Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            saveAllLocally(idColumnName, itemIdType, jsonArray, dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPutObject(String url, String idColumnName, Class itemIdType,
                                            @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                            Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            saveLocally(idColumnName, itemIdType, jsonObject, dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPutList(String url, String idColumnName, Class itemIdType,
                                          @NonNull JSONArray jsonArray, @NonNull Class dataClass,
                                          Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            saveAllLocally(idColumnName, itemIdType, jsonArray, dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicDeleteCollection(String url, String idColumnName, Class itemIdType,
                                                   @NonNull JSONArray jsonArray, @NonNull Class dataClass,
                                                   Class responseType, boolean saveToDisk, boolean cache) {
        return Flowable.defer(() -> {
            deleteLocally(mUtils.convertToListOfId(jsonArray, itemIdType), idColumnName, itemIdType,
                    dataClass, saveToDisk, cache);
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicDelete(url)
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public Single<Boolean> dynamicDeleteAll(Class dataClass) {
        return Single.error(new IllegalStateException("Can not delete all from cloud data store!"));
    }

    @NonNull
    @Override
    public Flowable<File> dynamicDownloadFile(String url, @NonNull File file) {
        return Flowable.defer(() -> {
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.dynamicDownload(url)
                    .map(responseBody -> {
                        try {
                            InputStream inputStream = null;
                            OutputStream outputStream = null;
                            try {
                                byte[] fileReader = new byte[4096];
                                long fileSize = responseBody.contentLength();
                                long fileSizeDownloaded = 0;
                                inputStream = responseBody.byteStream();
                                outputStream = new FileOutputStream(file);
                                while (true) {
                                    int read = inputStream.read(fileReader);
                                    if (read == -1) {
                                        break;
                                    }
                                    outputStream.write(fileReader, 0, read);
                                    fileSizeDownloaded += read;
                                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                                }
                                outputStream.flush();
                            } catch (IOException e) {
                                Log.e(TAG, "", e);
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "", e);
                        }
                        return file;
                    })
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends File>>) Flowable::error);
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicUploadFile(String url, @NonNull HashMap<String, File> keyFileMap,
                                             @Nullable HashMap<String, Object> parameters,
                                             @NonNull Class responseType) {
        return Flowable.defer(() -> {
            List<MultipartBody.Part> multiPartBodyParts = new ArrayList<>();
            for (Map.Entry<String, File> stringFileEntry : keyFileMap.entrySet()) {
                multiPartBodyParts.add(MultipartBody.Part.createFormData(stringFileEntry.getKey(),
                        stringFileEntry.getValue().getName(), RequestBody.create(MediaType.parse
                                (MULTIPART_FORM_DATA), stringFileEntry.getValue())));
            }
            if (!mUtils.isNetworkAvailable(Config.getInstance().getContext())) {
                return getErrorFlowableNotPersisted();
            }
            HashMap<String, RequestBody> map = new HashMap<>();
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    map.put(entry.getKey(), RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA),
                            String.valueOf(entry.getValue())));
                }
            }
            return mApiConnection.<M>dynamicUpload(url, map, multiPartBodyParts)
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext((Function<Throwable, Publisher<? extends M>>) Flowable::error);
        });
    }

    //    private <M> FlowableTransformer<M, M> applyExponentialBackoff() {
    //        return observable -> observable.retryWhen(attempts -> {
    //            return attempts.zipWith(
    //                    Flowable.range(COUNTER_START, ATTEMPTS), (n, i) -> i)
    //                    .flatMap(i -> {
    //                        Log.d(TAG, "delay retry by " + i + " second(s)");
    //                        return Flowable.timer(5 * i, TimeUnit.SECONDS);
    //                    });
    //        });
    //    }

    @Nullable
    private <M> M daoMapHelper(@NonNull Class dataClass, M object) {
        return object instanceof List ?
                mEntityDataMapper.mapAllTo((List) object, dataClass) :
                mEntityDataMapper.mapTo(object, dataClass);
    }

    private void saveAllToDisk(List collection, Class dataClass) {
        mDataBaseManager.putAll(collection, dataClass)
                .subscribeOn(Config.getBackgroundThread())
                .subscribe(new SimpleSubscriber(dataClass));
    }

    private void saveAllToMemory(String idColumnName, JSONArray jsonArray, Class dataClass) {
        mMemoryStore.cacheList(idColumnName, jsonArray, dataClass);
    }

    private void saveLocally(String idColumnName, Class itemIdType, @NonNull JSONObject jsonObject,
                             @NonNull Class dataClass, boolean saveToDisk, boolean cache) {
        if (mUtils.withDisk(saveToDisk)) {
            mDataBaseManager.put(jsonObject, idColumnName, itemIdType, dataClass)
                    .subscribeOn(Config.getBackgroundThread())
                    .subscribe(new SimpleSubscriber(dataClass));
        }
        if (mUtils.withCache(cache)) {
            mMemoryStore.cacheObject(idColumnName, jsonObject, dataClass);
        }
    }

    private void saveAllLocally(String idColumnName, Class itemIdType, @NonNull JSONArray jsonArray,
                                @NonNull Class dataClass, boolean saveToDisk, boolean cache) {
        if (mUtils.withDisk(saveToDisk)) {
            mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, dataClass)
                    .subscribeOn(Config.getBackgroundThread())
                    .subscribe(new SimpleSubscriber(dataClass));
        }
        if (mUtils.withCache(cache)) {
            mMemoryStore.cacheList(idColumnName, jsonArray, dataClass);
        }
    }

    private void deleteLocally(List<Object> ids, String idColumnName, Class itemIdType, Class dataClass,
                               boolean saveToDisk, boolean cache) {
        if (mUtils.withDisk(saveToDisk)) {
            int collectionSize = ids.size();
            for (int i = 0; i < collectionSize; i++) {
                mDataBaseManager.evictById(dataClass, idColumnName, ids.get(i), itemIdType);
            }
        }
        if (mUtils.withCache(cache)) {
            List<String> stringIds = Flowable.fromIterable(ids)
                    .map(String::valueOf)
                    .toList(ids.size())
                    .blockingGet();
            mMemoryStore.deleteList(stringIds, dataClass);
        }
    }

    private static class SimpleSubscriber implements SingleObserver {
        private final Class mClass;
        private Disposable subscription;

        SimpleSubscriber(Class aClass) {
            mClass = aClass;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            subscription = d;
        }

        @Override
        public void onSuccess(Object o) {
            subscription.dispose();
            Log.d(TAG, mClass.getSimpleName() + " persisted!");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "", e);
            subscription.dispose();
        }
    }
}
