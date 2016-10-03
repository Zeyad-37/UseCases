package com.zeyad.generic.usecase.dataaccesslayer.components;

import android.content.Context;

import com.zeyad.genericusecase.data.exceptions.NetworkConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        //empty
    }

    /**
     * Creates a String representing an error message.
     *
     * @param context   Context needed to retrieve string resources.
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Context context, Exception exception) {
        String message = "";
        if (exception instanceof NetworkConnectionException)
            message = "";
        else if (exception instanceof UnknownHostException)
            message = "";
        else if (exception instanceof HttpException) {
            try {
                message = new JSONObject(((HttpException) exception).response().errorBody().string())
                        .getString("error_description");
            } catch (IOException | JSONException e1) {
                try {
                    String fieldName = "";
                    JSONObject json = new JSONObject(((HttpException) exception).response().errorBody().string());
                    List<String> fieldNames = new ArrayList<>();
                    for (int i = 0; i < json.names().length(); i++) {
                        fieldName = json.names().optString(i);
                        if (fieldName.toLowerCase().contains("error"))
                            fieldNames.add(fieldName);
                    }
                    for (String string : fieldNames)
                        if (string.equalsIgnoreCase("error"))
                            fieldName = string;
                    if (!fieldName.equalsIgnoreCase("error"))
                        fieldName = fieldNames.get(0);
                    message = new JSONObject(((HttpException) exception).response().errorBody().string()).getString(fieldName);
                } catch (JSONException | IOException e) {
                    message = "Error de red";
                }
            }
        }
        return message;
    }
}