package com.example.swiping_break_bricks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.swiping_break_bricks.R;
public class TitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
    }

    public void onBtnStart(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
    public void onBtnQuit(View view)   {
        finish();
    }
}