package com.zeyad.genericusecase.domain.interactors;

import android.content.Context;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;

public class GenericUseCaseFactory {

    private static IGenericUseCase sGenericUseCase;

    public static IGenericUseCase getInstance() {
        return sGenericUseCase;
    }

    /**
     * initializes the Generic Use Case with given context.
     *
     * @param context      context of activity/application
     * @param entityMapper
     */
    public static void init(Context context, IEntityMapperUtil entityMapper) {
        GenericUseCase.init(context, entityMapper);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with given values.
     *
     * @param dataRepository data repository
     * @param jobExecutor    job executor
     * @param uiThread       Ui thread
     */
    public static void init(DataRepository dataRepository, JobExecutor jobExecutor, UIThread uiThread) {
        GenericUseCase.init(dataRepository, jobExecutor, uiThread);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * This method is meant for test purposes only. Use other versions of init for production code.
     *
     * @param genericUseCase mocked generic use(expected) or any IGenericUseCase implementation
     */
    public static void init(IGenericUseCase genericUseCase) {
        sGenericUseCase = genericUseCase;
    }
}