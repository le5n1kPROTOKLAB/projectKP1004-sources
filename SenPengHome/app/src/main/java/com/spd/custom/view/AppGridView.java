package com.spd.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

import java.util.List;

public class AppGridView extends GridView{
    private AppGridViewAdapter m_adapter;
    private boolean m_adapter_set_flag = false;
    public AppGridView(Context context) {
        super(context);
        m_adapter = new AppGridViewAdapter(this,context);
    }

    public AppGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_adapter = new AppGridViewAdapter(this,context);
    }
    public void setAppInfoList(List<AppInfo> c_app_list)
    {
        m_adapter.setAppInfoList(c_app_list);
        if (!m_adapter_set_flag)
        {
            m_adapter_set_flag = true;
            this.setAdapter(m_adapter);
        }
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        Log.d("SimonCheck002", "setAlpha: =="+alpha);
    }

    public void setShortCutClickListener(ShortCutClickListener c_listener)
    {
        m_adapter.setShortCutClickListener(c_listener);
    }
    int m_mask_height = 90;
    Paint m_paint = new Paint();
    LinearGradient m_shader = new LinearGradient(0,0, 0,m_mask_height,0x00ffffff, 0xffffffff, Shader.TileMode.MIRROR);// 创建线性渐变LinearGradient对象
    {
        m_paint.setShader(m_shader); // 绘制
        m_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }
    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), m_mask_height, m_paint);
    }


}
