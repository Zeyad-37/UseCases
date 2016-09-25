package com.zeyad.genericusecase.domain.interactors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FileIORequestTest {

    private FileIORequest mFileIORequest;

    @Before
    public void setUp() throws Exception {
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