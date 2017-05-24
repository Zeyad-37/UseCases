package com.zeyad.usecases.app;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.zeyad.usecases.app.screens.user_list.User;

/**
 * @author by ZIaDo on 5/23/17.
 */
@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract RoomBaseDao roomBaseDao();
}