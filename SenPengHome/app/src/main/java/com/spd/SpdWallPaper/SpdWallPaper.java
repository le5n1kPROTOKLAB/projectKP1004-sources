package com.spd.SpdWallPaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class SpdWallPaper extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new SpdWallPaperEngine();

    }

    class SpdWallPaperEngine extends  Engine implements Runnable{
        private CircleMotionView m_circle_view;
        private boolean m_draw_flag = true;
        @Override
        public void run() {
            while (m_draw_flag) {
                synchronized (this)
                {
                    doDraw();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void doDraw() {
            Canvas c_canvas = getSurfaceHolder().lockCanvas(); // 获得画布对象，开始对画布画画
            m_circle_view.onDraw(c_canvas);
            getSurfaceHolder().unlockCanvasAndPost(c_canvas); // 完成画画，把画布显示在屏幕上
        }
        private Thread m_thread;
        @Override
        public SurfaceHolder getSurfaceHolder() {
            return super.getSurfaceHolder();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d("SpdWallPaper", "onCreate: ");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("SpdWallPaper", "onDestroy: ");
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d("SpdWallPaper", "onSurfaceChanged: ");
            m_draw_flag = true;
            m_circle_view.setLayoutParameter(width,height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("SpdWallPaper", "onSurfaceCreated: ");
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            holder.unlockCanvasAndPost(canvas);
            m_circle_view = new CircleMotionView();
            if (m_thread == null || !m_thread.isAlive())
            {
                m_thread = new Thread(this);
                m_thread.start();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.d("SpdWallPaper", "onSurfaceDestroyed: ");
            synchronized (this)
            {
                m_draw_flag = false;
            }

            super.onSurfaceDestroyed(holder);

        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            Log.d("SpdWallPaper", "onOffsetsChanged: ");
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            Log.d("SpdWallPaper", "onCommand: "+action);
            return super.onCommand(action, x, y, z, extras, resultRequested);
        }


    }



}
