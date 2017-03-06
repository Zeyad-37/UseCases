package com.zeyad.usecases.domain.interactors;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.interactors.files.FileUseCase;
import com.zeyad.usecases.domain.interactors.files.IFileUseCase;
import com.zeyad.usecases.domain.repositories.Files;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author zeyad on 11/21/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileUseCaseTest {

    private IFileUseCase mFilesUseCase;
    private Files mFiles;
    private Observable observable;

    @Before
    public void setUp() throws Exception {
        observable = Observable.just(new Object());
        mFiles = mock(Files.class);
        mFilesUseCase = new FileUseCase(mFiles, new JobExecutor(), new UIThread());
    }

    public Observable fileNotFoundException() {
        return Observable.error(new FileNotFoundException());
    }

    @Test
    public void readFromResource() {
        when(mFiles.readFromResource(anyString())).thenReturn(observable);
        mFilesUseCase.readFromResource("");
        verify(mFiles, times(1)).readFromResource(anyString());
    }

    @Test
    public void readFromFile() {
        when(mFiles.readFromFile(anyString())).thenReturn(observable);
        mFilesUseCase.readFromFile("");
        verify(mFiles, times(1)).readFromFile(anyString());
    }

    @Test
    public void saveToFile() {
        when(mFiles.saveToFile(anyString(), anyString())).thenReturn(observable);
        mFilesUseCase.saveToFile("", "");
        verify(mFiles, times(1)).saveToFile(anyString(), anyString());
    }

    @Test
    public void uploadFile() {
        when(mFiles.uploadFileDynamically(anyString(), any(File.class), anyString(), (HashMap<String, Object>) anyMap(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(Class.class), any(Class.class)))
                .thenReturn(observable);
        mFilesUseCase.uploadFile(new FileIORequest());
        verify(mFiles, times(1)).uploadFileDynamically(anyString(), any(File.class), anyString(), (HashMap<String, Object>) anyMap(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(Class.class), any(Class.class));
    }

    @Test
    public void downloadFile() {
        when(mFiles.downloadFileDynamically(anyString(), any(File.class), anyBoolean(),
                anyBoolean(), anyBoolean(), any(Class.class), any(Class.class))).thenReturn(observable);
        mFilesUseCase.downloadFile(new FileIORequest());
        verify(mFiles, times(1)).downloadFileDynamically(anyString(), any(File.class), anyBoolean(),
                anyBoolean(), anyBoolean(), any(Class.class), any(Class.class));
    }
}
