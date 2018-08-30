package com.zeyad.usecases.mapper;

import com.google.gson.internal.LinkedTreeMap;
import com.zeyad.usecases.TestRealmModel;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author by ZIaDo on 5/14/17.
 */
public class DAOMapperTest {
    private DAOMapper daoMapper;

    @Before
    public void setUp() {
        daoMapper = new DAOMapper();
    }

    @After
    public void tearDown() {
        daoMapper = null;
    }

    @Test
    public void mapTo() {
        Assert.assertEquals(
                daoMapper.mapTo(new Object(), Object.class).getClass(), LinkedTreeMap.class);
        Assert.assertEquals(
                daoMapper.mapTo(new Object(), TestRealmModel.class).getClass(),
                TestRealmModel.class);
    }

    @Test
    public void mapAllTo() {
        Assert.assertEquals(
                daoMapper.mapAllTo(Collections.EMPTY_LIST, Object.class).getClass(),
                ArrayList.class);
        Assert.assertEquals(
                daoMapper
                        .mapAllTo(Collections.singletonList(new Object()), Object.class)
                        .getClass(),
                ArrayList.class);
        Assert.assertEquals(
                daoMapper
                        .mapAllTo(
                                Collections.singletonList(new TestRealmModel()),
                                TestRealmModel.class)
                        .getClass(),
                ArrayList.class);
    }
}
