package com.example.q.cs496_pj2_1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class Stringman{
    String text1;
    String text2;
    Stringman(String atext1, String atext2)
    {
        text1 = atext1;
        text2 = atext2;
    }
}


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText1 = (EditText)findViewById(R.id.text1);
        final EditText editText2 = (EditText)findViewById(R.id.text2);
        Button btn1 = (Button)findViewById(R.id.btn);
        final Progressi progressi = new Progressi(this);
        btn1.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                String first = editText1.getText().toString();
                String second = editText2.getText().toString();
                Stringman texts1 = new Stringman(first,second);
                progressi.execute(texts1);
            }
        });
    }
}

class Progressi extends AsyncTask<Stringman,String,String>{
    String jsonStr = null;
    OkHttpClient client = new OkHttpClient();
    private Context context;
    public Progressi (Context myContext)
    {
        this.context = myContext;
    }
    @Override
    protected String doInBackground(Stringman... text1) {
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("http").host("127.0.0.1").port(8080).encodedPath("/api/phones").build();
        //HttpUrl httpUrl = new HttpUrl.Builder().scheme("http").host("graph.facebook.com").encodedPath("/me/photos").build();
        String url = httpUrl.toString();
        System.out.println(url);
        /*try
        {
            InputStream stream = context.getAssets().open("addfile.json");
            byte[] buf = new byte[stream.available()];
            stream.read(buf); stream.close();
            jsonStr = new String(buf, "UTF-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        JSONObject jsonInput = null;
        try{
            jsonInput = new JSONObject(jsonStr);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }*/
        System.out.println(text1[0].text1);
        System.out.println(text1[0].text2);
        System.out.println("1");
        JSONObject jsonInput = null;
        System.out.println("2");
        jsonStr = "{" + "\"title\"" + " : " +  "\"" +text1[0].text1  + "\""+ ","
                + "\"author\"" + " : " + "\"" + text1[0].text2 + "\"" +"}";
        System.out.println("3");
        try{
            jsonInput = new JSONObject(jsonStr);
            System.out.println("4");
        }
        catch (JSONException e)
        {
            System.out.println("5");
            e.printStackTrace();
        }
        String aka;
        System.out.println("6");
        try
        {
            System.out.println("7");
            aka = post (url, jsonInput);
            //aka = get(url);
            System.out.println(aka);
        }
        catch (IOException e)
        {
            System.out.println("8");
            e.printStackTrace();
        }
        return null;
    }
    String post (String httpUrl, JSONObject jsonInput) throws IOException
    {
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInput.toString());
        Request request =new Request.Builder().url(httpUrl).post(reqBody).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    String get (String httpUrl) throws IOException
    {
        Request request =new Request.Builder().url(httpUrl).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
