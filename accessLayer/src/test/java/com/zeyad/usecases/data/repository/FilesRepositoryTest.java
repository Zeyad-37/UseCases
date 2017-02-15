package com.zeyad.usecases.data.repository;

import com.google.gson.Gson;
import com.zeyad.usecases.data.mappers.DAOMapperFactory;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.repository.stores.CloudDataStore;
import com.zeyad.usecases.data.repository.stores.DataStore;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/15/17.
 */
public class FilesRepositoryTest {

    private DataStore mockDataStore;
    private FilesRepository mFilesRepository; // class under test
    private DataStoreFactory mockDataStoreFactory;
    private String validUrl = "http://www.google.com";

    @Before
    public void setUp() throws Exception {
        // init mocks
        mockDataStore = mock(CloudDataStore.class);

        mockDataStoreFactory = mock(DataStoreFactory.class);

        // init class under test
        mFilesRepository = new FilesRepository(mockDataStoreFactory, mock(DAOMapperFactory.class),
                new Gson());
        // global stub
        when(mockDataStoreFactory.cloud(any(IDAOMapper.class))).thenReturn(mockDataStore);
    }

    @Test
    public void readFromResource() throws Exception {

    }

    @Test
    public void readFromFile() throws Exception {

    }

    @Test
    public void saveToFile() throws Exception {

    }

    @Test
    public void uploadFileDynamically() throws Exception {
        mFilesRepository.uploadFileDynamically(validUrl, new File(""), "", new HashMap<>(), false,
                false, false, Object.class, Object.class);
        verify(mockDataStoreFactory, times(1)).cloud(any(IDAOMapper.class));
        verify(mockDataStore, times(1)).dynamicUploadFile(anyString(), any(File.class), anyString(),
                (HashMap<String, Object>) anyMap(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(Class.class));
    }

    @Test
    public void downloadFileDynamically() throws Exception {
        mFilesRepository.downloadFileDynamically(validUrl, new File(""), false, false, false,
                Object.class, Object.class);
        verify(mockDataStoreFactory, times(1)).cloud(any(IDAOMapper.class));
        verify(mockDataStore, times(1)).dynamicDownloadFile(anyString(), any(File.class), anyBoolean(),
                anyBoolean(), anyBoolean());
    }
}