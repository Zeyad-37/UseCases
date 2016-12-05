package com.zeyad.usecases.domain.interactors;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.domain.interactors.files.FileUseCaseFactory;
import com.zeyad.usecases.domain.interactors.files.IFileUseCase;
import com.zeyad.usecases.domain.repositories.Files;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.FileNotFoundException;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author zeyad on 11/21/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileUseCaseTest {

    private IFileUseCase mFilesUseCase;

    @Before
    public void setUp() throws Exception {
        FileUseCaseFactory.init(RuntimeEnvironment.application.getApplicationContext());
        mFilesUseCase = FileUseCaseFactory.getInstance();
    }

    public static Files createMockedFilesUseCase() {
        final Files files = Mockito.mock(Files.class);
        Mockito.when(files.readFromResource(Mockito.anyString()))
                .thenReturn(readResponse());
        Mockito.when(files.readFromFile(Mockito.anyString()))
                .thenReturn(readResponse());
        Mockito.when(files.saveToFile(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(saveResponse());
        Mockito.when(files.saveToFile(Mockito.anyString(), new byte[]{}))
                .thenReturn(saveResponse());
        return files;
    }

    public static Observable<String> readResponse() {
        return Observable.just("");
    }

    public static Observable<Boolean> saveResponse() {
        return Observable.just(true);
    }

    public Observable FileNotFoundException() {
        return Observable.error(new FileNotFoundException());
    }

    @Test
    public void readFromResource() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        mFilesUseCase.readFromResource("").subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertError(new NullPointerException());
    }

    @Test
    public void readFromFile() {
//        assertThat(mFilesUseCase.readFromFile(""), is(equalTo(readResponse())));
        mFilesUseCase.readFromFile("").subscribe(new TestSubscriber<>());
        Mockito.verify(createMockedFilesUseCase()).readFromFile("");
    }

    @Test
    public void saveToFile() {
        assertThat(mFilesUseCase.saveToFile("", ""), is(equalTo(readResponse())));
    }

    @Test
    public void saveToFile2() {
        assertThat(mFilesUseCase.saveToFile("", new byte[]{}), is(equalTo(readResponse())));
    }
}
