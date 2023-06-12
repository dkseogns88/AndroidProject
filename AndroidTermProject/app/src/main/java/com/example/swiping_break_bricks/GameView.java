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
import android.util.Log;


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
    private BrickGenerator brickGenerator;
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
    private Item item;

    private int itemCount = 0; // 아이템 먹은 횟수


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

        // 벽돌 생성
        brickGenerator = new BrickGenerator(screenWidth, screenHeight);
        brickGenerator.generateNewRow();
        bricks = brickGenerator.getBricks();

        // 첫 번째 벽돌의 높이를 가져옵니다.
        float brickHeight = bricks.get(0).rect.height();

        // 아이템 생성
        int maxItems = 5; // 최대 아이템 개수
        float brickTop = brickHeight; // 벽돌이 생성되는 맨 위쪽 좌표
        float ballBottom = screenHeight * 3.0f / 4.0f - ballSize / 2; // 공이 생성되는 맨 아래쪽 좌표
        Bitmap itemBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bluejam);
        item = new Item(new RectF(), itemBitmap, screenWidth, screenHeight, brickHeight, ballPosition.y);
        item.createItems(maxItems);


        //벽돌의체력을 텍스트로
        paint = new Paint();
        paint.setTextSize(brickHeight * 0.5f);
        paint.setTextAlign(Paint.Align.CENTER);

        arrowPaint = new Paint();
        arrowPaint.setColor(Color.BLACK);
        arrowPaint.setStrokeWidth(10);


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

        item.draw(canvas);
        //공의 움직임임
        if (ballIsMoving) {
            ballPosition.x += ballVelocity.x;
            ballPosition.y += ballVelocity.y;

            // 아이템 업데이트
            item.update(new RectF(ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, ballPosition.x + ballBitmap.getWidth() / 2, ballPosition.y + ballBitmap.getHeight() / 2));

            // 아이템을 먹었을 경우 아이템 먹은 횟수 증가
            if (item.isEaten()) {
                itemCount++;
                item.setEaten(false); // 아이템 먹은 상태 초기화
                Log.d("Item", "ItemCount: " + itemCount); // 로깅 추가
            }

            // 아이템을 그리기 전에 아이템 상태 업데이트
            item.draw(canvas);


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


            // 공이 화면아래로 떨어졌을때
            if (ballPosition.y >= screenHeight) {
                ballIsMoving = false;
                ballPosition.set(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f); // 공의 위치를 대기 위치로

                // 기존 벽돌들을 아래로 이동
                brickGenerator.moveBricksDown(1);
                // 새로운 라인의 벽돌 생성
                brickGenerator.generateNewRow();


                // 화면 밖으로 벗어난 벽돌 제거
                for (int i = bricks.size() - 1; i >= 0; i--) {
                    if (bricks.get(i).rect.top >= screenHeight) {
                        bricks.remove(i);
                    }

                }

            }
            // 아이템 먹은 횟수 표시
            String itemCountText = "Item Count: " + itemCount;
            float textX = canvas.getWidth() - paint.measureText(itemCountText) - 16; // 텍스트를 오른쪽 아래에 위치시키기 위한 X 좌표 계산
            float textY = canvas.getHeight() - 16; // 텍스트를 오른쪽 아래에 위치시키기 위한 Y 좌표 계산
            canvas.drawText(itemCountText, textX, textY, paint);

            invalidate();
        }


    }
}