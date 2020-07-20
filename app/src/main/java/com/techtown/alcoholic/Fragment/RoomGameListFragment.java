package com.techtown.alcoholic.Fragment;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.techtown.alcoholic.Activity.GameReadyActivity;
import com.techtown.alcoholic.R;


public class RoomGameListFragment extends Fragment {

    View view;
    Context mContext;
    LinearLayout gameDictionary,gameYoutube,gameShakeIt,gameTenor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_room_game_list,container,false);
        mContext=getActivity();

        gameDictionary= view.findViewById(R.id.gameDictionary);
        gameYoutube= view.findViewById(R.id.gameYoutube);
        gameTenor= view.findViewById(R.id.gameTenor);
        gameShakeIt= view.findViewById(R.id.gameShakeIt);

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

        return  view;
    }
}
