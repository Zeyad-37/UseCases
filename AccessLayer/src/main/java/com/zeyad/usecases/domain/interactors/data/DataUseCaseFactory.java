package com.zeyad.usecases.domain.interactors.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.mappers.EntityDataMapper;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.EntityMapperUtil;
import com.zeyad.usecases.data.utils.IEntityMapperUtil;
import com.zeyad.usecases.data.utils.Utils;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class DataUseCaseFactory {

    private static IDataUseCase sGenericUseCase;

    public static IDataUseCase getInstance() {
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
        DataUseCase.initWithoutDB(new EntityMapperUtil() {
            @NonNull
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        });
        sGenericUseCase = DataUseCase.getInstance();
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
        DataUseCase.initWithoutDB(new EntityMapperUtil() {
            @NonNull
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                return new EntityDataMapper();
            }
        });
        sGenericUseCase = DataUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with Realm given context.
     *
     * @param context      context of application
     * @param entityMapper mapper from data layer to presentation layer
     */
    public static void initWithRealm(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper) {
        initCore(context, entityMapper, null, null);
        sGenericUseCase = DataUseCase.getInstance();
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
        sGenericUseCase = DataUseCase.getInstance();
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
        DataUseCase.initWithRealm(entityMapper);
    }

    public static void destoryInstance() {
        sGenericUseCase = null;
    }

    /**
     * This method is meant for test purposes only. Use other versions of initRealm for production code.
     *
     * @param genericUseCase mocked generic use(expected) or any IDataUseCase implementation
     */
    @VisibleForTesting
    private static void init(IDataUseCase genericUseCase) {
        sGenericUseCase = genericUseCase;
    }
}