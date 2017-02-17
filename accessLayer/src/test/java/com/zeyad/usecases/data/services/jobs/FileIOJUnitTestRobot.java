package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.utils.TestRealmModel;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

import static org.mockito.Mockito.mock;

class FileIOJUnitTestRobot {

    private static final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private static final InputStream INPUT_STREAM = mock(InputStream.class);
    @Nullable
    private static final FirebaseJobDispatcher JOB_SCHEDULER;

    static {
        JOB_SCHEDULER = new FirebaseJobDispatcher(new GooglePlayDriver(mock(Context.class)));
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    @NonNull
    static Class getPresentationClass() {
        return TestRealmModel.class;
    }


    @NonNull
    static Class getValidDataClass() {
        return TestRealmModel.class;
    }

    static FileIORequest createFileIoReq(boolean wifi, boolean isCharging, File file) {
        final FileIORequest fileIORequest = mock(FileIORequest.class);
        Mockito.when(fileIORequest.getDataClass()).thenReturn(getValidDataClass());
        Mockito.when(fileIORequest.getPresentationClass()).thenReturn(getPresentationClass());
        Mockito.when(fileIORequest.getUrl()).thenReturn(getValidUrl());
        Mockito.when(fileIORequest.getFile()).thenReturn(file);
        Mockito.when(fileIORequest.isWhileCharging()).thenReturn(isCharging);
        Mockito.when(fileIORequest.onWifi()).thenReturn(wifi);
        return fileIORequest;
    }

    @NonNull
    static File getValidFile() {
        return new File(Environment.getExternalStorageDirectory(), "someFile.txt");
    }

    static FirebaseJobDispatcher getGcmNetworkManager(Context context) {
        return new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    @NonNull
    static FileIO createFileIO(Context mockedContext, RestApi mockedRestApi, int trailCount
            , FileIORequest fileIoReq, boolean toDownLoad) {
        return new FileIO(mockedContext, mockedRestApi, trailCount, fileIoReq, toDownLoad);
    }

    @NonNull
    static File createFileWhichDoesNotExist() {
        getValidFile().delete();
        return getValidFile();
    }

    @NonNull
    static File createFileWhichDoesExist() throws IOException {
        getValidFile().delete();
        getValidFile().createNewFile();
        return getValidFile();
    }

    public static Context createMockedContext() throws PackageManager.NameNotFoundException {
        final Context context = mock(Context.class);
        final Resources resources = mock(Resources.class);
        final PackageManager packageManager = mock(PackageManager.class);
        Mockito.when(context.getApplicationContext())
                .thenReturn(mock(Context.class));
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(context.getSystemService(Context.STORAGE_SERVICE)).thenReturn(getMockedJobScheduler());
        return context;
    }

    @Nullable
    static FirebaseJobDispatcher getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    static RestApiImpl createRestApi() {
        final RestApiImpl restApi = mock(RestApiImpl.class);
        Mockito.when(restApi.dynamicDownload(Mockito.anyString())).thenReturn(getResponseBodyObservable());
        return restApi;
    }

    static Observable<ResponseBody> getResponseBodyObservable() {
        return Observable.create(new Observable.OnSubscribe<ResponseBody>() {
            @Override
            public void call(@NonNull Subscriber<? super ResponseBody> subscriber) {
                try {
                    subscriber.onNext(getResponseBody());
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static ResponseBody getResponseBody() throws IOException {
        Mockito.when(RESPONSE_BODY.byteStream()).thenReturn(getInputSreamReader());
        Mockito.when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    static InputStream getInputSreamReader() throws IOException {
        Mockito.when(INPUT_STREAM.read(Mockito.any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }

    static void clearAll() {
        Mockito.reset(RESPONSE_BODY, INPUT_STREAM, JOB_SCHEDULER);
    }
}
