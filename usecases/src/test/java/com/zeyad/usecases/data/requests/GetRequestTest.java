package com.zeyad.usecases.data.requests;

import com.zeyad.usecases.TestRealmModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class GetRequestTest {

    private final Class DATA_CLASS = TestRealmModel.class;
    private final boolean TO_PERSIST = false;
    private final String ID_COLUMN_NAME = "id";
    private final Class PRESENTATION_CLASS = Object.class;
    private final String URL = "www.google.com";
    private final boolean SHOULD_CACHE = true;
    private final Integer ID_COLUMN_ID = 1;
    private GetRequest mGetRequest;

    @Before
    public void setUp() throws Exception {
        mGetRequest = new GetRequest.GetRequestBuilder(DATA_CLASS, TO_PERSIST)
                .fullUrl(URL)
                .shouldCache(SHOULD_CACHE)
                .presentationClass(PRESENTATION_CLASS)
                .idColumnName(ID_COLUMN_NAME)
                .id(ID_COLUMN_ID)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mGetRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mGetRequest.getUrl(), is(equalTo(URL)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mGetRequest.getDataClass(), is(equalTo(DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mGetRequest.getPresentationClass(), is(equalTo(PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mGetRequest.isPersist(), is(equalTo(TO_PERSIST)));
    }

    @Test
    public void testIsShouldCache() throws Exception {
        assertThat(mGetRequest.isShouldCache(), is(equalTo(SHOULD_CACHE)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mGetRequest.getIdColumnName(), is(equalTo(ID_COLUMN_NAME)));
    }

    @Test
    public void testGetItemId() throws Exception {
        assertThat(mGetRequest.getItemId(), is(equalTo(ID_COLUMN_ID)));
    }
}