package com.zeyad.usecases.requests;

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
    private final String URL = "www.google.com";
    private final boolean SHOULD_CACHE = true;
    private final Long ID = 1L;
    private GetRequest mGetRequest;

    @Before
    public void setUp() {
        mGetRequest =
                new GetRequest.Builder(DATA_CLASS, TO_PERSIST)
                        .fullUrl(URL)
                        .cache(ID_COLUMN_NAME)
                        .id(ID, ID_COLUMN_NAME, long.class)
                        .build();
    }

    @After
    public void tearDown() {
        mGetRequest = null;
    }

    @Test
    public void testGetUrl() {
        assertThat(mGetRequest.getFullUrl(), is(equalTo(URL)));
    }

    @Test
    public void testGetDataClass() {
        assertThat(mGetRequest.getDataClass(), is(equalTo(DATA_CLASS)));
    }

    @Test
    public void testIsPersist() {
        assertThat(mGetRequest.getPersist(), is(equalTo(TO_PERSIST)));
    }

    @Test
    public void testIsShouldCache() {
        assertThat(mGetRequest.getCache(), is(equalTo(SHOULD_CACHE)));
    }

    @Test
    public void testGetIdColumnName() {
        assertThat(mGetRequest.getIdColumnName(), is(equalTo(ID_COLUMN_NAME)));
    }

    @Test
    public void testGetItemId() {
        assertThat(mGetRequest.getItemId(), is(equalTo(ID)));
    }
}

