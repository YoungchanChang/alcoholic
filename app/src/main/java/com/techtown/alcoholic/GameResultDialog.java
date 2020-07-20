package com.techtown.alcoholic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameResultDialog extends Dialog {
    private Context context;
    TextView textUserNickname1, textUserNickname2, textUserNickname3,textUserScore1,textUserScore2,textUserScore3;
     JSONArray jsonArray;
     Button btnGoHome;

    public GameResultDialog(Context context, JSONArray array) {
        super(context);
        this.context=context;
        this.jsonArray=array;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result_dialog);

        textUserNickname1 = findViewById(R.id.userNickname1);
        textUserNickname2 = findViewById(R.id.userNickname2);
        textUserNickname3 = findViewById(R.id.userNickname3);
        textUserScore1 = findViewById(R.id.resultData1);
        textUserScore2 = findViewById(R.id.resultData2);
        textUserScore3 = findViewById(R.id.resultData3);
        btnGoHome = findViewById(R.id.btnGoHome);

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        try{
            for (int i=0; i<jsonArray.length() ;i++){
                JSONObject jsonChild = jsonArray.getJSONObject(i);
                String userNickname= jsonChild.getString("userNickname");
                String userScore= jsonChild.getString("userScore");
                //결과값 보여줌
                insertTextView(i,userNickname,userScore);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    private void insertTextView(int index, String userNickname,String userScore){
        switch (index){
            case 0:
                textUserNickname1.setText(userNickname);
                textUserScore1.setText(userScore);
                break;
            case 1:
                textUserNickname2.setText(userNickname);
                textUserScore2.setText(userScore);
                break;
            case 2:
                textUserNickname3.setText(userNickname);
                textUserScore3.setText(userScore);
                break;
            default:
                break;
        }
    }
}