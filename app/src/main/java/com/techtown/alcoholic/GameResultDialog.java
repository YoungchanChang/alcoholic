package com.techtown.alcoholic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

public class GameResultDialog extends Dialog {

    public GameResultDialog(Context context) {
        super(context);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result_dialog);


    }
}