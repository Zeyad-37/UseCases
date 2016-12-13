package com.zeyad.usecases.data.requests;

import android.support.annotation.Nullable;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Config.class})
public class FileIORequestTest {

    @Nullable
    private FileIORequest mFileIORequest;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(DataUseCaseFactory.class);
        when(Config.getBaseURL()).thenReturn("www.google.com");
        mFileIORequest = FileIORequestTestRobot.createUploadRequest();
    }

    @After
    public void tearDown() throws Exception {
        mFileIORequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mFileIORequest.getUrl(), is(equalTo(FileIORequestTestRobot.URL)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mFileIORequest.onWifi(), is(equalTo(FileIORequestTestRobot.ON_WIFI)));
    }

    @Test
    public void testGetFile() throws Exception {
        assertThat(mFileIORequest.getFile(), is(equalTo(FileIORequestTestRobot.FILE)));
    }

    @Test
    public void testOnWifi() throws Exception {
        assertThat(mFileIORequest.onWifi(), is(equalTo(FileIORequestTestRobot.ON_WIFI)));
    }

    @Test
    public void testWhileChargingGetFile() throws Exception {
        assertThat(mFileIORequest.isWhileCharging(), is(equalTo(FileIORequestTestRobot.WHILE_CHARGING)));
    }
}