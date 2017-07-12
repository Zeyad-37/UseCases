package com.zeyad.usecases.app;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * @author by ZIaDo on 5/23/17.
 */
//@Database(entities = {User.class}, version = 1)
public abstract class BaseDatabase extends RoomDatabase {
    private static BaseDatabase INSTANCE;

    public static <T extends BaseDatabase> BaseDatabase getInstance(Context context, Class<T> clazz) {
        if (INSTANCE == null) {
            synchronized (clazz) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            clazz, "Sample.db")
                                   .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RoomBaseDao roomBaseDao();
}