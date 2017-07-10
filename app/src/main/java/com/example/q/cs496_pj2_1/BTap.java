package com.example.q.cs496_pj2_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by q on 2017-07-09.
 */

public class BTap extends Fragment {
    public String userID = AccessToken.getCurrentAccessToken().getUserId();
    public String url = "http://10.0.2.2:8080/api/people";
    public Integer REQUEST_GET_PHOTO = 1;
    TextView name;
    EditText email;
    TextView gender;
    ImageView image;
    int[] images = {R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5,
            R.drawable.img_6, R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10};

    public BTap() {
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.b_tap, null);

        name = (TextView) view.findViewById(R.id.userName);
        email = (EditText) view.findViewById(R.id.userEmail);
        gender = (TextView) view.findViewById(R.id.userGender);
        image = (ImageView) view.findViewById(R.id.image);

        //get user information
        final GetTask getTask = new GetTask();
        String userStr = "";
        JSONObject userJson = null;

        try {
            userStr = getTask.execute(url+"/"+userID).get();
            userJson = new JSONObject(userStr);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //set user information
        try {
            name.setText(userJson.getString("name"));
            email.setText(userJson.getString("email"));
            gender.setText(userJson.getString("gender"));
            //change!! get info from server.
            image.setImageResource(R.drawable.ic_person);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(
                        getActivity().getApplicationContext(),
                        ChoosePhoto.class
                );
                startActivityForResult(photoIntent, REQUEST_GET_PHOTO);
            }
        });

        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the information to server db. (email, image)
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_GET_PHOTO && resultCode == RESULT_OK) {
            /*byte[] imageBytes = data.getByteArrayExtra("imageBytes");
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(imageBitmap);*/
            int position = data.getIntExtra("position", 0);
            if (position < images.length) {
                image.setImageResource(images[position]);
            }else {
                Uri uri = data.getParcelableExtra("uri");
                AssetFileDescriptor afd = null;
                try {
                    afd = getActivity().getContentResolver().openAssetFileDescriptor(uri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
                image.setImageBitmap(bitmap);
            }
        }
    }
}
