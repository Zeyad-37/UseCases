package com.zeyad.usecases.db;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;

public interface RealmQueryProvider<T extends RealmModel> {
    @NonNull
        RealmQuery<T> create(Realm realm);
    }