package com.spd.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class AppPagerView extends ViewPager {
    private AppPagerAdapter m_adapter;
    private boolean m_adapter_set_flag = false;


    public AppPagerView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPageChangeListener(m_page_listener);
        this.setOffscreenPageLimit(2);
        CustomDesktopParam.getIconItemColumns(this, new CustomDesktopParam.OnValueListener() {
            @Override
            public void onValue(int... value) {
                m_adapter = new AppPagerAdapter(context);
                m_adapter.setColumnsAndRows(value[0], value[1]);
                m_adapter.setShortCutClickListener(c_listener);
                m_adapter.setAppInfoList(c_app_list);
                m_adapter_set_flag = true;
                setAdapter(m_adapter);
                m_page_count = m_adapter.getCount();
                if (m_order != null) {
                    m_order.onPageGuideChanged(m_page_index, m_page_count);
                }
                invalidate();
            }
        });
    }

    private List<AppInfo> c_app_list;

    public void setAppInfoList(List<AppInfo> c_app_list) {
        this.c_app_list = c_app_list;
        if (m_adapter != null) {
            m_adapter.setAppInfoList(c_app_list);
        }
    }

    public int getChildItemCount() {
        return m_adapter.getCount();
    }


    private PageGuideView.PageGuideOrder m_order;

    public void setPageGuideOrder(PageGuideView.PageGuideOrder c_order) {
        m_order = c_order;
        if (m_order != null) {
            m_order.onPageGuideChanged(m_page_index, m_page_count);
        }
    }

    private int m_page_index = 0;
    private int m_page_count = 1;
    private ViewPager.OnPageChangeListener m_page_listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            m_page_index = position;
            m_page_count = m_adapter.getCount();
            if (m_order != null) {
                m_order.onPageGuideChanged(m_page_index, m_page_count);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        Log.d("SimonCheck002", "setAlpha: ==" + alpha);
    }

    private ShortCutClickListener c_listener;

    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        this.c_listener = c_listener;
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
