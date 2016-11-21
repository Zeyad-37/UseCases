package com.zeyad.genericusecase.domain.repositories;

import rx.Observable;

/**
 * @author zeyad on 11/11/16.
 */
public interface Prefs {
    Observable<String> getString(String preferenceKey, String defaultValue);

    Observable<Integer> getInt(String preferenceKey, int defaultValue);

    Observable<Float> getFloat(String preferenceKey, float defaultValue);

    Observable<Long> getLong(String preferenceKey, long defaultValue);

    Observable<Boolean> getBoolean(String preferenceKey, boolean defaultValue);

    <T> Observable<T> getObject(String preferenceKey, Class<T> classOfT);

    void set(String preKey, String value);

    void set(String key, int value);

    void set(String key, boolean value);

    void set(String key, float value);

    void set(String key, long value);

    void set(String key, Object object);

    void remove(String key);

    boolean contains(String preferencesKey);

    void resetPreferences();
}
