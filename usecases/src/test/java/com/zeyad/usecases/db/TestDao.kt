package com.zeyad.usecases.db

import android.arch.persistence.room.Dao
import com.zeyad.usecases.TestModel

@Dao
interface TestDao : BaseDao<TestModel>