package com.zeyad.usecases.app.components.exceptions;

import com.zeyad.usecases.data.exceptions.NetworkConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {
    private static final String NO_INTERNET = "Please check your internet connection",
            UNKNOWN_ERROR = "Unknown error";

    /**
     * Creates a String representing an error message.
     *
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Exception exception) {
        if (exception instanceof NetworkConnectionException) {
            NetworkConnectionException networkConnectionException = (NetworkConnectionException) exception;
            if (networkConnectionException.getMessage().isEmpty())
                return NO_INTERNET;
            else return networkConnectionException.getMessage();
        } else if (exception instanceof UnknownHostException)
            return NO_INTERNET;
        else if (exception instanceof HttpException) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(((HttpException) exception).response().errorBody().string());
                return jsonObject.getJSONObject("error").getString("message");
            } catch (IOException | JSONException e) {
                try {
                    if (jsonObject.has("error")) {
                        jsonObject = new JSONObject(((HttpException) exception).response().errorBody().string());
                        if (jsonObject.get("error") instanceof JSONObject) {
                            if (jsonObject.getJSONObject("error").has("message"))
                                return jsonObject.getJSONObject("error").getString("message");
                            else if (jsonObject.getJSONObject("error").has("error_description"))
                                return jsonObject.getJSONObject("error").getString("error_description");
                            else if (jsonObject.getJSONObject("error").has("error"))
                                return jsonObject.getJSONObject("error").getString("error");
                        } else if (jsonObject.has("error_description"))
                            return jsonObject.getString("error_description");
                        else if (jsonObject.get("error") instanceof String)
                            return jsonObject.getString("error");
                    }
                } catch (JSONException | IOException ignored) {
                }
                return exception.getMessage();
            }
        }
        return UNKNOWN_ERROR;
    }
}
