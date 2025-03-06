package com.spd.WallpaperChooser;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.spd.home.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//AdapterView.OnItemClickListener
public class WallpaperChooserPopupWindow extends PopupWindow implements View.OnTouchListener, AdapterView.OnItemClickListener {
    private FrameLayout m_root_layout;
    private ObjectAnimator m_layout_anim;
    private boolean m_pop_show_flag = false;
    private Context m_context;
    private HorizontalListView m_horizontal_list_view;
    private HorizontalListViewAdapter m_list_adapter;
    private FrameLayout m_warning_layout;
    private Activity m_act;
    public interface WallpaperWindowListener
    {
        void onWallpaperWindowShow();
        void onWallpaperWindowHide();
    }
    private WallpaperWindowListener m_listener;
    public void setWallpaperWindowListener(WallpaperWindowListener c_listener)
    {
        m_listener = c_listener;
    }

    public WallpaperChooserPopupWindow(Activity context,View c_root_view) {
        super(context);
        m_act = context;
        m_context = context;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(160);
        ColorDrawable c_bg_drawale = new ColorDrawable();
        this.setBackgroundDrawable(c_bg_drawale);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(false);
        this.setTouchInterceptor(this);
        if (m_root_layout == null)
        {
            LayoutInflater c_inflater=(LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
            m_root_layout = (FrameLayout)c_inflater.inflate(R.layout.wallpaper_main_layout,null);
            m_root_layout.setTranslationY(60);
            m_root_layout.setAlpha(0);
            m_root_layout.setVisibility(View.GONE);
            this.setContentView(m_root_layout);
            m_horizontal_list_view = m_root_layout.findViewById(R.id.id_wallpaper_list_view);
            m_list_adapter = new HorizontalListViewAdapter(context,c_root_view);
            m_horizontal_list_view.setAdapter(m_list_adapter);
            m_horizontal_list_view.setOnItemClickListener(this);
            m_warning_layout = m_root_layout.findViewById(R.id.id_layout_for_set_wallpaper_warning);
        }
    }

    @Override
    public void dismiss() {
        if (m_pop_show_flag) {
            m_pop_show_flag = false;
            if (m_close_handler.hasMessages(1)) {
                m_close_handler.removeMessages(1);
            }
            m_close_handler.sendEmptyMessage(1);
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (!m_pop_show_flag)
        {
            m_pop_show_flag = true;
            if (m_layout_anim!= null && m_layout_anim.isRunning())
            {
                m_layout_anim.cancel();
            }
            PropertyValuesHolder c_main_layout_show_alpha = PropertyValuesHolder.ofFloat("Alpha",m_root_layout.getAlpha() , 1f);
            //PropertyValuesHolder c_main_layout_hide_visibility = PropertyValuesHolder.ofInt("Visibility",View.VISIBLE , View.GONE);
            PropertyValuesHolder c_main_layout_show_x = PropertyValuesHolder.ofFloat("TranslationY",m_root_layout.getTranslationY() , 0);
            m_root_layout.setVisibility(View.VISIBLE);
            m_layout_anim = ObjectAnimator.ofPropertyValuesHolder(m_root_layout,c_main_layout_show_alpha,c_main_layout_show_x);
            m_layout_anim.setDuration(500);
            m_layout_anim.start();
            if (m_close_handler.hasMessages(1))
            {
                m_close_handler.removeMessages(1);
            }
            m_close_handler.sendEmptyMessageDelayed(1,10000);
            if (m_listener != null)
            {
                m_listener.onWallpaperWindowShow();
            }
            m_list_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.d("Wallpaper", "onTouch: "+event.getX()+" "+event.getY());
        if (m_close_handler.hasMessages(1))
        {
            m_close_handler.removeMessages(1);
        }
        m_close_handler.sendEmptyMessageDelayed(1,10000);
        if (m_set_wall_paper_flag)
        {
            return true;
        }
        if (event.getY()>=0){//PopupWindow内部的事件
            return false;
        }
        if (m_pop_show_flag) {
            m_pop_show_flag = false;
            if (m_close_handler.hasMessages(1))
            {
                m_close_handler.removeMessages(1);
            }
            m_close_handler.sendEmptyMessage(1);
        }
        return true;
    }
    Handler m_close_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
            {
                WallpaperChooserPopupWindow.super.dismiss();
                if (m_es != null && !m_es.isShutdown())
                {
                    m_es.shutdown();
                }
            }
            else if (msg.what == 1)
            {
                m_pop_show_flag = false;
                if (m_layout_anim!= null && m_layout_anim.isRunning())
                {
                    m_layout_anim.cancel();
                }
                PropertyValuesHolder c_main_layout_hide_alpha = PropertyValuesHolder.ofFloat("Alpha",m_root_layout.getAlpha() ,0f);
                PropertyValuesHolder c_main_layout_hide_visibility = PropertyValuesHolder.ofInt("Visibility",View.VISIBLE , View.GONE);
                PropertyValuesHolder c_main_layout_hide_x = PropertyValuesHolder.ofFloat("TranslationY",m_root_layout.getTranslationY() , 60);
                m_layout_anim = ObjectAnimator.ofPropertyValuesHolder(m_root_layout,c_main_layout_hide_alpha,c_main_layout_hide_visibility,c_main_layout_hide_x);
                m_layout_anim.setDuration(500);
                m_layout_anim.addListener(new ObjectAnimator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation, boolean isReverse) {
                        Log.d("Wallpaper", "onAnimationStart: ");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation, boolean isReverse) {
                        Log.d("Wallpaper", "onAnimationEnd: ");
                        m_close_handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.d("Wallpaper", "onAnimationStart: ");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.d("Wallpaper", "onAnimationEnd: ");

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.d("Wallpaper", "onAnimationCancel: ");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        Log.d("Wallpaper", "onAnimationRepeat: ");

                    }
                });
                m_layout_anim.start();
                if (m_listener != null)
                {
                    m_listener.onWallpaperWindowHide();
                }
            }
        }
    };
    public static ExecutorService m_es;
    private boolean m_set_wall_paper_flag = false;
    private WallpaperIcon m_last_click_icon;
    private String m_wallpaper_key = "com.spd.home.wallpaper.color";
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Mychild", "onItemClick: ");
        if (!m_set_wall_paper_flag)
        {
            m_last_click_icon = (WallpaperIcon)view;
            m_warning_layout.setVisibility(View.VISIBLE);
            if (m_es == null || m_es.isShutdown())
            {
                m_es = Executors.newFixedThreadPool(1);
            }
            m_set_wall_paper_flag = true;

            m_es.submit(new Runnable() {
                @Override
                public void run() {
                    int c_wall_paper_color = m_last_click_icon.onWallpaperIconClick();
                    ContentResolver cv = WallpaperChooserPopupWindow.this.m_context.getContentResolver();
                    android.provider.Settings.System.putInt(cv,m_wallpaper_key , c_wall_paper_color);
                    m_set_wall_paper_flag = false;
                    m_warning_layout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
