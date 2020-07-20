package com.techtown.alcoholic.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.techtown.alcoholic.Activity.GameShakeItActivity;
import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;

import java.util.Hashtable;

public class RoomInfoFragment extends Fragment {

    View view;
    Context mContext;
    //QR코드가 보여질 이미지 부분
    private ImageView imageViewQRCode;
    //QR코드 값 받기
    private String textForQRCode;
    TextView userNickName1,userNickName2,userNickName3,userNumber;
    LinearLayout user1,user2,user3;
    int index=0;
    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;
    String TAG = "RoomInfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_room_info,container,false);
        mContext=getActivity();

        //SharedPref에서 유저정보를 가져와서 유저에게 전달한다.
        SharedPreferences pref= mContext.getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        textForQRCode = pref.getString("captainName", "");

        imageViewQRCode = view.findViewById(R.id.QRCode);
        QRFunction();

        user1 =view.findViewById(R.id.user1);
        user2 =view.findViewById(R.id.user2);
        user3 =view.findViewById(R.id.user3);
        userNickName1 =view.findViewById(R.id.userNickname1);
        userNickName2 =view.findViewById(R.id.userNickname2);
        userNickName3 =view.findViewById(R.id.userNickname3);
        userNumber=view.findViewById(R.id.userNumber);
        userNumber.setText(index);

        user2.setVisibility(View.GONE);
        user3.setVisibility(View.GONE);
        userNickName1.setText(textForQRCode);


        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());

        return  view;
    }

    public void QRFunction(){





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
                    case "timerThread":
                        //타이머스레드에서 데이터 받을 때

                        break;
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때

                        break;
                    default:
                        Log.i(TAG, "handleMessage: 아무것도 클릭되지 않음");
                        break;
                }
            }
        };
    }
}
