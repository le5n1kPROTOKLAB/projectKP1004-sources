package com.spd.custom.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.spd.home.R;

import java.util.List;

public class AppGridViewAdapter extends BaseAdapter {
    private Context m_context;
    private AppGridView m_parent_view;
    public AppGridViewAdapter(AppGridView c_parent_view,Context context)
    {
        m_context = context;
        m_parent_view = c_parent_view;
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
            c_para = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,180);
        }
        if (i < m_parent_view.getNumColumns())
        {
            c_para.height = 210;
        }
        else
        {
            c_para.height = 180;
        }
        c_result.setLayoutParams(c_para);
        return c_result;
    }
    private ShortCutClickListener m_listener;
    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        m_listener = c_listener;
    }
}
