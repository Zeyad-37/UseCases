package com.zeyad.usecases.requests;

import com.zeyad.usecases.TestRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class PostRequestTest {

    private final boolean TO_PERSIST = false;
    private final Class DATA_CLASS = TestRealmModel.class;
    private final Class PRESENTATION_CLASS = Object.class;
    private final String ID_COLUMN_NAME = "id";
    private final String URL = "www.google.com";
    private final JSONArray JSON_ARRAY = new JSONArray();
    private final JSONObject JSON_OBJECT = new JSONObject();
    private final HashMap<String, Object> HASH_MAP = new HashMap<>();
    private PostRequest mPostRequest;

    @Before
    public void setUp() throws Exception {
        mPostRequest = new PostRequest.PostRequestBuilder(DATA_CLASS, TO_PERSIST)
                .payLoad(HASH_MAP)
                .idColumnName(ID_COLUMN_NAME)
                .payLoad(JSON_ARRAY)
                .payLoad(JSON_OBJECT)
                .fullUrl(URL)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mPostRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mPostRequest.getUrl(), is(equalTo(URL)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mPostRequest.getDataClass(), is(equalTo(DATA_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mPostRequest.isPersist(), is(equalTo(TO_PERSIST)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mPostRequest.getIdColumnName(), is(equalTo(ID_COLUMN_NAME)));
    }
}