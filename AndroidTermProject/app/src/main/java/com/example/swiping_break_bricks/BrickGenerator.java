package com.example.swiping_break_bricks;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class BrickGenerator {
    private ArrayList<Brick> bricks;
    private int screenWidth, screenHeight;
    private int brickHeight;
    private int brickWidth;
    private int currentRowHealth = 1; // 첫 번째 줄의 벽돌 체력


    public BrickGenerator(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.brickHeight = screenHeight / 15;
        this.brickWidth = screenWidth / 5; // 벽돌의 너비는 항상 화면 너비의 1/5
        this.bricks = new ArrayList<>();
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }




    // 새로운 벽돌 줄을 생성하는 메서드
    public void generateNewRow() {
        Random random = new Random();
        int brickCount = random.nextInt(5) + 1; // 1개에서 5개 사이의 랜덤한 벽돌 개수
        float top = 0;
        if (!bricks.isEmpty()) {
            top = bricks.get(0).rect.top - brickHeight; // 가장 위에 있는 벽돌의 위에 새로운 벽돌을 생성
        }
        for (int i = 0; i < brickCount; i++) {
            float left = i * brickWidth;
            RectF rect = new RectF(left, top, left + brickWidth, top + brickHeight);
            bricks.add(0, new Brick(rect, currentRowHealth)); // 리스트의 가장 앞에 벽돌 추가
        }
        currentRowHealth++; // 다음 줄의 벽돌 체력 증가
    }

    // 기존의 벽돌을 한줄내림
    public void moveBricksDown(int rows) {
        for (Brick brick : bricks) {
            brick.rect.offset(0, rows * brickHeight);
        }
    }
}
