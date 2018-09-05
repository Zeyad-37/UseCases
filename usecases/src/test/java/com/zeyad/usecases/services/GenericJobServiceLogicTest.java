package com.zeyad.usecases.services;

import android.content.Context;
import android.os.Bundle;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.CloudStore;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Any;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;

import static com.zeyad.usecases.requests.PostRequest.POST;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 5/20/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class GenericJobServiceLogicTest {
    @Test
    public void startJob() {
        com.zeyad.usecases.Config.context = mock(Context.class);
        ApiConnection apiConnection = mock(ApiConnection.class);
        com.zeyad.usecases.Config.INSTANCE.setApiConnection(apiConnection);
        when(apiConnection.dynamicPost(anyString(), any())).thenReturn(Flowable.just(Collections
                .singletonList(new TestRealmModel())));

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

