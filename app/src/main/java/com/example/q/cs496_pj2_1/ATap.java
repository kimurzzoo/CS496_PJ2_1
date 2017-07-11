package com.example.q.cs496_pj2_1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;

/**
 * Created by q on 2017-07-09.
 */

class Items1
{
    Items1(String aimageurl1, String aname1, String ascore)
    {
        imageurl = aimageurl1;
        name =aname1;
        score = ascore;
    }
    String imageurl;
    String name;
    String score;
}

class Items
{
    Items(String aimageurl, String aname)
    {
        imageurl = aimageurl;
        name =aname;
    }
    String imageurl;
    String name;
}

class ItemsAdapter extends BaseAdapter
{
    ArrayList<Items> arSrc;
    int layout;
    Context maincon;
    LayoutInflater Inflater;

    public ItemsAdapter(Context context, int alayout, ArrayList<Items> aarSrc)
    {
        maincon = context;
        Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount()
    {
        return arSrc.size();
    }
    public String getItem(int position)
    {
        return arSrc.get(position).name;
    }
    public long getItemId(int position)
    {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        if(convertView == null)
        {
            convertView = Inflater.inflate(layout, parent, false);
        }
        ImageView img = (ImageView)convertView.findViewById(R.id.imagemoth);
        final Bitmap[] bitmaps = new Bitmap[1];
        Thread mThread = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    HttpURLConnection conn = null;
                    String asdfw = arSrc.get(pos).imageurl;
                    if(asdfw.contains("https://graph.facebook.com/") || asdfw.contains("http://") || asdfw.contains("https://"))
                    {
                        URL aurl = new URL(arSrc.get(pos).imageurl);
                        conn = (HttpURLConnection)aurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmaps[0] = BitmapFactory.decodeStream(is);
                    }
                    else
                    {
                        byte[] buf2 = Base64.decode(arSrc.get(pos).imageurl, Base64.DEFAULT);
                        bitmaps[0] = BitmapFactory.decodeByteArray(buf2, 0, buf2.length);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        };

        mThread.start();
        try
        {
            mThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        img.setImageBitmap(bitmaps[0]);

        TextView txt2 = (TextView)convertView.findViewById(R.id.textname);
        txt2.setText(arSrc.get(position).name);
        return convertView;
    }


}

class ItemsAdapter1 extends BaseAdapter
{
    ArrayList<Items1> arSrc;
    int layout;
    Context maincon;
    LayoutInflater Inflater;

    public ItemsAdapter1(Context context, int alayout, ArrayList<Items1> aarSrc)
    {
        maincon = context;
        Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount()
    {
        return arSrc.size();
    }
    public String getItem(int position)
    {
        return arSrc.get(position).name;
    }
    public long getItemId(int position)
    {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        if(convertView == null)
        {
            convertView = Inflater.inflate(layout, parent, false);
        }
        ImageView img = (ImageView)convertView.findViewById(R.id.imagemoth1);
        final Bitmap[] bitmaps = new Bitmap[1];
        Thread mThread = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    HttpURLConnection conn = null;
                    String asdfw = arSrc.get(pos).imageurl;
                    if(asdfw.contains("https://graph.facebook.com/") || asdfw.contains("http://") || asdfw.contains("https://"))
                    {
                        URL aurl = new URL(arSrc.get(pos).imageurl);
                        conn = (HttpURLConnection)aurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmaps[0] = BitmapFactory.decodeStream(is);
                    }
                    else
                    {
                        byte[] buf2 = Base64.decode(arSrc.get(pos).imageurl, Base64.DEFAULT);
                        bitmaps[0] = BitmapFactory.decodeByteArray(buf2, 0, buf2.length);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        };

        mThread.start();
        try
        {
            mThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        img.setImageBitmap(bitmaps[0]);

        TextView txt2 = (TextView)convertView.findViewById(R.id.textname1);
        txt2.setText(arSrc.get(position).name);

        TextView txt3 = (TextView)convertView.findViewById(R.id.score);
        txt3.setText(arSrc.get(position).score);
        return convertView;
    }


}

public class ATap extends Fragment{
    public  ATap() {
    }
    ListView mResult;
    ListView mResult1;
    ItemsAdapter Adapter;
    ItemsAdapter1 Adapter1;

    ArrayList<Items> sPhoneList = new ArrayList<Items>();
    ArrayList<Items1> sPhoneList1 = new ArrayList<Items1>();
    final ArrayList<String> naming = new ArrayList<String>();
    final ArrayList<String> naming1 = new ArrayList<String>();
    final ArrayList<String> photourl1 = new ArrayList<String>();
    final ArrayList<String> photourl = new ArrayList<String>();
    final ArrayList<String> scorelist = new ArrayList<String>();
    final ArrayList<String> scorelist1 = new ArrayList<String>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_a, container, false);
        mResult = (ListView)v.findViewById(R.id.listman);
        mResult1 = (ListView)v.findViewById(R.id.listman1);
        naming.clear();
        photourl.clear();
        naming1.clear();
        photourl1.clear();
        sPhoneList.clear();
        sPhoneList1.clear();
        scorelist.clear();
        scorelist1.clear();

        Thread mThread = new Thread()
        {
            @Override
            public void run() {
                if (AccessToken.getCurrentAccessToken() != null) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    JSONObject jsonObject = response.getJSONObject();
                                    try {
                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            String names = jsonArray.getJSONObject(i).get("name").toString();
                                            naming1.add(names);
                                            String appids = jsonArray.getJSONObject(i).get("id").toString();
                                            GetTask getTask = new GetTask();
                                            try
                                            {
                                                String whatit = getTask.execute("http://13.124.144.112:8080/api/people/" + appids).get();
                                                JSONObject whatobject = new JSONObject(whatit);
                                                photourl1.add(whatobject.get("image").toString());
                                                scorelist1.add(whatobject.get("score").toString());
                                            }
                                            catch (ExecutionException e)
                                            {
                                                e.printStackTrace();
                                            }
                                            catch (InterruptedException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    for(int i=0; i<naming1.size();i++)
                                    {
                                        sPhoneList1.add(new Items1(photourl1.get(i), naming1.get(i), scorelist1.get(i)));
                                    }
                                    Adapter1 = new ItemsAdapter1(getActivity(), R.layout.fragment_a_item2, sPhoneList1);
                                    mResult1.setAdapter(Adapter1);
                                    v.invalidate();
                                }
                            }
                    ).executeAsync();
                }
            }
        };
        mThread.start();
        try
        {
            mThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Thread nThread = new Thread()
        {
            @Override
            public void run() {
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/taggable_friends?limit=1500",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                JSONObject jsonObject = response.getJSONObject();
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String names = jsonArray.getJSONObject(i).get("name").toString();
                                        naming.add(names);
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(i).getJSONObject("picture");
                                        JSONObject jsonObject3 = jsonObject2.getJSONObject("data");
                                        String photourls = jsonObject3.get("url").toString();
                                        photourl.add(photourls);
                                        scorelist.add("0");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject jsonObject1 = null;
                                String jsonStr = "{ \"friends\" : \"[";
                                for (int i = 0; i < naming.size()-1; i++) {
                                    jsonStr += "{ \'name\' : " + "\'" + naming.get(i) + "\'" + "," + "\'imageurl\' : " + "\'" + photourl.get(i) + "\'},";
                                }
                                jsonStr += "{ \'name\' : " + "\'" + naming.get(naming.size()-1) + "\'" + "," + "\'imageurl\' : " + "\'" + photourl.get(naming.size() - 1) + "\'}]\"}";
                                try {
                                    jsonObject1 = new JSONObject(jsonStr);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                PutTask putTask = new PutTask("http://13.124.144.112:8080/api/people/friends/" + AccessToken.getCurrentAccessToken().getUserId(), jsonObject1);
                                putTask.execute("i");

                                for(int i=0; i<naming.size();i++)
                                {
                                    sPhoneList1.add(new Items1(photourl.get(i), naming.get(i),scorelist.get(i)));
                                }
                                Adapter1 = new ItemsAdapter1(getActivity(), R.layout.fragment_a_item2 ,sPhoneList1);
                                mResult1.setAdapter(Adapter1);
                                v.invalidate();
                            }
                        }
                ).executeAsync();
            }
        };
        nThread.start();
        try
        {
            nThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return v;
    }
}
