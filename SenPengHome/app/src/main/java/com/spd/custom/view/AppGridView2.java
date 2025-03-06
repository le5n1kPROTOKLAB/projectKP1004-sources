package com.spd.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.List;

public class AppGridView2 extends HorizontalScrollView {
    private AppGridViewAdapter2 m_adapter;
    private boolean m_adapter_set_flag = false;
    private LinearLayout m_linearLayout;
    private GridView m_grid_view;


    public AppGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_adapter = new AppGridViewAdapter2(context);
        m_linearLayout = new LinearLayout(context);
        HorizontalScrollView.LayoutParams c_linearlayout_para = new HorizontalScrollView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        m_linearLayout.setLayoutParams(c_linearlayout_para);
        m_grid_view = new GridView(context);
        LinearLayout.LayoutParams c_linear_layout_para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        c_linear_layout_para.gravity = Gravity.CENTER;
        m_grid_view.setLayoutParams(c_linear_layout_para);
        m_grid_view.setNumColumns(GridView.AUTO_FIT);
        m_grid_view.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        m_linearLayout.addView(m_grid_view);
        this.addView(m_linearLayout);
    }
    public void setAppInfoList(List<AppInfo> c_app_list)
    {
        int c_colume_num = c_app_list.size()/2+c_app_list.size()%2;
        LinearLayout.LayoutParams c_linear_layout_para = (LinearLayout.LayoutParams)m_grid_view.getLayoutParams();
        c_linear_layout_para.width = c_colume_num*AppGridViewAdapter2.app_icon_width;
        m_grid_view.requestLayout();
        m_grid_view.setColumnWidth(AppGridViewAdapter2.app_icon_width);     // 设置列表项宽
        m_grid_view.setHorizontalSpacing(10);   // 设置列表项水平间距
        m_grid_view.setStretchMode(GridView.NO_STRETCH);
        m_grid_view.setNumColumns(c_colume_num);
        m_adapter.setAppInfoList(c_app_list);
        if (!m_adapter_set_flag)
        {
            m_adapter_set_flag = true;
            m_grid_view.setAdapter(m_adapter);
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

/*    int m_mask_height = 90;
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
    }*/


}
