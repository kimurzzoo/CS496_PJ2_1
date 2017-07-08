package com.example.q.cs496_pj2_1;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by q on 2017-07-08.
 */

class PostTask extends AsyncTask<String, String , String> {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private JSONObject json;
    private String url;
    private OkHttpClient client;

    PostTask(String url, Person person) {
        client = new OkHttpClient();
        this.url = url;
        this.json = makeJson(person.name, person.image, person.score);
    }

    @Override
    protected String doInBackground(String... params){
        RequestBody body = RequestBody.create(JSON, json.toString());

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject makeJson (String name, String phoneNumber, String email){
        String info = "{'name':'" + name + "'," +
                "'image':'" + phoneNumber + "'," +
                "'score':'" + email + "'}";
        JSONObject jsonInfo = null;
        try {
            jsonInfo = new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonInfo;
    }
}

