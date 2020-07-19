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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.techtown.alcoholic.R;
import com.techtown.alcoholic.TimerThread;

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

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Handler handler;

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
                if("timerThread".equals(data.getString("isFrom"))) {
                    if(data.getInt("second")==0) {
                        textTimeLeft.setText("종료되었습니다");
                        mSensorManager.unregisterListener(GameShakeItActivity.this);
                    }else {
                        textTimeLeft.setText(data.getInt("second")+"초 남았습니다");
                    }
                }
            }
        };
    }
}
