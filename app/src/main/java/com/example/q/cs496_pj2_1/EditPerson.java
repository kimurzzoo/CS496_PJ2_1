package com.example.q.cs496_pj2_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by q on 2017-07-08.
 */

public class EditPerson extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText1 = (EditText)findViewById(R.id.text1);
        final EditText editText2 = (EditText)findViewById(R.id.text2);
        final EditText editText3 = (EditText)findViewById(R.id.text3);

        Button btn = (Button)findViewById(R.id.btn);

        btn.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                String name = editText1.getText().toString();
                String phoneNumber = editText2.getText().toString();
                String email = editText3.getText().toString();
                Person person = new Person(name, phoneNumber, email);

                String url = "http://10.0.2.2:8080/api/contacts";
                final PostTask postTask = new PostTask(url, person);
                postTask.execute();
            }
        });
    }

    //show with link
    /*Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            try{
                URL url = new URL(link);
                InputStream is =url.openStream();
                bitmap = BitmapFactory.decodeStream(is);

                //핸들러 사용
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgView.setImageBitmap(bitmap);
                    }
                });
                imgView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });

    thread.start();

    URLConnection conn = url.openConnection();
                            conn.connect();
                            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                            Bitmap bm = BitmapFactory.decodeStream(bis);
                            bis.close();
                            myImage.setImageBitmap(bm);
    */
}

