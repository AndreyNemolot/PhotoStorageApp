package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.Callback.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.Model.ActiveUser;
import com.example.nemol.googlephotokiller.Model.User;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import cz.msebera.android.httpclient.Header;


/**
 * Created by nemol on 02.11.2017.
 */

public class UserController {

    private final static String USER_URL = "user/user";
    private final static String USERS_URL = "user/users";
    private static CreateAnswerCallback callback;

    public static void registerCallBack(CreateAnswerCallback clb) {
        callback = clb;
    }

    public static void registration(User usr) {
        RestClient.registration(USER_URL, setParams(usr), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.createAnswer(statusCode);
                //201 created
                //409 conflict (user exist)
            }
        });
    }

    public static void authorization() {
        RequestParams params = new RequestParams();
        params.put("login", ActiveUser.getLogin());
        RestClient.authorization(ActiveUser.getLogin(), ActiveUser.getPassword(), USERS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    JSONObject firstEvent = (JSONObject) timeline.get(0);
                        ActiveUser.setId((Integer) firstEvent.get("id"));
                        callback.createAnswer(statusCode);
                } catch (JSONException ex) {
                    callback.createAnswer(statusCode);
                }
            }
        });
    }

    private static RequestParams setParams(User user) {
        RequestParams params = new RequestParams();
        params.put("user_id", "100");
        params.put("login", user.getLogin());
        params.put("enabled", user.getEnabled());
        params.put("role", user.getRole());
        params.put("password", user.getPassword());
        return params;
    }
}
