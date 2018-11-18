package com.zeyad.usecases.mapper

import com.google.gson.internal.LinkedTreeMap
import com.zeyad.usecases.TestModel
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * @author by ZIaDo on 5/14/17.
 */
class DAOMapperTest {
    private var daoMapper: DAOMapper? = null

    @Before
    fun setUp() {
        daoMapper = DAOMapper()
    }

    @After
    fun tearDown() {
        daoMapper = null
    }

    @Test
    fun mapTo() {
        Assert.assertEquals(
                daoMapper!!.mapTo<Any>(Any(), Any::class.java).javaClass, LinkedTreeMap::class.java)
        Assert.assertEquals(
                daoMapper!!.mapTo<Any>(Any(), TestModel::class.java).javaClass,
                TestModel::class.java)
    }

    @Test
    fun mapAllTo() {
        Assert.assertEquals(
                daoMapper!!.mapAllTo<Any>(Collections.EMPTY_LIST, Any::class.java).javaClass,
                ArrayList::class.java)
        Assert.assertEquals(
                daoMapper!!
                        .mapAllTo<Any>(listOf(Any()), Any::class.java).javaClass,
                ArrayList::class.java)
        Assert.assertEquals(
                daoMapper!!
                        .mapAllTo<Any>(
                                listOf(TestModel()),
                                TestModel::class.java).javaClass,
                ArrayList::class.java)
    }
}
