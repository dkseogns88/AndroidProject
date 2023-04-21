package com.example.swiping_break_bricks;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import com.example.swiping_break_bricks.BuildConfig;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View implements Choreographer.FrameCallback {
    private static final String TAG = GameView.class.getSimpleName();
    private ArrayList<Ball> balls = new ArrayList<>();

    public static Resources res;
    //    private Ball ball1, ball2;
    protected Paint fpsPaint;
    protected Paint borderPaint;


    public GameView(Context context) {
        super(context);
        init(null, 0);
    }
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private Handler handler;
    private void init(AttributeSet attrs, int defStyle) {



        GameView.res = getResources();
        Choreographer.getInstance().postFrameCallback(this);
        Bitmap ballBitmap = BitmapFactory.decodeResource(res,R.mipmap.yellow_ball);
        Ball.setBitmap(ballBitmap);
        balls.add(new Ball(0.5f,1.0f));

        if (BuildConfig.DEBUG) {
            fpsPaint = new Paint();
            fpsPaint.setColor(Color.BLUE);
            fpsPaint.setTextSize(100f);

            borderPaint = new Paint();
            borderPaint.setColor(Color.RED);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(0.1f);
        }
        handler = new Handler();
        reserveFrame();
    }
    private void reserveFrame(){
         handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
                invalidate();
                if(isShown()) {
                    reserveFrame();
                }
            }
        }, 16);
    }

    private void update(){
       for(Ball ball : balls){
           ball.update();
       }
    }

    private long previousNanos;
    @Override
    public void doFrame(long nanos) {
        if (previousNanos != 0) {
            long elapsedNanos = nanos - previousNanos;
            BaseScene scene = BaseScene.getTopScene();
            if(scene != null){
                scene.update(elapsedNanos);
            }
        }
        previousNanos = nanos;
        invalidate();
        if (isShown()) {
            Choreographer.getInstance().postFrameCallback(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float view_ratio = (float)w / (float)h;
        float game_ratio = Metrics.game_width / Metrics.game_height;
        if (view_ratio > game_ratio) {
            Metrics.x_offset = (int) ((w - h * game_ratio) / 2);
            Metrics.y_offset = 0;
            Metrics.scale = h / Metrics.game_height;
        } else {
            Metrics.x_offset = 0;
            Metrics.y_offset = (int)((h - w / game_ratio) / 2);
            Metrics.scale = w / Metrics.game_width;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        canvas.save();
        canvas.translate(Metrics.x_offset, Metrics.y_offset);
        canvas.scale(Metrics.scale, Metrics.scale);
        BaseScene scene = BaseScene.getTopScene();
        if (scene != null) {
            scene.draw(canvas);
        }

        if (BuildConfig.DEBUG) {
            canvas.drawRect(0, 0, Metrics.game_width, Metrics.game_height, borderPaint);
        }
        for(Ball ball : balls){
            ball.draw(canvas);
        }
        canvas.restore();

        if (BuildConfig.DEBUG && BaseScene.frameTime > 0) {
            int fps = (int) (1.0f / BaseScene.frameTime);
            canvas.drawText("FPS: " + fps, 100f, 200f, fpsPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = BaseScene.getTopScene().onTouchEvent(event);
        if (handled) {
            return true;
        }
        return super.onTouchEvent(event);
    }

}