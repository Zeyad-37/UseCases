package com.zeyad.genericusecase.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.domain.repositories.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;

/**
 * @author by ZIaDo on 11/12/16.
 */

public class FilesRepository implements Files {
    private static Files sInstance;

    private FilesRepository() {
    }

    public static Files getInstance() {
        if (sInstance == null)
            sInstance = new FilesRepository();
        return sInstance;
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
            return Observable.just(new Gson().fromJson(new InputStreamReader(Config.getInstance()
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
}
