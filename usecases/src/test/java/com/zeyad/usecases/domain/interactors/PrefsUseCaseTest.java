package com.zeyad.usecases.domain.interactors;

import android.content.Context;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.interactors.prefs.IPrefsUseCase;
import com.zeyad.usecases.domain.interactors.prefs.PrefsUseCase;
import com.zeyad.usecases.domain.repositories.Prefs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author zeyad on 11/21/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PrefsUseCaseTest {
    private IPrefsUseCase prefsUseCase;
    private Prefs mPrefs;
    private Observable observable;

    @Before
    public void setUp() throws Exception {
        observable = Observable.just(new Object());
        PrefsUseCase.init(mock(Context.class), "");
        mPrefs = mock(Prefs.class);
        prefsUseCase = new PrefsUseCase(mPrefs, new JobExecutor(), new UIThread());
    }

    @Test
    public void getString() {
        when(mPrefs.getString(anyString(), anyString())).thenReturn(observable);
        prefsUseCase.getString("");
        verify(mPrefs, times(1)).getString(anyString(), anyString());
    }

    @Test
    public void getInt() {
        when(mPrefs.getInt(anyString(), anyInt())).thenReturn(observable);
        prefsUseCase.getInt("");
        verify(mPrefs, times(1)).getInt(anyString(), anyInt());
    }

    @Test
    public void getFloat() {
        when(mPrefs.getFloat(anyString(), anyFloat())).thenReturn(observable);
        prefsUseCase.getFloat("");
        verify(mPrefs, times(1)).getFloat(anyString(), anyFloat());
    }

    @Test
    public void getLong() {
        when(mPrefs.getLong(anyString(), anyLong())).thenReturn(observable);
        prefsUseCase.getLong("");
        verify(mPrefs, times(1)).getLong(anyString(), anyLong());
    }

    @Test
    public void getBoolean() {
        when(mPrefs.getBoolean(anyString(), anyBoolean())).thenReturn(observable);
        prefsUseCase.getBoolean("");
        verify(mPrefs, times(1)).getBoolean(anyString(), anyBoolean());
    }

    @Test
    public void getObject() {
        when(mPrefs.getObject(anyString(), any())).thenReturn(observable);
        prefsUseCase.getObject("", Object.class);
        verify(mPrefs, times(1)).getObject(anyString(), any(Class.class));
    }

    @Test
    public void setString() {
        prefsUseCase.set("", "");
        verify(mPrefs, times(1)).set(anyString(), anyString());
    }

    @Test
    public void setInt() {
        prefsUseCase.set("", 0);
        verify(mPrefs, times(1)).set(anyString(), anyInt());
    }

    @Test
    public void setBoolean() {
        prefsUseCase.set("", false);
        verify(mPrefs, times(1)).set(anyString(), anyBoolean());
    }

    @Test
    public void setFloat() {
        prefsUseCase.set("", 0.0f);
        verify(mPrefs, times(1)).set(anyString(), anyFloat());
    }

    @Test
    public void setLong() {
        prefsUseCase.set("", 0l);
        verify(mPrefs, times(1)).set(anyString(), anyLong());
    }

    @Test
    public void setObject() {
        prefsUseCase.set("", new Object());
        verify(mPrefs, times(1)).set(anyString(), any(Object.class));
    }

    @Test
    public void remove() {
        prefsUseCase.remove("");
        verify(mPrefs, times(1)).remove(anyString());
    }

    @Test
    public void contains() {
        prefsUseCase.contains("");
        verify(mPrefs, times(1)).contains(anyString());
    }

    @Test
    public void resetPreferences() {
        prefsUseCase.resetPreferences();
        verify(mPrefs, times(1)).resetPreferences();
    }
}
