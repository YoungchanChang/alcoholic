package com.techtown.alcoholic.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.techtown.alcoholic.R;

public class GameShakeItActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "쉐킷쉐킷";
    private static final int SHAKE_SKIP_TIME =500;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private Long mShakeTime;

    TextView textCount;
    int count =0;

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_shake_it);
        textCount = findViewById(R.id.textCount);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeTime =System.currentTimeMillis();
        textCount.setText("hi");
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
}
