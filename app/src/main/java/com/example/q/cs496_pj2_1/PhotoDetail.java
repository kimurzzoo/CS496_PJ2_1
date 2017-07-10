package com.example.q.cs496_pj2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by q on 2017-07-10.
 */

public class PhotoDetail extends AppCompatActivity {

    int position;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.photo_detail);
        FloatingActionButton fab;

        Intent intent = getIntent();
        final int[] images = intent.getIntArrayExtra("images");
        position = intent.getIntExtra("position", 0);
        final ArrayList<Uri> uris = intent.getParcelableArrayListExtra("uris");

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (position < images.length){
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(images[position]);
            imageBitmap = drawable.getBitmap();
        } else {
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris.get(position-images.length));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(imageBitmap);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                /*
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
                byte[] imageBytes = stream.toByteArray();

                intent.putExtra("imageBytes", imageBytes);
                */

                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
