package com.spd.custom.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;


public class AppInfo {
    private static final String TAG = "AppInfo";
    private Context mContext;
    private Intent mIntent;
    public Bitmap iconBitmap;
    public int iconColor;
    public float colorHue;
    public String componentName;
    public String className;
    public String mAppName;
    public int m_app_id;
    public int m_short_cut_bg;
    public int m_short_cut_icon;
    public AppInfo(Context context, String c_package_name, String c_class_name, String c_app_name, Bitmap c_icon_bitmap) {
        mContext = context;
        componentName = c_package_name;
        className = c_class_name;
        mAppName = c_app_name;
        iconBitmap = c_icon_bitmap;
        iconColor = countBitmapColor(iconBitmap);
        colorHue = getHueFromColor(iconColor);
        m_app_id = 0;
    }
    public Intent getLaunchIntent() {
        if(mIntent == null) {
            int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
            if (!TextUtils.isEmpty(className))
            {
                mIntent = new Intent();
                mIntent.addCategory("android.intent.category.LAUNCHER");
                mIntent.setComponent(new ComponentName(componentName, className));
                mIntent.setFlags(launchFlags);
            }
            else if (!TextUtils.isEmpty(componentName) && mContext != null)
            {
                PackageManager manager = mContext.getPackageManager();
                mIntent = manager.getLaunchIntentForPackage(componentName);
            }
        }
        Log.d("SimonCheck003", "getLaunchIntent: mIntent = " + mIntent);
        return mIntent;
    }

    private int countBitmapColor(Bitmap c_bmp)
    {
        int c_width = c_bmp.getWidth();
        int c_height = c_bmp.getHeight();
        int c_sample = 4;
        float c_color_r = 0;
        float c_color_b = 0;
        float c_color_g = 0;
        float c_point_quanzhong = 0f;
        for (int c_x = c_sample/2 ; c_x<c_width ; c_x += c_sample)
        {
            for (int c_y = c_sample/2 ; c_y<c_height ; c_y += c_sample)
            {
                int c_point_color = c_bmp.getPixel(c_x,c_y);

                int c_alpha = (c_point_color & 0xff000000) >>> 24;
                float c_alpha_f = c_alpha/255f;
                int c_red = (c_point_color & 0x00ff0000) >>> 16;
                int c_green = (c_point_color & 0x0000ff00) >>> 8;
                int c_blue = c_point_color & 0x000000ff;
                c_color_r += c_red*c_alpha_f;
                c_color_g += c_green*c_alpha_f;
                c_color_b += c_blue*c_alpha_f;
                c_point_quanzhong += c_alpha_f;
            }
        }
        c_color_r /= c_point_quanzhong;
        c_color_g /= c_point_quanzhong;
        c_color_b /= c_point_quanzhong;
        int c_result = Color.rgb(Math.round(c_color_r), Math.round(c_color_g), Math.round(c_color_b));
        return c_result;
    }
    private float getHueFromColor(int c_color)
    {
        float h,s,v;
        int r = (c_color & 0x00ff0000) >> 16;
        int g =(c_color & 0x0000ff00) >> 8;
        int b = (c_color & 0x000000ff);
        float min, max, delta;
        min = Math.min( r, Math.min( g, b ));
        max = Math.max( r, Math.max( g, b ));
        v = max;               // v
        delta = max - min;
        if( max != 0 )
        {
            s = delta / max;
        }
        else {
            s = 0;
            h = -1;
            return h;
        }
        if( r == max )
        {
            h = ( g - b ) / delta;
        }
        else if( g == max )
        {
            h = 2 + ( b - r ) / delta;
        }
        else
        {
            h = 4 + ( r - g ) / delta;
        }
        h *= 60;
        if( h < 0 )
        {
            h += 360;
        }
        float c_hue = h * 1.0f / 32f;

        Log.d("SimonCheck003", "getHueFromColor: " + h + " " + s + " " + v);
        return c_hue;
    }
    public AppInfo(Context context, int c_system_app_id , String c_pack_name ,String c_class_name, String c_app_name , int c_short_cut_bg ,int c_short_cut_icon)
    {
        componentName = c_pack_name;
        className = c_class_name;
        mAppName = c_app_name;
        m_app_id = c_system_app_id;
        m_short_cut_bg = c_short_cut_bg;
        m_short_cut_icon = c_short_cut_icon;
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
        if (c_class_name != null)
        {
            mIntent = new Intent();
            mIntent.addCategory("android.intent.category.LAUNCHER");
            mIntent.setComponent(new ComponentName(c_pack_name,c_class_name));
            mIntent.setFlags(launchFlags);
        }
        else if (c_pack_name != null && !c_pack_name.equals(""))
        {
            PackageManager manager = context.getPackageManager();
            mIntent = manager.getLaunchIntentForPackage(c_pack_name);
        }
        if (c_system_app_id == 0)
        {
            iconBitmap = Utilities.getAppIcon(context,componentName,c_class_name);
            Log.d("SimonCheck003", "countBitmapColor: =1=");
            iconColor = countBitmapColor(iconBitmap);
            colorHue = getHueFromColor(iconColor);
        }
    }

    @Override
    public String toString() {
        String c_str = "componentName = "+componentName+" , mIntent = "+mIntent+" "+mIntent.getComponent();
        return c_str;
    }
}
