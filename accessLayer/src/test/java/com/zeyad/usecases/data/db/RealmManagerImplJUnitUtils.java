package com.zeyad.usecases.data.db;

import org.mockito.Mockito;

public class RealmManagerImplJUnitUtils {

    public static DataBaseManager createDBManagerWithMockedContext() {
        final RealmManager generalRealmManager = Mockito.mock(RealmManager.class);
//        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
//        Mockito.when(sharedPreferences.edit()).thenReturn(null);
//        Mockito.when(mockedContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);
//        Mockito.doNothing().when(generalRealmManager).writeToPreferences(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
//        Mockito.doReturn(1L).when(generalRealmManager).getFromPreferences(Mockito.anyString());
        return generalRealmManager;
    }

}
