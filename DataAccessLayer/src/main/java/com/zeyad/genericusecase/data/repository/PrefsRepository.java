package com.zeyad.genericusecase.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.domain.repository.Prefs;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


public class PrefsRepository implements Prefs {

    private static final String SETTINGS_FILE_NAME = "com.genericusecase.PREFS";
    private static Prefs sInstance;
    private static SharedPreferences sharedPreferences;
    private static Scheduler mSchedulers;

    private PrefsRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        mSchedulers = Schedulers.io();
    }

    public static Prefs getInstance() {
        if (sInstance == null)
            sInstance = new PrefsRepository(Config.getInstance().getContext());
        return sInstance;
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
        return Observable.defer(() -> Observable.just(new Gson().fromJson(getString(preferenceKey, "")
                .toBlocking().first(), classOfT)));
    }

    @Override
    public void set(String preKey, String value) {
        Observable.defer(() -> Observable.just(getEditor().putString(preKey, value).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void set(String key, int value) {
        Observable.defer(() -> Observable.just(getEditor().putInt(key, value).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void set(String key, boolean value) {
        Observable.defer(() -> Observable.just(getEditor().putBoolean(key, value).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void set(String key, float value) {
        Observable.defer(() -> Observable.just(getEditor().putFloat(key, value).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void set(String key, long value) {
        Observable.defer(() -> Observable.just(getEditor().putLong(key, value).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void set(String key, Object object) {
        Observable.defer(() -> Observable.just(getEditor().putString(key, new Gson().toJson(object))
                .commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void remove(String key) {
        Observable.defer(() -> Observable.just(getEditor().remove(key).commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public void resetPreferences() {
        Observable.defer(() -> Observable.just(getEditor().clear().commit()))
                .subscribeOn(mSchedulers)
                .subscribe();
    }

    @Override
    public boolean contains(String preferencesKey) {
        return getPreferencesFile().contains(preferencesKey);
    }
}
