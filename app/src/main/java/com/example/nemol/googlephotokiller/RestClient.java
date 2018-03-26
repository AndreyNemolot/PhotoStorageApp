package com.example.nemol.googlephotokiller;

import android.content.Context;
import android.util.Log;

import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.auth.AuthScope;

/**
 * Created by nemol on 19.10.2017.
 */

public class RestClient {
    private static final String BASE_URL = "http://192.169.1.57:8080/";
    //private static final String BASE_URL = "http://192.168.43.33:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void authorization(String login, String password, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(login, password);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void registration(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth("admin", "admin");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void get(String url, RequestParams params, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(getAbsoluteUrl(url), params, fileAsyncHttpResponseHandler);
    }
}
