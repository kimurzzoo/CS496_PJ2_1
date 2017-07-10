package com.example.q.cs496_pj2_1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by q on 2017-07-10.
 */

public class ChoosePhoto extends AppCompatActivity {
    final int REQUEST_CAMERA = 1;
    final int REQUEST_TAKE_PHOTO = 2;
    final int REQUEST_CHOOSE_PHOTO = 3;
    GridView gridView;
    String mCurrentPhotoPath;
    FloatingActionButton fab;

    int[] images = {R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5,
            R.drawable.img_6, R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10};

    ArrayList<Uri> uris = new ArrayList<Uri>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_photo);

        gridView = (GridView) findViewById(R.id.photo_list);
        gridView.setAdapter(new ImageAdapter(ChoosePhoto.this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(ChoosePhoto.this, PhotoDetail.class);
                intent.putExtra("images", images);
                intent.putExtra("position", position);
                intent.putExtra("uris", uris);
                startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                if(permissionCamera == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ChoosePhoto.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photoFile = createImageFile();

                    Uri photoUri = FileProvider.getUriForFile(
                            ChoosePhoto.this, BuildConfig.APPLICATION_ID+".provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(mCurrentPhotoPath);

            File file = new File(imageUri.getPath());
            String photoPath = imageUri.getPath();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath, options);

            try{
                ExifInterface exif = new ExifInterface(photoPath);
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);

                imageBitmap = rotate(imageBitmap, exifDegree);
                saveExifFile(imageBitmap, photoPath);
                imageBitmap.recycle();
            } catch (IOException e) {
                e.getStackTrace();
            }

            uris.add(imageUri);
            gridView.invalidateViews();
        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            //intent.putExtra("imageBytes", data.getByteArrayExtra("imageBytes"));
            int position =  data.getIntExtra("position", 0);
            intent.putExtra("position", position);
            if (position >= images.length)
                intent.putExtra("uri", uris.get(position-images.length));
            setResult(RESULT_OK, intent);
            finish();
        }
    }




    public class ImageAdapter extends BaseAdapter {
        private Context context;
        public Bitmap image;

        public ImageAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return images.length+uris.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView imageView;

            if (convertView == null) {
                imageView = new SquareImageView(context);
            } else {
                imageView = (SquareImageView) convertView;
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (position < images.length){
                image = decodeSampledBitmapFromResource(imageView.getResources(), images[position], 100,100);

            } else {
                try {
                    AssetFileDescriptor afd = ChoosePhoto.this.getContentResolver().openAssetFileDescriptor(uris.get(position - images.length), "r");
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 4;

                    image = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imageView.setImageBitmap(image);
            return imageView;
        }
    }

    public class SquareImageView  extends android.support.v7.widget.AppCompatImageView {

        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = 4;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp;
        File storageDir = ChoosePhoto.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;

        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        }catch (IOException e){
        }

        if(image != null)
            mCurrentPhotoPath = "file:"+image.getAbsolutePath();

        return image;
    }

    public int exifOrientationToDegrees(int exifOrientation){
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        Bitmap reBitmap = bitmap;

        if (degrees != 0 && bitmap != null){
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth()/2, (float) bitmap.getHeight()/2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted){
                    reBitmap = converted;
                    bitmap.recycle();;
                    bitmap = null;
                }
            } catch(OutOfMemoryError ex){
            }

        }
        return reBitmap;
    }

    public void saveExifFile(Bitmap imageBitmap, String savePath){
        FileOutputStream fos = null;
        File saveFile = null;

        try {
            saveFile = new File(savePath);
            fos = new FileOutputStream(saveFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }catch (FileNotFoundException e){
        }catch (IOException e){
        }finally {
            try{
                if(fos != null){
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
