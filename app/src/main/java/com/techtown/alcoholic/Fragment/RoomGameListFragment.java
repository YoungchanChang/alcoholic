package com.techtown.alcoholic.Fragment;

import android.content.Context;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.techtown.alcoholic.R;


public class RoomGameListFragment extends Fragment {

    View view;
    Context mContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_room_game_list,container,false);
        mContext=getActivity();


        return  view;
    }
}
