package com.spd.custom.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.spd.home.R;

import java.util.List;

public class AppGridViewAdapter2 extends BaseAdapter {
    public static final int app_icon_width = 200;
    public static final int app_icon_height = 230;

    private Context m_context;
    public AppGridViewAdapter2(Context context)
    {
        m_context = context;
    }
    private List<AppInfo> m_app_list;
    public void setAppInfoList(List<AppInfo> c_app_list)
    {
        m_app_list = c_app_list;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        int c_result = 0;
        if (m_app_list != null)
        {
            c_result = m_app_list.size();
        }
        return c_result;
    }

    @Override
    public Object getItem(int i) {
        return m_app_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppGridItemView c_result;
        if (view != null)
        {
            c_result = (AppGridItemView)view;
        }
        else
        {
            c_result = (AppGridItemView)View.inflate(m_context, R.layout.app_item_layout,null);
            c_result.setShortCutClickListener(m_listener);
        }
        c_result.setAppInfo(m_app_list.get(i));
        Object c_obj = c_result.getLayoutParams();
        AbsListView.LayoutParams c_para;
        if (c_obj != null && c_obj instanceof AbsListView.LayoutParams)
        {
            Log.d("ListView", "getView: has Para");
            c_para = (AbsListView.LayoutParams)c_obj;
        }
        else
        {
            Log.d("ListView", "getView: no Para");
            c_para = new AbsListView.LayoutParams(app_icon_width,app_icon_height);
        }
        c_result.setLayoutParams(c_para);
        return c_result;
    }
    private ShortCutClickListener m_listener;
    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        m_listener = c_listener;
    }
}
