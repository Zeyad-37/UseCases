package com.zeyad.genericusecase.domain.interactors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.mappers.EntityDataMapper;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.ApiConnectionFactory;
import com.zeyad.genericusecase.data.utils.EntityMapperUtil;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;
import com.zeyad.genericusecase.data.utils.Utils;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class GenericUseCaseFactory {

    private static IGenericUseCase sGenericUseCase;

    public static IGenericUseCase getInstance() {
        return sGenericUseCase;
    }

    /**
     * initializes the Generic Use Case with given context without a DB option.
     *
     * @param context context of application
     */
    public static void initWithoutDB(@NonNull Context context) {
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        ApiConnectionFactory.init();
        GenericUseCase.initWithoutDB(new EntityMapperUtil() {
            @NonNull
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        });
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with given context without a DB option.
     *
     * @param context       context of application
     * @param okhttpBuilder OkHttp3 builder
     * @param cache         cache module
     */
    public static void initWithoutDB(@NonNull Context context, OkHttpClient.Builder okhttpBuilder, Cache cache) {
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        if (okhttpBuilder == null)
            ApiConnectionFactory.init();
        else ApiConnectionFactory.init(okhttpBuilder, cache);
        GenericUseCase.initWithoutDB(new EntityMapperUtil() {
            @NonNull
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        });
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with Realm given context.
     *
     * @param context      context of application
     * @param entityMapper mapper from data layer to presentation layer
     */
    public static void initWithRealm(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper) {
        initCore(context, entityMapper, null, null);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with Realm given context.
     *
     * @param context       context of activity/application
     * @param entityMapper  mapper from data layer to presentation layer
     * @param okhttpBuilder OkHttp3 builder
     * @param entityMapper  cache module
     */
    public static void initWithRealm(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper,
                                     OkHttpClient.Builder okhttpBuilder, Cache cache) {
        initCore(context, entityMapper, okhttpBuilder, cache);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    static void initCore(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper,
                         OkHttpClient.Builder okhttpBuilder, Cache cache) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        if (okhttpBuilder == null)
            ApiConnectionFactory.init();
        else ApiConnectionFactory.init(okhttpBuilder, cache);
        if (entityMapper == null)
            entityMapper = new EntityMapperUtil() {
                @NonNull
                @Override
                public EntityMapper getDataMapper(Class dataClass) {
                    return new EntityDataMapper();
                }
            };
        GenericUseCase.initWithRealm(entityMapper);
    }

    /**
     * This method is meant for test purposes only. Use other versions of initRealm for production code.
     *
     * @param genericUseCase mocked generic use(expected) or any IGenericUseCase implementation
     */
    @VisibleForTesting
    private static void init(IGenericUseCase genericUseCase) {
        sGenericUseCase = genericUseCase;
    }
}