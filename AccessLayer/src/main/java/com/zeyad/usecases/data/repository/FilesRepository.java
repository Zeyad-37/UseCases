package com.zeyad.usecases.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.mappers.EntityDataMapper;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.utils.EntityMapperUtil;
import com.zeyad.usecases.data.utils.IEntityMapperUtil;
import com.zeyad.usecases.domain.repositories.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import rx.Observable;

/**
 * @author by ZIaDo on 11/12/16.
 */

public class FilesRepository implements Files {
    private static Gson mGson;
    private static Files sInstance;
    private final DataStoreFactory mDataStoreFactory;
    private final IEntityMapperUtil mEntityMapperUtil;

    private FilesRepository(Context context) {
        if (Config.getInstance().getDataStoreFactory() == null) {
            mDataStoreFactory = new DataStoreFactory(Config.getInstance().getContext() == null ?
                    context : Config.getInstance().getContext());
            Config.getInstance().setDataStoreFactory(mDataStoreFactory);
        } else
            mDataStoreFactory = Config.getInstance().getDataStoreFactory();
        mEntityMapperUtil = new EntityMapperUtil() {
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        };
        mGson = Config.getGson();
    }

    public static Files getInstance() throws IllegalArgumentException {
        if (sInstance == null)
            throw new NullPointerException("FilesRepository is null, please call FilesRepository#init(Context) first");
        return sInstance;
    }

    public static Files getInstance(Context context) {
        if (sInstance == null)
            sInstance = new FilesRepository(context);
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new FilesRepository(context);
    }

    @Override
    public Observable<String> readFromResource(String filePath) {
        return Observable.defer(() -> {
            StringBuilder returnString = new StringBuilder();
            InputStream fIn = null;
            InputStreamReader isr = null;
            BufferedReader input = null;
            try {
                fIn = Config.getInstance().getContext().getResources().getAssets().open(filePath);
                isr = new InputStreamReader(fIn);
                input = new BufferedReader(isr);
                String line;
                while ((line = input.readLine()) != null)
                    returnString.append(line);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            } finally {
                if (isr != null)
                    try {
                        isr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (fIn != null)
                    try {
                        fIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return Observable.just(returnString.toString());
        });
    }

    @Override
    public Observable<String> readFromFile(String fullFilePath) {
        try {
            return Observable.just(mGson.fromJson(new InputStreamReader(Config.getInstance()
                    .getContext().openFileInput(fullFilePath)), String.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, String data) {
        return Observable.defer(() -> {
            FileOutputStream outputStream;
            try {
                outputStream = Config.getInstance().getContext().openFileOutput(new File(fullFilePath)
                        .getName(), Context.MODE_PRIVATE);
                outputStream.write(data.getBytes());
                outputStream.close();
                return Observable.just(true);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        });
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, byte[] data) {
        return Observable.defer(() -> {
            FileOutputStream outputStream;
            try {
                outputStream = Config.getInstance().getContext().openFileOutput(new File(fullFilePath)
                        .getName(), Context.MODE_PRIVATE);
                outputStream.write(data);
                outputStream.close();
                return Observable.just(true);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        });
    }

    @NonNull
    @Override
    public Observable<?> uploadFileDynamically(String url, File file, String key, HashMap<String, Object> parameters,
                                               boolean onWifi, boolean whileCharging, boolean queuable,
                                               Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicUploadFile(url, file, key, parameters, onWifi, queuable, whileCharging, domainClass);
    }


    @NonNull
    @Override
    public Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                                 boolean queuable, Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicDownloadFile(url, file, onWifi, whileCharging, queuable);
    }
}
