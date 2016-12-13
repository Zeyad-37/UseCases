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
public class PostRequestTest {

    @Nullable
    private PostRequest mPostRequest;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(DataUseCaseFactory.class);
        when(Config.getBaseURL()).thenReturn("www.google.com");
        mPostRequest = PostRequestTestRobot.buildPostRequest();
    }

    @After
    public void tearDown() throws Exception {
        mPostRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mPostRequest.getUrl(), is(equalTo(PostRequestTestRobot.URL)));
    }

    @Test
    public void testGetSubscriber() throws Exception {
        assertThat(mPostRequest.getSubscriber(), is(equalTo(PostRequestTestRobot.SUBSCRIBER)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mPostRequest.getDataClass(), is(equalTo(PostRequestTestRobot.DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mPostRequest.getPresentationClass(), is(equalTo(PostRequestTestRobot.PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mPostRequest.isPersist(), is(equalTo(PostRequestTestRobot.TO_PERSIST)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mPostRequest.getIdColumnName(), is(equalTo(PostRequestTestRobot.ID_COLUMN_NAME)));
    }
}