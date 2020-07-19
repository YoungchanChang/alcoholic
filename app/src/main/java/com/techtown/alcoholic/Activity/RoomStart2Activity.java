package com.techtown.alcoholic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.techtown.alcoholic.R;

public class RoomStart2Activity extends AppCompatActivity {

    ImageView image_gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_start2);

        image_gif = findViewById(R.id.image_gif);

    }
}
