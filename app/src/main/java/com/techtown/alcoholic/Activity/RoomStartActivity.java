package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.techtown.alcoholic.R;

public class RoomStartActivity extends AppCompatActivity implements AutoPermissionsListener, View.OnClickListener {
    private static final String TAG = "GameLog";
    Button btnShakeIt;
    Button btnImageGame,btnInitialSound,btnYoutube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_start);
        btnShakeIt = findViewById(R.id.btnShakeIt);
        btnShakeIt.setOnClickListener(this);
        btnImageGame = findViewById(R.id.btnImageGame);
        btnImageGame.setOnClickListener(this);
        btnInitialSound= findViewById(R.id.btnInitialSound);
        btnInitialSound.setOnClickListener(this);
        btnYoutube= findViewById(R.id.btnYoutubeViews);
        btnYoutube.setOnClickListener(this);
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

@Override
public void onRequestPermissionsResult(int requestCode, String permissions[],
        int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
        }

@Override
public void onDenied(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
        }

@Override
public void onGranted(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShakeIt:
                Intent intent = new Intent(RoomStartActivity.this,GameShakeItActivity.class);
                startActivity(intent);
                break;
            case R.id.btnImageGame:
                Intent intent2 = new Intent(RoomStartActivity.this,GameImageActivity.class);
                startActivity(intent2);
                Log.d(TAG, "onClick: ");
                break;
            case R.id.btnInitialSound:
                Intent intent3 = new Intent(RoomStartActivity.this,GameInitialSound.class);
                startActivity(intent3);
                break;
            case R.id.btnYoutubeViews:
                Intent intent4 = new Intent(RoomStartActivity.this,GameYoutubeViewsActivity.class);
                startActivity(intent4);
                break;
            default:
                Log.d(TAG, "defaultTest");
        }
    }
}
