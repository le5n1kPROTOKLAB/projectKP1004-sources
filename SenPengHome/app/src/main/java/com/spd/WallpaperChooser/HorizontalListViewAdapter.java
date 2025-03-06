package com.spd.WallpaperChooser;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.spd.home.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private int m_list_count = 5;
    private int m_live_wallpaper_count = 0;
    private List<ResolveInfo> m_live_wallpaper_list;
    private PackageManager mPackageManager;
    private int[] m_default_wallpaper_id = new int[] {R.drawable.default_bg_00,R.drawable.default_bg_01,R.drawable.default_bg_02,
            R.drawable.default_bg_03,R.drawable.default_bg_04,R.drawable.default_bg_05,R.drawable.default_bg_06,R.drawable.default_bg_07};
    private Activity m_act;
    private View m_root_view;
    public HorizontalListViewAdapter(Activity context,View c_root_view){
        this.mContext = context;
        m_root_view = c_root_view;
        m_act = context;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
        buildLiveWallpaperList();
    }

    public void buildLiveWallpaperList()
    {
        mPackageManager = mContext.getPackageManager();
        m_live_wallpaper_list = mPackageManager.queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);
        m_live_wallpaper_count = m_live_wallpaper_list.size();
        Log.d("Wallpaper", "getLiveWallpaperList: "+m_live_wallpaper_count);
    }

    @Override
    public void notifyDataSetChanged() {
        buildLiveWallpaperList();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return m_live_wallpaper_count+8;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WallpaperIcon c_result;
        if(convertView==null){
            c_result = new WallpaperIcon(m_act,m_root_view);
            ViewGroup.LayoutParams c_para = new ViewGroup.LayoutParams(210,ViewGroup.LayoutParams.MATCH_PARENT);
            c_result.setLayoutParams(c_para);
        }else{
            c_result = (WallpaperIcon)convertView;

        }
        if (position<m_live_wallpaper_count)
        {
            WallpaperInfo info = null;
            try {
                info = new WallpaperInfo(mContext, m_live_wallpaper_list.get(position));
                c_result.setLiveWallpaperInfo(info);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        }
        else
        {
            c_result.setStaticWallpaperInfo(position-m_live_wallpaper_count,m_default_wallpaper_id[position-m_live_wallpaper_count]);
        }
        return c_result;
    }

}

