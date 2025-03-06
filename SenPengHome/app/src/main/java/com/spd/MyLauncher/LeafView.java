package com.spd.MyLauncher;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.spd.home.R;

public class LeafView extends View{
    private Bitmap m_leaf_01,m_leaf_02,m_leaf_03,m_leaf_04,m_delete_icon;

    private int m_leaf_width , m_leaf_height;
    private Matrix m_drawMatrix;
    private Paint m_paint;
    private float LeafScale01 = 1f,LeafScale02= 1f,LeafScale03 = 1f,LeafScale04 = 1f,LeafAlpha = 5f;
    ObjectAnimator mAnin_leaf_01,mAnin_leaf_02,mAnin_leaf_03,mAnin_leaf_04,mAnin_leaf_alpha;
    protected static PaintFlagsDrawFilter m_default_filter;
    public void setLeafAlpha (float c_value)
    {
        LeafAlpha = c_value;
        this.invalidate();
    }
    public void setLeafScale01 (float c_value)
    {
        LeafScale01 = c_value;
        this.invalidate();
    }
    public void setLeafScale02 (float c_value)
    {
        LeafScale02 = c_value;
        this.invalidate();
    }
    public void setLeafScale03 (float c_value)
    {
        LeafScale03 = c_value;
        this.invalidate();
    }
    public void setLeafScale04 (float c_value)
    {
        LeafScale04 = c_value;
        this.invalidate();
    }
    private int mSceneState = -1;
    public void setSceneState(int c_temp)
    {
        if (mSceneState != c_temp)
        {
            if (c_temp < 0)
            {
                c_temp = -c_temp;
            }
            else
            {

            }
            mSceneState = c_temp;
        }
    }

    public LeafView(Context context) {
        super(context);
        initLeaf(context);
    }

    public LeafView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLeaf(context);
    }

    public void resetLeafMotion(boolean first,int c_delay_time)
    {
        if (mAnin_leaf_01 != null)
        {
            mAnin_leaf_01.pause();
            mAnin_leaf_01.cancel();
        }
        if (mAnin_leaf_02 != null)
        {
            mAnin_leaf_02.pause();
            mAnin_leaf_02.cancel();
        }
        if (mAnin_leaf_03 != null)
        {
            mAnin_leaf_03.pause();
            mAnin_leaf_03.cancel();
        }
        if (mAnin_leaf_04 != null)
        {
            mAnin_leaf_04.pause();
            mAnin_leaf_04.cancel();
        }
        if (mAnin_leaf_alpha != null)
        {
            mAnin_leaf_alpha.pause();
            mAnin_leaf_alpha.cancel();
        }

        if (first)
        {
            setLeafAlpha(0);
            mAnin_leaf_alpha = ObjectAnimator.ofFloat(this,"LeafAlpha",0f,5f);
            mAnin_leaf_alpha.setDuration(1200);
            mAnin_leaf_alpha.setStartDelay(c_delay_time);
            mAnin_leaf_alpha.start();
        }

        mAnin_leaf_01 = ObjectAnimator.ofFloat(this,"LeafScale02",-1f,0f,1f);
        mAnin_leaf_01.setDuration(500);
        mAnin_leaf_01.setStartDelay(c_delay_time);
        mAnin_leaf_01.start();
        mAnin_leaf_02 = ObjectAnimator.ofFloat(this,"LeafScale04",-1f,0f,1f);
        mAnin_leaf_02.setStartDelay(200+c_delay_time);
        mAnin_leaf_02.setDuration(500);

        mAnin_leaf_02.start();
        mAnin_leaf_03 = ObjectAnimator.ofFloat(this,"LeafScale03",-1f,0f,1f);
        mAnin_leaf_03.setStartDelay(400+c_delay_time);
        mAnin_leaf_03.setDuration(500);

        mAnin_leaf_03.start();
        mAnin_leaf_04 = ObjectAnimator.ofFloat(this,"LeafScale01",-1f,0f,1f);
        mAnin_leaf_04.setStartDelay(600+c_delay_time);
        mAnin_leaf_04.setDuration(500);

        mAnin_leaf_04.start();


    }

    private void initLeaf(Context context)
    {
        m_leaf_01 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leaf_01);
        m_leaf_02 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leaf_02);
        m_leaf_03 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leaf_03);
        m_leaf_04 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leaf_04);
        m_delete_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.desktop_delete);
        m_leaf_width = m_leaf_01.getWidth();
        m_leaf_height = m_leaf_01.getHeight();
        m_drawMatrix = new Matrix();
        m_paint = new Paint();
        m_paint.setColor(0xffffffff);
        m_paint.setStrokeWidth(1f);
        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
    }
    private MotionParameter m_click_value = new MotionParameter();
    private float m_touch_radius_inside = 40;
    public static final int LEAF_MODE_FLOWER = 0;
    public static final int LEAF_MODE_DELETE = 1;
    private int m_draw_mode = LEAF_MODE_FLOWER;
    public void setLeafMode(int c_mode)
    {
        m_draw_mode = c_mode;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(m_default_filter);
        if (LeafAlpha >0.001)
        {
            float c_circle_angle_step = 5f;
            int c_draw_angle = Math.min((int)(LeafAlpha*90f/c_circle_angle_step),(int)(360f/c_circle_angle_step));
            for (int i = 0 ; i<c_draw_angle ; i++)
            {
                float c_point_x = (float)Math.sin(i*c_circle_angle_step/180f*Math.PI);
                float c_point_y = -(float)Math.cos(i*c_circle_angle_step/180f*Math.PI);
                m_paint.setAlpha(205);
                canvas.drawPoint(canvas.getWidth()/2f+c_point_x*40f,canvas.getHeight()/2f+c_point_y*40f,m_paint);
                m_paint.setAlpha(96);
                canvas.drawPoint(canvas.getWidth()/2f+c_point_x*46f,canvas.getHeight()/2f+c_point_y*46f,m_paint);
            }
            float c_extend_alpha = Math.min(5f-LeafAlpha,1.0f);
            if (c_extend_alpha >0.01f)
            {
                for (int i = 2 ; i > -5 ; i--)
                {
                    int c_target_posint_index = i+c_draw_angle;
                    float c_point_x = (float)Math.sin(c_target_posint_index*c_circle_angle_step/180f*Math.PI);
                    float c_point_y = -(float)Math.cos(c_target_posint_index*c_circle_angle_step/180f*Math.PI);
                    m_paint.setAlpha((int)((1.0f-Math.abs(i)/5f)*255*c_extend_alpha));
                    float c_line_length = 2f-Math.abs(i)*0.2f;
                    canvas.drawLine(canvas.getWidth()/2f+c_point_x*(40f-c_line_length),canvas.getHeight()/2f+c_point_y*(40f-c_line_length),
                            canvas.getWidth()/2f+c_point_x*(40f+c_line_length),canvas.getHeight()/2f+c_point_y*(40f+c_line_length),m_paint);
                    m_paint.setAlpha((int)((1.0f-Math.abs(i)/5f)*128*c_extend_alpha));
                    canvas.drawLine(canvas.getWidth()/2f+c_point_x*(46f-c_line_length),canvas.getHeight()/2f+c_point_y*(46f-c_line_length),
                            canvas.getWidth()/2f+c_point_x*(46f+c_line_length),canvas.getHeight()/2f+c_point_y*(46f+c_line_length),m_paint);
                }
            }
        }
        if (m_draw_mode == LEAF_MODE_FLOWER)
        {
            float c_angle_offset = 20f;
            m_drawMatrix.reset();
            m_drawMatrix.postTranslate(0,-m_leaf_height);
            m_drawMatrix.postRotate(45f);
            m_drawMatrix.postRotate((float)Math.sin(LeafScale02*Math.PI)*c_angle_offset);
            m_drawMatrix.postScale(2f-Math.abs(LeafScale02),1.0f);
            m_drawMatrix.postRotate(-45f);
            m_drawMatrix.postTranslate(canvas.getWidth()/2f,canvas.getHeight()/2f);
            m_paint.setAlpha((int)(Math.min(LeafAlpha,1f)*255));
            canvas.drawBitmap(m_leaf_02,m_drawMatrix,m_paint);
            if (LeafAlpha >1f)
            {
                m_drawMatrix.reset();
                m_drawMatrix.postRotate(-45);
                m_drawMatrix.postRotate((float)Math.sin(LeafScale04*Math.PI)*c_angle_offset);
                m_drawMatrix.postScale(2f-Math.abs(LeafScale04),1.0f);
                m_drawMatrix.postRotate(45f);
                m_drawMatrix.postTranslate(canvas.getWidth()/2f,canvas.getHeight()/2f);
                m_paint.setAlpha((int)(Math.min(LeafAlpha-1f,1f)*255));
                canvas.drawBitmap(m_leaf_04,m_drawMatrix,m_paint);

                if (LeafAlpha >2f)
                {
                    m_drawMatrix.reset();
                    m_drawMatrix.postTranslate(-m_leaf_width,0);
                    m_drawMatrix.postRotate(-135f);
                    m_drawMatrix.postRotate((float)Math.sin(LeafScale03*Math.PI)*c_angle_offset);
                    m_drawMatrix.postScale(2f-Math.abs(LeafScale03),1.0f);
                    m_drawMatrix.postRotate(135f);
                    m_drawMatrix.postTranslate(canvas.getWidth()/2f,canvas.getHeight()/2f);
                    m_paint.setAlpha((int)(Math.min(LeafAlpha-2f,1f)*255));
                    canvas.drawBitmap(m_leaf_03,m_drawMatrix,m_paint);
                    if (LeafAlpha >3f)
                    {
                        m_drawMatrix.reset();
                        m_drawMatrix.postTranslate(-m_leaf_width,-m_leaf_height);
                        m_drawMatrix.postRotate(135f);
                        m_drawMatrix.postRotate((float)Math.sin(LeafScale01*Math.PI)*c_angle_offset);
                        m_drawMatrix.postScale(2f-Math.abs(LeafScale01),1.0f);
                        m_drawMatrix.postRotate(-135f);
                        m_drawMatrix.postTranslate(canvas.getWidth()/2f,canvas.getHeight()/2f);
                        m_paint.setAlpha((int)(Math.min(LeafAlpha-3f,1f)*255));
                        canvas.drawBitmap(m_leaf_01,m_drawMatrix,m_paint);

                    }
                }
            }
        }
        else
        {
            m_paint.setAlpha(255);
            canvas.drawBitmap(m_delete_icon,canvas.getWidth()/2f-m_delete_icon.getWidth()/2f,canvas.getHeight()/2f-m_delete_icon.getHeight()/2f,m_paint);
        }
        if (m_click_value.getCurrentValue() >0.01f && m_click_value.getCurrentValue() < 1.99f)
        {
            m_paint.setAlpha((int) (Math.min(255, (1f-Math.abs(1f - m_click_value.getCurrentValue())) * 128)));
            canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, m_click_value.getCurrentValue()*m_touch_radius_inside, m_paint);
        }
        if (m_click_value.motionValue())
        {
            this.invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.d("TEST003", "onTouch: Down");
            m_click_value.setValue(0.0f);
            m_click_value.gotoValue(1.0f);
            this.invalidate();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
        {
            Log.d("TEST003", "onTouch: Up");
            m_click_value.gotoValue(2.0f);
            this.invalidate();
        }
        return super.dispatchTouchEvent(event);
    }
}
