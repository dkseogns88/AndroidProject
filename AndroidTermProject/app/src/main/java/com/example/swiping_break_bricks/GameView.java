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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


//벽돌을 클래스로 생성,벽돌의 정보를 저장함
class Brick {
    RectF rect;
    int health;

    Brick(RectF rect, int health) {
        this.rect = rect;
        this.health = health;
    }
}


public class GameView extends View {
    public static Resources res;
    private Paint paint;
    private Bitmap ballBitmap;
    private Bitmap brickBitmap;
    private PointF ballPosition;
    private PointF ballVelocity;


    private boolean drawArrow = false;
    private PointF initialTouch = new PointF();
    private PointF currentTouch = new PointF();
    private Paint arrowPaint;

    private ArrayList<Brick> bricks;
    private boolean ballIsMoving = false;
    private float initialTouchX, initialTouchY;
    private int screenWidth, screenHeight;


    public GameView(Context context) {
        super(context);

        //화면크기
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //공의 이미지와 위치
        int ballSize = Math.min(screenWidth, screenHeight) / 15;
        ballBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, ballSize, ballSize, false);
        brickBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.brick);
        ballPosition = new PointF(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f);
        ballVelocity = new PointF(0, 0);

        //벽돌
        bricks = new ArrayList<Brick>();
        int brickWidth = screenWidth / 6;  //한줄의 벽돌의개수
        int brickHeight = screenHeight / 10;
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            RectF brickRect = new RectF(i * brickWidth, 0, (i + 1) * brickWidth, brickHeight);
            int health = random.nextInt(10) + 1;
            bricks.add(new Brick(brickRect, health));
        }

        //드래그 화살표
        arrowPaint = new Paint();
        arrowPaint.setColor(Color.RED);
        arrowPaint.setColor(Color.RED);
        arrowPaint.setStrokeWidth(5);

        //벽돌의체력을 텍스트로
        paint = new Paint();
        paint.setTextSize(brickHeight * 0.5f);
        paint.setTextAlign(Paint.Align.CENTER);

    }

    //터치이벤트처리
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //터치 시작하면 위치 저장 및 화살표를 그려줌
                initialTouch.set(event.getX(), event.getY());
                currentTouch.set(event.getX(), event.getY());
                drawArrow = true;
                break;

            case MotionEvent.ACTION_MOVE:
                //터치 이동시 현재의 위치저장
                currentTouch.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_UP:
                //터치 종료시 드래그 방향및 거리계산
                float deltaX = event.getX() - initialTouch.x;
                float deltaY = event.getY() - initialTouch.y;

                // 드래그로 증가하는속도를 제한
                float maxDragDistance = 100.0f;
                float currentDragDistance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                if (currentDragDistance > maxDragDistance) {
                    deltaX = deltaX * (maxDragDistance / currentDragDistance);
                    deltaY = deltaY * (maxDragDistance / currentDragDistance);
                }
                //공 발사 방향을 아래로 못하게
                if (deltaY < 0) {
                    Toast.makeText(getContext(), "공을 아래로 발사할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ballVelocity.set(-deltaX / 10, -deltaY / 10);
                    ballIsMoving = true;
                }
                drawArrow = false;
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //배경색설정
        canvas.drawColor(Color.WHITE);
        //공 비트맵으로그리기
        canvas.drawBitmap(ballBitmap, ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, paint);

        //벽돌비트맵,체력표시
        for (Brick brick : bricks) {
            canvas.drawBitmap(brickBitmap, null, brick.rect, paint);
            canvas.drawText(String.valueOf(brick.health), brick.rect.centerX(), brick.rect.centerY() + (paint.getTextSize() / 2), paint);
        }


        //화살표, 드래그중에만그려짐
        if (drawArrow) {
            canvas.drawLine(initialTouch.x, initialTouch.y, currentTouch.x, currentTouch.y, arrowPaint);
            canvas.drawCircle(initialTouch.x, initialTouch.y, 10, arrowPaint);
            canvas.drawCircle(currentTouch.x, currentTouch.y, 10, arrowPaint);
        }
        //공의 움직임임
       if (ballIsMoving) {
            ballPosition.x += ballVelocity.x;
            ballPosition.y += ballVelocity.y;

            //공이 왼쪽, 오른쪽 벽에 닿았을때
            if (ballPosition.x - ballBitmap.getWidth() / 2 <= 0 || ballPosition.x + ballBitmap.getWidth() / 2 >= screenWidth) {
                ballVelocity.x *= -1; //x축의 속도반전
                ballPosition.x = Math.max(ballPosition.x, ballBitmap.getWidth() / 2); // 왼쪽 벽 밖으로 벗어나지 않도록
                ballPosition.x = Math.min(ballPosition.x, screenWidth - ballBitmap.getWidth() / 2); // 오른쪽 벽 밖으로 벗어나지 않도록
            }
            //공이 위쪽 벽에 닿았을때
            if (ballPosition.y - ballBitmap.getHeight() / 2 <= 0) {
                ballVelocity.y *= -1; //y축의 속도반전
                ballPosition.y = ballBitmap.getHeight() / 2; // 상단 벽 밖으로 벗어나지 않도록
            }

            //벽돌,공 충돌처리
            for (int i = 0; i < bricks.size(); i++) {
                if (RectF.intersects(bricks.get(i).rect, new RectF(ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, ballPosition.x + ballBitmap.getWidth() / 2, ballPosition.y + ballBitmap.getHeight() / 2))) {
                    bricks.get(i).health -= 1; //벽돌의체력감소
                    if (bricks.get(i).health == 0) {
                        bricks.remove(i); //벽돌제거
                    }
                    ballVelocity.y *= -1; //벽돌과 충돌후,y축속도반전
                    break;  //다른벽돌과 충돌처리 방지
                }
            }

            //공이 화면아래로 떨어졌을때
            if (ballPosition.y >= screenHeight) {
                ballIsMoving = false;
                ballPosition.set(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f); //공의위치를 대기위치로

            }
        }
        invalidate();
    }
}