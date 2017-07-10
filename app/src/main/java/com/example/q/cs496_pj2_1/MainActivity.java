package com.example.q.cs496_pj2_1;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private FragmentTabHost mTabHost;
    public Integer REQUEST_LOGIN = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AccessToken.getCurrentAccessToken() == null) {
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivityForResult(loginIntent, REQUEST_LOGIN);
        }
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Friends", null), ATap.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("My Page", null), BTap.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Game", null), CTap.class, null);
        mTabHost.setCurrentTab(1);
        if (AccessToken.getCurrentAccessToken() != null) {
            //System.out.println("already login");
            mTabHost.setCurrentTab(1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
            //System.out.println("login finished");
            mTabHost.setCurrentTab(1);
        }
    }
}
