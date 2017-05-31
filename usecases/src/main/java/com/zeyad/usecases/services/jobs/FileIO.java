package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.stores.CloudDataStore;
import com.zeyad.usecases.utils.Utils;

import java.io.File;

import io.reactivex.Completable;

/** @author Zeyad on 6/05/16. */
public class FileIO {
    private static final String TAG = FileIO.class.getSimpleName(), ON_ERROR = "onError";
    private static int mTrailCount;
    @NonNull private final FirebaseJobDispatcher mDispatcher;
    private final FileIORequest mFileIORequest;
    private final CloudDataStore mCloudDataStore;
    private final Utils mUtils;
    private final boolean mIsDownload;

    public FileIO(
            int trailCount,
            FileIORequest payLoad,
            Context context,
            boolean isDownload,
            CloudDataStore cloudDataStore,
            Utils utils) {
        mCloudDataStore = cloudDataStore;
        mTrailCount = trailCount;
        mFileIORequest = payLoad;
        mIsDownload = isDownload;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mUtils = utils;
    }

    @NonNull
    public Completable execute() {
        File file = mFileIORequest.getFile();
        return mIsDownload
                ? Completable.fromObservable(
                        mCloudDataStore
                                .dynamicDownloadFile(
                                        mFileIORequest.getUrl(),
                                        file,
                                        mFileIORequest.onWifi(),
                                        mFileIORequest.isWhileCharging(),
                                        mFileIORequest.isQueuable())
                                .doOnSubscribe(
                                        subscription -> Log.d(TAG, "Downloading " + file.getName()))
                                .doOnError(this::onError)
                                .toObservable())
                : Completable.fromObservable(
                        mCloudDataStore
                                .dynamicUploadFile(
                                        mFileIORequest.getUrl(),
                                        file,
                                        mFileIORequest.getKey(),
                                        mFileIORequest.getParameters(),
                                        mFileIORequest.onWifi(),
                                        mFileIORequest.isWhileCharging(),
                                        mFileIORequest.isQueuable(),
                                        mFileIORequest.getDataClass())
                                .doOnSubscribe(
                                        subscription -> Log.d(TAG, "Uploading " + file.getName()))
                                .doOnError(this::onError)
                                .toObservable());
    }

    private void onError(Throwable throwable) {
        queueIOFile();
        Log.e(TAG, ON_ERROR, throwable);
    }

    void queueIOFile() {
        mTrailCount++;
        if (mTrailCount < 3) {
            mUtils.queueFileIOCore(mDispatcher, mIsDownload, mFileIORequest);
        }
    }
}
