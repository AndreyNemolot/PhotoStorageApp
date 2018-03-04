package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.Callback.UserControllerCallback;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.User;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by nemol on 02.11.2017.
 */

public class UserController {

    private final static String USER_URL = "user/user";
    private final static String USERS_URL = "user/users";
    private static UserControllerCallback callback;

    public static void registerCallBack(UserControllerCallback clb) {
        callback = clb;
    }

    public static void registration(User user) {
        RequestParams params = new RequestParams();
        params.put("user_id", "100");
        params.put("login", user.getLogin());
        params.put("enabled", user.getEnabled());
        params.put("role", user.getRole());
        params.put("password", user.getPassword());

        RestClient.registration(USER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.userAction(statusCode);
            }
        });
    }

    public static void authorization() {
        RequestParams params = new RequestParams();
        params.put("login", ActiveUser.getLogin());
        RestClient.authorization(ActiveUser.getLogin(), ActiveUser.getPassword(), USERS_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.userAction(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.userAction(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    JSONObject firstEvent = (JSONObject) timeline.get(0);
                        ActiveUser.setId((Integer) firstEvent.get("id"));
                        callback.userAction(statusCode);
                } catch (JSONException ex) {
                    callback.userAction(statusCode);
                }
            }
        });
    }
}
