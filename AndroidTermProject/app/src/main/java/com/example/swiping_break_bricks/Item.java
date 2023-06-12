package com.example.swiping_break_bricks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class Item {
    private RectF rect;
    private Bitmap bitmap;

    private boolean eaten = false;
    private boolean destroyed = false;

    public Item(RectF rect, Bitmap bitmap) {
        this.rect = rect;
        this.bitmap = bitmap;
        this.eaten = false;
    }

    public void update(RectF ballRect) {
        if (!destroyed && RectF.intersects(ballRect, rect)) {
            eaten = true;
            destroyed = true; // 아이템이 공과 충돌했으므로 파괴 상태로 설정
        }
    }

    public void draw(Canvas canvas) {
        if (!destroyed) { // 아이템이 파괴되지 않았을 때만 그림
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }
}
