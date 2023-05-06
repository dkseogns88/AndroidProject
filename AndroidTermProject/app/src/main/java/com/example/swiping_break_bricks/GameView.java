package com.example.swiping_break_bricks;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class GameView extends View {
    public static Resources res;
    private Bitmap ballBitmap;
    private Bitmap brickBitmap;
    private PointF ballPosition;
    private PointF ballVelocity;
    private ArrayList<RectF> bricks;
    private boolean ballIsMoving = false;
    private float initialTouchX, initialTouchY;
    private int screenWidth, screenHeight;



    public GameView(Context context) {
        super(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        int ballSize = Math.min(screenWidth, screenHeight) / 15;
        ballBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, ballSize, ballSize, false);
        brickBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.brick);



        ballPosition = new PointF(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f);
        ballVelocity = new PointF(0, 0);

        bricks = new ArrayList<>();
        int brickWidth = screenWidth / 10;
        int brickHeight = screenHeight / 20;
        for (int i = 0; i < 10; i++) {
            RectF brick = new RectF(i * brickWidth, 0, (i + 1) * brickWidth, brickHeight);
            bricks.add(brick);
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!ballIsMoving) {
                    initialTouchX = event.getX();
                    initialTouchY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!ballIsMoving) {
                    float dx = event.getX() - initialTouchX;
                    float dy = event.getY() - initialTouchY;
                    float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
                    ballVelocity.set(-dx / magnitude * 20, -dy / magnitude * 20);
                    ballIsMoving = true;
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(ballBitmap, ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, paint);

        for (RectF brick : bricks) {
            canvas.drawBitmap(brickBitmap, null, brick, paint);
        }

        if (ballIsMoving) {
            ballPosition.x += ballVelocity.x;
            ballPosition.y += ballVelocity.y;


            if (ballPosition.x - ballBitmap.getWidth() / 2 <= 0 || ballPosition.x + ballBitmap.getWidth() / 2 >= screenWidth) {
                ballVelocity.x *= -1;
                ballPosition.x = Math.max(ballPosition.x, ballBitmap.getWidth() / 2); // 왼쪽 벽 밖으로 벗어나지 않도록
                ballPosition.x = Math.min(ballPosition.x, screenWidth - ballBitmap.getWidth() / 2); // 오른쪽 벽 밖으로 벗어나지 않도록
            }
            if (ballPosition.y - ballBitmap.getHeight() / 2 <= 0) {
                ballVelocity.y *= -1;
                ballPosition.y = ballBitmap.getHeight() / 2; // 상단 벽 밖으로 벗어나지 않도록
            }


            for (int i = 0; i < bricks.size(); i++) {
                if (RectF.intersects(bricks.get(i), new RectF(ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, ballPosition.x + ballBitmap.getWidth() / 2, ballPosition.y + ballBitmap.getHeight() / 2))) {
                    bricks.remove(i);
                    ballVelocity.y *= -1;
                    break;
                }
            }

            if (ballPosition.y >= screenHeight) {
                ballIsMoving = false;
                ballPosition.set(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f);
            }
        }
        invalidate();
    }
}