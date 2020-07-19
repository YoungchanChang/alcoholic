package com.techtown.alcoholic.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

    private static final String TAG = "SingletonHttpLog";
    public static final String USER_FIND = "MySQL/USER_Find.php";
    Activity context;

    //request_queue와 response_listener을 static으로 선언하지 않은 이유는
    //실수로 입력하지 않았을 경우 null값이 됨으로 메소드로 처리하였다.
    RequestQueue requestQueue;
    Response.Listener<String> response_listener;

    //파라미터값들
    Map<String,String> params = new HashMap<String,String>();
    private static final HTTPRequest ourInstance = new HTTPRequest();

    public static HTTPRequest getInstance() {
        return ourInstance;
    }

    private HTTPRequest() {
    }
    String my_domain = "https://youngchanserver.tk/";
    String specific_URI = "";

    public void setHttpProperty(Activity context, Response.Listener<String> response_listener){
        this.context = context;
        this.response_listener = response_listener;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }
    public void putParams(Map<String, String> params){
        this.params = params;
    }

    public void putURI(String specific_URI){
        this.specific_URI = specific_URI;
    }

    public void makeRequest() {
        Log.d(TAG, "makeRequest: 시작점");
        String url = my_domain+ specific_URI;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response_listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            Log.d(TAG, "onErrorResponse: " + jsonError);
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }


//    public void forActivity(){
//
//        Response.Listener<String> response_listener =  new Response.Listener<String>(){
//            @Override
//            public void onResponse(String response) {
//
//                //뷰에 반영한다.
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                });
//            }
//        };
//
//        //서버에 보낼 파라미터를 설정한다.
//        Map<String,String> params = new HashMap<String,String>();
//        params.put("user_phone", "param");
//        SingletonNewHttp.getInstance().putParams(params);
//        //서버에 보낼 목적지 URI를 설정한다.
//        SingletonNewHttp.getInstance().putURI(SingletonNewHttp.USER_FIND);
//        //성공시 처리한다.
//        SingletonNewHttp.getInstance().setHttpProperty(AcitivityBlank.this, response_listener);
//
//        SingletonNewHttp.getInstance().makeRequest();
//    }

}
