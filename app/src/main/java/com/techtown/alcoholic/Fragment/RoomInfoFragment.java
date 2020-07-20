package com.techtown.alcoholic.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.techtown.alcoholic.R;

import java.util.Hashtable;

public class RoomInfoFragment extends Fragment {

    View view;
    Context mContext;
    //QR코드가 보여질 이미지 부분
    private ImageView imageViewQRCode;
    //QR코드 값 받기
    private String textForQRCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_room_info,container,false);
        mContext=getActivity();


        //SharedPref에서 유저정보를 가져와서 유저에게 전달한다.
        SharedPreferences pref= mContext.getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        textForQRCode = pref.getString("captainName", "");

        imageViewQRCode = view.findViewById(R.id.QRCode);
        QRFunction();

        return  view;
    }

    public void QRFunction(){


        textForQRCode = "테스트";


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
