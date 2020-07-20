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


}
