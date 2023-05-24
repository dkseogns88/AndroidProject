package com.example.swiping_break_bricks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Item {

    private RectF rect;       // 아이템의 위치와 크기를 나타내는 사각형
    private Bitmap bitmap;    // 아이템 이미지

    private boolean isEaten; // 아이템이 먹혔는지 여부를 저장하는 변수
    private boolean isCollected;   // 아이템이 플레이어에 의해 수집되었는지 여부
    private List<Item> itemList;    // 아이템 목록
    private Random random;        // 랜덤한 위치 생성을 위한 Random 객체
    private int numCollected;   // 아이템을 먹은 개수

    private int screenWidth;   // 화면의 가로 길이
    private int screenHeight;  // 화면의 세로 길이
    private float brickTop;     // 벽돌이 생성되는 맨 위쪽 좌표
    private float ballBottom;    // 공이 생성되는 맨 아래쪽 좌표


    public Item(RectF rect, Bitmap bitmap, int screenWidth, int screenHeight, float brickTop, float ballBottom) {
        this.rect = rect;
        this.bitmap = bitmap;
        this.isCollected = false;
        this.itemList = new ArrayList<>();
        this.random = new Random();
        this.numCollected = 0;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.brickTop = brickTop;
        this.ballBottom = ballBottom;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public int getNumCollected() {
        return numCollected;
    }

    // 아이템이 먹혔는지 여부를 반환하는 메서드
    public boolean isEaten() {
        return isEaten;
    }

    // 아이템을 먹었다고 설정하는 메서드
    public void setEaten(boolean eaten) {
        isEaten = eaten;
    }

    public void createItems(int maxItems) {
        // 최대 maxItems 개수의 아이템 생성
        while (itemList.size() < maxItems) {
            // 아이템의 위치 및 크기 설정 (랜덤한 위치)
            float left = getRandomXCoordinate();
            float top = getRandomYCoordinate(brickTop, ballBottom);
            float right = left + bitmap.getWidth();
            float bottom = top + bitmap.getHeight();
            RectF itemRect = new RectF(left, top, right, bottom);

            // 아이템 생성 및 목록에 추가
            Item item = new Item(itemRect, bitmap, screenWidth, screenHeight, brickTop, ballBottom);
            itemList.add(item);
        }
    }


    private float getRandomXCoordinate() {
        // 화면 가로 길이를 벗어나지 않는 랜덤한 X 좌표 생성
        return random.nextInt(screenWidth);
    }
    private float getRandomYCoordinate(float brickTop, float ballBottom) {
        // 벽돌이 생성되는 맨 위쪽과 공이 생성되는 맨 아래쪽을 제외한 랜덤한 Y 좌표 생성

        float minY = brickTop + bitmap.getHeight();
        float maxY = ballBottom - bitmap.getHeight();
        return minY + random.nextInt((int) (maxY - minY));
    }

    public void update(RectF playerRect) {
        // 아이템 업데이트 로직 작성
        for (Item item : itemList) {
            if (!item.isCollected() && RectF.intersects(item.getRect(), playerRect)) {
                item.setCollected(true);
                numCollected++;
            }
        }
    }

    public void draw(Canvas canvas) {
        // 아이템 그리기 로직 작성
        for (Item item : itemList) {
            if (!item.isCollected()) {
                canvas.drawBitmap(item.getBitmap(), item.getRect().left, item.getRect().top, null);
            }
        }
    }
}