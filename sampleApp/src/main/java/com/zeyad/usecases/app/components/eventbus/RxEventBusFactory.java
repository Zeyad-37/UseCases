package com.zeyad.usecases.app.components.eventbus;

public final class RxEventBusFactory {

    private RxEventBusFactory() {
    }

    public static IRxEventBus getInstance() {
        return RxEventBus.getInstance();
    }
}
