package com.techtown.alcoholic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SocketReceiveThread extends Thread{
    private String TAG = "데이터 소켓수신 스레드";
    private final int SERVER_PORT = 5001;
    private static SocketReceiveThread socketReceiveThread = null;
    public boolean isConnected = false;
    private Handler handler = null;
    private String url = null;
    private Socket socket = null;
    private PrintWriter pw = null;
    private ArrayList<String> dataList = null;


    public static SocketReceiveThread getInstance(String url, Handler handler, Socket socket) {
        if(socketReceiveThread ==null) {
            socketReceiveThread = new SocketReceiveThread();
            socketReceiveThread.url = url;
            socketReceiveThread.handler = handler;
            socketReceiveThread.socket =socket;
            socketReceiveThread.dataList = new ArrayList<>();
            socketReceiveThread.start();
        }
        return socketReceiveThread;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void run() {
        //연결되어있으면
        while (true) {
            isConnected = connectSocket();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                Log.i(TAG, "run: 소켓 메세지 수신 대기");
                String msg = br.readLine();
                Log.i(TAG, "run: 소켓 메세지 수신"+msg);
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("isFrom","receiveThread");
                bundle.putString("value", msg);
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.e(TAG, "run: 메세지 수신 오류", e);
                e.printStackTrace();
            }
        }
    }

    private boolean connectSocket() {
        synchronized (socket) {
            try {
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    Log.i(TAG, "run: 소켓연결 시도");
                    //소켓 연결
                    socket.connect(new InetSocketAddress(url, SERVER_PORT));
                    pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                    Log.i(TAG, "run: 소켓연결 되었습니다");
                    return true;
                } else if (socket.isConnected()) {
                    //Log.i(TAG, "run: 소켓 연결되어있음");
                    return true;
                }
            } catch (Exception e) {
                //Log.e(TAG, "run: 소켓연결오류", e);
                return false;
            }
            return false;
        }
    }

    public void sendData(String data) {
        //"~:~:~"형태의 데이터
        dataList.add(data);
    }

}
