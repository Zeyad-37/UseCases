package com.zeyad.usecases.db.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * @author by ZIaDo on 5/23/17.
 */
@Database(entities = {User.class}, version = 1)
public abstract class GenericDatabase extends RoomDatabase {
    public abstract RoomBaseDao roomBaseDao();
}