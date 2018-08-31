package com.zeyad.usecases.requests;

import com.zeyad.usecases.TestRealmModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class FileIORequestTest {

    private final boolean ON_WIFI = false;
    private final boolean QUEUABLE = false;
    private final boolean WHILE_CHARGING = false;
    private final String URL = "www.google.com";
    private final File FILE = Mockito.mock(File.class);
    private final Class DATA_CLASS = TestRealmModel.class;
    private FileIORequest mFileIORequest;

    @Before
    public void setUp() {
        mFileIORequest =
                new FileIORequest.Builder(URL)
                        .file(FILE)
                        //                        .queuable(ON_WIFI, WHILE_CHARGING)
                        .responseType(DATA_CLASS)
                        .build();
    }

    @After
    public void tearDown() {
        mFileIORequest = null;
    }

    @Test
    public void testGetUrl() {
        assertThat(mFileIORequest.getUrl(), is(equalTo(URL)));
    }

    @Test
    public void testIsPersist() {
        assertThat(mFileIORequest.getQueuable(), is(equalTo(QUEUABLE)));
    }

    @Test
    public void testGetFile() {
        assertThat(mFileIORequest.getFile(), is(equalTo(FILE)));
    }

    @Test
    public void testOnWifi() {
        assertThat(mFileIORequest.getOnWifi(), is(equalTo(ON_WIFI)));
    }

    @Test
    public void testWhileChargingGetFile() {
        assertThat(mFileIORequest.getWhileCharging(), is(equalTo(WHILE_CHARGING)));
    }
}
