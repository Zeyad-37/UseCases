package com.zeyad.genericusecase.domain.interactors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.DatabaseManagerFactory;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.repository.generalstore.DataStoreFactory;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;
import com.zeyad.genericusecase.data.utils.ModelConverters;
import com.zeyad.genericusecase.domain.executors.PostExecutionThread;
import com.zeyad.genericusecase.domain.executors.ThreadExecutor;
import com.zeyad.genericusecase.domain.repository.Repository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.realm.RealmQuery;
import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class GenericUseCase implements IGenericUseCase {

    private final Repository mRepository;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;
    private static GenericUseCase sGenericUseCase;

    private GenericUseCase(Repository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mRepository = repository;
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     *
     * @param context context of application or instrumentation(testing only)
     */
    public static void init(Context context, IEntityMapperUtil entityMapper) {
        DatabaseManagerFactory.init(context);
        final DataBaseManager dataBaseManager = DatabaseManagerFactory.getInstance();
        final DataStoreFactory dataStoreFactory = new DataStoreFactory(dataBaseManager, context);
        final DataRepository repository = new DataRepository(dataStoreFactory, entityMapper);
        final JobExecutor threadExecutor = new JobExecutor();
        final UIThread postExecutionThread = new UIThread();
        sGenericUseCase = new GenericUseCase(repository, threadExecutor, postExecutionThread);
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     *
     * @param dataRepository data repository
     * @param jobExecutor    job executor
     * @param uiThread       ui thread implementation
     */
    public static void init(DataRepository dataRepository, JobExecutor jobExecutor, UIThread uiThread) {
        sGenericUseCase = new GenericUseCase(dataRepository, jobExecutor, uiThread);
    }

    public static GenericUseCase getInstance() {
        if (sGenericUseCase == null)
            throw new NullPointerException("GenericUseCase#init must be called before calling getInstance()");
        return sGenericUseCase;
    }

    /**
     * Executes the current use case.
     *
     * @param genericUseCaseRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable getList(@NonNull GetListRequest genericUseCaseRequest) {
        return mRepository.getListDynamically(genericUseCaseRequest.getUrl(), genericUseCaseRequest.getPresentationClass(),
                genericUseCaseRequest.getDataClass(), genericUseCaseRequest.isPersist(), genericUseCaseRequest.isShouldCache())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param getObjectRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable getObject(@NonNull GetObjectRequest getObjectRequest) {
        return mRepository.getObjectDynamicallyById(getObjectRequest.getUrl(), getObjectRequest.getIdColumnName(),
                getObjectRequest.getItemId(), getObjectRequest.getPresentationClass(), getObjectRequest.getDataClass(),
                getObjectRequest.isPersist(), getObjectRequest.isShouldCache())
                .compose(applySchedulers());
    }

    @Override
    public Observable postObject(@NonNull PostRequest postRequest) {
        JSONObject jsonObject = null;
        if (postRequest.getKeyValuePairs() != null) {
            jsonObject = ModelConverters.convertToJsonObject(postRequest.getKeyValuePairs());
        } else if (postRequest.getJsonObject() != null)
            jsonObject = postRequest.getJsonObject();
        if (jsonObject != null)
            return mRepository.postObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(), jsonObject,
                    postRequest.getPresentationClass(), postRequest.getDataClass(), postRequest.isPersist())
                    .compose(applySchedulers());
        else
            return Observable.defer(() -> Observable.error(new Exception("payload is null!")));
    }

    @Override
    public Observable postList(@NonNull PostRequest postRequest) {
        return mRepository.postListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getJsonArray(), postRequest.getPresentationClass(), postRequest.getDataClass(),
                postRequest.isPersist()).compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @Override
    public Observable putObject(@NonNull PostRequest postRequest) {
        return mRepository.putObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                ModelConverters.convertToJsonObject(postRequest.getKeyValuePairs()),
                postRequest.getPresentationClass(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    public Observable putList(@NonNull PostRequest postRequest) {
        JSONArray jsonArray = null;
        if (postRequest.getKeyValuePairs() != null)
            jsonArray = ModelConverters.convertToJsonArray(postRequest.getKeyValuePairs());
        else if (postRequest.getJsonArray() != null)
            jsonArray = postRequest.getJsonArray();
        if (jsonArray != null)
            return mRepository.putListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                    jsonArray, postRequest.getPresentationClass(), postRequest.getDataClass(),
                    postRequest.isPersist())
                    .compose(applySchedulers());
        else
            return Observable.error(new Exception("Missing Payload!"));
    }

    @Override
    public Observable deleteCollection(@NonNull PostRequest deleteRequest) {
        return mRepository.deleteListDynamically(deleteRequest.getUrl(),
                ModelConverters.convertToJsonArray(deleteRequest.getKeyValuePairs()),
                deleteRequest.getPresentationClass(), deleteRequest.getDataClass(), deleteRequest.isPersist())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @Override
    public Observable<Boolean> deleteAll(@NonNull PostRequest postRequest) {
        return mRepository.deleteAllDynamically(postRequest.getUrl(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers());
    }

    @Override
    public Observable uploadFile(@NonNull FileIORequest fileIORequest) {
        return mRepository.uploadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.onWifi(), fileIORequest.isWhileCharging(), fileIORequest.getPresentationClass(),
                fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Observable downloadFile(@NonNull FileIORequest fileIORequest) {
        return mRepository.downloadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.onWifi(), fileIORequest.isWhileCharging(), fileIORequest.getPresentationClass(),
                fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable executeSearch(String query, String column, @NonNull Class presentationClass,
                                    Class dataClass) {
        return mRepository.searchDisk(query, column, presentationClass, dataClass)
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable executeSearch(RealmQuery realmQuery, @NonNull Class presentationClass) {
        return mRepository.searchDisk(realmQuery, presentationClass)
                .compose(applySchedulers());
    }

    @Override
    public Observable readFromResource(String filePath) {
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

    @NonNull
    @Override
    public Observable<String> readFromFile(@NonNull String fullFilePath) {
        try {
            FileInputStream fis = Config.getInstance().getContext().openFileInput(fullFilePath);
            ObjectInputStream is = new ObjectInputStream(fis);
            String data = (String) is.readObject();
            is.close();
            return Observable.just(data);
        } catch (@NonNull ClassNotFoundException | IOException e) {
            e.printStackTrace();
            try {
                return Observable.just(new Gson().fromJson(new InputStreamReader(new FileInputStream(new File(fullFilePath))), String.class));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return Observable.error(e1);
            }
        }
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, String data) {
        return Observable.defer(() -> {
            FileOutputStream fos = null;
            try {
                fos = Config.getInstance().getContext().openFileOutput(fullFilePath, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
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
    public Observable<Boolean> saveToFile(@NonNull String fullFilePath, @NonNull byte[] data) {
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
    @Override
    @NonNull
    public <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler())
                .unsubscribeOn(Schedulers.from(mThreadExecutor));
    }
}
