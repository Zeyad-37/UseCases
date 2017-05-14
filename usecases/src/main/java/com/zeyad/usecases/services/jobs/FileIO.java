package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.utils.Utils;

import java.io.File;
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

/**
 * @author Zeyad on 6/05/16.
 */
public class FileIO {
    private static final String TAG = FileIO.class.getSimpleName();
    private static int mTrailCount;
    private final FirebaseJobDispatcher mDispatcher;
    private final FileIORequest mFileIORequest;
    private final ApiConnection mRestApi;
    private final Utils mUtils;
    private boolean mIsDownload;

    public FileIO(int trailCount, FileIORequest payLoad, Context context, boolean isDownload, ApiConnection restApi,
                  Utils utils) {
        mRestApi = restApi;
        mTrailCount = trailCount;
        mFileIORequest = payLoad;
        mIsDownload = isDownload;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mUtils = utils;
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
        File file = mFileIORequest.getFile();
        if (mIsDownload) {
            if (!file.exists())
                file.mkdir();
            return mRestApi.dynamicDownload(mFileIORequest.getUrl())
                    .doOnSubscribe(() -> Log.d(TAG, "Downloading " + file.getName()))
                    .subscribe(responseBody -> {
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        try {
                            byte[] fileReader = new byte[4096];
                            long fileSize = responseBody.contentLength();
                            long fileSizeDownloaded = 0;
                            outputStream = new FileOutputStream(file);
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
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse(getMimeType(file.getAbsolutePath())),
                    file);
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(mFileIORequest.getKey(), requestFile);
            if (mFileIORequest.getParameters() != null && !mFileIORequest.getParameters().isEmpty())
                for (Map.Entry<String, Object> entry : mFileIORequest.getParameters().entrySet())
                    map.put(entry.getKey(), Utils.getInstance().createPartFromString(entry.getValue()));
            return mRestApi.dynamicUpload(mFileIORequest.getUrl(), map, MultipartBody.Part
                    .createFormData(mFileIORequest.getKey(), file.getName(), requestFile))
                    .doOnSubscribe(() -> Log.d(TAG, "Uploading " + file.getName()))
                    .subscribe(o -> {
                    }, throwable -> queueIOFile());
        }
    }

    void queueIOFile() {
        mTrailCount++;
        if (mTrailCount < 3) {
            mUtils.queueFileIOCore(mDispatcher, mIsDownload, mFileIORequest);
        }
    }
}
