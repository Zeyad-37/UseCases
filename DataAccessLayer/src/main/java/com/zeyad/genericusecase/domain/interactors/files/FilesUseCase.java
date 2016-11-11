package com.zeyad.genericusecase.domain.interactors.files;

import com.google.gson.Gson;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.domain.executors.PostExecutionThread;
import com.zeyad.genericusecase.domain.executors.ThreadExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/11/16.
 */

public class FilesUseCase implements IFilesUseCase {

    private static FilesUseCase sFilesUseCase;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private FilesUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
    }

    public static void init() {
        sFilesUseCase = new FilesUseCase(new JobExecutor(), new UIThread());
    }

    public static FilesUseCase getInstance() {
        if (sFilesUseCase == null)
            sFilesUseCase = new FilesUseCase(new JobExecutor(), new UIThread());
        return sFilesUseCase;
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
            } catch (IOException e) {
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
        }).compose(applySchedulers());
    }


    @Override
    public Observable<String> readFromFile(String fullFilePath) {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(fullFilePath)));
            String data = (String) is.readObject();
            is.close();
            return Observable.just(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(fullFilePath)));
                String data = new Gson().fromJson(inputStreamReader, String.class);
                inputStreamReader.close();
                return Observable.just(data);
            } catch (IOException e1) {
                e1.printStackTrace();
                return Observable.error(e1);
            }
        }
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, String data) {
        return Observable.defer(() -> {
            try {
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(fullFilePath)));
                os.writeObject(data);
                os.flush();
                os.close();
                return Observable.just(true);
            } catch (IOException e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        }).compose(applySchedulers());
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, byte[] data) {
        return Observable.defer(() -> {
            try {
                File outFile = new File(fullFilePath);
                FileOutputStream outStream = new FileOutputStream(outFile);
                outStream.write(data);
                outStream.flush();
                outStream.close();
                return Observable.just(true);
            } catch (IOException e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        }).compose(applySchedulers());
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler())
                .unsubscribeOn(Schedulers.from(mThreadExecutor));
    }
}
