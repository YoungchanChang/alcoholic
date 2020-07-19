package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.techtown.alcoholic.R;

public class RoomStartActivity extends AppCompatActivity implements AutoPermissionsListener, View.OnClickListener {

    Button btnShakeIt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_start);
        btnShakeIt = findViewById(R.id.btnShakeIt);
        btnShakeIt.setOnClickListener(this);

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
        Toast.makeText(this, "permissions denied : " + permissions.length,
        Toast.LENGTH_LONG).show();
        }

@Override
public void onGranted(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShakeIt:
                Intent intent = new Intent(RoomStartActivity.this,GameShakeItActivity.class);
                startActivity(intent);
                break;
        }
    }
}
