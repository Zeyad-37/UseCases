package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.data.network.ApiConnection;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.utils.Utils;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileIOTest {
    private final InputStream inputStream = mock(InputStream.class);
    private ResponseBody responseBody;
    private ApiConnection apiConnection;
    private Context mockContext;
    private Utils utils;
    // item under test
    private FileIO fileIO;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        utils = mock(Utils.class);
        apiConnection = createRestApi();
    }

    @Test
    public void testDownload() {
        FileIORequest fileIOReq = mockFileIoReq(true, true, getValidFile());
        fileIO = createFileIO(fileIOReq, true);
        fileIO.execute();
        verify(apiConnection).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testUpload() throws JSONException {
        FileIORequest fileIOReq = mockFileIoReq(true, true, getValidFile());
        fileIO = createFileIO(fileIOReq, false);
        fileIO.execute();
        verify(apiConnection).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testReQueue() throws JSONException {
        FileIORequest fileIOReq = mockFileIoReq(true, true, getValidFile());
        fileIO = createFileIO(fileIOReq, true);
        Mockito.doNothing().when(utils).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class));
    }

    private String getValidUrl() {
        return "http://www.google.com";
    }

    private FileIORequest mockFileIoReq(boolean wifi, boolean isCharging, File file) {
        file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("/fake/dir");
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
        File file = new File(Environment.getExternalStorageDirectory(), "someFile.txt");
        file.mkdir();
        return file;
    }

    @NonNull
    private FileIO createFileIO(FileIORequest fileIoReq, boolean isDownload) {
        return new FileIO(0, fileIoReq, mockContext, isDownload, createRestApi(), utils);
    }

    private ApiConnection createRestApi() {
        final ApiConnection restApi = mock(ApiConnection.class);
        Mockito.when(restApi.dynamicDownload(Mockito.anyString())).thenReturn(getResponseBodyObservable());
        Mockito.when(restApi.dynamicUpload(Mockito.anyString(), anyMap(), any(MultipartBody.Part.class)))
                .thenReturn(Observable.just(new Object()));
        return restApi;
    }

    private Observable<ResponseBody> getResponseBodyObservable() {
        return Observable.fromCallable(this::getResponseBody);
    }

    private ResponseBody getResponseBody() throws IOException {
        responseBody = mock(ResponseBody.class);
//        Mockito.when(responseBody.byteStream()).thenReturn(getInputStreamReader());
//        Mockito.when(responseBody.contentLength()).thenReturn((long) (1096 * 1096));
        return responseBody;
    }

    private InputStream getInputStreamReader() throws IOException {
        Mockito.when(inputStream.read(Mockito.any())).thenReturn(1096, 1096, 1096, -1);
        return inputStream;
    }
}
