package com.example.q.cs496_pj2_1;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by q on 2017-07-09.
 */

public class ATap extends Fragment{
    public  ATap() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_a, container, false);
        friends();
        return v;
    }

    void friends()
    {
        GraphRequest graphRequest;
        if (AccessToken.getCurrentAccessToken() != null) {
            final ArrayList<String> naming = new ArrayList<String>();
            final ArrayList<String> photourl = new ArrayList<String>();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/taggable_friends?limit=1500",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject jsonObject = response.getJSONObject();
                            try
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    String names = jsonArray.getJSONObject(i).get("name").toString();
                                    naming.add(names);
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i).getJSONObject("picture");
                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("data");
                                    String photourls = jsonObject3.get("url").toString();
                                    photourl.add(photourls);
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            JSONObject jsonObject1;
                            String jsonStr = "{ \"friends\" : [";
                            for(int i=0;i<naming.size()-1;i++)
                            {
                                jsonStr += "{ \"name\" : " + "\"" + naming.get(i) + "\"" + "," + "\"imageurl\" : " + "\"" + photourl.get(i) + "\"},";
                            }
                            jsonStr += "{ \"name\" : " + "\"" + naming.get(naming.size()-1) + "\"" + "," + "\"imageurl\" : " + "\"" + photourl.get(naming.size()-1) + "\"}";
                            System.out.println(jsonStr);
                            try
                            {
                                jsonObject1 = new JSONObject(jsonStr);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();

        }

    }
}
