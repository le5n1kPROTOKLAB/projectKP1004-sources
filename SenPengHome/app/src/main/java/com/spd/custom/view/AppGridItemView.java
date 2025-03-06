package com.spd.custom.view;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.spd.home.R;

public class AppGridItemView extends FrameLayout implements View.OnClickListener,View.OnLongClickListener,View.OnTouchListener{
    private ImageView m_icon;
    private TextView m_text;
    private ColorMatrixColorFilter m_press_color_filter;
    public AppGridItemView(Context context) {
        super(context);
        m_icon = new ImageView(context);
        FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        c_para.gravity = Gravity.CENTER;
        m_icon.setLayoutParams(c_para);
        this.addView(m_icon);
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(0.5f,0.5f,0.5f,1f);
        m_press_color_filter = new ColorMatrixColorFilter(cm);
    }

    public AppGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(0.5f,0.5f,0.5f,1f);
        m_press_color_filter = new ColorMatrixColorFilter(cm);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        m_icon = this.findViewById(R.id.id_app_icon);
        m_text = this.findViewById(R.id.id_app_text);
        checkAppInfo();
    }
    private AppInfo m_app_info;
    public void setAppInfo(AppInfo c_app_info)
    {
        m_app_info = c_app_info;
        checkAppInfo();
    }

    private void checkAppInfo()
    {
        if (m_icon != null && m_text != null)
        {
            if (m_app_info != null)
            {
                m_icon.setImageBitmap(m_app_info.iconBitmap);
                m_text.setText(m_app_info.mAppName);
                m_icon.setVisibility(View.VISIBLE);
                m_text.setVisibility(View.VISIBLE);
                m_icon.setOnTouchListener(this);
                m_icon.setOnLongClickListener(this);
                m_icon.setOnClickListener(this);
            }
            else
            {
                m_icon.setVisibility(View.GONE);
                m_text.setVisibility(View.GONE);
                m_icon.setOnTouchListener(null);
                m_icon.setOnLongClickListener(null);
                m_icon.setOnClickListener(null);
            }
        }
    }
    private ShortCutClickListener m_listener;
    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        Log.d("SimonCheck003", "setShortCutClickListener: ");
        m_listener = c_listener;
    }
    @Override
    public void onClick(View view) {
        m_listener.onAppClicked(m_app_info);
    }

    @Override
    public boolean onLongClick(View view) {
        m_listener.onAppLongClicked(m_app_info);
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_icon.setColorFilter(m_press_color_filter);
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
        {
            m_icon.clearColorFilter();
        }
        return false;
    }
}
