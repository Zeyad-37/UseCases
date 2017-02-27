package com.zeyad.usecases.data.services.jobs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.rule.BuildConfig;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

import static com.zeyad.usecases.data.services.GenericJobService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericJobService.JOB_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileIOJUnitTest {
    // ---- //
    private final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private final InputStream INPUT_STREAM = mock(InputStream.class);
    @Nullable
    private final FirebaseJobDispatcher JOB_SCHEDULER;
    Context mockContext;

    {
        JOB_SCHEDULER = new FirebaseJobDispatcher(new GooglePlayDriver(mock(Context.class)));
    }

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {
        clearAll();
    }

    @Test
    public void testExecute_ifFileIsDownloaded_whenFileIsToBeDownloadedAndFileDoesNotExist() throws PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.execute();
        verify(restApi).dynamicDownload(eq(getValidUrl()));
    }

    @Test
    public void testQueueIoFile_ifTrailCountIncrements_whenFileIsToBeDownloadedAndFileDoesNotExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        assertThat(fileIO.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsScheduled_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager(mockContext);
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(gcmNetworkManager).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToGCMNetworkManager_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, false, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager(mockContext);
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
//        ArgumentCaptor<OneoffTask> peopleCaptor = ArgumentCaptor.forClass(OneoffTask.class);
//        Mockito.verify(gcmNetworkManager).schedule(peopleCaptor.capture());
//        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
//        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
//        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
//        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
//        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
//        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericGCMService.class.getName()));
//        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(fileIOReq.onWifi() ? NETWORK_STATE_UNMETERED : NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifJobSchedulerIsInvoked_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(getMockedJobScheduler()).schedule(Mockito.any(Job.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToJobScheduler_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(getMockedJobScheduler()).schedule(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue().getService().getClassName(), is(equalTo(GenericJobService.class.getName())));
//        assertThat(argumentCaptor.getValue().isRequireCharging(), is(fileIOReq.isWhileCharging()));
//        assertThat(argumentCaptor.getValue().isPersisted(), is(true));
//        assertThat(argumentCaptor.getValue().getNetworkType(), is(fileIOReq.onWifi() ? NETWORK_TYPE_UNMETERED : NETWORK_TYPE_ANY));
        assertThat(argumentCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(argumentCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsNotScheduled_whenGooglePlayServicesAreNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager(mockContext);
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 2
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(gcmNetworkManager, times(0)).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testExecute_ifFileIsNotDownloaded_whenFileIsToBeDownloadedAndFileDoesExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                createFileIoReq(true, true, createFileWhichDoesExist());
        final RestApiImpl restApi = createRestApi();
        FileIO fileIO = createFileIO(createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.execute();
        verify(restApi, times(0)).dynamicDownload(anyString());
    }

    String getValidUrl() {
        return "http://www.google.com";
    }


    FileIORequest createFileIoReq(boolean wifi, boolean isCharging, File file) {
        final FileIORequest fileIORequest = mock(FileIORequest.class);
        Mockito.when(fileIORequest.getDataClass()).thenReturn(TestRealmModel.class);
        Mockito.when(fileIORequest.getPresentationClass()).thenReturn(Object.class);
        Mockito.when(fileIORequest.getUrl()).thenReturn(getValidUrl());
        Mockito.when(fileIORequest.getFile()).thenReturn(file);
        Mockito.when(fileIORequest.isWhileCharging()).thenReturn(isCharging);
        Mockito.when(fileIORequest.onWifi()).thenReturn(wifi);
        return fileIORequest;
    }

    @NonNull
    File getValidFile() {
        return new File(Environment.getExternalStorageDirectory(), "someFile.txt");
    }

    FirebaseJobDispatcher getGcmNetworkManager(Context context) {
        return new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    @NonNull
    FileIO createFileIO(Context mockedContext, RestApi mockedRestApi, int trailCount
            , FileIORequest fileIoReq, boolean toDownLoad) {
        return new FileIO(mockedContext, mockedRestApi, trailCount, fileIoReq, toDownLoad);
    }

    @NonNull
    File createFileWhichDoesNotExist() {
        getValidFile().delete();
        return getValidFile();
    }

    @NonNull
    File createFileWhichDoesExist() throws IOException {
        getValidFile().delete();
        getValidFile().createNewFile();
        return getValidFile();
    }

    public Context createMockedContext() throws PackageManager.NameNotFoundException {
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
    FirebaseJobDispatcher getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    RestApiImpl createRestApi() {
        final RestApiImpl restApi = mock(RestApiImpl.class);
        Mockito.when(restApi.dynamicDownload(Mockito.anyString())).thenReturn(getResponseBodyObservable());
        return restApi;
    }

    Observable<ResponseBody> getResponseBodyObservable() {
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

    ResponseBody getResponseBody() throws IOException {
        Mockito.when(RESPONSE_BODY.byteStream()).thenReturn(getInputSreamReader());
        Mockito.when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    InputStream getInputSreamReader() throws IOException {
        Mockito.when(INPUT_STREAM.read(Mockito.any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }

    void clearAll() {
        Mockito.reset(RESPONSE_BODY, INPUT_STREAM, JOB_SCHEDULER);
    }
}
