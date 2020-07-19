package com.techtown.alcoholic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

//싱글톤 소켓통신 스레드
public class SocketThread extends Thread {
    private String TAG = "연결 스레드";
    private static SocketThread socketThread = null;
    public boolean isConnected = false;
//    public boolean
//
//    public static SocketThread getInstance(String url , Handler handler) {
//        if(httpCon==null) {
//            httpCon = new HttpCon();
//            httpCon.url = url;
//            httpCon.start();
//            httpCon.gmailSend = new GmailSend();
//        }
//        httpCon.handler=handler;
//        return httpCon;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void run() {
//        while (true) {
////                Log.i(TAG, "run: 스레드 시작");
//            //먼저 네트워크 체크
//            if (isNetworkAvailable(getApplicationContext())) {
//                //네트워크 연결
////                    Log.i(TAG, "run: 네트워크 연결 확인");
//                try {
//                    if (socket == null || socket.isClosed() || !socket.isConnected()) {
//                        Log.i(TAG, "run: 소켓연결 시도");
//                        //소켓 연결
//                        socket.connect(new InetSocketAddress(getString(R.string.pure_server_ip), SERVER_PORT));
//                        PrintWriter pw = null;
//                        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
//                        String request = "join:" + eventId + ":" + userId + ":" + nickname + "\r\n";
//                        pw.println(request);
//
////                                request = "getChatting:" + eventId + ":" + userId + ":" + timestamp + "\r\n";
////                                pw.println(request);
//                        Log.i(TAG, "run: 소켓연결 되었습니다");
//                        isConnected = true;
//                    } else if (socket.isConnected()) {
//                        Log.i(TAG, "run: 소켓 연결되어있음");
//                        isConnected = true;
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "run: 소켓연결오류", e);
//                    isConnected = false;
//                    e.printStackTrace();
//                    try {
//                        Thread.sleep(3000);
//                    } catch (Exception e2) {
//                        e2.printStackTrace();
//                    }
//                }
//            } else {
//                //네트워크 연결 안됨
//                isConnected = false;
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }

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