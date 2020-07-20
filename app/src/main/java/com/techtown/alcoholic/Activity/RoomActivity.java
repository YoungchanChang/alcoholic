package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.techtown.alcoholic.Fragment.RoomGameListFragment;
import com.techtown.alcoholic.Fragment.RoomInfoFragment;

import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;

import java.util.Hashtable;

//hihihhihihihihih
public class RoomActivity extends AppCompatActivity {
    private String TAG = "RoomLog";

    Button btnRoomInfoFragment,btnRoomGameListFragment;

    RoomGameListFragment roomGameListFragment;
    RoomInfoFragment roomInfoFragment;
    Context context;
    //QR코드가 보여질 이미지 부분
    private ImageView imageViewQRCode;
    //QR코드 값 받기
    private String textForQRCode;

    //QR코드 값 받기
    TextView userNickName1,userNickName2,userNickName3,userNumber;
    LinearLayout user1,user2,user3;
    int index=0;
    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        context=this;

        btnRoomGameListFragment= findViewById(R.id.btn_RoomGameListFragment);
        btnRoomInfoFragment = findViewById(R.id.btn_RoomInfoFragment);

        btnRoomInfoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnRoomGameListFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        SharedPreferences pref= this.getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        textForQRCode = pref.getString("captainName", "");

        imageViewQRCode = findViewById(R.id.QRCode);
        QRFunction();
        index +=1;

        user1 =findViewById(R.id.user1);
        user2 =findViewById(R.id.user2);
        user3 =findViewById(R.id.user3);
        userNickName1 =findViewById(R.id.userNickname1);
        userNickName2 =findViewById(R.id.userNickname2);
        userNickName3 =findViewById(R.id.userNickname3);
        userNumber = findViewById(R.id.userNumber);
        userNumber.setText(index+"");

        user2.setVisibility(View.GONE);
        user3.setVisibility(View.GONE);
        userNickName1.setText(textForQRCode);

        handler = getHandler();
        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());

    }


    public void QRFunction(){

        imageViewQRCode = (ImageView)findViewById(R.id.QRCode);


        textForQRCode = "테스트";

        Log.d(TAG, "onCreate: ");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            /* Encode to utf-8 */
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            //width랑 height에서 QR코드 이미지 크기 조정
            BitMatrix bitMatrix = multiFormatWriter.encode(textForQRCode, BarcodeFormat.QR_CODE,200,200, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageViewQRCode.setImageBitmap(bitmap);

        }catch (Exception e){}
    }

    @SuppressLint("HandlerLeak")
    private Handler getHandler() {
        return new Handler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Log.i(TAG, "handleMessage: 데이테 전달받음"+data.toString());
                switch (data.getString("isFrom")) {
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
                        String token[] =value.split(":");
                        if (token[0].equals("joinRoom")){
                            for (int i =1; i<token.length;i++){
                                joinRoom(i,token[i]);
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
    private void joinRoom(int index,String userName){
        switch (index){
            case 1:
                user1.setVisibility(View.VISIBLE);
                userNickName1.setText(userName);
                break;
            case 2:
                user2.setVisibility(View.VISIBLE);
                userNickName2.setText(userName);
                break;
            case 3:
                user3.setVisibility(View.VISIBLE);
                userNickName3.setText(userName);
                break;
        }

    }
}
