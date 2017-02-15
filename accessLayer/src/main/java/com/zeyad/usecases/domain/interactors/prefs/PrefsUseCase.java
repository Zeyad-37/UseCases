package com.zeyad.usecases.domain.interactors.prefs;

import android.content.Context;

import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.repository.PrefsRepository;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.repositories.Prefs;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/11/16.
 */
public class PrefsUseCase implements IPrefsUseCase {

    private static PrefsUseCase sPrefsUseCases;
    private final Prefs mPrefs;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private PrefsUseCase(Context context, String prefsFileName, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        PrefsRepository.init(context, prefsFileName);
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mPrefs = PrefsRepository.getInstance();
    }

    public PrefsUseCase(Prefs prefs, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mPrefs = prefs;
    }

    public static void init(Context context, String prefsFileName) {
        sPrefsUseCases = new PrefsUseCase(context, prefsFileName, new JobExecutor(), new UIThread());
    }

    public static void init(Context context, String prefsFileName, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        sPrefsUseCases = new PrefsUseCase(context, prefsFileName, threadExecutor, postExecutionThread);
    }

    protected static PrefsUseCase getInstance() {
        if (sPrefsUseCases == null)
            throw new NullPointerException("PrefsUseCase is null. please call PrefsUseCaseFactory#init(context)");
        return sPrefsUseCases;
    }

    @Override
    public Observable<String> getString(String preferenceKey, String defaultValue) {
        return mPrefs.getString(preferenceKey, defaultValue).compose(applySchedulers());
    }

    @Override
    public Observable<String> getString(String preferenceKey) {
        return getString(preferenceKey, "");
    }

    @Override
    public String getStringBlocking(String preferenceKey, String defaultValue) {
        return getString(preferenceKey, defaultValue).toBlocking().first();
    }

    @Override
    public String getStringBlocking(String preferenceKey) {
        return getString(preferenceKey).toBlocking().first();
    }

    @Override
    public Observable<Integer> getInt(String preferenceKey, int defaultValue) {
        return mPrefs.getInt(preferenceKey, defaultValue).compose(applySchedulers());
    }

    @Override
    public Observable<Integer> getInt(String preferenceKey) {
        return getInt(preferenceKey, 0);
    }

    @Override
    public int getIntBlocking(String preferenceKey, int defaultValue) {
        return getInt(preferenceKey, defaultValue).toBlocking().first();
    }

    @Override
    public int getIntBlocking(String preferenceKey) {
        return getInt(preferenceKey).toBlocking().first();
    }

    @Override
    public Observable<Float> getFloat(String preferenceKey, float defaultValue) {
        return mPrefs.getFloat(preferenceKey, defaultValue).compose(applySchedulers());
    }

    @Override
    public Observable<Float> getFloat(String preferenceKey) {
        return getFloat(preferenceKey, 0.0f);
    }

    @Override
    public float getFloatBlocking(String preferenceKey, float defaultValue) {
        return getFloat(preferenceKey, defaultValue).toBlocking().first();
    }

    @Override
    public float getFloatBlocking(String preferenceKey) {
        return getFloat(preferenceKey).toBlocking().first();
    }

    @Override
    public Observable<Long> getLong(String preferenceKey, long defaultValue) {
        return mPrefs.getLong(preferenceKey, defaultValue).compose(applySchedulers());
    }

    @Override
    public Observable<Long> getLong(String preferenceKey) {
        return getLong(preferenceKey, 0L);
    }

    @Override
    public long getLongBlocking(String preferenceKey, long defaultValue) {
        return getLong(preferenceKey, defaultValue).toBlocking().first();
    }

    @Override
    public long getLongBlocking(String preferenceKey) {
        return getLong(preferenceKey).toBlocking().first();
    }

    @Override
    public Observable<Boolean> getBoolean(String preferenceKey, boolean defaultValue) {
        return mPrefs.getBoolean(preferenceKey, defaultValue).compose(applySchedulers());
    }

    @Override
    public Observable<Boolean> getBoolean(String preferenceKey) {
        return getBoolean(preferenceKey, false);
    }

    @Override
    public boolean getBooleanBlocking(String preferenceKey, boolean defaultValue) {
        return getBoolean(preferenceKey, defaultValue).toBlocking().first();
    }

    @Override
    public boolean getBooleanBlocking(String preferenceKey) {
        return getBoolean(preferenceKey).toBlocking().first();
    }

    @Override
    public <T> Observable<T> getObject(String preferenceKey, Class<T> classOfT) {
        return mPrefs.getObject(preferenceKey, classOfT).compose(applySchedulers());
    }

    @Override
    public <T> T getObjectBlocking(String preferenceKey, Class<T> classOfT) {
        return getObject(preferenceKey, classOfT).toBlocking().first();
    }

    @Override
    public void set(String key, String value) {
        mPrefs.set(key, value);
    }

    @Override
    public void set(String key, int value) {
        mPrefs.set(key, value);
    }

    @Override
    public void set(String key, boolean value) {
        mPrefs.set(key, value);
    }

    @Override
    public void set(String key, float value) {
        mPrefs.set(key, value);
    }

    @Override
    public void set(String key, long value) {
        mPrefs.set(key, value);
    }

    @Override
    public void set(String key, Object value) {
        mPrefs.set(key, value);
    }

    @Override
    public void remove(String key) {
        mPrefs.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return mPrefs.contains(key);
    }

    @Override
    public void resetPreferences() {
        mPrefs.resetPreferences();
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler());
    }
}
