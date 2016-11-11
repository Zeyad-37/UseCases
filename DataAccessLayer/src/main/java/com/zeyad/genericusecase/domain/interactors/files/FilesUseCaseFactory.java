package com.zeyad.genericusecase.domain.interactors.files;

/**
 * @author zeyad on 11/11/16.
 */

public class FilesUseCaseFactory {

    private static IFilesUseCase sFilesUseCase;

    public static IFilesUseCase getInstance() {
        return sFilesUseCase;
    }

    public static void init() {
        FilesUseCase.init();
        sFilesUseCase = FilesUseCase.getInstance();
    }
}
