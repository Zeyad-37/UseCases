package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;

/**
 * @author Zeyad on 6/05/16.
 */
public class FileIO {
    private static final String TAG = FileIO.class.getSimpleName();
    private static int mTrailCount;
    private final FirebaseJobDispatcher mDispatcher;
    private final FileIORequest mFileIORequest;
    private final Context mContext;
    private final RestApi mRestApi;
    private final Gson gson;
    private boolean mIsDownload;

    public FileIO(@NonNull Intent intent, @NonNull Context context, boolean isDownload) {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        mRestApi = new RestApiImpl();
        mContext = context;
        mTrailCount = intent.getIntExtra(TRIAL_COUNT, 0);
        mFileIORequest = new Gson().fromJson(intent.getStringExtra(PAYLOAD), FileIORequest.class);
        mIsDownload = isDownload;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    /**
     * This constructor meant to be used in testing and restricted environments only. Use public constructors instead.
     */
    FileIO(Context context, RestApi restApi, int trailCount, FileIORequest fileIORequest, boolean isDownload) {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        mContext = context;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mFileIORequest = fileIORequest;
        mIsDownload = isDownload;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    @Nullable
    private static String getMimeType(String uri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        if (extension != null)
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return type;
    }

    public Subscription execute() {
        if (mIsDownload) {
            if (!mFileIORequest.getFile().exists()) {
                return mRestApi.dynamicDownload(mFileIORequest.getUrl()).subscribe(responseBody -> {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        byte[] fileReader = new byte[4096];
                        long fileSize = responseBody.contentLength();
                        long fileSizeDownloaded = 0;
                        outputStream = new FileOutputStream(mFileIORequest.getFile());
                        inputStream = responseBody.byteStream();
                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1)
                                break;
                            outputStream.write(fileReader, 0, read);
                            fileSizeDownloaded += read;
                            Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null)
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (outputStream != null)
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }, throwable -> {
                    queueIOFile();
                    throwable.printStackTrace();
                });
            }
            return Subscriptions.empty();
        } else {
            RequestBody requestFile = RequestBody.create(MediaType
                    .parse(getMimeType(mFileIORequest.getFile()
                            .getAbsolutePath())), mFileIORequest.getFile());
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(mFileIORequest.getKey(), requestFile);
            if (mFileIORequest.getParameters() != null && !mFileIORequest.getParameters().isEmpty())
                for (Map.Entry<String, Object> entry : mFileIORequest.getParameters().entrySet())
                    map.put(entry.getKey(), Utils.createPartFromString(entry.getValue()));
            return mRestApi.upload(mFileIORequest.getUrl(), map, MultipartBody.Part
                    .createFormData(mFileIORequest.getKey(), mFileIORequest.getFile().getName(), requestFile))
                    .subscribe(o -> {
                    }, throwable -> queueIOFile());
        }
    }

    void queueIOFile() {
        mTrailCount++;
        if (mTrailCount < 3) {
            Utils.queueFileIOCore(mDispatcher, mIsDownload, mFileIORequest, gson);
        }
    }

    int getTrailCount() {
        return mTrailCount;
    }
}
