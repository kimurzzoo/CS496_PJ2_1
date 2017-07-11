package com.example.q.cs496_pj2_1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by q on 2017-07-09.
 */

public class CTap extends Fragment{
    public CTap() {

    }
    public String userID = AccessToken.getCurrentAccessToken().getUserId();
    public Boolean Start = false;
    public Boolean Playing = false;
    public Boolean Resume = false;
    public Boolean Success = false;
    public Boolean Fail = false;
    public int yellow = Color.rgb(255,234,93);
    public int red = Color.rgb(255,129,24);
    public int blue = Color.rgb(90,114,255);
    public int gray = Color.rgb(176,176,176);
    public int[] colors = new int[]{gray, yellow, red, blue};
    Button gameButton;
    ImageView success;
    ImageView fail;
    TextView scoreText;

    Integer[] gameCells = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0};
    Integer[] userCells = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0};
    public Integer round = 1;
    public int score = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.c_tab, null);
        ImageView cell_00 = (ImageView) view.findViewById(R.id.cell_00);
        ImageView cell_01 = (ImageView) view.findViewById(R.id.cell_01);
        ImageView cell_02 = (ImageView) view.findViewById(R.id.cell_02);
        ImageView cell_10 = (ImageView) view.findViewById(R.id.cell_10);
        ImageView cell_11 = (ImageView) view.findViewById(R.id.cell_11);
        ImageView cell_12 = (ImageView) view.findViewById(R.id.cell_12);
        ImageView cell_20 = (ImageView) view.findViewById(R.id.cell_20);
        ImageView cell_21 = (ImageView) view.findViewById(R.id.cell_21);
        ImageView cell_22 = (ImageView) view.findViewById(R.id.cell_22);
        ImageView cell_30 = (ImageView) view.findViewById(R.id.cell_30);
        ImageView cell_31 = (ImageView) view.findViewById(R.id.cell_31);
        ImageView cell_32 = (ImageView) view.findViewById(R.id.cell_32);
         final ImageView[] cells = new ImageView[]{cell_00, cell_01, cell_02,
                cell_10, cell_11, cell_12, cell_20, cell_21, cell_22, cell_30, cell_31, cell_32};
        success = (ImageView) view.findViewById(R.id.success);
        scoreText = (TextView) view.findViewById(R.id.score);
        fail = (ImageView) view.findViewById(R.id.fail);
        System.out.println("C tab start");

        for (int i=0; i<cells.length; i++) {
            cells[i].setClickable(false);
            cells[i].setFocusable(false);
            final int finalI = i;
            cells[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    imageClickListener(cells, finalI);
                }
            });
        }

        gameButton = (Button) view.findViewById(R.id.gameButton);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Success == true){
                    System.out.println("next round start");
                    for (int i=0; i<cells.length; i++)
                        cells[i].setBackgroundColor(colors[0]);
                    Playing = false;
                    Resume = false;
                    Start = false;
                    Success = false;
                }else if (Fail == true) {
                    Playing = false;
                    Resume = false;
                    Start = false;
                    Fail = false;
                }
                if (Start == false) {
                    System.out.println("game start");
                    fail.setVisibility(View.GONE);
                    success.setVisibility(View.GONE);
                    gameButton.setText("RESUME");
                    Start = true;
                    gameStart(cells);

                } else if (Playing == true) {
                    gameButton.setText("RESTART");
                    System.out.println("game resume");
                    Playing = false;
                    Resume = true;
                    for (int i=0; i<cells.length; i++) {
                        cells[i].setClickable(false);
                        cells[i].setFocusable(false);
                    }
                } else {
                    gameButton.setText("RESUME");
                    System.out.println("game restart");
                    Resume = false;
                    Playing = true;
                    for (int i=0; i<cells.length; i++) {
                        cells[i].setClickable(true);
                        cells[i].setFocusable(true);
                    }
                }
            }
        });

        return view;
    }

    public void imageClickListener(ImageView[] cells, int position) {
        if (Resume == false){
            if (Playing == false){
                for (int i=0; i<cells.length; i++)
                    cells[i].setBackgroundColor(colors[0]);
                Playing = true;
            }

            userCells[position] += 1;
            cells[position].setBackgroundColor(colors[userCells[position]]);

            if (userCells[position] > gameCells[position]){
                System.out.println("fail");
                fail.setVisibility(View.VISIBLE);

                for (int i=0; i<cells.length; i++) {
                    cells[i].setClickable(false);
                    cells[i].setFocusable(false);
                    cells[i].setBackgroundColor(colors[0]);
                }
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        "Score: "+score, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                String url = "http://13.124.144.112:8080/api/people/";
                GetTask getTask = new GetTask();
                String userStr;
                JSONObject userJson;
                String highScore = null;

                try {
                    userStr = getTask.execute(url + userID).get();
                    userJson = new JSONObject(userStr);
                    highScore = userJson.getString("score");
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }

                if (Integer.parseInt(highScore) < score){
                    String jsonInfo = "{'score': '" + score + "'}";
                    final PutTask putTask = new PutTask(url+"score/"+
                            AccessToken.getCurrentAccessToken().getUserId(),jsonInfo);
                    putTask.execute();
                }
                round = 1;
                score = 0;
                scoreText.setText("score: "+score);
                Fail = true;
                gameButton.setText("START GAME!");
            }else {
                score += 1;
                scoreText.setText("score: "+score);
                if (Arrays.equals(gameCells, userCells)){
                    System.out.println("success");
                    success.setVisibility(View.VISIBLE);
                    for (int i=0; i<cells.length; i++) {
                        cells[i].setClickable(false);
                        cells[i].setFocusable(false);
                    }

                    round += 1;
                    score += 5;
                    scoreText.setText("score: "+score);
                    Success = true;
                    gameButton.setText("NEXT ROUND!");
                }
            }
        }
    }

    public void gameStart(final ImageView[] cells){
        gameCells = makeGame();
        userCells = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i = 0; i<gameCells.length; i++){
            switch (gameCells[i]) {
                case 1:
                    cells[i].setBackgroundColor(colors[1]);
                    break;
                case 2:
                    cells[i].setBackgroundColor(colors[2]);
                    break;
                case 3:
                    cells[i].setBackgroundColor(colors[3]);
                    break;
            }
            cells[i].setClickable(true);
            cells[i].setFocusable(true);
        }

        //after 2 second, you should start.
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (Playing == false) {
                        for (int i=0; i<cells.length; i++)
                            cells[i].setBackgroundColor(colors[0]);
                        Playing = true;
                    }
                } catch (InterruptedException e) { }
            }
        };

        mThread.start();
    }

    public Integer[] makeGame() {
        Random random = new Random();
        Integer[] gameCells = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0};

        Integer maxPerCell;
        Integer minTotalCells;
        Integer maxTotalCells;

        if (round >= 24){
            maxPerCell = 3;
            minTotalCells = 10;
            maxTotalCells = 36;
        }else {
            maxPerCell = 1 + round/8;
            maxTotalCells = 8 + round;
            minTotalCells = Math.min(10, maxTotalCells/2);
        }
        Integer totalCells = Math.max(minTotalCells, random.nextInt(maxTotalCells));
        Integer count = 0;
        while (count < totalCells) {
            int pos = random.nextInt(12);
            if (gameCells[pos] < maxPerCell){
                gameCells[pos] += 1;
                count += 1;
            }
        }
        return gameCells;
    }
}
