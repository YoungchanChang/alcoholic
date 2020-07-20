package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoStatistics;
import com.google.gson.JsonArray;
import com.techtown.alcoholic.GameResultItem;
import com.techtown.alcoholic.HTTP.HTTPRequest;
import com.techtown.alcoholic.R;
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketReceiveThread;
import com.techtown.alcoholic.SocketSendThread;
import com.techtown.alcoholic.TimerThread;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GameYoutubeViewsActivity extends AppCompatActivity {

    private String TAG ="GAMEYOUTUBEVIEWS";
    private String API_KEY = "AIzaSyCXWxnu_c0IF-koLZm_wE5M6b5TKRIsGVc";
    private EditText enterWords;
    private TextView tv_result,textResultTitle,textResultView,textWait,textKeyword,rankOne,rankTwo,rankThree;
    private String result;
    private String keyword = "먹방";
    String viewCount ="";
    Handler handler;
    String rank;
    LinearLayout linearLank;
    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;

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
        setContentView(R.layout.activity_game_youtube_views);

        rankOne= findViewById(R.id.rankOne);
        rankTwo= findViewById(R.id.rankTwo);
        rankThree= findViewById(R.id.rankThree);
        linearLank =findViewById(R.id.LinearRank);
        tv_result = findViewById(R.id.textTitle);
        textResultTitle = findViewById(R.id.textResultTitle);
        textResultView = findViewById(R.id.textResultView);
        textWait = findViewById(R.id.wait);
        textKeyword = findViewById(R.id.keyword);
        enterWords =findViewById(R.id.enterWords);
        textKeyword.setText(keyword);
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = enterWords.getText().toString();
                if (word.contains(keyword)){
                    YoutubeAsyncTask youtubeAsyncTask = new YoutubeAsyncTask(word);
                    youtubeAsyncTask.execute();
                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(enterWords.getWindowToken(), 0);

                }else {
                    Toast.makeText(getApplicationContext(),"키워드를 포함한 검색어를 입력해주세요",Toast.LENGTH_LONG).show();
                }

            }
        });


        handler = getHandler();
        timerThread = new TimerThread(timeLimit, handler);
        timerThread.start();
        textTimeLeft = findViewById(R.id.textTimeLeft);
        textTimeLeft.setText(timeLimit+"초 남았습니다");
        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());
    }

    protected void onResume() {
        super.onResume();
        startTimestamp = System.currentTimeMillis();
    }


    private class YoutubeAsyncTask extends AsyncTask<Void, Void, Void> {
        String keyword;
        YoutubeAsyncTask(String keyword){
            this.keyword=keyword;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                final JsonFactory JSON_FACTORY = new JacksonFactory();
                final long NUMBER_OF_VIDEOS_RETURNED = 5;

                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-search-sample").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");

                search.setKey(API_KEY);

                search.setQ(keyword);
                search.setOrder("relevance"); //date relevance

                search.setType("video");

                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                SearchListResponse searchResponse = search.execute();

                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    prettyPrint(searchResultList.iterator(), keyword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Tag",result);
                            try{

                            }catch(Exception e){e.printStackTrace();}


                        }
                    });
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        public void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }

            StringBuilder sb = new StringBuilder();


                SearchResult singleVideo = iteratorSearchResults.next();

                ResourceId rId = singleVideo.getId();

                // Double checks the kind is video.
                if (rId.getKind().equals("youtube#video")) {

                    sb.append(" 제목 : " + singleVideo.getSnippet().getTitle().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "") );
                    sb.append("\n");


                    forActivity(rId.getVideoId());
                }


            result = sb.toString();
        }

            public void forActivity(String test2){

                Response.Listener<String> response_listener =  new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        Log.d("test", "onResponse: " + response);
                        //뷰에 반영한다.
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            JSONArray jsonArray = jsonObject.optJSONArray("items");
                            String statistics = jsonArray.get(0).toString();
                            JSONObject jsonObject1 = new JSONObject(statistics);
                            Log.d("test", "viewCount: " + statistics);
                            String statistics1 =jsonObject1.getString("statistics");
                            Log.d("test", "viewCount: " + statistics1);
                            JSONObject jsonObject2 = new JSONObject(statistics1);
                             viewCount =jsonObject2.getString("viewCount");

                            Log.d("test", "viewCount: " + viewCount);
                        }catch (JSONException j){
                            j.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textResultView.setText(viewCount);
                                textResultTitle.setText(result);
                                textTimeLeft.setVisibility(View.INVISIBLE);
                                findViewById(R.id.btnSearch).setVisibility(View.GONE);
                                endTimestamp = System.currentTimeMillis();
                                String request = "gameResult:"+(endTimestamp-startTimestamp);
                                socketSendThread.sendData(request);
                                sendDate=false;
                                Log.d(TAG,"send data");
                            }
                        });
                    }
                };

                //서버에 보낼 파라미터를 설정한다.
                Map<String,String> params = new HashMap<String,String>();
                HTTPRequest.getInstance().putParams(params);


                //서버에 보낼 목적지 URI를 설정한다.


                        HTTPRequest.getInstance().putURI(test2);
                //성공시 처리한다.
                        HTTPRequest.getInstance().setHttpProperty(GameYoutubeViewsActivity.this, response_listener);

                        HTTPRequest.getInstance().makeRequest();
            }
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

}