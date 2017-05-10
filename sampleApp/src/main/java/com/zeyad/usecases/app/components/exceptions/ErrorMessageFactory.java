package com.zeyad.usecases.app.components.exceptions;

import com.zeyad.usecases.exceptions.NetworkConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Factory used to create errorResult messages from an Exception as a condition.
 */
public class ErrorMessageFactory {
    private static final String NO_INTERNET = "Please check your internet connection",
            UNKNOWN_ERROR = "Unknown errorResult";

    /**
     * Creates a String representing an errorResult message.
     *
     * @param exception An exception used as a condition to retrieve the correct errorResult message.
     * @return {@link String} an errorResult message.
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
                return jsonObject.getJSONObject("errorResult").getString("message");
            } catch (IOException | JSONException e) {
                try {
                    if (jsonObject.has("errorResult")) {
                        jsonObject = new JSONObject(((HttpException) exception).response().errorBody().string());
                        if (jsonObject.get("errorResult") instanceof JSONObject) {
                            if (jsonObject.getJSONObject("errorResult").has("message"))
                                return jsonObject.getJSONObject("errorResult").getString("message");
                            else if (jsonObject.getJSONObject("errorResult").has("error_description"))
                                return jsonObject.getJSONObject("errorResult").getString("error_description");
                            else if (jsonObject.getJSONObject("errorResult").has("errorResult"))
                                return jsonObject.getJSONObject("errorResult").getString("errorResult");
                        } else if (jsonObject.has("error_description"))
                            return jsonObject.getString("error_description");
                        else if (jsonObject.get("errorResult") instanceof String)
                            return jsonObject.getString("errorResult");
                    }
                } catch (JSONException | IOException ignored) {
                }
                return exception.getMessage();
            }
        }
        return UNKNOWN_ERROR;
    }
}
