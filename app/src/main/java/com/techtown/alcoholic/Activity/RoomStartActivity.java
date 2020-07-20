package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;

public class RoomStartActivity extends AppCompatActivity implements AutoPermissionsListener, View.OnClickListener {
    private static final String TAG = "GameLog";
    Button btnShakeIt,btnReadyGame;
    Button btnImageGame,btnInitialSound,btnYoutube;
    //방만들기, 방찾기 버튼
    Button btnMakeRoom, basicFirst;
    Button btnSearchingRoom;
    EditText editTextNickname;
    EditText editTextRoomName;

    SocketSendThread socketSendThread;
    SocketReceiveThread socketReceiveThread;

    Handler handler;

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
        btnMakeRoom = findViewById(R.id.btnMakeRoom);
        btnMakeRoom.setOnClickListener(this);
        btnSearchingRoom = findViewById(R.id.btnSearchingRoom);
        btnSearchingRoom.setOnClickListener(this);
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextRoomName = findViewById(R.id.editTextRoomName);
        btnReadyGame = findViewById(R.id.btnReadyGame);
        btnReadyGame.setOnClickListener(this);

        basicFirst= findViewById(R.id.basicFirst);
        basicFirst.setOnClickListener(this);
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        handler = getHandler();
        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());
        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip),SingleToneSocket.getInstance());

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
                String requestShakeIt = "gameStart:shakeIt";
                socketSendThread.sendData(requestShakeIt);
                break;
            case R.id.btnImageGame:
                String requestImageGame = "gameStart:imageGame";
                socketSendThread.sendData(requestImageGame);
                break;
            case R.id.btnInitialSound:
                String requestInitialSound = "gameStart:initialSound";
                socketSendThread.sendData(requestInitialSound);
                break;
            case R.id.btnYoutubeViews:
                String requestYoutubeViews = "gameStart:youtubeViews";
                socketSendThread.sendData(requestYoutubeViews);
                break;
            case R.id.basicFirst:
                Intent intent7 = new Intent(RoomStartActivity.this,RoomStart2Activity.class);
                startActivity(intent7);
                break;

            case R.id.btnMakeRoom:
                if(!editTextNickname.getText().toString().equals("")&&!editTextRoomName.getText().toString().equals("")) {
                    String request = "makeRoom:"+editTextNickname.getText().toString()+":"+editTextRoomName.getText().toString();
                    socketSendThread.sendData(request);
                }
                break;
            case R.id.btnSearchingRoom:
                if(!editTextNickname.getText().toString().equals("")&&!editTextRoomName.getText().toString().equals("")) {
                    String request = "joinRoom:"+editTextNickname.getText().toString()+":"+editTextRoomName.getText().toString();
                    socketSendThread.sendData(request);
                }
                break;
            case R.id.btnReadyGame:
                Intent intent8 = new Intent(RoomStartActivity.this,GameReadyActivity.class);
                startActivity(intent8);
                break;
            default:
                Log.d(TAG, "defaultTest");
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
                Log.i(TAG, "handleMessage: 데이테 전달받음2222222"+data.toString());
                switch (data.getString("isFrom")) {
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
//                        new JSONArray(value)
                        //Toast.makeText(RoomStartActivity.this,value,Toast.LENGTH_SHORT).show();
                        String[] tokens = value.split(":");
                        if("gameStart".equals(tokens[0])) {
                            switch(tokens[1]){
                                case "shakeIt" :
                                    Intent intent = new Intent(RoomStartActivity.this,GameShakeItActivity.class);
                                    startActivity(intent);
                                    break;
                                case "initialSound" :
                                    Intent intent2 = new Intent(RoomStartActivity.this,GameInitialSound.class);
                                    startActivity(intent2);
                                    break;
                                case "imageGame":
                                    Intent intent3 = new Intent(RoomStartActivity.this,GameImageActivity.class);
                                    startActivity(intent3);
                                    break;
                                case "youtubeViews":
                                    Intent intent4 = new Intent(RoomStartActivity.this,GameYoutubeViewsActivity.class);
                                    startActivity(intent4);
                                    break;
                            }
                        }
                        break;
                    default:
                        Log.i(TAG, "handleMessage: 아무것도 클릭되지 않음");
                        break;
                }
            }
        };
    }
}
