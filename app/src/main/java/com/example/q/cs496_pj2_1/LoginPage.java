package com.example.q.cs496_pj2_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class LoginPage extends AppCompatActivity {
    CallbackManager callbackManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        //user permission
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "read_custom_friendlists"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {
                                System.out.println("login seccuesful");
                                //after login, check whether they already logged in before.
                                //if not, post the information to server.
                                String url = "http://13.124.144.112:8080/api/people";
                                String res = "";
                                String userID = "";

                                try {
                                    userID = object.getString("id");
                                    System.out.println(userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                final GetTask getTask = new GetTask();
                                try {
                                    res = getTask.execute(url+"/"+userID).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();

                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                //Log.v("res", res);
                                if(res.contains("error")){
                                    String imageUrl = "https://graph.facebook.com/"+userID+"/picture";

                                    try {
                                        object.put("image", imageUrl);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                final PostTask postTask = new PostTask(url, object);
                                postTask.execute();

                                setResult(RESULT_OK);
                                finish();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
