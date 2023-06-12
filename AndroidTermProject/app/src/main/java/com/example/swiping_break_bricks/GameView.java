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
    private ArrayList<PointF> ballPositions;
    private ArrayList<PointF> ballVelocities;

    private boolean drawArrow = false;
    private PointF initialTouch = new PointF();
    private PointF currentTouch = new PointF();
    private Paint arrowPaint;

    private ArrayList<Brick> bricks;
    private boolean ballIsMoving = false;
    private float initialTouchX, initialTouchY;
    private int screenWidth, screenHeight;
    private ItemManager itemManager;

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

        ballPositions = new ArrayList<>();
        ballVelocities = new ArrayList<>();

        // 초기 공의 위치와 속도를 추가합니다.
        PointF initialBallPosition = new PointF(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f);
        PointF initialBallVelocity = new PointF(0, 0);
        ballPositions.add(initialBallPosition);
        ballVelocities.add(initialBallVelocity);

        // 벽돌 생성
        brickGenerator = new BrickGenerator(screenWidth, screenHeight);
        brickGenerator.generateNewRow();
        bricks = brickGenerator.getBricks();

        // 첫 번째 벽돌의 높이를 가져옵니다.
        float brickHeight = bricks.get(0).rect.height();

        // 아이템 매니저 생성
        itemManager = new ItemManager(screenWidth, screenHeight, brickHeight, ballPositions.get(0).y);

//        // 아이템 생성
//        int maxItems = 5; // 최대 아이템 개수
//        Bitmap itemBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bluejam);


        //벽돌의체력을 텍스트로
        paint = new Paint();
        paint.setTextSize(brickHeight * 0.5f);
        paint.setTextAlign(Paint.Align.CENTER);

        //드래그화살표
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
                float maxDragDistance = 5000.0f;
                float currentDragDistance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                if (currentDragDistance > maxDragDistance) {
                    deltaX = deltaX * (maxDragDistance / currentDragDistance);
                    deltaY = deltaY * (maxDragDistance / currentDragDistance);
                }
                //공 발사 방향을 아래로 못하게
                if (deltaY < 0) {
                    Toast.makeText(getContext(), "공을 아래로 발사할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (PointF ballVelocity : ballVelocities) {
                        ballVelocity.set(-deltaX / 10, -deltaY / 10);
                    }
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

        // 아이템 생성
        int maxItems = 5; // 최대 아이템 개수
        Bitmap itemBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bluejam);

        //배경색설정
        canvas.drawColor(Color.WHITE);

        boolean allBallsFallen = false; //모든공이 떨어졌는지확인하는변수

        for (int i = 0; i < ballPositions.size(); i++) {
            PointF ballPosition = ballPositions.get(i);
            PointF ballVelocity = ballVelocities.get(i);

            //공 비트맵으로그리기
            canvas.drawBitmap(ballBitmap, ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, paint);

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
            for (int j = 0; j < bricks.size(); j++) {
                if (RectF.intersects(bricks.get(j).rect, new RectF(ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, ballPosition.x + ballBitmap.getWidth() / 2, ballPosition.y + ballBitmap.getHeight() / 2))) {
                    bricks.get(j).health -= 1; //벽돌의체력감소
                    if (bricks.get(j).health == 0) {
                        bricks.remove(j); //벽돌제거
                    }
                    ballVelocity.y *= -1; //벽돌과 충돌후,y축속도반전
                    break;  //다른벽돌과 충돌처리 방지
                }
            }

            // 아이템 업데이트
            itemManager.update(new RectF(ballPosition.x - ballBitmap.getWidth() / 2, ballPosition.y - ballBitmap.getHeight() / 2, ballPosition.x + ballBitmap.getWidth() / 2, ballPosition.y + ballBitmap.getHeight() / 2));

            // 공이 화면아래로 떨어졌을때
            if (ballPosition.y >= screenHeight) {
                ballPositions.remove(i);
                ballVelocities.remove(i);
                i--; // 리스트에서 요소를 제거하면 인덱스가 변경되므로 i를 감소시킵니다.
                allBallsFallen = true; // 공이 화면 아래로 떨어졌으므로 변수를 true로 설정
            } else {
                allBallsFallen = false;  // 공이 아직 화면 아래로 떨어지지 않았으므로 변수를 false로 설정
                break; // 아직 화면 아래로 떨어지지 않은 공이 있으므로 루프를 종료
            }
        }
        //모든공이 화면아래로떨어지면
        if (allBallsFallen) {
            ballIsMoving = false;

            // 기존 벽돌들을 아래로 이동
            brickGenerator.moveBricksDown(1);
            // 새로운 라인의 벽돌 생성
            brickGenerator.generateNewRow();

            // 화면 밖으로 벗어난 벽돌 제거
            for (int j = bricks.size() - 1; j >= 0; j--) {
                if (bricks.get(j).rect.top >= screenHeight) {
                    bricks.remove(j);
                }
            }
            // 아이템 먹은 횟수가 공의 개수보다 많을 경우 새로운 공 추가
            while (itemManager.getItemCount() > ballPositions.size() - 1) {
                // 새로운 공의 위치와 속도를 초기 위치와 속도로 설정
                ballPositions.add(new PointF(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f));
                ballVelocities.add(new PointF(0, 0));

            }
            // 아이템 생성
            itemManager.createItems(3, itemBitmap);


            // 모든 공의 위치를 초기 위치로 설정
            for (PointF position : ballPositions) {
                position.set(screenWidth / 2.0f, screenHeight * 3.0f / 4.0f);
            }

        }

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

        // 아이템을 그리기 전에 아이템 상태 업데이트
        itemManager.draw(canvas);

        // 아이템 먹은 횟수 표시
        String itemCountText = "Item Count: " + itemManager.getItemCount();
        float textX = canvas.getWidth() - paint.measureText(itemCountText) - 16; // 텍스트를 오른쪽 아래에 위치시키기 위한 X 좌표 계산
        float textY = canvas.getHeight() - 16; // 텍스트를 오른쪽 아래에 위치시키기 위한 Y 좌표 계산
        canvas.drawText(itemCountText, textX, textY, paint);

        invalidate();
    }
}
