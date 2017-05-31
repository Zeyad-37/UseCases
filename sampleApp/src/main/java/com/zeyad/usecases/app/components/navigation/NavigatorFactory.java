package com.zeyad.usecases.app.components.navigation;

public final class NavigatorFactory {
    private NavigatorFactory() {
    }

    public static INavigator getInstance() {
        return Navigator.getInstance();
    }
}
