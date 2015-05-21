package com.example.administrator.STUM;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class SpriteView extends View {

    int frameWidth;
    int frameHeight;
    Bitmap spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet);

    Rect src = new Rect();
    Rect dst = new Rect();
    int x, y;
    boolean go;
    SpriteThread spriteThread;

    public SpriteView(Context context) {
        super(context);
        init();
    }

    public SpriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        frameWidth = spriteSheet.getWidth()/6;
        frameHeight = spriteSheet.getHeight()/5;
        dst.left = dst.top = 0;
        dst.right = frameWidth;
        dst.bottom = frameHeight;
    }

    public void startAnimation() {
        go = true;
        spriteThread = new SpriteThread();
        spriteThread.start();
    }

    public void stopAnimation() {
        go = false;
        try {
            spriteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(spriteSheet,src, dst, null);
    }

    /* Sprite Sheet에서 읽어와야할 프레임 이미지의 좌표를 계산하는 쓰레드 */
    public class SpriteThread extends Thread {

        int screenWidth, screenHeight;
        int speed = 5;

        @Override
        public void run() {
            screenWidth = getWidth();
            screenHeight = getHeight();

            while(go) {
                for(int i=0;i<5;i++) {
                    for(int k=0;k<6;k++) {
                        src.left = k*frameWidth;
                        src.top = i*frameHeight;
                        src.right = src.left+frameWidth;
                        src.bottom = src.top+frameHeight;

                        dst.left = x;
                        dst.top = 0;
                        dst.right = dst.left+frameWidth;
                        dst.bottom = dst.top+frameHeight;
                        postInvalidate();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }// end of inner for()
                }// end of outer for()
            }// end of while()
        }// end of run()
    }// end of class SpriteThread
}
