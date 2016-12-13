package com.zeyad.generic.usecase.dataaccesslayer.utils;

import android.support.annotation.Nullable;

import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author by ZIaDo on 10/1/16.
 */

public class Utils {

    @Nullable
    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(@Nullable CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed())
            return new CompositeSubscription();
        return subscription;
    }

    public static void unsubscribeIfNotNull(@Nullable Subscription subscription) {
        if (subscription != null)
            subscription.unsubscribe();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty() && !text.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }
}
