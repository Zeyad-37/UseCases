package com.zeyad.genericusecase.domain.interactors;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.mappers.EntityDataMapper;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.ApiConnectionFactory;
import com.zeyad.genericusecase.data.utils.EntityMapperUtil;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;
import com.zeyad.genericusecase.data.utils.Utils;

public class GenericUseCaseFactory {

    private static IGenericUseCase sGenericUseCase;

    public static IGenericUseCase getInstance() {
        return sGenericUseCase;
    }

    /**
     * initializes the Generic Use Case with given context without a DB option.
     *
     * @param context context of activity/application
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
     * initializes the Generic Use Case with Realm given context.
     *
     * @param context      context of activity/application
     * @param entityMapper
     */
    public static void initWithRealm(@NonNull Context context, @Nullable IEntityMapperUtil entityMapper) {
        initCore(context, null, entityMapper);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    /**
     * initializes the Generic Use Case with SQLBrite given context.
     *
     * @param context      context of application
     * @param entityMapper
     */
    public static void initWithSQLBrite(@NonNull Context context, @NonNull SQLiteOpenHelper sqLiteOpenHelper,
                                        @Nullable IEntityMapperUtil entityMapper) {
        initCore(context, sqLiteOpenHelper, entityMapper);
        sGenericUseCase = GenericUseCase.getInstance();
    }

    static void initCore(@NonNull Context context, SQLiteOpenHelper sqLiteOpenHelper,
                         @Nullable IEntityMapperUtil entityMapper) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(context);
        Config.getInstance().setPrefFileName("com.generic.use.case.PREFS");
        ApiConnectionFactory.init();
        if (entityMapper == null)
            entityMapper = new EntityMapperUtil() {
                @NonNull
                @Override
                public EntityMapper getDataMapper(Class dataClass) {
                    return new EntityDataMapper();
                }
            };
        if (sqLiteOpenHelper == null)
            GenericUseCase.initWithRealm(entityMapper);
        else
            GenericUseCase.initWithSQLBrite(sqLiteOpenHelper, entityMapper);
    }

    /**
     * This method is meant for test purposes only. Use other versions of initRealm for production code.
     *
     * @param genericUseCase mocked generic use(expected) or any IGenericUseCase implementation
     */
    public static void init(IGenericUseCase genericUseCase) {
        sGenericUseCase = genericUseCase;
    }
}