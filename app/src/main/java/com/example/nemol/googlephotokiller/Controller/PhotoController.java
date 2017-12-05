package com.example.nemol.googlephotokiller.Controller;

import android.support.v7.app.AppCompatActivity;
import com.example.nemol.googlephotokiller.CreateAnswerCallback;
import com.example.nemol.googlephotokiller.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nemol on 28.11.2017.
 */

public class PhotoController extends AppCompatActivity {

    private static CreateAnswerCallback callback;

    public static void registerCallBack(CreateAnswerCallback clbk) {
        callback = clbk;
    }

    public static void uploadPhoto() {

        RequestParams params = new RequestParams();
        final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
        File myFile = new File("/storage/emulated/0/wallpapers/2.jpg");
        try {
            params.put("file", myFile, contentType);
            params.put("album_id", 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.setHttpEntityIsRepeatable(true);
        params.setUseJsonStreamer(false);
        RestClient.post("photo/photo", params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.createAnswer(statusCode, "upload");
                //200 done
                //201 created
                //409 conflict (user exist)
            }
        });

    }

}
