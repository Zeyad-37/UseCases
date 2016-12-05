package com.zeyad.usecases.data.db;

import org.powermock.api.mockito.PowerMockito;

import io.realm.Realm;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author zeyad on 11/30/16.
 */

public class MockSupport {

    public static Realm mockRealm() {
        mockStatic(Realm.class);
        Realm mockRealm = PowerMockito.mock(Realm.class);

//        when(mockRealm.createObject(Address.class)).thenReturn(new Address());
//        when(mockRealm.createObject(Offer.class)).thenReturn(new Offer());
//        when(mockRealm.createObject(Tasker.class)).thenReturn(new Tasker());
//        when(mockRealm.createObject(Job.class)).thenReturn(new Job());

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);
        return mockRealm;
    }
}
