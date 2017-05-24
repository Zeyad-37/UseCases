package com.zeyad.usecases.app;

import android.support.annotation.NonNull;

import com.zeyad.usecases.app.screens.user_list.User;
import com.zeyad.usecases.mapper.DAOMapper;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author by ZIaDo on 5/23/17.
 */
public class RoomManager<DB extends UserDatabase> {

    private DB db;
    private DAOMapper entityDataMapper;

    public RoomManager(DB db, @NonNull DAOMapper entityDataMapper) {
        this.db = db;
        this.entityDataMapper = entityDataMapper;
    }

    @NonNull
    public <M> Flowable<M> getById(Class dataClass) {
//        db.beginTransaction();
//        try {
//            Flowable flowable = db.roomBaseDao().getItem("Select * From " + dataClass.getSimpleName())
//                    .map(entity -> entityDataMapper.<M>mapTo(entity, dataClass));
//            db.setTransactionSuccessful();
//            return flowable;
//        } finally {
//            db.endTransaction();
//        }
        return null;
    }

    @NonNull
    public <M> Flowable<List<M>> getAll(Class dataClass) {
//        db.beginTransaction();
//        try {
//            Flowable flowable = db.roomBaseDao().getAllItems("Select * From " + dataClass.getSimpleName())
//                    .map(entity -> entityDataMapper.<M>mapTo(entity, dataClass));
//            db.setTransactionSuccessful();
//            return flowable;
//        } finally {
//            db.endTransaction();
//        }
        return null;
    }

    @NonNull
    public <M> Completable putAll(List<M> items) {
        db.beginTransaction();
        try {
            db.roomBaseDao().insertItemsReplace((User[]) items.toArray());
            db.setTransactionSuccessful();
            return Completable.complete();
        } finally {
            db.endTransaction();
        }
    }
}
