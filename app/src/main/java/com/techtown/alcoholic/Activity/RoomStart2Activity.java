package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.techtown.alcoholic.R;
import com.bumptech.glide.Glide;
public class RoomStart2Activity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RoomStart";

    ImageView image_gif;
    ImageButton imageMakeRoom;
    ImageButton imageFindRoom;


    AlertDialog.Builder ad;

    AlertDialog.Builder adFindRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_start2);

        image_gif = findViewById(R.id.image_gif);
        Glide.with(this).asGif().load(R.drawable.tenor).into(image_gif);

        imageMakeRoom = findViewById(R.id.imageMakeRoom);
        imageMakeRoom.setOnClickListener(this);

        imageFindRoom = findViewById(R.id.imageFindRoom);
        imageFindRoom.setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageMakeRoom:
                showDialogue();
                ad.show();
                break;
            case R.id.imageFindRoom:
                showDialogueFindRoom();
                adFindRoom.show();

                break;
        }
    }

    public void showDialogue(){
        ad = new AlertDialog.Builder(RoomStart2Activity.this);
        ad.setTitle("방장 닉네임 설정");       // 제목 설정
        ad.setMessage("방장의 닉네임을 설정해 주세요.");   // 내용 설정

        final EditText et = new EditText(RoomStart2Activity.this);
        ad.setView(et);


        ad.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();

            }
        });

        ad.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value = et.getText().toString();

                //로그인 성공시에 유저 info 저장시에 쓰일 SharedPref
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);;
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("captainName", value);
                editor.commit();

                //value값이 captain

                Intent goHome = new Intent(getApplicationContext(), RoomActivity.class);
                //user_id를 전달하면 메인홈에서 바로 SELECT문으로 회원정보 가져올 것이다.
                startActivity(goHome);
                finish();
                dialog.dismiss();     //닫기




            }
        });
    }


    public void showDialogueFindRoom(){
        adFindRoom = new AlertDialog.Builder(RoomStart2Activity.this);
        adFindRoom.setTitle("유저 닉네임 설정");       // 제목 설정
        adFindRoom.setMessage("유저의 닉네임을 설정해 주세요.");   // 내용 설정

        final EditText et = new EditText(RoomStart2Activity.this);
        adFindRoom.setView(et);


        adFindRoom.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();

            }
        });

        adFindRoom.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value = et.getText().toString();

                //로그인 성공시에 유저 info 저장시에 쓰일 SharedPref
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);;
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userName", value);
                editor.commit();

                //value값이 captain

                Intent goHome = new Intent(getApplicationContext(), RoomSearchingActivity.class);
                //user_id를 전달하면 메인홈에서 바로 SELECT문으로 회원정보 가져올 것이다.
                startActivity(goHome);

                dialog.dismiss();     //닫기



            }
        });
    }
}
