package com.zeyad.genericusecase.data.db;

import android.content.Context;

import org.mockito.Mockito;

public class GeneralRealmManagerImplUtils {

    public static DataBaseManager createDBManagerWithMockedContext(Context mockedContext) {
        final RealmManager generalRealmManager = Mockito.mock(RealmManager.class);
        Mockito.when(generalRealmManager.getContext()).thenReturn(mockedContext);
//        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
//        Mockito.when(sharedPreferences.edit()).thenReturn(null);
//        Mockito.when(mockedContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
//        Mockito.doNothing().when(generalRealmManager).writeToPreferences(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
//        Mockito.doReturn(1L).when(generalRealmManager).getFromPreferences(Mockito.anyString());
        return generalRealmManager;
    }

}
