package com.zeyad.genericusecase.domain.interactors.files;

/**
 * @author zeyad on 11/11/16.
 */

public class FileUseCaseFactory {

    private static IFileUseCase sFilesUseCase;

    public static IFileUseCase getInstance() {
        return sFilesUseCase;
    }

    public static void init() {
        FileUseCase.init();
        sFilesUseCase = FileUseCase.getInstance();
    }
}
