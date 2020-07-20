package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.techtown.alcoholic.HTTP.HTTPRequest;
import com.techtown.alcoholic.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameYoutubeViewsActivity extends AppCompatActivity {

    private String API_KEY = "AIzaSyCXWxnu_c0IF-koLZm_wE5M6b5TKRIsGVc";
    private EditText enterWords;
    private TextView tv_result,textResultTitle,textResultView,textWait,textKeyword;
    private String result;
    private String keyword = "먹방";
    String viewCount ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_youtube_views);

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
                               textResultTitle.setText(result);
                                findViewById(R.id.btnSearch).setVisibility(View.GONE);
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

}