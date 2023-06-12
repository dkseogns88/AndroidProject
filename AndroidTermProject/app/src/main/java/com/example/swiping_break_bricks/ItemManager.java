package com.example.swiping_break_bricks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class ItemManager {
    private ArrayList<Item> items;
    private int screenWidth, screenHeight;
    private float brickHeight;
    private float ballY;
    private int itemCount = 0; // 아이템 먹은 횟수

    //아이템관리
    public ItemManager(int screenWidth, int screenHeight, float brickHeight, float  ballY) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.brickHeight = brickHeight;
        this.ballY = ballY;
        this.items = new ArrayList<>();
    }

    //아이템생성
    public void createItems(int maxItems, Bitmap bitmap) {
        Random random = new Random();
        for (int i = 0; i < maxItems; i++) {
            float left = random.nextInt(screenWidth - bitmap.getWidth());
            float top = random.nextInt((int) (ballY - brickHeight - bitmap.getHeight()));
            RectF rect = new RectF(left, top, left + bitmap.getWidth(), top + bitmap.getHeight());
            items.add(new Item(rect, bitmap));
        }
    }

    public void update(RectF ballRect) {
        for (Item item : items) {
            item.update(ballRect);
            if (item.isEaten()) {
                itemCount++; // 아이템을 먹었을 때 카운트 증가
                item.setEaten(false); // 아이템 먹은 상태 초기화
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Item item : items) {
            item.draw(canvas);
        }
    }

    public int getItemCount() {
        return itemCount;
    }
}