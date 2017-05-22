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

import rx.Completable;

/**
 * @author Zeyad on 6/05/16.
 */
public class FileIO {
    private static final String TAG = FileIO.class.getSimpleName();
    private static int mTrailCount;
    @NonNull
    private final FirebaseJobDispatcher mDispatcher;
    private final FileIORequest mFileIORequest;
    private final CloudDataStore mCloudDataStore;
    private final Utils mUtils;
    private boolean mIsDownload;

    public FileIO(int trailCount, FileIORequest payLoad, Context context, boolean isDownload,
                  CloudDataStore cloudDataStore, Utils utils) {
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
        return mIsDownload ? mCloudDataStore.dynamicDownloadFile(mFileIORequest.getUrl(), file, mFileIORequest.onWifi(),
                mFileIORequest.isWhileCharging(), mFileIORequest.isQueuable())
                .doOnSubscribe(() -> Log.d(TAG, "Downloading " + file.getName()))
                .toCompletable()
                .doOnError(throwable -> {
                    queueIOFile();
                    throwable.printStackTrace();
                }) : mCloudDataStore.dynamicUploadFile(mFileIORequest.getUrl(), file, mFileIORequest.getKey(),
                mFileIORequest.getParameters(), mFileIORequest.onWifi(), mFileIORequest.isWhileCharging(),
                mFileIORequest.isQueuable(), mFileIORequest.getDataClass())
                .doOnSubscribe(() -> Log.d(TAG, "Uploading " + file.getName()))
                .toCompletable()
                .doOnError(throwable -> {
                    queueIOFile();
                    throwable.printStackTrace();
                });
    }

    void queueIOFile() {
        mTrailCount++;
        if (mTrailCount < 3) {
            mUtils.queueFileIOCore(mDispatcher, mIsDownload, mFileIORequest);
        }
    }
}
