package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.Nullable;

import com.zeyad.genericusecase.domain.interactors.requests.GetRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class GetRequestTest {

    @Nullable
    private GetRequest mGetRequest;

    @Before
    public void setUp() throws Exception {
        mGetRequest = GetRequestTestRobot.createGetObjectRequest();
    }

    @After
    public void tearDown() throws Exception {
        mGetRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mGetRequest.getUrl(), is(equalTo(GetRequestTestRobot.URL)));
    }

    @Test
    public void testGetSubscriber() throws Exception {
        assertThat(mGetRequest.getUrl(), is(equalTo(GetRequestTestRobot.URL)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mGetRequest.getDataClass(), is(equalTo(GetRequestTestRobot.DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mGetRequest.getPresentationClass(), is(equalTo(GetRequestTestRobot.PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mGetRequest.isPersist(), is(equalTo(GetRequestTestRobot.TO_PERSIST)));
    }

    @Test
    public void testIsShouldCache() throws Exception {
        assertThat(mGetRequest.isShouldCache(), is(equalTo(GetRequestTestRobot.SHOULD_CACHE)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mGetRequest.getIdColumnName(), is(equalTo(GetRequestTestRobot.ID_COLUMN_NAME)));
    }

    @Test
    public void testGetItemId() throws Exception {
        assertThat(mGetRequest.getItemId(), is(equalTo(GetRequestTestRobot.ID_COLUMN_ID)));
    }
}