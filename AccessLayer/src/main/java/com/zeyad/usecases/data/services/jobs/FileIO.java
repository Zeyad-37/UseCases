package com.zeyad.usecases.data.services.jobs;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.usecases.R;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.repository.stores.CloudDataStore;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.services.GenericGCMService;
import com.zeyad.usecases.data.services.GenericJobService;
import com.zeyad.usecases.data.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static android.app.job.JobInfo.NETWORK_TYPE_UNMETERED;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_UNMETERED;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

/**
 * @author Zeyad on 6/05/16.
 */
public class FileIO {
    private static final String TAG = com.zeyad.usecases.data.services.jobs.FileIO.class.getSimpleName();
    private final Context mContext;
    private final RestApi mRestApi;
    private int mTrailCount;
    private FileIORequest mFileIORequest;
    private boolean mIsDownload;
    private GcmNetworkManager mGcmNetworkManager;
    private boolean mGooglePlayServicesAvailable;

    public FileIO(@NonNull Intent intent, @NonNull Context context, boolean isDownload) {
        mRestApi = new RestApiImpl();
        mContext = context;
        mTrailCount = intent.getIntExtra(TRIAL_COUNT, 0);
        mFileIORequest = new Gson().fromJson(intent.getStringExtra(PAYLOAD), FileIORequest.class);
        mIsDownload = isDownload;
        mGcmNetworkManager = GcmNetworkManager.getInstance(mContext);
        mGooglePlayServicesAvailable = Utils.isGooglePlayServicesAvailable(mContext);
    }

    /**
     * This constructor meant to be used in testing and restricted environments only. Use public constructors instead.
     */
    FileIO(Context context, RestApi restApi, int trailCount, FileIORequest fileIORequest, boolean isDownload,
           GcmNetworkManager gcmNetworkManager, boolean googlePlayServicesAvailable, boolean hasLollipop) {
        mContext = context;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mFileIORequest = fileIORequest;
        mIsDownload = isDownload;
        mGcmNetworkManager = gcmNetworkManager;
        mGooglePlayServicesAvailable = googlePlayServicesAvailable;
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
            if (mGooglePlayServicesAvailable) {
                Bundle extras = new Bundle();
                extras.putString(JOB_TYPE, mIsDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
                extras.putString(PAYLOAD, new Gson().toJson(mFileIORequest));
                mGcmNetworkManager.schedule(new OneoffTask.Builder()
                        .setService(GenericGCMService.class)
                        .setRequiredNetwork(mFileIORequest.onWifi() ? NETWORK_STATE_UNMETERED : NETWORK_STATE_CONNECTED)
                        .setRequiresCharging(mFileIORequest.isWhileCharging())
                        .setUpdateCurrent(false)
                        .setPersisted(true)
                        .setExtras(extras)
                        .setTag(CloudDataStore.FILE_IO_TAG)
                        .setExecutionWindow(0, 30)
                        .build());
                Log.d(TAG, mContext.getString(R.string.requeued, "GcmNetworkManager", "true"));
            } else {
                if (Utils.hasLollipop()) {
                    PersistableBundle persistableBundle = new PersistableBundle();
                    persistableBundle.putString(JOB_TYPE, mIsDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
                    persistableBundle.putString(PAYLOAD, new Gson().toJson(mFileIORequest));
                    boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1,
                            new ComponentName(mContext, GenericJobService.class))
                            .setRequiredNetworkType(mFileIORequest.onWifi() ? NETWORK_TYPE_UNMETERED : NETWORK_TYPE_ANY)
                            .setRequiresCharging(mFileIORequest.isWhileCharging())
                            .setPersisted(true)
                            .setExtras(persistableBundle)
                            .build());
                    Log.d(TAG, mContext.getString(R.string.requeued, "JobScheduler", String.valueOf(isScheduled)));
                }
            }
        }
    }

    int getTrailCount() {
        return mTrailCount;
    }
}
