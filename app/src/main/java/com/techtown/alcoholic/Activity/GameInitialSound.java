package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techtown.alcoholic.GameResultDialog;
import com.techtown.alcoholic.GameResultItem;
import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;
import com.techtown.alcoholic.TimerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class GameInitialSound extends AppCompatActivity {

    EditText enterWord;
    TextView textResult,textResultDescription,firstLetter,secondLetter;
    Button btnEnter;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();

    Random rnd = new Random();
    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;
    String TAG="GameInitialSound";
    ArrayList<GameResultItem> gameResultItems = new ArrayList<>();

    Long startTimestamp;
    Long endTimestamp;
    DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_initial_sound);

        String letter[] =  {"ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ"};




        firstLetter = findViewById(R.id.firstInitialLetter);
        secondLetter = findViewById(R.id.secondInitialLetter);
        textResultDescription = findViewById(R.id.textResultDescription);
        textResult = findViewById(R.id.textResult);
        enterWord = findViewById(R.id.enterWords);
        btnEnter = findViewById(R.id.btnEnter);

        int num = rnd.nextInt(letter.length);
        firstLetter.setText(letter[num]);
        num = rnd.nextInt(letter.length);
        secondLetter.setText(letter[num]);

        handler = getHandler();
        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());
        socketReceiveThread = SocketReceiveThread.getInstance(getString(R.string.server_ip),handler, SingleToneSocket.getInstance());

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.clear();
                description.clear();
                String word = enterWord.getText().toString();
                NaverSearch naverSearch = new NaverSearch(word);
                naverSearch.start();
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(enterWord.getWindowToken(), 0);

            }
        });
    }

    protected void onResume() {
        super.onResume();
        startTimestamp = System.currentTimeMillis();
    }


    private class NaverSearch extends Thread{
        String value;
        NaverSearch(String value){
            this.value=value;

        }
        public void run() {

            final String keyword = getNaverSearch(value);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Tag",keyword);
                    try{
                        if (description.size() < 2){
                            textResult.setText("없는 단어입니다. 다시 입력해주세요...");
                        }else{
                            for (int i=0;i<title.size();i++){
                                textResult.setText("존재하는 단어입니다. 다른 유저를 기다려주세요");
                                textResultDescription.setText(description.get(1));
                                btnEnter.setVisibility(View.GONE);

                            }
                            endTimestamp = System.currentTimeMillis();
                            String request = "gameResult:"+(endTimestamp-startTimestamp);
                            socketSendThread.sendData(request);
                        }

                    }catch(Exception e){e.printStackTrace();}


                }
            });

        }

    }

    public String getNaverSearch(String keyword) {

        String clientID = "gu1Or7S6KZzSs68QiZis";
        String clientSecret = "3VTLzmh6HH";
        StringBuffer sb = new StringBuffer();

        try {


            String text = URLEncoder.encode(keyword, "UTF-8");



            String apiURL = "https://openapi.naver.com/v1/search/encyc.xml?query=" + text + "&display=5" + "&start=1";


            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //inputStream으로부터 xml값 받기
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기

                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals("title")) {

                            sb.append("제목 : ");

                            xpp.next();
                            String value =xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            title.add(value);
                            sb.append(value);
                            sb.append("\n");

                        } else if (tag.equals("description")) {

                            sb.append("내용 : ");
                            xpp.next();
                            String value =xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            description.add(value);
                            sb.append(value);
                            sb.append("\n");


                        }
                        break;
                }

                eventType = xpp.next();

            }

        } catch (Exception e) {
            return e.toString();

        }

        return sb.toString();
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
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            if (jsonObject.length() ==3){
                                for (int i=0; i<jsonObject.length();i++){
                                   String token[] = jsonObject.getString(i+"").split(":");
                                    //token 0 유저 닉네임
                                    //token 1 결과값
                                    gameResultItems.add(new GameResultItem(token[0],token[1]));

                                }
                                showDialog();
                            }

                        }catch (JSONException e){ e.printStackTrace();}


                        //value = "joinRoom:유저닉네임"
                        break;
                    default:
                        Log.i(TAG, "handleMessage: 아무것도 클릭되지 않음");
                        break;
                }
            }
        };
    }

    private void showDialog(){
        final GameResultDialog custom_dialog = new GameResultDialog(getApplicationContext(), gameResultItems);
        WindowManager.LayoutParams wm = custom_dialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
        wm.width = dm.widthPixels / 2;  //화면 너비의 절반
        wm.height = dm.heightPixels / 2;  //화면 높이의 절반
        wm.copyFrom(custom_dialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미

        custom_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

               finish();
            }
        });
        custom_dialog.show();
    }
}