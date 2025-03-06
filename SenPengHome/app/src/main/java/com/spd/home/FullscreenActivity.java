package com.spd.home;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.senptec.util.DensityHelper;
import com.spd.Scene.HomeSceneManager;
import com.spd.custom.view.BtHelper;
import com.spd.custom.view.CustomDesktopParam;
import com.spd.custom.view.MediaHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * AppCompatActivity
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private float m_touch_x;
    private FrameLayout m_root_view;
    public static float DESKTOP_WIDTH = 1280;
    public static float DESKTOP_HEIGHT = 720;
    public static int DESKTOP_PAGE_COLUMN = CustomDesktopParam.DESKTOP_PAGE_COLUMN;
    public static int DESKTOP_PAGE_ROW = CustomDesktopParam.DESKTOP_PAGE_ROW;
    public static float DESKTOP_MARGIN_LEFT = CustomDesktopParam.DESKTOP_MARGIN_LEFT;
    public static float DESKTOP_MARGIN_RIGHT = CustomDesktopParam.DESKTOP_MARGIN_RIGHT;
    public static float DESKTOP_MARGIN_TOP = CustomDesktopParam.DESKTOP_MARGIN_TOP;
    public static float DESKTOP_MARGIN_BOTTOM = CustomDesktopParam.DESKTOP_MARGIN_BOTTOM;
    public static float m_view_draw_scale = 0;
    public static int m_icon_width = 80;
    //private LeafView m_leaf_view;
    private HomeSceneManager m_home_scene_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DensityHelper.setCustomDensity(this);
        super.onCreate(savedInstanceState);
        CustomDesktopParam.init(this);
//        if (DESKTOP_WIDTH == 1024)
//        {
//            m_view_draw_scale = 0;
//            DESKTOP_MARGIN_LEFT = 30;
//            DESKTOP_MARGIN_RIGHT = 30;
//            DESKTOP_MARGIN_TOP = 60;
//            DESKTOP_MARGIN_BOTTOM = 140;
//            m_icon_width = 80;
//        }
//        else if (DESKTOP_WIDTH == 1280)
//        {
//            m_view_draw_scale = -50;
//            DESKTOP_MARGIN_LEFT = 110;
//            DESKTOP_MARGIN_RIGHT = 110;
//            DESKTOP_MARGIN_TOP = 80;
//            DESKTOP_MARGIN_BOTTOM = 160;
//            m_icon_width = 96;
//        }
//		else if (DESKTOP_WIDTH == 1920)
//        {
//            m_view_draw_scale = 0;
//            DESKTOP_MARGIN_LEFT = 80;
//            DESKTOP_MARGIN_RIGHT = 80;
//            DESKTOP_MARGIN_TOP = 130;
//            DESKTOP_MARGIN_BOTTOM = 88;
//            m_icon_width = 96;
//        }
        //verifyStoragePermissions(this);
        this.overridePendingTransition(0, 0);
        MediaHelper.get().initMediaHelper(getApplicationContext());
        BtHelper.get().initBtHelper(getApplicationContext());
        //WallpaperManager c_manager = WallpaperManager.getInstance(this);
        //Intent c_back_bg_activity = new Intent("com.spd.bgmotion.Start");
        //c_back_bg_activity.addFlags(Intent.FLAG_FROM_BACKGROUND);
        //startActivity(c_back_bg_activity);

        //setLiveWallPaper();
        /*Intent preview = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        preview.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this,SpdWallPapaer.class));
        startActivity(preview);*/


/*        Intent localIntent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        //localIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);//android.service.wallpaper.CHANGE_LIVE_WALLPAPER
        localIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, SpdWallPapaer.class));
        localIntent.addCategory("android.intent.category.DEFAULT");
        this.startActivity(localIntent);*/

/*
        setContentView(R.layout.activity_fullscreen);
        GLSurfaceView c_gl_view = (GLSurfaceView)findViewById(R.id.id_gl_view);
        c_gl_view.setEGLContextClientVersion(2);
        //c_gl_view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //c_gl_view.setBackgroundResource(R.drawable.bg);
        //c_gl_view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //c_gl_view.setZOrderOnTop(true);
        m_gl_render = new MyGLRender(this , c_gl_view);
        c_gl_view.setRenderer(m_gl_render);
        //c_gl_view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        c_gl_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //m_gl_render.changeDotMotionMode(0);
            }
        });

        c_gl_view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            m_touch_x = motionEvent.getX();
                        }

                        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                        {
                            float c_x = motionEvent.getX();
                            float c_offset_x = c_x - m_touch_x;
                            m_touch_x = c_x;
                            m_gl_render.onViewMotionMoveOffset(c_offset_x);
                        }
                        return false;
                    }
                }
        );
        c_gl_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

*/

        Window c_window = getWindow();
        c_window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        c_window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        c_window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        c_window.setStatusBarColor(Color.TRANSPARENT);
        c_window.setNavigationBarColor(Color.TRANSPARENT);
        c_window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        Log.d("Test010", "onCreate: " + this.hashCode());
        setContentView(R.layout.temp_fullscreen);
        m_root_view = findViewById(R.id.id_scene_view);
        m_home_scene_manager = new HomeSceneManager(this, m_root_view);

        Log.d("TEST090", "onCreate: " + DESKTOP_WIDTH + " " + DESKTOP_HEIGHT);
        m_home_scene_manager.registerListener();
/*        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo == null) {
            Drawable c_drawable = wallpaperManager.getDrawable();
            m_root_view.setBackground(c_drawable);
        }*/
        m_init_intent = getIntent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        DensityHelper.setCustomDensity(this);
        super.onConfigurationChanged(newConfig);
    }

    Intent m_init_intent;

    public void showWallpaperSet() {
        m_home_scene_manager.showWallpaperSet();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        m_init_intent = intent;
        checkIntent();
    }

    private void checkIntent() {
        if (m_init_intent != null) {
            if (m_init_intent.hasExtra("wallpaper.set")) {
                boolean c_action = m_init_intent.getBooleanExtra("wallpaper.set", false);
                if (c_action) {
                    showWallpaperSet();
                }
            } else {
                if (m_home_scene_manager != null) {
                    m_home_scene_manager.onBackPressed();
                }
            }
            m_init_intent = null;
        }
    }

    public void setLiveWallPaper() {
        try {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            ComponentName c_componet_name = new ComponentName("com.spd.home", "com.spd.SpdWallPaper.SpdWallPapaer");
            Method c_mothod = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class);
            c_mothod.setAccessible(true);
            c_mothod.invoke(manager, c_componet_name);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        m_home_scene_manager.onBackPressed();
    }

    private boolean m_main_app_list_init_flag = false;
    private String m_wallpaper_key = "com.spd.home.wallpaper.color";

    @Override
    protected void onResume() {
        Log.d("SimonCheck002", "HomeActivity onResume: ");
        super.onResume();
        int c_default_wall_paper_color = 0xff4786ca;
        ContentResolver cv = this.getContentResolver();
        int color = android.provider.Settings.System.getInt(cv, m_wallpaper_key, -1);
        if (color == -1) {
            android.provider.Settings.System.putInt(cv, m_wallpaper_key, c_default_wall_paper_color);
        }
        m_home_scene_manager.enterScene();
        checkIntent();
    }

    @Override
    protected void onPause() {
        Log.d("SimonCheck002", "HomeActivity onPause: ");
        //m_home_scene_manager.exitScene();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_home_scene_manager.unregisterListener();
        //m_home_scene_manager.unbindSceneService();
    }
}
