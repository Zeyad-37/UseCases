package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.stores.CloudStore;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Flowable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileIOTest {
    private CloudStore cloudStore;
    private Context mockContext;
    private Utils utils;
    // item under test
    private FileIO fileIO;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        utils = mock(Utils.class);
        cloudStore = createCloudDataStore();
    }

    @After
    public void tearDown() throws Exception {
        reset(cloudStore);
    }

    @Test
    public void testDownload() {
        //        fileIO = createFileIO(mockFileIoReq(true, true, getValidFile()), true);
        //        fileIO.execute();
        //        verify(cloudStore).dynamicDownloadFile(anyString(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    public void testUpload() throws JSONException {
        //        fileIO = createFileIO(mockFileIoReq(true, true, getValidFile()), false);
        //        fileIO.execute();
        //        verify(cloudStore).dynamicUploadFile(anyString(), any(), anyString(), (HashMap<String, Object>) anyMap(),
        //                anyBoolean(), anyBoolean(), anyBoolean(), any());
    }

    @Test
    public void testReQueue() throws JSONException {
        FileIORequest fileIOReq = mockFileIoReq(true, true, getValidFile());
        fileIO = createFileIO(fileIOReq, true);
        Mockito.doNothing()
                .when(utils)
                .queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class), anyInt());
        fileIO.queueIOFile();
        verify(utils, times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class), anyInt());
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
        Mockito.when(fileIORequest.isOnWifi()).thenReturn(wifi);
        return fileIORequest;
    }

    @NonNull
    private File getValidFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "someFile.png");
        file.mkdir();
        return file;
    }

    @NonNull
    private FileIO createFileIO(FileIORequest fileIoReq, boolean isDownload) {
        return new FileIO(0, fileIoReq, mockContext, isDownload, createCloudDataStore(), utils);
    }

    private CloudStore createCloudDataStore() {
        final CloudStore cloudStore = mock(CloudStore.class);
        Mockito.when(
                cloudStore.dynamicDownloadFile(
                        Mockito.anyString(),
                        any(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(Flowable.empty());
        Mockito.when(
                cloudStore.dynamicUploadFile(
                        Mockito.anyString(),
                        (HashMap) anyMap(),
                        (HashMap) anyMap(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        any()))
                .thenReturn(Flowable.empty());
        return cloudStore;
    }
}
