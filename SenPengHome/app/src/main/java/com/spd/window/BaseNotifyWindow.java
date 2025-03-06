package com.spd.window;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BaseNotifyWindow implements View.OnClickListener,View.OnKeyListener{
    public static final int MEDIA_DEVICE_NONE          = 0x00;
    public static final int MEDIA_DEVICE_USB0          = 0x01;
    public static final int MEDIA_DEVICE_USB1          = 0x02;
    public static final int MEDIA_DEVICE_FAVOURITEs    = 0x03;
    public static final int MEDIA_DEVICE_LOCAL         = 0x04;
    public static final int MEDIA_DEVICE_SD            = 0x05;
    public static final int MEDIA_DEVICE_DVD           = 0x06;
    public static final int MEDIA_DEVICE_BT_MUSIC      = 0x07;
    private WindowManager m_window_manager;
    protected Context m_context;
    private static int m_window_width , m_window_height;
    private WindowManager.LayoutParams m_window_para;
    public BaseNotifyWindow(Context context)
    {
        m_context = context;
        if (m_window_manager == null)
        {
            m_window_manager = (WindowManager) m_context.getSystemService(m_context.WINDOW_SERVICE);
            Display display = m_window_manager.getDefaultDisplay();
            Point p = new Point();
            display.getRealSize(p);
            m_window_width = p.x;
            m_window_height = p.y;
        }
        m_window_para = new WindowManager.LayoutParams(
                m_window_width,
                m_window_height,
                0, 0,
                PixelFormat.TRANSPARENT
        );
        // flag 设置 Window 属性
        m_window_para.flags=  WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        // type 设置 Window 类别（层级）
        m_window_para.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
        m_window_para.gravity = Gravity.TOP;
        m_window_root_layout = new FrameLayout(context);
        m_window_background = new ColorDrawable();
        m_window_background.setColor(0xa0000000);
        m_window_root_layout.setBackground(m_window_background);
        m_window_root_layout.setOnClickListener(this);
        m_window_root_layout.setOnKeyListener(this);
    }

    protected abstract View getShowWindowView();
    protected abstract void onDialogViewShow(View c_view);
    protected abstract void onDialogViewHide(View c_view);
    protected abstract void onDismiss();
    private boolean m_is_window_show_flag = false;
    private FrameLayout m_window_root_layout;
    private ColorDrawable m_window_background;
    private ObjectAnimator m_window_anim;
    private View m_current_dialog_view;
    protected void showWindow(int c_auto_close_time)
    {
        if (!m_is_window_show_flag)
        {
            m_is_window_show_flag = true;
            m_current_dialog_view = getShowWindowView();
            if (m_current_dialog_view != null && m_current_dialog_view.getParent() != m_window_root_layout)
            {
                m_window_root_layout.addView(m_current_dialog_view);
            }
            onDialogViewShow(m_current_dialog_view);
            if (m_close_window_handler.hasMessages(0))
            {
                m_close_window_handler.removeMessages(0);
            }
            if (m_close_window_handler.hasMessages(1))
            {
                m_close_window_handler.removeMessages(1);
            }
            if (c_auto_close_time > 0)
            {
                m_close_window_handler.sendEmptyMessageDelayed(1,c_auto_close_time);
            }
            m_window_manager.addView(m_window_root_layout, m_window_para);
            if (m_window_anim != null && m_window_anim.isRunning())
            {
                m_window_anim.cancel();
            }
            m_window_anim = ObjectAnimator.ofArgb(m_window_background,"Color",0x00000000,0xc0000000);
            m_window_anim.setDuration(300);
            m_window_anim.start();
        }
        else
        {
            if (m_close_window_handler.hasMessages(0))
            {
                m_close_window_handler.removeMessages(0);
            }
            if (m_close_window_handler.hasMessages(1))
            {
                m_close_window_handler.removeMessages(1);
            }
            if (c_auto_close_time > 0)
            {
                m_close_window_handler.sendEmptyMessageDelayed(1,c_auto_close_time);
            }
        }
    }
    public void hideWindow()
    {
        if (m_is_window_show_flag)
        {
            if (m_close_window_handler.hasMessages(1))
            {
                m_close_window_handler.removeMessages(1);
            }
            m_is_window_show_flag = false;
            if (m_window_anim != null && m_window_anim.isRunning())
            {
                m_window_anim.cancel();
            }
            m_window_anim = ObjectAnimator.ofArgb(m_window_background,"Color",0xc0000000,0x00000000);
            m_window_anim.setDuration(300);
            m_window_anim.start();
            if (m_current_dialog_view != null)
            {
                onDialogViewHide(m_current_dialog_view);
            }
            if (m_close_window_handler.hasMessages(0))
            {
                m_close_window_handler.removeMessages(0);
            }
            m_close_window_handler.sendEmptyMessageDelayed(0,300);
        }
    }
    private Handler m_close_window_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
            {
                m_window_manager.removeView(m_window_root_layout);
                onDismiss();
            }
            else if (msg.what == 1)
            {
                hideWindow();
            }
        }
    };

    protected void delayWindow(int c_auto_close_time)
    {
        if (m_close_window_handler.hasMessages(0))
        {
            m_close_window_handler.removeMessages(0);
        }
        if (m_close_window_handler.hasMessages(1))
        {
            m_close_window_handler.removeMessages(1);
        }
        if (c_auto_close_time > 0)
        {
            m_close_window_handler.sendEmptyMessageDelayed(1,c_auto_close_time);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == m_window_root_layout)
        {
            hideWindow();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d("TEST006", "onKey: "+v+" "+keyCode+" "+event);
        return false;
    }
}
