package com.zeyad.genericusecase.domain.interactors.files;

import rx.Observable;

/**
 * @author zeyad on 11/11/16.
 */

public interface IFilesUseCase {

    /**
     * Returns a string of contents of the file.
     *
     * @param filePath path of the file to read.
     * @return Observable with the String.
     */
    Observable<String> readFromResource(String filePath);

    /**
     * Returns a string of contents of the file.
     *
     * @param fullFilePath path of the file to read.
     * @return Observable with the String.
     */

    Observable<String> readFromFile(String fullFilePath);

    /**
     * Saves a string of data to a file.
     *
     * @param fullFilePath path of the file to read.
     * @return Observable with the boolean of success.
     */
    Observable<Boolean> saveToFile(String fullFilePath, String data);

    /**
     * Saves a byte array of data to a file.
     *
     * @param fullFilePath path of the file to read.
     * @return Observable with the boolean of success.
     */
    Observable<Boolean> saveToFile(String fullFilePath, byte[] data);
}
