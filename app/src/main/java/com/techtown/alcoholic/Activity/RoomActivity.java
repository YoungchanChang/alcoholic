package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;



import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.techtown.alcoholic.Fragment.RoomGameListFragment;
import com.techtown.alcoholic.Fragment.RoomInfoFragment;

import com.techtown.alcoholic.R;

import java.util.Hashtable;

public class RoomActivity extends AppCompatActivity {
    private String TAG = "RoomLog";

    Button btnRoomInfoFragment,btnRoomGameListFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RoomGameListFragment roomGameListFragment;
    RoomInfoFragment roomInfoFragment;
    Context context;
    Context context2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        context=this;

        fragmentManager = getSupportFragmentManager();

        btnRoomGameListFragment= findViewById(R.id.btn_RoomGameListFragment);
        btnRoomInfoFragment = findViewById(R.id.btn_RoomInfoFragment);


        fragmentTransaction= fragmentManager.beginTransaction();

        btnRoomInfoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag(0);
            }
        });
        btnRoomGameListFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag(1);
            }
        });

        roomGameListFragment = new RoomGameListFragment();
        roomInfoFragment = new RoomInfoFragment();


        fragmentTransaction.replace(R.id.FrameLayout,roomInfoFragment).commitAllowingStateLoss();
        setFrag(0);

    }



    public void setFrag(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.FrameLayout,roomInfoFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                    fragmentTransaction.replace(R.id.FrameLayout,roomGameListFragment);

                fragmentTransaction.commit();
                break;

        }

    }

}
