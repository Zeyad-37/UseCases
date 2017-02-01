package com.zeyad.usecases.app.utils;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

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
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Log.d("Subscription", "unsubscribing");
        }
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty() && !text.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static List union(List first, List last) {
        if (isNotEmpty(first) && isNotEmpty(last)) {
            first.addAll(last);
            return first;
        } else if (isNotEmpty(first) && !isNotEmpty(last)) {
            return first;
        }
        return last;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
