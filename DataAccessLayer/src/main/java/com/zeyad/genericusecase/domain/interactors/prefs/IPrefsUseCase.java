package com.zeyad.genericusecase.domain.interactors.prefs;

import rx.Observable;

/**
 * @author zeyad on 11/11/16.
 */
public interface IPrefsUseCase {

    Observable<String> getString(String preferenceKey, String defaultValue);

    Observable<String> getString(String preferenceKey);

    String getStringBlocking(String preferenceKey, String defaultValue);

    String getStringBlocking(String preferenceKey);

    Observable<Integer> getInt(String preferenceKey, int defaultValue);

    Observable<Integer> getInt(String preferenceKey);

    int getIntBlocking(String preferenceKey, int defaultValue);

    int getIntBlocking(String preferenceKey);

    Observable<Float> getFloat(String preferenceKey, float defaultValue);

    Observable<Float> getFloat(String preferenceKey);

    float getFloatBlocking(String preferenceKey, float defaultValue);

    float getFloatBlocking(String preferenceKey);

    Observable<Long> getLong(String preferenceKey, long defaultValue);

    Observable<Long> getLong(String preferenceKey);

    long getLongBlocking(String preferenceKey, long defaultValue);

    long getLongBlocking(String preferenceKey);

    Observable<Boolean> getBoolean(String preferenceKey, boolean defaultValue);

    Observable<Boolean> getBoolean(String preferenceKey);

    boolean getBooleanBlocking(String preferenceKey, boolean defaultValue);

    boolean getBooleanBlocking(String preferenceKey);

    <T> Observable<T> getObject(String preferenceKey, Class<T> classOfT);

    <T> T getObjectBlocking(String preferenceKey, Class<T> classOfT);

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
