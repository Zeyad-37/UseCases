package com.zeyad.genericusecase.domain.interactors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.mappers.EntityDataMapper;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.ApiConnectionFactory;
import com.zeyad.genericusecase.data.utils.EntityMapperUtil;
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
    public static void init(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper) {
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        ApiConnectionFactory.init();
        if (entityMapper == null)
            entityMapper = new EntityMapperUtil() {
                @Override
                public EntityMapper getDataMapper(Class dataClass) {
                    return new EntityDataMapper();
                }
            };
        GenericUseCase.init(context, entityMapper);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with given context.
     *
     * @param context context of activity/application
     */
    public static void init(@NonNull Context context) {
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        ApiConnectionFactory.init();
        GenericUseCase.init(context, new EntityMapperUtil() {
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        });
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