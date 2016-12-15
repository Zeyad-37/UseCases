package com.zeyad.usecases.app.components.eventbus;

public class RxEventBusFactory {

    public static IRxEventBus getInstance() {
        return RxEventBus.getInstance();
    }
}
