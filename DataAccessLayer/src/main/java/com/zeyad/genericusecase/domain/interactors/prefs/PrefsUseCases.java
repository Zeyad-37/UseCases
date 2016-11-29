package com.zeyad.genericusecase.domain.interactors.prefs;

import com.zeyad.genericusecase.data.repository.PrefsRepository;
import com.zeyad.genericusecase.domain.repositories.Prefs;

import rx.Observable;

/**
 * @author zeyad on 11/11/16.
 */

public class PrefsUseCases implements IPrefsUseCase {

    private static PrefsUseCases sPrefsUseCases;
    private final Prefs mPrefs;

    private PrefsUseCases() {
        mPrefs = PrefsRepository.getInstance();
    }

    public static void init() {
        sPrefsUseCases = new PrefsUseCases();
    }

    protected static PrefsUseCases getInstance() {
        if (sPrefsUseCases == null)
            sPrefsUseCases = new PrefsUseCases();
        return sPrefsUseCases;
    }

    @Override
    public Observable<String> getString(String preferenceKey, String defaultValue) {
        return mPrefs.getString(preferenceKey, defaultValue);
    }

    @Override
    public Observable<Integer> getInt(String preferenceKey, int defaultValue) {
        return mPrefs.getInt(preferenceKey, defaultValue);
    }

    @Override
    public Observable<Float> getFloat(String preferenceKey, float defaultValue) {
        return mPrefs.getFloat(preferenceKey, defaultValue);
    }

    @Override
    public Observable<Long> getLong(String preferenceKey, long defaultValue) {
        return mPrefs.getLong(preferenceKey, defaultValue);
    }

    @Override
    public Observable<Boolean> getBoolean(String preferenceKey, boolean defaultValue) {
        return mPrefs.getBoolean(preferenceKey, defaultValue);
    }

    @Override
    public <T> Observable<T> getObject(String preferenceKey, Class<T> classOfT) {
        return mPrefs.getObject(preferenceKey, classOfT);
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
}
