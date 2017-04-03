package com.zeyad.usecases.app.components.exceptions;

import com.zeyad.usecases.data.exceptions.NetworkConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Factory used to create errorState messages from an Exception as a condition.
 */
public class ErrorMessageFactory {
    private static final String NO_INTERNET = "Please check your internet connection",
            UNKNOWN_ERROR = "Unknown errorState";

    /**
     * Creates a String representing an errorState message.
     *
     * @param exception An exception used as a condition to retrieve the correct errorState message.
     * @return {@link String} an errorState message.
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
                return jsonObject.getJSONObject("errorState").getString("message");
            } catch (IOException | JSONException e) {
                try {
                    if (jsonObject.has("errorState")) {
                        jsonObject = new JSONObject(((HttpException) exception).response().errorBody().string());
                        if (jsonObject.get("errorState") instanceof JSONObject) {
                            if (jsonObject.getJSONObject("errorState").has("message"))
                                return jsonObject.getJSONObject("errorState").getString("message");
                            else if (jsonObject.getJSONObject("errorState").has("error_description"))
                                return jsonObject.getJSONObject("errorState").getString("error_description");
                            else if (jsonObject.getJSONObject("errorState").has("errorState"))
                                return jsonObject.getJSONObject("errorState").getString("errorState");
                        } else if (jsonObject.has("error_description"))
                            return jsonObject.getString("error_description");
                        else if (jsonObject.get("errorState") instanceof String)
                            return jsonObject.getString("errorState");
                    }
                } catch (JSONException | IOException ignored) {
                }
                return exception.getMessage();
            }
        }
        return UNKNOWN_ERROR;
    }
}
