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
import android.widget.LinearLayout;
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
    TextView textResult,textResultDescription,firstLetter,secondLetter,textRank,rankOne,rankTwo,rankThree;
    Button btnEnter;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();

    Random rnd = new Random();
    Handler handler;

    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;
    String TAG="GameInitialSound";
    String rank;
    ArrayList<GameResultItem> gameResultItems = new ArrayList<>();
    LinearLayout linearLank;
    Long startTimestamp;
    Long endTimestamp;
//
    DisplayMetrics dm;
    Boolean sendDate =true;
    //0.
    //1. 게임 시간이 0이 되면 시간초 관련 데이터를 서버에 보내
    //2. 특정 조건을 완료하면 데이터를 서버에 보내
    //2-1. 완료했으면 시간초가 멈춰야되. (멈추는 것 처럼 보여야되)
    //내가 찍으면 시간초 관련된 뷰가 invisible처리
    //3. 서버에서 3개의 관련 데이터가 왔을 때 다이얼로그 띄워준다.
    //0초가 됬을 때는 안 보내져야 한다.


    //게임 시작 기록하는 변수
    long startTime;
    //게임 끝났을 시간을 기록하는 변수, startTime과의 초가 게임 시간 차이이다.
    long endTime;

    //시간초가 실제로 내려가는 쓰레드
    TimerThread timerThread;
    //몇초인지 보여주는 뷰 ( Layout에 있어야 한다 )
    TextView textTimeLeft;
    //핸들러(쓰레드의 값을 보여주는 핸들러 객체)

    //내가 지정하고 싶은 시간
    int timeLimit = 15;

    //특정조건 만족했을 시에 서버에 정보를 보내주지 않게 하는 변수
    boolean isOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_initial_sound);

        String letter[] =  {"ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ"};


        rankOne= findViewById(R.id.rankOne);
        rankTwo= findViewById(R.id.rankTwo);
        rankThree= findViewById(R.id.rankThree);
        linearLank =findViewById(R.id.LinearRank);

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



        handler = getHandler();
        timerThread = new TimerThread(timeLimit, handler);
        timerThread.start();
        textTimeLeft = findViewById(R.id.textTimeLeft);
        textTimeLeft.setText(timeLimit+"초 남았습니다");
        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());

         dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
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
                            textTimeLeft.setVisibility(View.INVISIBLE);
                            endTimestamp = System.currentTimeMillis();
                            String request = "gameResult:"+(endTimestamp-startTimestamp);
                            socketSendThread.sendData(request);
                            textTimeLeft.setVisibility(View.INVISIBLE);
                            sendDate =false;
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
                        Log.d("스위치","스위치 작동");

                            try {
                                JSONObject jsonObject = new JSONObject(value);

                                String token[] = jsonObject.getString(0+"").split(":");
                                String token1[] = jsonObject.getString(1+"").split(":");
                                String token2[] = jsonObject.getString(2+"").split(":");
                                //token 0 유저 닉네임
                                //token 1 결과값

                                String userNickname = token[0];
                                String userScore = token[1];
                                long score = Integer.parseInt(userScore);
                                String userNickname1 = token1[0];
                                String userScore1 = token1[1];
                                long score1 = Integer.parseInt(userScore1);
                                String userNickname2 = token2[0];
                                String userScore2 = token2[1];
                                long score2 = Integer.parseInt(userScore2);

                                if(score<score1&&score1<score2) {
                                    rankOne.setText("1등:"+userNickname2);
                                    rankTwo.setText("2등:"+userNickname1);
                                    rankTwo.setText("3등:"+userNickname1);
                                } else if(score<score2&&score2<score1) {
                                    rankOne.setText("1등:"+userNickname1);
                                    rankTwo.setText("2등:"+userNickname2);
                                    rankThree.setText("3등:"+userNickname);
                                } else if(score1<score&&score<score2) {
                                    rankOne.setText("1등:"+userNickname2);
                                    rankTwo.setText("2등:"+userNickname);
                                    rankThree.setText("3등:"+userNickname1);
                                } else if(score1<score2&&score2<score) {
                                    rankOne.setText("1등:"+userNickname);
                                    rankTwo.setText("2등:"+userNickname2);
                                    rankThree.setText("3등:"+userNickname1);

                                } else if(score2<score&&score<score1) {
                                    rankOne.setText("1등:"+userNickname1);
                                    rankTwo.setText("2등:"+userNickname);
                                    rankThree.setText("3등:"+userNickname2);
                                } else if(score2<score1&&score1<score) {
                                    rankOne.setText("1등:"+userNickname);
                                    rankTwo.setText("2등:"+userNickname1);
                                    rankThree.setText("3등:"+userNickname2);
                                }
                                linearLank.setVisibility(View.VISIBLE);


                                //결과값 보여줌

                        }catch (JSONException e){ e.printStackTrace();}


                        //value = "joinRoom:유저닉네임"
                        break;
                    case "timerThread":
                        //타이머스레드에서 데이터 받을 때

                        Log.d(TAG, "TimeLeft " + timeLimit);
                        if(data.getInt("second")==0) {
                            textTimeLeft.setText("종료되었습니다");

                            Log.d(TAG, "TimeLeftYet " + timeLimit);
                            //게임결과 전송

                                Log.d(TAG, "TimeLeftEnd " + timeLimit);
                                //count변수 15초가 흘러간다.
                                //TODO
                                if (sendDate){
                                    endTimestamp = System.currentTimeMillis();
                                    String request = "gameResult:"+(endTimestamp-startTimestamp);
                                    socketSendThread.sendData(request);
                                }


                        }else {
                            textTimeLeft.setText(data.getInt("second")+"초 남았습니다");
                        }


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
//        custom_dialog.show();
    }





}