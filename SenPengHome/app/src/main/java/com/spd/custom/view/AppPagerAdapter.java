package com.spd.custom.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
/*import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;*/

import com.android.internal.widget.PagerAdapter;
import com.android.internal.widget.ViewPager;
import com.spd.home.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 桌面一页的Apapter
 */
public class AppPagerAdapter extends PagerAdapter {
    private Context m_context;
    private int m_page_count = 0;
    public static int m_page_colum_num = 5;
    public static int m_page_row_num = 2;
    public static int m_page_count_per_page = m_page_colum_num * m_page_row_num;

    public AppPagerAdapter(Context context) {
        super();
        m_context = context;
    }

    // 桌面用不到，应用列表页面会用到
    @Override
    public int getCount() {
        return m_page_count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private List<FrameLayout> m_free_layout_list = new ArrayList<>();
    private List<FrameLayout> m_use_layout_list = new ArrayList<>();

    // 桌面用不到，应用列表页面会用到
    private FrameLayout getFreeLayout() {
        FrameLayout c_result;
        if (m_free_layout_list.size() > 0) {
            c_result = m_free_layout_list.remove(0);
        } else {
            c_result = new FrameLayout(m_context);
            ViewPager.LayoutParams c_gridlayout_para = new ViewPager.LayoutParams();
            c_result.setLayoutParams(c_gridlayout_para);
            GridLayout c_grid_layout = new GridLayout(m_context);
            c_grid_layout.setColumnCount(m_page_colum_num);
            c_grid_layout.setRowCount(m_page_row_num);
            FrameLayout.LayoutParams c_grid_layout_para = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            c_grid_layout.setLayoutParams(c_grid_layout_para);
            c_result.addView(c_grid_layout);
        }
        return c_result;
    }

    // 桌面用不到，应用列表页面会用到
    private void refreshItemLayout(int position, FrameLayout c_item_layout) {
        GridLayout c_grid_layout = (GridLayout) c_item_layout.getChildAt(0);
        for (int c_page_index = 0; c_page_index < m_page_count_per_page; c_page_index++) {
            int c_app_index = position * m_page_count_per_page + c_page_index;
            View view = c_grid_layout.getChildAt(c_page_index);
            final AppGridItemView c_app_view;
            if (view != null && view instanceof AppGridItemView) {
                c_app_view = (AppGridItemView) view;
            } else {
                // TODO 查看应用列表 item 项
                c_app_view = (AppGridItemView) View.inflate(m_context, R.layout.app_item_layout, null);
                c_app_view.setShortCutClickListener(m_listener);
                GridLayout.LayoutParams c_para = new GridLayout.LayoutParams();
                c_para.width = 0;
                c_para.height = 0;
                c_para.columnSpec = GridLayout.spec(c_page_index % m_page_colum_num, 1f);
                c_para.rowSpec = GridLayout.spec(c_page_index / m_page_colum_num, 1f);
                c_app_view.setLayoutParams(c_para);

                c_grid_layout.addView(c_app_view);
            }
            if (c_app_index < m_app_list.size()) {
                c_app_view.setAppInfo(m_app_list.get(c_app_index));
            } else {
                c_app_view.setAppInfo(null);
            }
        }
    }

    // 桌面用不到，应用列表页面会用到
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d("Test010", "instantiateItem: " + m_app_list.size());
        FrameLayout c_item_layout = getFreeLayout();
        refreshItemLayout(position, c_item_layout);
        m_use_layout_list.add(c_item_layout);
        container.addView(c_item_layout);
        return c_item_layout;
    }

    // 桌面用不到，应用列表页面会用到
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d("Test010", "destroyItem: ");
        if (object != null && object instanceof FrameLayout) {
            FrameLayout c_layout = (FrameLayout) object;
            m_free_layout_list.add(c_layout);
            m_use_layout_list.remove(c_layout);
            container.removeView(c_layout);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private List<AppInfo> m_app_list;

    // 桌面用不到，应用列表页面会用到
    public void setAppInfoList(List<AppInfo> c_app_list) {
        m_app_list = c_app_list;
        if (c_app_list != null) {
            Log.d("Test010", "setAppInfoList: " + c_app_list.size());
        } else {
            Log.d("Test010", "setAppInfoList: " + c_app_list);
        }
        if (m_app_list != null) {
            m_page_count = (m_app_list.size() / m_page_count_per_page) + Math.min(1, m_app_list.size() % m_page_count_per_page);
        } else {
            m_page_count = 0;
        }
        this.notifyDataSetChanged();
    }

    private ShortCutClickListener m_listener;

    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        m_listener = c_listener;
    }

    public void setColumnsAndRows(int columns, int rows) {
        m_page_colum_num = columns;
        m_page_row_num = rows;
        m_page_count_per_page = m_page_colum_num * m_page_row_num;
    }
}
