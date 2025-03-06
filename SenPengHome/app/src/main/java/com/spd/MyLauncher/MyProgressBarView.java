package com.spd.MyLauncher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.spd.home.R;


public class MyProgressBarView extends View {
    private int m_view_width = 0 , m_view_height = 0 ;
    private float m_progress_left,m_progress_right,m_progress_top,m_progress_bottom , m_progress_point , m_progress_height = 10f , m_progress_padding = 30f;

    private float m_current_value = 0.0f , m_touch_value = 0.5f;

    private Paint m_paint;
    public MyProgressBarView(Context context) {
        super(context);
        m_paint = new Paint();
        m_paint.setColor(Color.WHITE);
        m_paint.setAntiAlias(true);
    }

    public MyProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_paint = new Paint();
        m_paint.setColor(Color.WHITE);
        m_paint.setAntiAlias(true);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.CustomView
                , 0, 0);
        m_progress_height = array.getFloat(R.styleable.CustomView_fillet_height,10);
        m_progress_padding = array.getFloat(R.styleable.CustomView_fillet_width,40);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int c_new_width = right-left;
        int c_new_height = bottom-top;
        if (m_view_width != c_new_width || m_view_height != c_new_height)
        {
            m_view_width = c_new_width;
            m_view_height = c_new_height;
            m_progress_left = m_progress_padding;
            m_progress_right = m_view_width-m_progress_padding;
            m_progress_top = m_view_height/2f - m_progress_height/2f;
            m_progress_bottom = m_view_height/2f + m_progress_height/2f;
        }
    }

    public void setCurrentValue(float c_value)
    {
        if (m_current_value != c_value)
        {
            m_current_value = c_value;
            if (m_current_value < 0f)
            {
                m_current_value = 0f;
            }
            else if (m_current_value > 1f)
            {
                m_current_value = 1f;
            }
            invalidate();
        }
    }
    private int m_focus_color = 0xffff8d39;
    public void setProgressBarFocusColor(int c_color)
    {
        m_focus_color = c_color;
        this.invalidate();
    }

    private boolean m_real_touch = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        m_paint.setColor(0xff676767);
        canvas.drawRect(m_progress_left,m_progress_top,m_progress_right,m_progress_bottom,m_paint);
        float c_draw_point_x = m_current_value*(m_progress_right-m_progress_left)+m_progress_left;
        if (m_touch_flag)
        {
            c_draw_point_x = m_touch_value*(m_progress_right-m_progress_left)+m_progress_left;
        }
        m_paint.setColor(m_focus_color);
        canvas.drawRect(m_progress_left,m_progress_top,c_draw_point_x,m_progress_bottom,m_paint);
/*        m_paint.setColor(0x30ffffff);
        canvas.drawLine(m_progress_left,m_progress_top+0.5f,m_progress_right,m_progress_top+0.5f,m_paint);
        m_paint.setColor(0x30000000);
        canvas.drawLine(m_progress_left,m_progress_bottom-0.5f,m_progress_right,m_progress_bottom-0.5f,m_paint);*/
        if (m_real_touch)
        {
            m_paint.setColor(0x80ffffff);
            canvas.drawCircle(c_draw_point_x,m_view_height/2f,30f,m_paint);
            m_paint.setColor(0xffffffff);
            canvas.drawCircle(c_draw_point_x,m_view_height/2f,22f,m_paint);
        }
        else
        {
            m_paint.setColor(0x80ffffff);
            canvas.drawCircle(c_draw_point_x,m_view_height/2f,12f,m_paint);
            m_paint.setColor(0xffffffff);
            canvas.drawCircle(c_draw_point_x,m_view_height/2f,6f,m_paint);
        }
    }

    private boolean m_touch_flag = false;
    private boolean m_touch_confirm = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.d("TEST", "onTouchEvent: DOWN");
            if (Math.abs(event.getY() - this.getHeight()/2f)<20)
            {
                m_touch_flag = true;
                m_real_touch = true;
                if (m_send_hander.hasMessages(1))
                {
                    m_send_hander.removeMessages(1);
                }
                sendTouchEvent(event.getX(),false);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            Log.d("TEST", "onTouchEvent: MOVE");
            if (m_touch_flag)
            {
                sendTouchEvent(event.getX(),false);
            }

        }
        else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
        {
            Log.d("TEST", "onTouchEvent: UP");
            //m_touch_flag = false;
            if (m_touch_flag)
            {
                if (!m_send_hander.hasMessages(1))
                {
                    m_send_hander.sendEmptyMessageDelayed(1,500);
                }
                if (m_send_hander.hasMessages(0))
                {
                    sendTouchEvent(event.getX(),true);
                }
            }
            m_real_touch = false;
            this.invalidate();
        }
        super.onTouchEvent(event);
        return m_touch_flag;
    }
    public boolean isTouchMode()
    {
        return m_touch_flag;
    }
    private void sendTouchEvent (float c_motion_x , boolean c_immediately)
    {
        float c_touch_position = c_motion_x - m_progress_padding;
        float c_touch_value = c_touch_position/(m_progress_right-m_progress_left);
        if (c_touch_value < 0f)
        {
            c_touch_value = 0f;
        }
        else if (c_touch_value >1f)
        {
            c_touch_value = 1f;
        }
        m_touch_value = c_touch_value;
        m_current_value = c_touch_value;
        if (m_progress_listener != null)
        {
            m_progress_listener.onProgressBarTouchChanged(MyProgressBarView.this.getId(),m_touch_value);
            if (c_immediately)
            {
                if (m_send_hander.hasMessages(0))
                {
                    m_send_hander.removeMessages(0);
                }
                Message c_msg = new Message();
                c_msg.what = 0 ;
                c_msg.obj = c_touch_value;
                m_send_hander.sendMessage(c_msg);
            }
            else
            {
                if (!m_send_hander.hasMessages(0))
                {
                    Message c_msg = new Message();
                    c_msg.what = 0 ;
                    c_msg.obj = c_touch_value;
                    m_send_hander.sendMessageDelayed(c_msg,200);
                }
            }
        }
        invalidate();
    }
    private Handler m_send_hander = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && m_progress_listener != null)
            {
                m_progress_listener.onProgressBarValueChanged(MyProgressBarView.this.getId(),(float)msg.obj);
            }
            else if (msg.what == 1)
            {
                m_touch_flag = false;
            }
        }
    };

    private ProgressBarEventListener m_progress_listener;
    public void setProgressBarEventListener(ProgressBarEventListener c_listener)
    {
        m_progress_listener = c_listener;
    }
    public interface ProgressBarEventListener
    {
        public void onProgressBarValueChanged(int c_id , float c_value);
        public void onProgressBarTouchChanged(int c_id , float c_value);
    }
}
