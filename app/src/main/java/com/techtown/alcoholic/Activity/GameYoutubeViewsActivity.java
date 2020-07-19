package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


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
import com.techtown.alcoholic.R;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GameYoutubeViewsActivity extends AppCompatActivity {

    private String API_KEY = "AIzaSyCXWxnu_c0IF-koLZm_wE5M6b5TKRIsGVc";
    private EditText enterWords;
    private TextView tv_result;
    private String result;
    private String keyword;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_youtube_views);

        tv_result = findViewById(R.id.tv_result);
        enterWords =findViewById(R.id.enterWords);
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoutubeAsyncTask youtubeAsyncTask = new YoutubeAsyncTask(keyword);
                youtubeAsyncTask.execute();
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
                               tv_result.setText(result);

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

            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Double checks the kind is video.
                if (rId.getKind().equals("youtube#video")) {
                    Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");
                    sb.append("ID : " + rId.getVideoId() + " , 제목 : " + singleVideo.getSnippet().getTitle() + " , 썸네일 주소 : " + thumbnail.getUrl());
                    sb.append("\n");
                }
            }

            result = sb.toString();
        }
    }

}