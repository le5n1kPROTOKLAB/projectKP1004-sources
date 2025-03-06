package com.spd.WallpaperChooser;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.spd.home.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WallpaperIcon extends FrameLayout{
    private PackageManager mPackageManager;
    private WallpaperManager mWallpaperManager;

    private ImageView m_background_img , m_live_img;
    private Activity m_act;
    private View m_root_view;
    public WallpaperIcon(Activity context,View c_root_view) {
        super(context);
        m_root_view = c_root_view;
        m_act = context;
        mPackageManager = context.getPackageManager();
        mWallpaperManager = WallpaperManager.getInstance(context);
        m_background_img = new ImageView(context);
        m_live_img = new ImageView(context);
        FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        c_para.gravity = Gravity.CENTER|Gravity.BOTTOM;
        c_para.bottomMargin = 10;
        m_live_img.setLayoutParams(c_para);
        m_live_img.setImageResource(R.drawable.wallpaper_live_icon);
        c_para = new FrameLayout.LayoutParams(200,110);
        c_para.gravity = Gravity.CENTER;
        c_para.leftMargin = 5;
        c_para.rightMargin = 5;
        m_background_img.setLayoutParams(c_para);
        m_background_img.setBackgroundColor(0xffff0000);
        this.addView(m_background_img);
        this.addView(m_live_img);
        m_live_img.setVisibility(View.GONE);
    }
    private int m_static_wallpaper_resource = 0;
    private int m_default_wallpaper_index = -1;
    private static HashMap<Integer, WeakReference<Bitmap>> m_static_bimap_buffer = new HashMap<Integer, WeakReference<Bitmap>>();
    public void setStaticWallpaperInfo(int c_default_wallpaper_index , int c_bitmap_resource)
    {
        if (m_static_wallpaper_resource != c_bitmap_resource)
        {
            m_default_wallpaper_index = c_default_wallpaper_index;
            m_wallpaper_info = null;
            Bitmap bitmap = null;
            m_static_wallpaper_resource = c_bitmap_resource;
            WeakReference<Bitmap> c_old_buffer = m_static_bimap_buffer.get(c_bitmap_resource);
            if (c_old_buffer != null && c_old_buffer.get() != null && !c_old_buffer.get().isRecycled()) {
                bitmap = c_old_buffer.get();
            }
            else
            {
                int width = 200;
                int height = 110;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(),c_bitmap_resource,options);
                options.inJustDecodeBounds = false; // 设为 false
                int h = options.outHeight;
                int w = options.outWidth;
                int beWidth = w / width;
                int beHeight = h / height;
                int be = 1;
                if (beWidth < beHeight) {
                    be = beWidth;
                } else {
                    be = beHeight;
                }
                if (be <= 0) {
                    be = 1;
                }
                options.inSampleSize = be;
                bitmap = BitmapFactory.decodeResource(getResources(),c_bitmap_resource,options);
                if(bitmap != null && (bitmap.getWidth() > width || bitmap.getHeight()>height))
                {
                    float c_scale_width = (float)width/(float)bitmap.getWidth();
                    float c_scale_height = (float)height/(float)bitmap.getHeight();
                    float c_scall = c_scale_width;
                    if (c_scale_height>c_scale_width)
                    {
                        c_scall = c_scale_height;
                    }
                    float c_offset_x = (width - bitmap.getWidth()*c_scall)/2f;
                    float c_offset_y = (height - bitmap.getHeight()*c_scall)/2f;
                    Bitmap c_swap_bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                    Paint c_scale_paint = new Paint();
                    c_scale_paint.setAntiAlias(true);
                    Canvas c_scale_canvas = new Canvas(c_swap_bmp);
                    c_scale_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                    Matrix c_scale_matrix = new Matrix();
                    c_scale_matrix.postScale(c_scall,c_scall);
                    c_scale_matrix.postTranslate(c_offset_x,c_offset_y);
                    c_scale_canvas.drawBitmap(bitmap,c_scale_matrix,c_scale_paint);
                    c_scale_canvas.setBitmap(null);
                    bitmap.recycle();
                    bitmap = c_swap_bmp;
                    m_static_bimap_buffer.put(c_bitmap_resource , new WeakReference<>(bitmap));
                }
            }
            //setImageDrawable(null);
            m_background_img.setImageBitmap(bitmap);
        }
        m_live_img.setVisibility(View.GONE);
    }
    private static HashMap<String, WeakReference<Bitmap>> m_live_bimap_buffer = new HashMap<String, WeakReference<Bitmap>>();
    private WallpaperInfo m_wallpaper_info ;
    public void setLiveWallpaperInfo(WallpaperInfo c_wallpaper_info) {
        m_wallpaper_info = c_wallpaper_info;
        if (m_wallpaper_info != null)
        {
            Drawable thumb = m_wallpaper_info.loadThumbnail(mPackageManager);
            if (thumb == null)
            {
                m_background_img.setImageResource(R.drawable.wallpaper_default_icon);
            }
            else
            {
                String c_live_key = c_wallpaper_info.getPackageName()+c_wallpaper_info.getServiceName();
                WeakReference<Bitmap> c_old_buffer = m_live_bimap_buffer.get(c_live_key);
                if (c_old_buffer != null && c_old_buffer.get() != null && !c_old_buffer.get().isRecycled()) {
                    Bitmap c_bitmap = c_old_buffer.get();
                    m_background_img.setImageBitmap(c_bitmap);
                    m_background_img.invalidate();
                }
                else
                {
                    Bitmap c_bitmap = buildWallpaperIcon(thumb,200,110);
                    m_live_bimap_buffer.put(c_live_key , new WeakReference<>(c_bitmap));
                    m_background_img.setImageBitmap(c_bitmap);
                    m_background_img.invalidate();
                }
            }
        }
        else
        {
            m_background_img.setImageResource(R.drawable.icon_bottom_wallpaper);
        }
        m_static_wallpaper_resource = 0;
        m_default_wallpaper_index = -1;
        m_live_img.setVisibility(View.VISIBLE);
    }

    private Bitmap buildWallpaperIcon(Drawable drawable , int width , int height)
    {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        if(bitmap != null && (bitmap.getWidth() > width || bitmap.getHeight()>height))
        {
            float c_scale_width = (float)width/(float)bitmap.getWidth();
            float c_scale_height = (float)height/(float)bitmap.getHeight();
            float c_scall = c_scale_width;
            if (c_scale_height>c_scale_width)
            {
                c_scall = c_scale_height;
            }
            float c_offset_x = (width - bitmap.getWidth()*c_scall)/2f;
            float c_offset_y = (height - bitmap.getHeight()*c_scall)/2f;
            Bitmap c_swap_bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Paint c_scale_paint = new Paint();
            c_scale_paint.setAntiAlias(true);
            Canvas c_scale_canvas = new Canvas(c_swap_bmp);
            c_scale_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            Matrix c_scale_matrix = new Matrix();
            c_scale_matrix.postScale(c_scall,c_scall);
            c_scale_matrix.postTranslate(c_offset_x,c_offset_y);
            c_scale_canvas.drawBitmap(bitmap,c_scale_matrix,c_scale_paint);
            c_scale_canvas.setBitmap(null);
            bitmap.recycle();
            bitmap = c_swap_bmp;
        }
        else
        {
            Bitmap c_swap_bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            c_swap_bmp.eraseColor(0xff000000);
            Paint c_src_paint = new Paint();
            c_src_paint.setAntiAlias(true);
            Canvas c_src_canvas = new Canvas(c_swap_bmp);
            c_src_canvas.drawBitmap(bitmap,width/2-bitmap.getWidth()/2,height/2-bitmap.getHeight()/2,c_src_paint);
            c_src_canvas.setBitmap(null);
            bitmap.recycle();
            bitmap = c_swap_bmp;
        }
        return bitmap;
    }
    private int[] m_default_wallpaper_color = new int[]{0xff00c7ba,0xffed183d,0xff0e85db,0xff1b5de1,0xffe19029,0xffaf11d7,0xff07c252,0xffd60a93};

    public int onWallpaperIconClick() {
        int c_result_color = 0xff1246ff;
        if (m_wallpaper_info != null) {
/*            Intent preview = new Intent(this, FullscreenActivity.class);
            preview.putExtra(FullscreenActivity.EXTRA_LIVE_WALLPAPER_INFO, m_wallpaper_info);
            startActivityForResult(preview, REQUEST_PREVIEW);*/
/*            Intent c_intent = new Intent(WallpaperService.SERVICE_INTERFACE)
                    .setClassName(m_wallpaper_info.getPackageName(), m_wallpaper_info.getServiceName());
            //getContext().startActivity(c_intent);
            mWallpaperManager.setWallpaperComponent(c_intent.getComponent());
            mWallpaperManager.setWallpaperOffsetSteps(0.5f, 0.0f);*/
            //c_wallpaperManager.setWallpaperOffsets(windowToken, 0.5f, 0.0f);
            //ComponentName c_cn = new ComponentName(m_wallpaper_info.getPackageName(),m_wallpaper_info.getServiceName());
            //Intent preview = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            //preview.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,c_cn);
            //startActivity(preview);
            ComponentName c_componet_name = new ComponentName(m_wallpaper_info.getPackageName(),m_wallpaper_info.getServiceName());
            Method c_mothod = null;
            try {
                c_mothod = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class);
                c_mothod.setAccessible(true);
                c_mothod.invoke(mWallpaperManager,c_componet_name);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        else if (m_static_wallpaper_resource != 0)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),m_static_wallpaper_resource);
            if (m_default_wallpaper_index >= 0)
            {
                c_result_color = m_default_wallpaper_color[m_default_wallpaper_index];
            }
            final DisplayMetrics metrics = Util.setWallpaperManagerFitScreen(m_act);
            final int width = metrics.widthPixels;
            final int height = metrics.heightPixels;

            Bitmap wallpaper = Util.centerCrop(bitmap, metrics);

            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(m_act);
            try {
                wallpaperManager.setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return c_result_color;
    }
}
