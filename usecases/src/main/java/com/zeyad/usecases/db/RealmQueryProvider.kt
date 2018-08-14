package com.zeyad.usecases.db

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

interface RealmQueryProvider<T : RealmModel> {
    fun create(realm: Realm): RealmQuery<T>
}
