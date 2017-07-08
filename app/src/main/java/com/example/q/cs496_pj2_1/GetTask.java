package com.example.q.cs496_pj2_1;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by q on 2017-07-08.
 */

class GetTask extends AsyncTask<String, String , String> {
    private OkHttpClient client;

    GetTask(){
        client = new OkHttpClient();
    }

    @Override
    protected String doInBackground(String... url){
        final Request request = new Request.Builder()
                .url(url[0])
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}