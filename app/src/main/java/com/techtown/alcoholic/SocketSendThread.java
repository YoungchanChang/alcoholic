package com.techtown.alcoholic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//싱글톤 소켓통신 스레드
public class SocketSendThread extends Thread {
    private static String TAG = "데이터 소켓송신 스레드";
    private final int SERVER_PORT = 5001;
    private static SocketSendThread socketSendThread = null;
    public boolean isConnected = false;
    private String url;
    private Socket socket = null;
    private PrintWriter pw = null;
    private ArrayList<String> dataList;


    public static SocketSendThread getInstance(String url, Socket socket) {
        if(socketSendThread ==null) {
            socketSendThread = new SocketSendThread();
            socketSendThread.url = url;
            socketSendThread.socket = socket;
            socketSendThread.dataList = new ArrayList<>();
            socketSendThread.start();
        }
        return socketSendThread;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void run() {
        while (true) {
            isConnected = connectSocket();
            //연결되어있으면
            if(dataList.size()!=0) {
                pw.println(dataList.remove(0));
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
                    pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "run: 소켓연결오류", e);
                return false;
            }
            return false;
        }
    }

    public void sendData(String data) {
        //"~:~:~"형태의 데이터
        dataList.add(data);
    }

    //네트워크 연결상태 확인 메소드
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;
    }
}