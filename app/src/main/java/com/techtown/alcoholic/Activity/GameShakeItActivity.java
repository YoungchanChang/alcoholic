package com.techtown.alcoholic.Activity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;
import com.techtown.alcoholic.TimerThread;

import org.json.JSONArray;

public class GameShakeItActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private static final String TAG = "쉐킷쉐킷";
    private static final int SHAKE_SKIP_TIME =500;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private Long mShakeTime;

    TextView textCount;
    TextView textTimeLeft;

    int count =0;
    int timeLimit = 15;
    boolean isOver = false;

    String userNick = "팀원이유빈";
    String roomKey = "방장이유빈";

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_shake_it);
        textCount = findViewById(R.id.textCount);
        textTimeLeft = findViewById(R.id.textTimeLeft);
        textTimeLeft.setText(timeLimit+"초 남았습니다");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeTime =System.currentTimeMillis();

        handler = getHandler();
        TimerThread timerThread = new TimerThread(timeLimit,handler);
        timerThread.start();

        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip));
        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float gravityX = axisX/SensorManager.GRAVITY_EARTH;
            float gravityY = axisY/SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ/SensorManager.GRAVITY_EARTH;

            Float f = gravityX*gravityX + gravityY*gravityY + gravityZ*gravityZ;
            double squaredD = Math.sqrt(f.doubleValue());
            float gForce = (float) squaredD;
            if(gForce>SHAKE_THRESHOLD_GRAVITY) {
                long currentTime = System.currentTimeMillis();
                if(mShakeTime+SHAKE_SKIP_TIME > currentTime) {
                    return;
                }
                count++;
                Log.d(TAG, "onSensorChanged: 흔들리고 있음"+gForce);
                textCount.setText(""+count);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                        if(data.getInt("second")==0) {
                            textTimeLeft.setText("종료되었습니다");
                            mSensorManager.unregisterListener(GameShakeItActivity.this);
                            //게임결과 전송
                            String request = "game:shakeIt:"+userNick+":"+count;
                            socketSendThread.sendData(request);
                        }else {
                            textTimeLeft.setText(data.getInt("second")+"초 남았습니다");
                        }
                        break;
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
//                        new JSONArray(value)
                        Toast.makeText(GameShakeItActivity.this,value,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.i(TAG, "handleMessage: 아무것도 클릭되지 않음");
                        break;
                }
            }
        };
    }
}
