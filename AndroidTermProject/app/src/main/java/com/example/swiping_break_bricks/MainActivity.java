package com.example.swiping_break_bricks;

import androidx.appcompat.app.AppCompatActivity;
import com.example.swiping_break_bricks.GameView;

import android.os.Bundle;

import com.example.swiping_break_bricks.R;
import com.example.swiping_break_bricks.GameView;
public class MainActivity extends AppCompatActivity {
    private GameView gameView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);

    }
}