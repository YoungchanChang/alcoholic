package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.techtown.alcoholic.R;

public class RoomGameList2Activity extends AppCompatActivity {
    LinearLayout gameDictionary,gameYoutube,gameShakeIt,gameTenor;

    Button btnRoomInfoFragment,btnRoomGameListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_game_list2);


        btnRoomGameListFragment= findViewById(R.id.btn_RoomGameListFragment);
        btnRoomInfoFragment = findViewById(R.id.btn_RoomInfoFragment);

        btnRoomInfoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(goHome);
                overridePendingTransition(0, 0);

            }
        });
        btnRoomGameListFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(), RoomGameList2Activity.class);
                startActivity(goHome);
                overridePendingTransition(0, 0);
            }
        });


        gameDictionary= findViewById(R.id.gameDictionary);
        gameYoutube= findViewById(R.id.gameYoutube);
        gameTenor= findViewById(R.id.gameTenor);
        gameShakeIt= findViewById(R.id.gameShakeIt);

        gameDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameReadyActivity.class);
                intent.putExtra("gameGenre",1);
            }
        });

        gameYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameReadyActivity.class);
                intent.putExtra("gameGenre",2);
            }
        });
        gameTenor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameReadyActivity.class);
                intent.putExtra("gameGenre",3);
            }
        });
        gameShakeIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameReadyActivity.class);
                intent.putExtra("gameGenre",4);
            }
        });


    }
}
