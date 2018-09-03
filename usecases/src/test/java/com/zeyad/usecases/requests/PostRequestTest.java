package com.zeyad.usecases.requests;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.integration.Success;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PostRequestTest {

    private final boolean TO_PERSIST = false;
    private final Class DATA_CLASS = TestRealmModel.class;
    private final String ID_COLUMN_NAME = "id";
    private final String URL = "www.google.com";
    //    private final JSONArray JSON_ARRAY = new JSONArray();
    private JSONObject JSON_OBJECT;
    private final HashMap<String, Object> HASH_MAP = new HashMap<>();
    private PostRequest mPostRequest;

    @Before
    public void setUp() throws JSONException {
        Success success = new Success(true);
        HASH_MAP.put("success", true);
        JSON_OBJECT = new JSONObject(HASH_MAP);
        JSON_OBJECT = new JSONObject("{}");
        JSON_OBJECT.put("success", true);
        mPostRequest = new PostRequest.Builder(DATA_CLASS, TO_PERSIST)
//                .payLoad(HASH_MAP)
//                .payLoad(JSON_ARRAY)
                .idColumnName(ID_COLUMN_NAME, int.class)
                .payLoad(Config.INSTANCE.getGson().toJson(success))
                .fullUrl(URL)
                .build();
    }

    @After
    public void tearDown() {
        mPostRequest = null;
    }

    @Test
    public void testGetUrl() {
        assertThat(mPostRequest.getFullUrl(), is(equalTo(URL)));
    }

    @Test
    public void testGetDataClass() {
        assertThat(mPostRequest.getRequestType(), is(equalTo(DATA_CLASS)));
    }

    @Test
    public void testIsPersist() {
        assertThat(mPostRequest.getPersist(), is(equalTo(TO_PERSIST)));
    }

    @Test
    public void testGetIdColumnName() {
        assertThat(mPostRequest.getIdColumnName(), is(equalTo(ID_COLUMN_NAME)));
    }
}

