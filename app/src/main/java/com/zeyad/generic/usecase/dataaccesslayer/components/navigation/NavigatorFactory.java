package com.zeyad.generic.usecase.dataaccesslayer.components.navigation;

public class NavigatorFactory {

    public static INavigator getNavigator() {
        return Navigator.getInstance();
    }
}
