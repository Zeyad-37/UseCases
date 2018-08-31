package com.zeyad.usecases.services;

import android.os.Bundle;

import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.CloudStore;

import org.json.JSONArray;
import org.junit.Test;
import org.mockito.internal.matchers.Any;

import io.reactivex.observers.TestObserver;

import static com.zeyad.usecases.requests.PostRequest.POST;
import static org.mockito.Mockito.mock;

/**
 * @author by ZIaDo on 5/20/17.
 */
public class GenericJobServiceLogicTest {
    @Test
    public void startJob() {
        GenericJobServiceLogic genericJobServiceLogic = new GenericJobServiceLogic();
        TestObserver testSubscriber = new TestObserver();

        Bundle extras = new Bundle(2);
        extras.putString(GenericJobService.Companion.getJOB_TYPE(), GenericJobService.Companion.getPOST());
        extras.putParcelable(GenericJobService.Companion.getPAYLOAD(), new PostRequest.Builder(Any.class, true)
                .idColumnName("id", int.class)
                .payLoad(new JSONArray())
                .url("")
                .method(POST)
                .build());
        genericJobServiceLogic.startJob(extras, mock(CloudStore.class), "")
                .subscribe(new TestObserver<>());
        testSubscriber.assertNoErrors();
        //        testSubscriber.assertComplete();
    }
}

