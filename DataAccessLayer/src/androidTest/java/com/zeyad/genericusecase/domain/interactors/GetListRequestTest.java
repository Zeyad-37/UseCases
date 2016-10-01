package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.Nullable;

import com.zeyad.genericusecase.domain.interactors.requests.GetListRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class GetListRequestTest {

    @Nullable
    private GetListRequest mGetListRequest;

    @Before
    public void setUp() throws Exception {
        mGetListRequest = GetListRequestTestRobot
                .createGetListRequest();
    }

    @After
    public void tearDown() throws Exception {
        mGetListRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mGetListRequest.getUrl(), is(equalTo(GetListRequestTestRobot.URL)));
    }

    @Test
    public void testGetSubscriber() throws Exception {
        assertThat(mGetListRequest.getSubscriber(), is(equalTo(GetListRequestTestRobot.SUBSCRIBER)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mGetListRequest.getDataClass(), is(equalTo(GetListRequestTestRobot.DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mGetListRequest.getPresentationClass(), is(equalTo(GetListRequestTestRobot.PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mGetListRequest.isPersist(), is(equalTo(GetListRequestTestRobot.TO_PERSIST)));
    }

    @Test
    public void testIsShouldCache() throws Exception {
        assertThat(mGetListRequest.isShouldCache(), is(equalTo(GetListRequestTestRobot.SHOULD_CACHE)));
    }
}