package com.zeyad.generic.usecase.dataaccesslayer.components.eventbus;

public class RxEventBusFactory {

    public static IRxEventBus getInstance() {
        return RxEventBus.getInstance();
    }
}
