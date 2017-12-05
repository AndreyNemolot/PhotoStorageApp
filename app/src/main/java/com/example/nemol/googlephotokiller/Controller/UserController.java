package com.example.nemol.googlephotokiller.Controller;


import com.example.nemol.googlephotokiller.CreateAnswerCallback;
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
    private final static int workload = 4;
    private static CreateAnswerCallback callback;

    public static void registerCallBack(CreateAnswerCallback clb) {
        callback = clb;
    }

    public static void registration(User usr) {
        ActiveUser.setLogin("admin");
        ActiveUser.setPassword("admin");
        RestClient.post(USER_URL, setParams(usr), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.createAnswer(statusCode, "registration");
                //201 created
                //409 conflict (user exist)
            }
        });
    }

    public static void authorization() {
        RestClient.get("photo/photos", null, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    JSONObject firstEvent = (JSONObject) timeline.get(0);
                    callback.createAnswer(statusCode, "authorization");

                } catch (JSONException ex) {
                }
            }
        });
    }

    private static String passwordEncode(String password) {
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(password, salt);
    }

    private static RequestParams setParams(User user) {
        RequestParams params = new RequestParams();
        params.put("login", user.getLogin());
        params.put("enabled", user.getEnabled());
        params.put("role", user.getRole());
        params.put("password", passwordEncode(user.getPassword()));
        return params;
    }
}
