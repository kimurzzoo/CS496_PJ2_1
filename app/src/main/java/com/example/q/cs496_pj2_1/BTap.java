package com.example.q.cs496_pj2_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by q on 2017-07-09.
 */

public class BTap extends Fragment {
    public AccessToken user = AccessToken.getCurrentAccessToken();
    public String url = "http://13.124.144.112:8080/api/people";
    public String userID;
    public Integer REQUEST_GET_PHOTO = 1;
    TextView nameView;
    EditText emailView;
    TextView genderView;
    ImageView imageView;

    int[] images = {R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5,
            R.drawable.img_6, R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10};

    public BTap() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.b_tap, null);

        if (user != null) {
            userID = AccessToken.getCurrentAccessToken().getUserId();

            nameView = (TextView) view.findViewById(R.id.userName);
            emailView = (EditText) view.findViewById(R.id.userEmail);
            genderView = (TextView) view.findViewById(R.id.userGender);
            imageView = (ImageView) view.findViewById(R.id.image);

            imageView.setClickable(false);
            imageView.setFocusable(false);
            emailView.setFocusableInTouchMode(false);

            //get user information
            final GetTask getTask = new GetTask();
            String userStr = "";
            JSONObject userJson = null;

            try {
                userStr = getTask.execute(url + "/" + userID).get();
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
                nameView.setText(userJson.getString("name"));
                emailView.setText(userJson.getString("email"));
                genderView.setText(userJson.getString("gender"));

                String imageSource = userJson.getString("image");
                Bitmap imageBitmap;
                if(imageSource.contains("https://")) {
                    URL imageUrl = new URL(imageSource);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    imageBitmap = BitmapFactory.decodeStream(is);
                } else {
                    byte[] buf = Base64.decode(imageSource, Base64.DEFAULT);
                    imageBitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
                }
                imageView.setImageBitmap(imageBitmap);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageView.isClickable() && imageView.isFocusable()) {
                        Intent photoIntent = new Intent(
                                getActivity().getApplicationContext(),
                                ChoosePhoto.class
                        );
                        startActivityForResult(photoIntent, REQUEST_GET_PHOTO);
                    }
                }
            });

            final Button button = (Button) view.findViewById(R.id.changeANDsave);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (button.getText().equals("수정")){
                        imageView.setClickable(true);
                        imageView.setFocusable(true);
                        emailView.setFocusableInTouchMode(true);
                        emailView.setClickable(true);
                        emailView.setFocusable(true);
                        button.setText("저장");
                    }else if (button.getText().equals("저장")) {
                        Bitmap imageBitmap = ((BitmapDrawable) (imageView.getDrawable())).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] buf = stream.toByteArray();
                        String encodedImage = Base64.encodeToString(buf, Base64.DEFAULT);

                        String jsonStr = "{'email':'" + emailView.getText().toString() + "', " +
                                "'image':'" + encodedImage + "'}";

                        PutTask putTask = new PutTask("http://13.124.144.112:8080/api/people/"+userID, jsonStr);
                        putTask.execute();

                        emailView.setClickable(false);
                        emailView.setFocusable(false);
                        emailView.setFocusableInTouchMode(false);
                        imageView.setClickable(false);
                        imageView.setFocusable(false);
                        button.setText("수정");
                    }
                }
            });
        }

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_GET_PHOTO && resultCode == RESULT_OK) {
            /*byte[] imageBytes = data.getByteArrayExtra("imageBytes");
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(imageBitmap);*/
            int position = data.getIntExtra("position", 0);
            if (position < images.length) {
                imageView.setImageResource(images[position]);
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
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
