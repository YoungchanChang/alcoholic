package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.techtown.alcoholic.R;
import com.bumptech.glide.Glide;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;
import com.techtown.alcoholic.TimerThread;

public class RoomStart2Activity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RoomStart";

    ImageView image_gif;
    ImageButton imageMakeRoom;
    ImageButton imageFindRoom;


    AlertDialog.Builder ad;

    AlertDialog.Builder adFindRoom;

    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;


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

        //핸들러와 소켓통신 스레드
        handler = getHandler();

        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());
        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());

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

    @SuppressLint("HandlerLeak")
    private Handler getHandler() {
        return new Handler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Log.i(TAG, "handleMessage: 데이터 전달받음"+data.toString());
                switch (data.getString("isFrom")) {
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
                        Toast.makeText(RoomStart2Activity.this,value,Toast.LENGTH_SHORT).show();
                        if("makeRoom".equals(value)) {
                            //방 생성완료 시에
                            Intent intent = new Intent(RoomStart2Activity.this,RoomActivity.class);
                            startActivity(intent);
                        }
                        break;
                    default:
                        Log.i(TAG, "handleMessage: isFrom오류");
                        break;
                }
            }
        };
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
                String request = "makeRoom:"+value;
                socketSendThread.sendData(request);

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
