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
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.utils.Utils;

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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileIOJUnitTest {
    private final ResponseBody responseBody = mock(ResponseBody.class);
    private final InputStream inputStream = mock(InputStream.class);
    private FirebaseJobDispatcher firebaseJobDispatcher;
    private Context mockContext;
    private Utils utils;
    // item under test
    private FileIO fileIO;

    @Before
    public void setUp() throws Exception {
        firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mock(Context.class)));
        mockContext = mock(Context.class);
        utils = mock(Utils.class);
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(responseBody, inputStream);
    }

    @Test
    public void testExecute_ifFileIsDownloaded_whenFileIsToBeDownloadedAndFileDoesNotExist() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesNotExist());
        final RestApiImpl restApi = createRestApi();
        fileIO = createFileIO(fileIOReq);
        fileIO.execute();
        verify(restApi).dynamicDownload(eq(getValidUrl()));
    }

    @Test
    public void testQueueIoFile_ifTrailCountIncrements_whenFileIsToBeDownloadedAndFileDoesNotExist() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        fileIO.queueIOFile();
        assertThat(fileIO.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsScheduled_whenGooglePlayServicesAreAvailable() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
    }

    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToGCMNetworkManager_whenGooglePlayServicesAreAvailable() {
        FileIORequest fileIOReq =
                mockFileIoReq(true, false, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifJobSchedulerIsInvoked_whenGooglePlayServicesAreAvailable() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToJobScheduler_whenGooglePlayServicesAreAvailable() {
        FileIORequest fileIOReq =
                mockFileIoReq(true, true, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(getMockedJobScheduler()).schedule(argumentCaptor.capture());
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsNotScheduled_whenGooglePlayServicesAreNotAvailable() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesNotExist());
        fileIO = createFileIO(fileIOReq);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
    }

    @Test
    public void testExecute_ifFileIsNotDownloaded_whenFileIsToBeDownloadedAndFileDoesExist() throws IOException {
        FileIORequest fileIOReq = mockFileIoReq(true, true, createFileWhichDoesExist());
        final RestApiImpl restApi = createRestApi();
        fileIO = createFileIO(fileIOReq);
        fileIO.execute();
        verify(restApi, times(0)).dynamicDownload(anyString());
    }

    private String getValidUrl() {
        return "http://www.google.com";
    }

    private FileIORequest mockFileIoReq(boolean wifi, boolean isCharging, File file) {
        final FileIORequest fileIORequest = mock(FileIORequest.class);
        Mockito.when(fileIORequest.getDataClass()).thenReturn(TestRealmModel.class);
        Mockito.when(fileIORequest.getUrl()).thenReturn(getValidUrl());
        Mockito.when(fileIORequest.getFile()).thenReturn(file);
        Mockito.when(fileIORequest.isWhileCharging()).thenReturn(isCharging);
        Mockito.when(fileIORequest.onWifi()).thenReturn(wifi);
        return fileIORequest;
    }

    @NonNull
    private File getValidFile() {
        return new File(Environment.getExternalStorageDirectory(), "someFile.txt");
    }

    @NonNull
    private FileIO createFileIO(FileIORequest fileIoReq) {
        return new FileIO(3, fileIoReq, mockContext, true, createRestApi(), utils);
    }

    @NonNull
    private File createFileWhichDoesNotExist() {
        getValidFile().delete();
        return getValidFile();
    }

    @NonNull
    private File createFileWhichDoesExist() throws IOException {
        getValidFile().delete();
        getValidFile().createNewFile();
        return getValidFile();
    }

    private Context createMockedContext() {
        final Context context = mock(Context.class);
        final Resources resources = mock(Resources.class);
        final PackageManager packageManager = mock(PackageManager.class);
        Mockito.when(context.getApplicationContext()).thenReturn(mock(Context.class));
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(context.getSystemService(Context.STORAGE_SERVICE)).thenReturn(getMockedJobScheduler());
        return context;
    }

    @Nullable
    private FirebaseJobDispatcher getMockedJobScheduler() {
        return firebaseJobDispatcher;
    }

    private RestApiImpl createRestApi() {
        final RestApiImpl restApi = mock(RestApiImpl.class);
        Mockito.when(restApi.dynamicDownload(Mockito.anyString())).thenReturn(getResponseBodyObservable());
        return restApi;
    }

    private Observable<ResponseBody> getResponseBodyObservable() {
        return Observable.fromCallable(this::getResponseBody);
    }

    private ResponseBody getResponseBody() throws IOException {
        Mockito.when(responseBody.byteStream()).thenReturn(getInputStreamReader());
        Mockito.when(responseBody.contentLength()).thenReturn((long) (1096 * 1096));
        return responseBody;
    }

    private InputStream getInputStreamReader() throws IOException {
        Mockito.when(inputStream.read(Mockito.any())).thenReturn(1096, 1096, 1096, -1);
        return inputStream;
    }
}
