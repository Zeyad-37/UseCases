package com.zeyad.genericusecase.domain.interactors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class GetObjectRequestTest {

    private GetObjectRequest mGetObjectRequest;

    @Before
    public void setUp() throws Exception {
        mGetObjectRequest = GetObjectRequestTestRobot.createGetObjectRequest();
    }

    @After
    public void tearDown() throws Exception {
        mGetObjectRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mGetObjectRequest.getUrl(), is(equalTo(GetObjectRequestTestRobot.URL)));
    }

    @Test
    public void testGetSubscriber() throws Exception {
        assertThat(mGetObjectRequest.getUrl(), is(equalTo(GetObjectRequestTestRobot.URL)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mGetObjectRequest.getDataClass(), is(equalTo(GetObjectRequestTestRobot.DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mGetObjectRequest.getPresentationClass(), is(equalTo(GetObjectRequestTestRobot.PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mGetObjectRequest.isPersist(), is(equalTo(GetObjectRequestTestRobot.TO_PERSIST)));
    }

    @Test
    public void testIsShouldCache() throws Exception {
        assertThat(mGetObjectRequest.isShouldCache(), is(equalTo(GetObjectRequestTestRobot.SHOULD_CACHE)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mGetObjectRequest.getIdColumnName(), is(equalTo(GetObjectRequestTestRobot.ID_COLUMN_NAME)));
    }

    @Test
    public void testGetItemId() throws Exception {
        assertThat(mGetObjectRequest.getItemId(), is(equalTo(GetObjectRequestTestRobot.ID_COLUMN_ID)));
    }
}