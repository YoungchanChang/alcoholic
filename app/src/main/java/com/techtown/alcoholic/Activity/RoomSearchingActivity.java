package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;

import java.io.IOException;

public class RoomSearchingActivity extends AppCompatActivity {
    static final String TAG="방탐색 액티비티";

    CameraSource cameraSource;
    SurfaceView cameraSurface;
    Button btn_cancel;
    String barcodeContents;

//    Handler handler;
//
//    SocketReceiveThread socketReceiveThread;
//    SocketSendThread socketSendThread;

    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_searching);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        cameraSurface = (SurfaceView) findViewById(R.id.cameraSurface); // SurfaceView 선언 :: Boilerplate

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE) // QR_CODE로 설정하면 좀더 빠르게 인식할 수 있습니다.
                .build();
        Log.d("NowStatus", "BarcodeDetector Build Complete");


        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(0.01f) // 프레임 높을 수록 리소스를 많이 먹겠죠
                .setRequestedPreviewSize(1080, 1920)    // 확실한 용도를 잘 모르겠음. 필자는 핸드폰 크기로 설정
                .setAutoFocusEnabled(true)  // AutoFocus를 안하면 초점을 못 잡아서 화질이 많이 흐립니다.
                .build();
        Log.d("NowStatus", "CameraSource Build Complete");


        cameraSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {   // try-catch 문은 Camera 권한획득을 위한 권장사항
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraSurface.getHolder());  // Mobile Vision API 시작
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();    // SurfaceView가 종료되었을 때, Mobile Vision API 종료
                Log.d("NowStatus", "SurfaceView Destroyed and CameraSource Stopped");
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d("NowStatus", "BarcodeDetector SetProcessor Released");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                // 바코드가 인식되었을 때 무슨 일을 할까?
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                Boolean a = true;

                if(barcodes.size() != 0 && a == true) {
                    barcodeContents = barcodes.valueAt(0).displayValue; // 바코드 인식 결과물

                    //바코드 인식했을 때 결과가 나와
                    Log.d("Detection", barcodeContents);

                    a = false;
                    //1. 서버에 해당되는 결과를 보내.
                    //2. 방이 일치할 때, 처리해야됨.
                    //로그인 성공시에 유저 info 저장시에 쓰일 SharedPref
                    SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);;
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("captainName", barcodeContents);
                    editor.commit();

                    //barcodeContents의 String값은 방장 이름 값으로 서버에 보내면 된다.
                    userName = pref.getString("userName", "");
                    String request = "joinRoom:"+userName+":"+barcodeContents;
//                    socketSendThread.sendData(request);


                    Intent goHome = new Intent(getApplicationContext(), RoomActivity.class);
                    //user_id를 전달하면 메인홈에서 바로 SELECT문으로 회원정보 가져올 것이다.
                    startActivity(goHome);
                }

            }
        });

//        handler = getHandler();
//
//        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());
//        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());
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
                Log.i(TAG, "handleMessage: 데이터 전달받음"+data.getString("isFrom"));
                switch (data.getString("isFrom")) {
                    case "receiveThread":

                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
                        Log.d(TAG, "handleMessage123 " +value);
                        Toast.makeText(RoomSearchingActivity.this,value,Toast.LENGTH_SHORT).show();
                        String[] tokens = value.split(":");

                        for(int i=0; i<tokens.length; i++){
                            Log.d(TAG, "handleMessage "+tokens[i]);
                        }

                        if("joinRoom".equals(tokens[0])&&userName.equals(tokens[1])) {
                            //방 생성완료 시에
                            Intent intent = new Intent(RoomSearchingActivity.this,RoomActivity.class);
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
}
