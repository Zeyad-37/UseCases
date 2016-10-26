package com.zeyad.genericusecase.data.services.jobs;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.requests.FileIORequest;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.data.utils.Utils;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

class FileIOTestRobot {


    private static final ResponseBody RESPONSE_BODY = Mockito.mock(ResponseBody.class);
    private static final InputStream INPUT_STREAM = Mockito.mock(InputStream.class);
    @Nullable
    private static final JobScheduler JOB_SCHEDULER;

    static {
        JOB_SCHEDULER = Utils.hasLollipop() ? Mockito.mock(JobScheduler.class) : null;
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    @NonNull
    static Class getValidDomainClass() {
        return TestViewModel.class;
    }

    static int getValidColumnId() {
        return 12;
    }

    @NonNull
    static HashMap<String, Object> getValidHashmap() {
        return new HashMap<>();
    }

    @NonNull
    static Class getPresentationClass() {
        return TestModel.class;
    }

    static String getValidColumnName() {
        return "id";
    }

    @NonNull
    static Class getValidDataClass() {
        return TestModel.class;
    }

    static FileIORequest createFileIoReq(boolean wifi, boolean isCharging, File file) {
        final FileIORequest fileIORequest = Mockito.mock(FileIORequest.class);
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

    static GcmNetworkManager getGcmNetworkManager() {
        return Mockito.mock(GcmNetworkManager.class);
    }

    @NonNull
    static FileIO createFileIO(Context mockedContext, RestApi mockedRestApi, int trailCount
            , FileIORequest fileIoReq, boolean toDownLoad, GcmNetworkManager mockedNetorkManager, boolean googlePlayServicesAvailable, boolean hasLollipop) {
        return new FileIO(mockedContext, mockedRestApi, trailCount
                , fileIoReq, toDownLoad, mockedNetorkManager, googlePlayServicesAvailable, hasLollipop);
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
        final Context context = Mockito.mock(Context.class);
        final Resources resources = Mockito.mock(Resources.class);
        final PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(context.getApplicationContext())
                .thenReturn(Mockito.mock(Context.class));
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(context.getSystemService(Context.STORAGE_SERVICE)).thenReturn(getMockedJobScheduler());
        return context;
    }

    @Nullable
    static JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    static RestApiImpl createRestApi() {
        final RestApiImpl restApi = Mockito.mock(RestApiImpl.class);
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
