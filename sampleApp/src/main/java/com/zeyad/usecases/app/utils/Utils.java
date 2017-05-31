package com.zeyad.usecases.app.utils;

import android.os.Build;

import java.util.List;

/** @author by ZIaDo on 10/1/16. */
public class Utils {

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty() && !text.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static <T> List union(List<T> first, List<T> last) {
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

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
