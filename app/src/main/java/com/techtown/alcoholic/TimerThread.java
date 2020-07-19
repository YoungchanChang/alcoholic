package com.techtown.alcoholic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Date;

//시간제한을 10초로 설정하면 9부터 0까지 1초간격으로 보내주는 타이머스레드
//생성자의 파라미터는 제한시간(초)와 핸들러입니다.
//GameShakeItActivity 의 핸들러(getHandler메소드)를 참고해주세요
public class TimerThread extends Thread {
    private static final String TAG = "타이머 스레드";
    private int termSecond;
    private Handler handler;

    public TimerThread(int termSecond,Handler handler) {
        this.termSecond = termSecond;
        this.handler = handler;
    }
    public void run() {
        while (termSecond>0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            termSecond--;
            Log.i(TAG, "run: 스레드 작동중");
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("isFrom","timerThread");
            bundle.putInt("second",termSecond);
            //채팅 액티비티에서 소켓통신을 통해 받은 메세지와 구분하기 위한 파라메다
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
