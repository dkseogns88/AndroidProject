package com.example.swiping_break_bricks;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class BrickGenerator {
    private ArrayList<Brick> bricks;
    private int screenWidth, screenHeight;
    private int brickWidth, brickHeight;
    private Random random;

    public BrickGenerator(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.brickWidth = screenWidth / 10;
        this.brickHeight = screenHeight / 20;
        this.bricks = new ArrayList<>();
        this.random = new Random();
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }

    // 모든 벽돌을 한 줄 내리는 메서드
    public void moveBricksDown(float distance) {
        for (Brick brick : bricks) {
            brick.rect.offset(0, distance);
        }
    }


    // 새로운 벽돌 줄을 생성하는 메서드
    public void generateNewRow() {
        for (int i = 0; i < 5; i++) {
            int health = random.nextInt(3) + 1; // 1~3의 랜덤한 체력
            RectF rect = new RectF(i * brickWidth, 0, (i + 1) * brickWidth, brickHeight);
            bricks.add(0, new Brick(rect, health)); // 새로운 벽돌을 리스트의 맨 앞에 추가
        }
    }

    public void moveBricksDown(int rows) {
        for (Brick brick : bricks) {
            brick.rect.top += brickHeight * rows;
            brick.rect.bottom += brickHeight * rows;
        }

    }
}
