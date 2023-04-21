package com.example.swiping_break_bricks;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Ball {
    private static Bitmap bitmap;
    private RectF dstRect = new RectF();
    private float dx, dy;

    public Ball(float dx, float dy) {
        this.dx = dx/3;
        this.dy = dy/3;
        //ball의 크기
        dstRect.set(0, 0, 2.0f, 2.0f);
    }

    public static void setBitmap(Bitmap bitmap) {
        Ball.bitmap = bitmap;
    }

    //ball 의행동반경
    public void update() {
        dstRect.offset(dx, dy);
        if (dx > 0) {
            if (dstRect.right > 9.0f) {
                dx = -dx;
            }
        } else {
            if (dstRect.left < 0) {
                dx = -dx;
            }
        }
        if (dy > 0) {
            if (dstRect.bottom > 15.0) {
                dy = -dy;
            }
        } else {
            if (dstRect.top < 0) {
                dy = -dy;
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, dstRect, null);
    }}