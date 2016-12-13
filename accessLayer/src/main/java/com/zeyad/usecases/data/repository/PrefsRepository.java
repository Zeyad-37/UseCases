package com.zeyad.usecases.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.domain.repositories.Prefs;

import rx.Observable;


public class PrefsRepository implements Prefs {

    private static Prefs sInstance;
    private static Gson mGson;
    private static SharedPreferences sharedPreferences;

    private PrefsRepository(Context context, String prefsFileName) {
        sharedPreferences = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        mGson = Config.getGson();
    }

    public static Prefs getInstance() throws IllegalArgumentException {
        if (sInstance == null)
            throw new NullPointerException("PrefsUseCase is null. please call PrefsUseCaseFactory#init");
        return sInstance;
    }

    public static void init(Context context, String prefsFileName) {
        sInstance = new PrefsRepository(context, prefsFileName);
    }

    private synchronized SharedPreferences getPreferencesFile() {
        return sharedPreferences;
    }

    private SharedPreferences.Editor getEditor() {
        return getPreferencesFile().edit();
    }

    @Override
    public Observable<String> getString(String preferenceKey, String defaultValue) {
        return Observable.defer(() -> Observable.just(getPreferencesFile().getString(preferenceKey,
                defaultValue)));
    }

    @Override
    public Observable<Integer> getInt(String preferenceKey, int defaultValue) {
        return Observable.defer(() -> Observable.just(getPreferencesFile().getInt(preferenceKey,
                defaultValue)));
    }

    @Override
    public Observable<Float> getFloat(String preferenceKey, float defaultValue) {
        return Observable.defer(() -> Observable.just(getPreferencesFile().getFloat(preferenceKey,
                defaultValue)));
    }

    @Override
    public Observable<Long> getLong(String preferenceKey, long defaultValue) {
        return Observable.defer(() -> Observable.just(getPreferencesFile().getLong(preferenceKey,
                defaultValue)));
    }

    @Override
    public Observable<Boolean> getBoolean(String preferenceKey, boolean defaultValue) {
        return Observable.defer(() -> Observable.just(getPreferencesFile().getBoolean(preferenceKey,
                defaultValue)));
    }

    @Override
    public <T> Observable<T> getObject(String preferenceKey, Class<T> classOfT) {
        return Observable.defer(() -> Observable.just(mGson.fromJson(getString(preferenceKey, "")
                .toBlocking().first(), classOfT)));
    }

    @Override
    public void set(String preKey, String value) {
        Observable.defer(() -> Observable.just(getEditor().putString(preKey, value).commit()));
    }

    @Override
    public void set(String key, int value) {
        Observable.defer(() -> Observable.just(getEditor().putInt(key, value).commit()));
    }

    @Override
    public void set(String key, boolean value) {
        Observable.defer(() -> Observable.just(getEditor().putBoolean(key, value).commit()));
    }

    @Override
    public void set(String key, float value) {
        Observable.defer(() -> Observable.just(getEditor().putFloat(key, value).commit()));
    }

    @Override
    public void set(String key, long value) {
        Observable.defer(() -> Observable.just(getEditor().putLong(key, value).commit()));
    }

    @Override
    public void set(String key, Object object) {
        Observable.defer(() -> Observable.just(getEditor().putString(key, mGson.toJson(object))
                .commit()));
    }

    @Override
    public void remove(String key) {
        Observable.defer(() -> Observable.just(getEditor().remove(key).commit()));
    }

    @Override
    public void resetPreferences() {
        Observable.defer(() -> Observable.just(getEditor().clear().commit()));
    }

    @Override
    public boolean contains(String preferencesKey) {
        return getPreferencesFile().contains(preferencesKey);
    }
}
