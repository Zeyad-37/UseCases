package com.zeyad.usecases.app.components.snackbar;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.zeyad.usecases.app.utils.Utils;

import java.lang.annotation.Retention;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SnackBarFactory {

    public static final String TYPE_INFO = "typeInfo";
    public static final String TYPE_ERROR = "typeError";

    public static Snackbar getSnackBar(@SnackBarType String snackBarType, @NonNull View view,
                                       @StringRes int stringId, int duration) {
        return createSnackBar(snackBarType, Snackbar.make(view, stringId, duration));
    }

    public static Snackbar getSnackBar(@SnackBarType String snackBarType, @NonNull View view,
                                       @NonNull CharSequence text, int duration) {
        return createSnackBar(snackBarType, Snackbar.make(view, text, duration));
    }

    public static Snackbar getSnackBarWithAction(@SnackBarType String snackBarType, @NonNull View view,
                                                 @NonNull CharSequence text, String actionText,
                                                 View.OnClickListener onClickListener) {
        return createSnackBar(snackBarType, Snackbar.make(view, text, LENGTH_INDEFINITE)
                .setAction(Utils.isNotEmpty(actionText) ? actionText : "RETRY", onClickListener)
                .setActionTextColor(Color.BLACK));
    }

    private static Snackbar createSnackBar(@SnackBarType String snackBarType, Snackbar snackbar) {
        switch (snackBarType) {
            case TYPE_INFO:
                return ColoredSnackbar.info(snackbar, Color.parseColor("#45d482"));
            case TYPE_ERROR:
                return ColoredSnackbar.error(snackbar, Color.parseColor("#e15D50"));
            default:
                return snackbar;
        }
    }

    @Retention(SOURCE)
    @StringDef({TYPE_INFO, TYPE_ERROR})
    public @interface SnackBarType {
    }
}