package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techtown.alcoholic.R;

public class GameReadyActivity extends AppCompatActivity {

    Button btnBack;
    Button btnStart;

    //3개가 변하는 부분
    ImageView imageName;
    TextView textTitle;
    TextView textExplain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ready);

        imageName = findViewById(R.id.imageName);
        textTitle = findViewById(R.id.textTitle);
        textExplain = findViewById(R.id.textExplain);
        btnStart = findViewById(R.id.btnStart);

        Intent get_intent = getIntent();
        Integer gameGenre = get_intent.getIntExtra("gameGenre", 1);


        //TODO
        //버튼 클릭시 -> 정보[어떤 종류의 게임이 서버에 보내졌다]가 서버에 보내진다.
        //서버는 해당 정보를 유저객체들에게 보낸다.
        //유저들이 서비스에서 해당 정보 받으면 종류에 따라서 액티비티 실행한다.

        switch (gameGenre){
            case 1:
                imageName.setImageDrawable(getResources().getDrawable(R.drawable.dictionary, getApplicationContext().getTheme()));
                textTitle.setText("초성게임");
                textExplain.setText("제시된 초성으로 단어를 빨리 찾아서 입력하세요. 가장 빨리 사전에 등록된 단어를 맞춘 플레이어가 승리합니다.");

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goGame = new Intent(getApplicationContext(), GameInitialSound.class);
                        startActivity(goGame);
                        finish();

                    }
                });
                 break;
            case 2:
                imageName.setImageDrawable(getResources().getDrawable(R.drawable.youtube, getApplicationContext().getTheme()));
                textTitle.setText("높은 조회수를 찾아라");
                textExplain.setText("제시된 키워드가 포함된 단어로 가장 높은 조회수가 나올 것 같은 검색어를 입력해주세요. 가장 높은 조회수를 찾은 플레이어가 승리합니다.");

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goGame2 = new Intent(getApplicationContext(), GameYoutubeViewsActivity.class);
                        startActivity(goGame2);
                        finish();

                    }
                });


                break;
            case 3:
                imageName.setImageDrawable(getResources().getDrawable(R.drawable.search_icon, getApplicationContext().getTheme()));
                textTitle.setText("전국물건자랑");
                textExplain.setText("제시된 물건과 가장 비슷한 물건을 찾아서 사진을 찍으세요. 가장 빨리 비슷한 물건을 찍은 플레이어가 승리합니다.");

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goGame3 = new Intent(getApplicationContext(), GameImageActivity.class);
                        startActivity(goGame3);
                        finish();

                    }
                });


                break;
            case 4:
                imageName.setImageDrawable(getResources().getDrawable(R.drawable.shakeitshakeit, getApplicationContext().getTheme()));
                textTitle.setText("쉐킷쉐킷");
                textExplain.setText("핸드폰을 마구 흔들어주세요. 그러나 핸드폰이 날아갈 수 있으니 이 게임은 하지 마세요. 책임지지 않습니다.");
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goGame4 = new Intent(getApplicationContext(), GameShakeItActivity.class);
                        startActivity(goGame4);
                        finish();

                    }
                });

                break;
            default :
                break;
        }


    }
}
