package com.example.swiping_break_bricks;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class BrickGenerator {
    private ArrayList<Brick> bricks;
    private int screenWidth;
    private int screenHeight;

    public BrickGenerator(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
    }

    public void generateBricks(int numBricks) {
        int brickWidth = screenWidth / numBricks;  // 한 줄의 벽돌의 개수
        int brickHeight = screenHeight / 10;
        Random random = new Random();
        for (int i = 0; i < numBricks; i++) {
            RectF brickRect = new RectF(i * brickWidth, 0, (i + 1) * brickWidth, brickHeight);
            int health = random.nextInt(10) + 1;
            bricks.add(new Brick(brickRect, health));
        }
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }
}
