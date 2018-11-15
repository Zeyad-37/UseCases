package com.zeyad.usecases.app

import android.arch.persistence.room.Dao
import com.zeyad.usecases.app.screens.user.User
import com.zeyad.usecases.app.screens.user.detail.Repository
import com.zeyad.usecases.db.BaseDao

@Dao
interface UserDao : BaseDao<User>

@Dao
interface RepoDao : BaseDao<Repository>