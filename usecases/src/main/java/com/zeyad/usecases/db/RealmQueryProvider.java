package com.zeyad.usecases.db;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;

public interface RealmQueryProvider<T extends RealmModel> {
        RealmQuery<T> create(Realm realm);
    }