package com.zeyad.genericusecase.domain.interactor;

import com.zeyad.genericusecase.domain.interactors.files.FileUseCase;
import com.zeyad.genericusecase.domain.interactors.files.FileUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.files.IFileUseCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.io.IOException;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author zeyad on 11/21/16.
 */
@RunWith(JUnit4.class)
public class FileUseCaseTest {

    private IFileUseCase mFilesUseCase;

    @Before
    public void setUp() throws Exception {
        FileUseCase.init();
        mFilesUseCase = FileUseCaseFactory.getInstance();
    }

    public static IFileUseCase createMockedfilesUseCase() {
        final IFileUseCase filesUseCase = Mockito.mock(IFileUseCase.class);

        Mockito.when(filesUseCase.readFromResource(Mockito.anyString()))
                .thenReturn(readResponse());
        Mockito.when(filesUseCase.readFromFile(Mockito.anyString()))
                .thenReturn(readResponse());
        Mockito.when(filesUseCase.saveToFile(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(saveResponse());
        Mockito.when(filesUseCase.saveToFile(Mockito.anyString(), new byte[]{}))
                .thenReturn(saveResponse());
        return filesUseCase;
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
        subscriber.assertError(new IOException());
    }

    @Test
    public void readFromFile() {
        assertThat(mFilesUseCase.readFromFile(""), is(equalTo(readResponse())));
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
