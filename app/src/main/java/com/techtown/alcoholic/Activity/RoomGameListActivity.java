package com.techtown.alcoholic.Activity;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.techtown.alcoholic.Activity.GameReadyActivity;
import com.techtown.alcoholic.R;


public class RoomGameListActivity extends AppCompatActivity {

    View view;
    Context mContext;
    LinearLayout gameDictionary,gameYoutube,gameShakeIt,gameTenor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_room_game_list);

    gameDictionary= findViewById(R.id.gameDictionary);
        gameYoutube= findViewById(R.id.gameYoutube);
        gameTenor= findViewById(R.id.gameTenor);
        gameShakeIt= findViewById(R.id.gameShakeIt);

        gameDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameReadyActivity.class);
                intent.putExtra("gameGenre",1);
            }
        });

        gameYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameReadyActivity.class);
                intent.putExtra("gameGenre",2);
            }
        });
        gameTenor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameReadyActivity.class);
                intent.putExtra("gameGenre",3);
            }
        });
        gameShakeIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameReadyActivity.class);
                intent.putExtra("gameGenre",4);
            }
        });


    }
}
