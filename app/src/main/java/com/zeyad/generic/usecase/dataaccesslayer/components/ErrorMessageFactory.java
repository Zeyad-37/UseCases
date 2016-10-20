package com.zeyad.generic.usecase.dataaccesslayer.components;

import android.content.Context;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.genericusecase.data.exceptions.NetworkConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
    }

    /**
     * Creates a String representing an error message.
     *
     * @param context   Context needed to retrieve string resources.
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Context context, Exception exception) {
        if (exception instanceof NetworkConnectionException || exception instanceof UnknownHostException)
            return context.getString(R.string.exception_message_no_connection);
        else if (exception instanceof HttpException)
            try {
                return new JSONObject(((HttpException) exception).response().errorBody().string())
                        .getJSONObject("error").getString("message");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return exception.getMessage();
            }
        return exception.getMessage();
    }
}
