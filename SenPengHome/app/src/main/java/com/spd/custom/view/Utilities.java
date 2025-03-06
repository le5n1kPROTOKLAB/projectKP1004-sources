package com.spd.custom.view;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.spd.home.FullscreenActivity;
import com.spd.home.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class Utilities {
    public static final int SYSTEM_APP_ID_NAVI = 0xfa000001;
    public static final int SYSTEM_APP_ID_MUSIC = 0xfa000002;
    public static final int SYSTEM_APP_ID_RADIO = 0xfa000003;
    public static final int SYSTEM_APP_ID_EQ = 0xfa000004;
    public static final int SYSTEM_APP_ID_360 = 0xfa000005;
    public static final int SYSTEM_APP_ID_WECHAT_AND_BROWNS = 0xfa000006;
    public static final int SYSTEM_APP_ID_VIDEO = 0xfa000007;
    public static final int SYSTEM_APP_ID_PHOTO = 0xfa000008;
    public static final int SYSTEM_APP_ID_CARLIFE = 0xfa000009;
    public static final int SYSTEM_APP_ID_DVR = 0xfa00000A;
    public static final int SYSTEM_APP_ID_AUX_IN = 0xfa00000B;
    public static final int SYSTEM_APP_ID_SCREEN_SET = 0xfa00000C;
    public static final int SYSTEM_APP_ID_CAR_SETTING = 0xfa00000D;
    public static final int SYSTEM_APP_ID_GOOGLE_PLAY = 0xfa00000E;
    public static final int SYSTEM_APP_ID_YOUTUBE = 0xfa00000F;

    public static final int SYSTEM_APP_ID_CARPLAY_AND_AUX_IN = 0xfa000010;
    public static final int SYSTEM_APP_ID_CAR_SETTING_AND_SCREEN_SAVER_SET = 0xfa000011;
    public static final int SYSTEM_APP_ID_SETTING = 0xfa000012;
    public static final int SYSTEM_APP_ID_AIQU = 0xfa000013;
    public static final int SYSTEM_APP_ID_QQ_MUSIC = 0xfa000014;
    public static final int SYSTEM_APP_ID_KAOLA = 0xfa000015;
    public static final int SYSTEM_APP_ID_XIMALAYA = 0xfa000016;
    public static final int SYSTEM_APP_ID_TENGXUN_VIDEO = 0xfa000017;
    public static final int SYSTEM_APP_ID_KUWO = 0xfa000018;
    public static final int SYSTEM_APP_ID_CHROME = 0xfa000019;
    public static final int SYSTEM_APP_ID_ES_BROWNS = 0xfa00001A;
    public static final int SYSTEM_APP_ID_CAR_PLAY = 0xfa00001B;
    public static final int SYSTEM_APP_ID_ANDROID_AUTO = 0xfa00001C;
    public static final int SYSTEM_APP_ID_PHONE = 0xfa00001D;
    public static final int SYSTEM_APP_ID_CAR_SWITCH = 0xfa00001E;
    public static final int SYSTEM_APP_ID_CAR_INFO = 0xfa00001F;
    public static final int SYSTEM_APP_ID_AIR_CONDITIONER = 0xfa000020;
    public static final int SYSTEM_APP_ID_APPS = 0xfa000021;

    public static final String TAG = "Launcher.Utilities";
    public static final float PI = 3.14159265357f;
    private static int sIconWidth = -1;
    private static int sIconHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconTextureHeight = -1;

    private static final Paint sBlurPaint = new Paint();
    private static final Paint sGlowColorPressedPaint = new Paint();
    private static final Paint sGlowColorFocusedPaint = new Paint();
    private static final Paint sDisabledPaint = new Paint();
    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
    }

    static int sColors[] = {0xffff0000, 0xff00ff00, 0xff0000ff};
    static int sColorIndex = 0;

    private final static String[] SYSTEM_APP_CMP_ARRAY = new String[]{
            "com.spd.spdeq.EQActivity",
            "com.spd.spdmedia.MusicActivity",
            "com.spd.spdmedia.VideoActivity",
            "com.spd.spdmedia.PhotoActivity",
            "com.spd.bt.FullscreenActivity",
            "com.spd.spdradio.FullscreenActivity"
    };

    /**
     * Returns a bitmap suitable for the all apps view. Used to convert pre-ICS
     * icon bitmaps that are stored in the database (which were 74x74 pixels at hdpi size)
     * to the proper size (48dp)
     */
    /*static Bitmap createIconBitmap(Bitmap icon, Context context) {
        int textureWidth = sIconTextureWidth;
        int textureHeight = sIconTextureHeight;
        int sourceWidth = icon.getWidth();
        int sourceHeight = icon.getHeight();
        if (sourceWidth > textureWidth && sourceHeight > textureHeight) {
            // Icon is bigger than it should be; clip it (solves the GB->ICS migration case)
            return Bitmap.createBitmap(icon,
                    (sourceWidth - textureWidth) / 2,
                    (sourceHeight - textureHeight) / 2,
                    textureWidth, textureHeight);
        } else if (sourceWidth == textureWidth && sourceHeight == textureHeight) {
            // Icon is the right size, no need to change it
            return icon;
        } else {
            // Icon is too small, render to a larger bitmap
            final Resources resources = context.getResources();
            return createIconBitmap(new BitmapDrawable(resources, icon), context);
        }
    }*/

    /**
     * Returns a bitmap suitable for the all apps view.
     */
    private static final MyBitmap drawableToBitmap(Drawable drawable) {
        MyBitmap c_result = new MyBitmap();
        c_result.m_bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(c_result.m_bmp);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return c_result;
    }

    private static class BitmapSideInfo {
        private int m_side_min;
        private int m_side_max;
        private int m_side_volume;
        private int m_shadow_volume;
        private boolean m_side_irregular;

        @Override
        public String toString() {
            return "BitmapSideInfo min = " + m_side_min + " max =" + m_side_max + " side=" + m_side_volume + " shadow=" + m_shadow_volume + " irr=" + m_side_irregular;
        }
    }

    private static int m_count_static = 0;

    private static class MyBitmap {
        private static final int ICON_TYPE_RECT = 0; //完全的矩形图标
        private static final int ICON_TYPE_RECT_IRREGULAR = 1; //缺损的矩形图标
        private static final int ICON_TYPE_ROUNDED_RECT = 2; //带圆角的矩形图标
        private static final int ICON_TYPE_ROUNDED_RECT_IRREGULAR = 3; //带圆角的缺损矩形图标
        private static final int ICON_TYPE_CIRCLE = 4; //圆形图标
        private static final int ICON_TYPE_IRREGULAR_FULL = 5; //不规则图标，但图标较为丰满
        private static final int ICON_TYPE_IRREGULAR_NARROW = 6; //不规则图标，但图标较为纤细


        public Bitmap m_bmp;
        public int m_icon_type = -1;
        public Rect m_full_icon_rect = new Rect();
        public Rect m_full_shadow_rect = new Rect();

        public void countIconType() {
            BitmapSideInfo c_top_side_info = countIconTopSideType(m_bmp);
            Log.d("IconTest", "countIconType: c_top_side_info = " + c_top_side_info.toString());
            BitmapSideInfo c_bottom_side_info = countIconBottomSideType(m_bmp);
            Log.d("IconTest", "countIconType: c_bottom_side_info = " + c_bottom_side_info.toString());
            BitmapSideInfo c_left_side_info = countIconLeftSideType(m_bmp);
            Log.d("IconTest", "countIconType: c_left_side_info = " + c_left_side_info.toString());
            BitmapSideInfo c_right_side_info = countIconRightSideType(m_bmp);
            Log.d("IconTest", "countIconType: c_right_side_info = " + c_right_side_info.toString());
            if (c_top_side_info.m_side_min == c_left_side_info.m_side_volume
                    && c_top_side_info.m_side_max == c_right_side_info.m_side_volume
                    && c_bottom_side_info.m_side_min == c_left_side_info.m_side_volume
                    && c_bottom_side_info.m_side_max == c_right_side_info.m_side_volume
                    && c_left_side_info.m_side_min == c_top_side_info.m_side_volume
                    && c_left_side_info.m_side_max == c_bottom_side_info.m_side_volume
                    && c_right_side_info.m_side_min == c_top_side_info.m_side_volume
                    && c_right_side_info.m_side_max == c_bottom_side_info.m_side_volume) {
                m_full_icon_rect.top = c_top_side_info.m_side_volume;
                m_full_icon_rect.bottom = c_bottom_side_info.m_side_volume;
                m_full_icon_rect.left = c_left_side_info.m_side_volume;
                m_full_icon_rect.right = c_right_side_info.m_side_volume;
                m_full_shadow_rect.top = c_top_side_info.m_shadow_volume;
                m_full_shadow_rect.bottom = c_bottom_side_info.m_shadow_volume;
                m_full_shadow_rect.left = c_left_side_info.m_shadow_volume;
                m_full_shadow_rect.right = c_right_side_info.m_shadow_volume;
                m_full_icon_rect.set(m_full_icon_rect.left, m_full_icon_rect.top, m_full_icon_rect.right, m_full_icon_rect.bottom);
                m_full_shadow_rect.set(m_full_shadow_rect.left, m_full_shadow_rect.top, m_full_shadow_rect.right, m_full_shadow_rect.bottom);

                if (!c_top_side_info.m_side_irregular && !c_bottom_side_info.m_side_irregular && !c_left_side_info.m_side_irregular && !c_right_side_info.m_side_irregular) {
                    m_icon_type = ICON_TYPE_RECT;
                } else {
                    m_icon_type = ICON_TYPE_RECT_IRREGULAR;
                }
            } else if ((c_top_side_info.m_side_max - c_top_side_info.m_side_min) > (c_right_side_info.m_side_volume - c_left_side_info.m_side_volume) * 0.6f
                    && (c_bottom_side_info.m_side_max - c_bottom_side_info.m_side_min) > (c_right_side_info.m_side_volume - c_left_side_info.m_side_volume) * 0.6f
                    && (c_left_side_info.m_side_max - c_left_side_info.m_side_min) > (c_bottom_side_info.m_side_volume - c_top_side_info.m_side_volume) * 0.6f
                    && (c_right_side_info.m_side_max - c_right_side_info.m_side_min) > (c_bottom_side_info.m_side_volume - c_top_side_info.m_side_volume) * 0.6f) {
                m_full_icon_rect.top = c_top_side_info.m_side_volume;
                m_full_icon_rect.bottom = c_bottom_side_info.m_side_volume;
                m_full_icon_rect.left = c_left_side_info.m_side_volume;
                m_full_icon_rect.right = c_right_side_info.m_side_volume;
                m_full_shadow_rect.top = c_top_side_info.m_shadow_volume;
                m_full_shadow_rect.bottom = c_bottom_side_info.m_shadow_volume;
                m_full_shadow_rect.left = c_left_side_info.m_shadow_volume;
                m_full_shadow_rect.right = c_right_side_info.m_shadow_volume;
                m_full_icon_rect.set(m_full_icon_rect.left, m_full_icon_rect.top, m_full_icon_rect.right, m_full_icon_rect.bottom);
                m_full_shadow_rect.set(m_full_shadow_rect.left, m_full_shadow_rect.top, m_full_shadow_rect.right, m_full_shadow_rect.bottom);
                if (!c_top_side_info.m_side_irregular && !c_bottom_side_info.m_side_irregular && !c_left_side_info.m_side_irregular && !c_right_side_info.m_side_irregular) {
                    m_icon_type = ICON_TYPE_ROUNDED_RECT;
                } else {
                    m_icon_type = ICON_TYPE_ROUNDED_RECT_IRREGULAR;
                }
            } else {
                //先测试图标是否圆形
                boolean c_is_circle = false;
                if (Math.abs((c_right_side_info.m_side_volume - c_left_side_info.m_side_volume) - (c_bottom_side_info.m_side_volume - c_top_side_info.m_side_volume)) < 5) {
                    c_is_circle = true;
                    int c_test_circle_x = (c_left_side_info.m_side_volume + c_right_side_info.m_side_volume) / 2;
                    int c_test_circle_y = (c_top_side_info.m_side_volume + c_bottom_side_info.m_side_volume) / 2;
                    float c_test_r = Math.min((c_right_side_info.m_side_volume - c_left_side_info.m_side_volume), (c_bottom_side_info.m_side_volume - c_top_side_info.m_side_volume)) * 0.5f;
                    float c_test_side_offset = 3f;
                    float c_test_out_side_r = c_test_r + c_test_side_offset;
                    float c_test_in_side_r = c_test_r - c_test_side_offset;
                    float c_test_count = 72f;
                    for (int i = 0; i < c_test_count; i++) {
                        double c_rotation = i * Math.PI * 2f / c_test_count;
                        int c_test_out_x = (int) Math.round(Math.cos(c_rotation) * c_test_out_side_r) + c_test_circle_x;
                        if (c_test_out_x >= m_bmp.getWidth()) {
                            c_test_out_x = m_bmp.getWidth() - 1;
                        } else if (c_test_out_x < 0) {
                            c_test_out_x = 0;
                        }
                        int c_test_out_y = (int) Math.round(Math.sin(c_rotation) * c_test_out_side_r) + c_test_circle_y;
                        if (c_test_out_y >= m_bmp.getHeight()) {
                            c_test_out_y = m_bmp.getHeight() - 1;
                        } else if (c_test_out_y < 0) {
                            c_test_out_y = 0;
                        }
                        int c_point_out_color = m_bmp.getPixel(c_test_out_x, c_test_out_y);
                        int c_point_out_alpha = (c_point_out_color & 0xff000000) >>> 24;
                        int c_test_in_x = (int) Math.round(Math.cos(c_rotation) * c_test_in_side_r) + c_test_circle_x;
                        int c_test_in_y = (int) Math.round(Math.sin(c_rotation) * c_test_in_side_r) + c_test_circle_y;
                        int c_point_in_color = m_bmp.getPixel(c_test_in_x, c_test_in_y);
                        int c_point_in_alpha = (c_point_in_color & 0xff000000) >>> 24;
                        if (c_point_out_alpha > 250 || c_point_in_alpha < 250) {
                            c_is_circle = false;
                            break;
                        }
                    }
                }
                if (c_is_circle) {
                    m_full_icon_rect.top = c_top_side_info.m_side_volume;
                    m_full_icon_rect.bottom = c_bottom_side_info.m_side_volume;
                    m_full_icon_rect.left = c_left_side_info.m_side_volume;
                    m_full_icon_rect.right = c_right_side_info.m_side_volume;
                    m_full_shadow_rect.top = c_top_side_info.m_shadow_volume;
                    m_full_shadow_rect.bottom = c_bottom_side_info.m_shadow_volume;
                    m_full_shadow_rect.left = c_left_side_info.m_shadow_volume;
                    m_full_shadow_rect.right = c_right_side_info.m_shadow_volume;
                    m_full_icon_rect.set(m_full_icon_rect.left, m_full_icon_rect.top, m_full_icon_rect.right, m_full_icon_rect.bottom);
                    m_full_shadow_rect.set(m_full_shadow_rect.left, m_full_shadow_rect.top, m_full_shadow_rect.right, m_full_shadow_rect.bottom);
                    m_icon_type = ICON_TYPE_CIRCLE;
                } else {
                    int c_full_point_count = 0;
                    int c_empty_point_count = 0;

                    for (int c_x = c_left_side_info.m_side_volume; c_x < c_right_side_info.m_side_volume; c_x++) {
                        for (int c_y = c_top_side_info.m_side_volume; c_y < c_bottom_side_info.m_side_volume; c_y++) {
                            int c_point_color = m_bmp.getPixel(c_x, c_y);
                            int c_point_alpha = (c_point_color & 0xff000000) >>> 24;
                            if (c_point_alpha > 200) {
                                c_full_point_count++;
                            } else {
                                c_empty_point_count++;
                            }
                        }
                    }
                    int c_all_point_count = c_full_point_count + c_empty_point_count;
                    float c_full_ratio = (float) c_full_point_count / (float) c_all_point_count;
                    m_full_icon_rect.top = c_top_side_info.m_side_volume;
                    m_full_icon_rect.bottom = c_bottom_side_info.m_side_volume;
                    m_full_icon_rect.left = c_left_side_info.m_side_volume;
                    m_full_icon_rect.right = c_right_side_info.m_side_volume;
                    m_full_shadow_rect.top = c_top_side_info.m_shadow_volume;
                    m_full_shadow_rect.bottom = c_bottom_side_info.m_shadow_volume;
                    m_full_shadow_rect.left = c_left_side_info.m_shadow_volume;
                    m_full_shadow_rect.right = c_right_side_info.m_shadow_volume;
                    m_full_icon_rect.set(m_full_icon_rect.left, m_full_icon_rect.top, m_full_icon_rect.right, m_full_icon_rect.bottom);
                    m_full_shadow_rect.set(m_full_shadow_rect.left, m_full_shadow_rect.top, m_full_shadow_rect.right, m_full_shadow_rect.bottom);
                    if (c_full_ratio > 0.4f) {
                        m_icon_type = ICON_TYPE_IRREGULAR_FULL;
                    } else {
                        m_icon_type = ICON_TYPE_IRREGULAR_NARROW;
                    }
                }
            }
        }

        public BitmapSideInfo countIconTopSideType(Bitmap c_bmp) {
            BitmapSideInfo c_result = new BitmapSideInfo();
            int c_left_alpha;
            int c_right_alpha;
            int c_shadow_test_alpha = 0;
            int c_shadow_test_y = 0;
            for (int c_y = 0; c_y < c_bmp.getHeight(); c_y++) {
                int c_left_x = 0;
                int c_right_x = c_bmp.getWidth() - 1;
                int c_point_left_color = c_bmp.getPixel(c_left_x, c_y);
                c_left_alpha = (c_point_left_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_left_alpha > 10) {
                    c_shadow_test_alpha = c_left_alpha;
                    c_shadow_test_y = c_y;
                }
                while (c_left_alpha < 250 && c_left_x < c_right_x) {
                    c_left_x++;
                    c_point_left_color = c_bmp.getPixel(c_left_x, c_y);
                    c_left_alpha = (c_point_left_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_left_alpha > 10) {
                        c_shadow_test_alpha = c_left_alpha;
                        c_shadow_test_y = c_y;
                    }
                }
                int c_point_right_color = c_bmp.getPixel(c_right_x, c_y);
                c_right_alpha = (c_point_right_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_right_alpha > 10) {
                    c_shadow_test_alpha = c_right_alpha;
                    c_shadow_test_y = c_y;
                }
                while (c_right_alpha < 250 && c_left_x < c_right_x) {
                    c_right_x--;
                    c_point_right_color = c_bmp.getPixel(c_right_x, c_y);
                    c_right_alpha = (c_point_right_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_right_alpha > 10) {
                        c_shadow_test_alpha = c_right_alpha;
                        c_shadow_test_y = c_y;
                    }
                }
                if (c_right_x - c_left_x > 0) {
                    c_result.m_side_min = c_left_x;
                    c_result.m_side_max = c_right_x;
                    c_result.m_side_volume = c_y;
                    c_result.m_shadow_volume = c_shadow_test_y;
                    c_result.m_side_irregular = false;
                    for (int c_test_x = c_left_x + 1; c_test_x < c_right_x; c_test_x++) {
                        int c_point_test_color = c_bmp.getPixel(c_test_x, c_y);
                        int c_alpha = (c_point_test_color & 0xff000000) >>> 24;
                        if (c_alpha < c_left_alpha || c_alpha < c_right_alpha) {
                            c_result.m_side_irregular = true;
                            break;
                        }
                    }
                    break;
                }
            }
            return c_result;
        }

        public BitmapSideInfo countIconBottomSideType(Bitmap c_bmp) {
            BitmapSideInfo c_result = new BitmapSideInfo();

            int c_left_alpha;
            int c_right_alpha;
            int c_shadow_test_alpha = 0;
            int c_shadow_test_y = 0;
            for (int c_y = c_bmp.getHeight() - 1; c_y > 0; c_y--) {
                int c_left_x = 0;
                int c_right_x = c_bmp.getWidth() - 1;
                int c_point_left_color = c_bmp.getPixel(c_left_x, c_y);
                c_left_alpha = (c_point_left_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_left_alpha > 10) {
                    c_shadow_test_alpha = c_left_alpha;
                    c_shadow_test_y = c_y;
                }
                while (c_left_alpha < 250 && c_left_x < c_right_x) {
                    c_left_x++;
                    c_point_left_color = c_bmp.getPixel(c_left_x, c_y);
                    c_left_alpha = (c_point_left_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_left_alpha > 10) {
                        c_shadow_test_alpha = c_left_alpha;
                        c_shadow_test_y = c_y;
                    }
                }
                int c_point_right_color = c_bmp.getPixel(c_right_x, c_y);
                c_right_alpha = (c_point_right_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_right_alpha > 10) {
                    c_shadow_test_alpha = c_right_alpha;
                    c_shadow_test_y = c_y;
                }
                while (c_right_alpha < 250 && c_left_x < c_right_x) {
                    c_right_x--;
                    c_point_right_color = c_bmp.getPixel(c_right_x, c_y);
                    c_right_alpha = (c_point_right_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_right_alpha > 10) {
                        c_shadow_test_alpha = c_right_alpha;
                        c_shadow_test_y = c_y;
                    }
                }
                if (c_right_x - c_left_x > 0) {
                    c_result.m_side_min = c_left_x;
                    c_result.m_side_max = c_right_x;
                    c_result.m_side_volume = c_y;
                    c_result.m_shadow_volume = c_shadow_test_y;
                    c_result.m_side_irregular = false;
                    for (int c_test_x = c_left_x + 1; c_test_x < c_right_x; c_test_x++) {
                        int c_point_test_color = c_bmp.getPixel(c_test_x, c_y);
                        int c_alpha = (c_point_test_color & 0xff000000) >>> 24;
                        if (c_alpha < c_left_alpha || c_alpha < c_right_alpha) {
                            c_result.m_side_irregular = true;
                            break;
                        }
                    }
                    break;
                }
            }
            return c_result;
        }

        public BitmapSideInfo countIconLeftSideType(Bitmap c_bmp) {
            BitmapSideInfo c_result = new BitmapSideInfo();

            int c_top_alpha;
            int c_bottom_alpha;
            int c_shadow_test_alpha = 0;
            int c_shadow_test_x = 0;
            for (int c_x = 0; c_x < c_bmp.getWidth(); c_x++) {
                int c_top_y = 0;
                int c_bottom_y = c_bmp.getHeight() - 1;
                int c_point_top_color = c_bmp.getPixel(c_x, c_top_y);
                c_top_alpha = (c_point_top_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_top_alpha > 10) {
                    c_shadow_test_alpha = c_top_alpha;
                    c_shadow_test_x = c_x;
                }
                while (c_top_alpha < 250 && c_top_y < c_bottom_y) {
                    c_top_y++;
                    c_point_top_color = c_bmp.getPixel(c_x, c_top_y);
                    c_top_alpha = (c_point_top_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_top_alpha > 10) {
                        c_shadow_test_alpha = c_top_alpha;
                        c_shadow_test_x = c_x;
                    }
                }
                int c_point_bottom_color = c_bmp.getPixel(c_x, c_bottom_y);
                c_bottom_alpha = (c_point_bottom_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_bottom_alpha > 10) {
                    c_shadow_test_alpha = c_bottom_alpha;
                    c_shadow_test_x = c_x;
                }
                while (c_bottom_alpha < 250 && c_top_y < c_bottom_y) {
                    c_bottom_y--;
                    c_point_bottom_color = c_bmp.getPixel(c_x, c_bottom_y);
                    c_bottom_alpha = (c_point_bottom_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_bottom_alpha > 10) {
                        c_shadow_test_alpha = c_bottom_alpha;
                        c_shadow_test_x = c_x;
                    }
                }
                if (c_bottom_y - c_top_y > 0) {
                    c_result.m_side_min = c_top_y;
                    c_result.m_side_max = c_bottom_y;
                    c_result.m_side_volume = c_x;
                    c_result.m_shadow_volume = c_shadow_test_x;
                    c_result.m_side_irregular = false;
                    for (int c_test_y = c_top_y + 1; c_test_y < c_bottom_y; c_test_y++) {
                        int c_point_test_color = c_bmp.getPixel(c_x, c_test_y);
                        int c_alpha = (c_point_test_color & 0xff000000) >>> 24;
                        if (c_alpha < c_top_alpha || c_alpha < c_bottom_alpha) {
                            c_result.m_side_irregular = true;
                            break;
                        }
                    }
                    break;
                }
            }
            return c_result;
        }

        public BitmapSideInfo countIconRightSideType(Bitmap c_bmp) {
            BitmapSideInfo c_result = new BitmapSideInfo();
            int c_top_alpha;
            int c_bottom_alpha;
            int c_shadow_test_alpha = 0;
            int c_shadow_test_x = 0;
            for (int c_x = c_bmp.getWidth() - 1; c_x > 0; c_x--) {
                int c_top_y = 0;
                int c_bottom_y = c_bmp.getHeight() - 1;
                int c_point_top_color = c_bmp.getPixel(c_x, c_top_y);
                c_top_alpha = (c_point_top_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_top_alpha > 10) {
                    c_shadow_test_alpha = c_top_alpha;
                    c_shadow_test_x = c_x;
                }
                while (c_top_alpha < 250 && c_top_y < c_bottom_y) {
                    c_top_y++;
                    c_point_top_color = c_bmp.getPixel(c_x, c_top_y);
                    c_top_alpha = (c_point_top_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_top_alpha > 10) {
                        c_shadow_test_alpha = c_top_alpha;
                        c_shadow_test_x = c_x;
                    }
                }
                int c_point_bottom_color = c_bmp.getPixel(c_x, c_bottom_y);
                c_bottom_alpha = (c_point_bottom_color & 0xff000000) >>> 24;
                if (c_shadow_test_alpha == 0 && c_bottom_alpha > 10) {
                    c_shadow_test_alpha = c_bottom_alpha;
                    c_shadow_test_x = c_x;
                }
                while (c_bottom_alpha < 250 && c_top_y < c_bottom_y) {
                    c_bottom_y--;
                    c_point_bottom_color = c_bmp.getPixel(c_x, c_bottom_y);
                    c_bottom_alpha = (c_point_bottom_color & 0xff000000) >>> 24;
                    if (c_shadow_test_alpha == 0 && c_bottom_alpha > 10) {
                        c_shadow_test_alpha = c_bottom_alpha;
                        c_shadow_test_x = c_x;
                    }
                }
                if (c_bottom_y - c_top_y > 0) {
                    c_result.m_side_min = c_top_y;
                    c_result.m_side_max = c_bottom_y;
                    c_result.m_side_volume = c_x;
                    c_result.m_shadow_volume = c_shadow_test_x;
                    c_result.m_side_irregular = false;
                    for (int c_test_y = c_top_y + 1; c_test_y < c_bottom_y; c_test_y++) {
                        int c_point_test_color = c_bmp.getPixel(c_x, c_test_y);
                        int c_alpha = (c_point_test_color & 0xff000000) >>> 24;
                        if (c_alpha < c_top_alpha || c_alpha < c_bottom_alpha) {
                            c_result.m_side_irregular = true;
                            break;
                        }
                    }
                    break;
                }
            }
            return c_result;
        }
    }

    private static Bitmap buildIconBitmap(Context c_context, MyBitmap c_src_bmp, int c_width, int c_height) {
        c_src_bmp.countIconType();
        Log.d("IconTest", "buildIconBitmap: m_full_icon_rect = " + c_src_bmp.m_full_icon_rect.toString());

        int c_real_bmp_width = c_width + 24;
        int c_real_bmp_height = c_height + 24;
        Bitmap c_draw_bitmap = Bitmap.createBitmap(c_real_bmp_width, c_real_bmp_height, Bitmap.Config.ARGB_8888);
        float c_width_scale = (float) c_src_bmp.m_full_icon_rect.width() / (float) c_width;
        float c_height_scale = (float) c_src_bmp.m_full_icon_rect.height() / (float) c_height;
        float c_all_scale = c_width_scale > c_height_scale ? c_width_scale : c_height_scale;
        //c_draw_bitmap.eraseColor(0x32ff0000);
        Canvas c_canvas = new Canvas(c_draw_bitmap);
        c_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Matrix c_matrix = new Matrix();
        c_matrix.postTranslate(-c_src_bmp.m_full_icon_rect.centerX(), -c_src_bmp.m_full_icon_rect.centerY());
        c_matrix.postScale(1f / c_all_scale, 1f / c_all_scale);
        c_matrix.postTranslate(c_real_bmp_width / 2, c_real_bmp_height / 2);
        Paint c_paint = new Paint();
        c_paint.setAlpha(255);
        c_canvas.drawBitmap(c_src_bmp.m_bmp, c_matrix, c_paint);
        if ((c_src_bmp.m_full_shadow_rect.width() - c_src_bmp.m_full_icon_rect.width()) <= 8 * c_all_scale && (c_src_bmp.m_full_shadow_rect.height() - c_src_bmp.m_full_icon_rect.height()) <= 8 * c_all_scale) {
            Bitmap c_bmp_shadow = ShadowBuilder.buildIconBitmapShadow(c_context, c_draw_bitmap, 8);
            c_paint.setAlpha(188);
            c_draw_bitmap.eraseColor(0x00000000);
            c_canvas.drawBitmap(c_bmp_shadow, 2, 2, c_paint);
            c_paint.setAlpha(255);
            c_canvas.drawBitmap(c_src_bmp.m_bmp, c_matrix, c_paint);
        }
        c_src_bmp.m_bmp.recycle();
        c_src_bmp.m_bmp = c_draw_bitmap;
        return c_src_bmp.m_bmp;
    }

    private static Bitmap createMyIconBitmap(Drawable icon, Context context) {
        MyBitmap c_my_icon = drawableToBitmap(icon);
        Bitmap c_result = buildIconBitmap(context, c_my_icon, FullscreenActivity.m_icon_width, FullscreenActivity.m_icon_width);
        return c_result;
    }

    private static Bitmap createIconBitmap(Drawable icon, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }

            int width = sIconWidth;
            int height = sIconHeight;

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }


            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();
            if (sourceWidth > 0 && sourceHeight > 0) {
                // There are intrinsic sizes.
                if (width < sourceWidth || height < sourceHeight) {
                    // It's too big, scale it down.
                    final float ratio = (float) sourceWidth / sourceHeight;
                    if (sourceWidth > sourceHeight) {
                        height = (int) (width / ratio);
                    } else if (sourceHeight > sourceWidth) {
                        width = (int) (height * ratio);
                    }
                } else if (sourceWidth < width && sourceHeight < height) {
                    // Don't scale up the icon
                    width = sourceWidth;
                    height = sourceHeight;
                }
            }

            // no intrinsic size --> use default size
            int textureWidth = sIconTextureWidth;
            int textureHeight = sIconTextureHeight;

            final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);

            final int left = (textureWidth - width) / 2;
            final int top = (textureHeight - height) / 2;

            @SuppressWarnings("all") // suppress dead code warning
            final boolean debug = true;
            if (debug) {
                // draw a big box for the icon for debugging
                canvas.drawColor(sColors[sColorIndex]);
                if (++sColorIndex >= sColors.length) sColorIndex = 0;
                Paint debugPaint = new Paint();
                debugPaint.setColor(0xffcccc00);
                canvas.drawRect(left, top, left + width, top + height, debugPaint);
            }

            sOldBounds.set(icon.getBounds());
            icon.setBounds(left, top, left + width, top + height);
            icon.draw(canvas);
            icon.setBounds(sOldBounds);
            canvas.setBitmap(null);

            return bitmap;
        }
    }

    static void drawSelectedAllAppsBitmap(Canvas dest, int destWidth, int destHeight,
                                          boolean pressed, Bitmap src) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                // We can't have gotten to here without src being initialized, which
                // comes from this file already.  So just assert.
                //initStatics(context);
                throw new RuntimeException("Assertion failed: Utilities not initialized");
            }

            dest.drawColor(0, PorterDuff.Mode.CLEAR);

            int[] xy = new int[2];
            Bitmap mask = src.extractAlpha(sBlurPaint, xy);

            float px = (destWidth - src.getWidth()) / 2;
            float py = (destHeight - src.getHeight()) / 2;
            dest.drawBitmap(mask, px + xy[0], py + xy[1],
                    pressed ? sGlowColorPressedPaint : sGlowColorFocusedPaint);

            mask.recycle();
        }
    }

    /**
     * Returns a Bitmap representing the thumbnail of the specified Bitmap.
     * The size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     *
     * @param bitmap  The bitmap to get a thumbnail of.
     * @param context The application's context.
     * @return A thumbnail for the specified bitmap or the bitmap itself if the
     * thumbnail could not be created.
     */
    /*static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }

            if (bitmap.getWidth() == sIconWidth && bitmap.getHeight() == sIconHeight) {
                return bitmap;
            } else {
                final Resources resources = context.getResources();
                return createIconBitmap(new BitmapDrawable(resources, bitmap), context);
            }
        }
    }*/
    static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }
            final Bitmap disabled = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(disabled);

            canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);

            canvas.setBitmap(null);

            return disabled;
        }
    }

    private static void initStatics(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        final float density = metrics.density;

        sIconWidth = sIconHeight = 120;
        sIconTextureWidth = sIconTextureHeight = sIconWidth;

        sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density, BlurMaskFilter.Blur.NORMAL));
        sGlowColorPressedPaint.setColor(0xffffc300);
        sGlowColorFocusedPaint.setColor(0xffff8e00);

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.2f);
        sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        sDisabledPaint.setAlpha(0x88);
    }

    /**
     * Only works for positive numbers.
     */
    static int roundToPow2(int n) {
        int orig = n;
        n >>= 1;
        int mask = 0x8000000;
        while (mask != 0 && (n & mask) == 0) {
            mask >>= 1;
        }
        while (mask != 0) {
            n |= mask;
            mask >>= 1;
        }
        n += 1;
        if (n != orig) {
            n <<= 1;
        }
        return n;
    }

    static int generateRandomId() {
        return new Random(System.currentTimeMillis()).nextInt(1 << 24);
    }

    /*private static String getAppName(Context context , int c_app_id)
    {
        String c_result = null;
        switch (c_app_id)
        {
            case SYSTEM_APP_ID_NAVI:
                c_result = context.getString(R.string.app_name_navigation);
                break;
            case SYSTEM_APP_ID_MUSIC:
                c_result = context.getString(R.string.app_name_music);
                break;
            case SYSTEM_APP_ID_RADIO:
                c_result = context.getString(R.string.app_name_radio);
                break;
            case SYSTEM_APP_ID_EQ:
                c_result = context.getString(R.string.app_name_eq);
                break;
            case SYSTEM_APP_ID_360:
                c_result = context.getString(R.string.app_name_360);
                break;
            case SYSTEM_APP_ID_CAR_WECHAT:
                c_result = context.getString(R.string.app_name_car_wechat);
                break;
            case SYSTEM_APP_ID_VIDEO:
                c_result = context.getString(R.string.app_name_video);
                break;
            case SYSTEM_APP_ID_PHOTO:
                c_result = context.getString(R.string.app_name_photo);
                break;
            case SYSTEM_APP_ID_CARLIFE:
                c_result = context.getString(R.string.app_name_car_life);
                break;
            case SYSTEM_APP_ID_CARPLAY:
                c_result = context.getString(R.string.app_name_car_play);
                break;
            case SYSTEM_APP_ID_CAR_SETTING_AND_SCREEN_SAVER_SET:
                c_result = context.getString(R.string.app_name_car_setting);
                break;
            case SYSTEM_APP_ID_SETTING:
                c_result = context.getString(R.string.app_name_settings);
                break;
            case SYSTEM_APP_ID_AIQU:
                c_result = context.getString(R.string.app_name_ai_qu);
                break;
            case SYSTEM_APP_ID_QQ_MUSIC:
                c_result = context.getString(R.string.app_name_music);
                break;
            case SYSTEM_APP_ID_KAOLA:
                c_result = context.getString(R.string.app_name_kaola);
                break;
            case SYSTEM_APP_ID_XIMALAYA:
                c_result = context.getString(R.string.app_name_ximalaya);
                break;
            case SYSTEM_APP_ID_TENGXUN_VIDEO:
                c_result = context.getString(R.string.app_name_video);
                break;
            case SYSTEM_APP_ID_KUWO:
                c_result = context.getString(R.string.app_name_ku_wo);
                break;
        }
        return c_result;
    }*/

    private static boolean isWechatEnable(Context context) {
        return false;
    }

    private static boolean isCarSettingsEnable(Context context) {
        ContentResolver cv = context.getContentResolver();
        int c_car_settings_enable = android.provider.Settings.System.getInt(cv, "SETTING_CARINFO_SETTING_ENABLE", 0);
        return c_car_settings_enable == 1;
    }

    private static boolean isCarPlayEnable(Context context) {
        boolean c_result = true;
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo("com.spd.carplay.ui", PackageManager.GET_ACTIVITIES);
            //String c_app_name = getAppName(context, "com.spd.carplay.ui", "com.spd.carplay.ui.MainActivity");
            c_result = true;
        } catch (PackageManager.NameNotFoundException e) {
            c_result = false;
        }
        return c_result;
    }

    private static boolean getAvmShowFlag(int c_avm_hardware, int c_avm_type) {
        boolean c_result;
        if (c_avm_hardware == 0) {
            c_result = false;
        } else if (c_avm_hardware == 1) {
            c_result = true;
        } else if (c_avm_hardware == 4) {
            if (c_avm_type == 0)
                c_result = false;
            else
                c_result = true;
        } else {
            if (c_avm_type == 0)
                c_result = false;
            else
                c_result = true;
        }
        return c_result;
    }

    private static ShortCut getAppShortcut(Context context, int c_app_id) {
        ShortCut c_result = null;
        switch (c_app_id) {

            case SYSTEM_APP_ID_NAVI:
                String c_app_name = context.getString(R.string.app_name_navigation);
                ContentResolver cv = context.getContentResolver();
                String c_app_componet_name = android.provider.Settings.System.getString(cv, "SETTING_NAVI_APP");
                if (c_app_componet_name != null) {
                    c_app_componet_name = c_app_componet_name.trim();
                }
                AppInfo c_child_app = new AppInfo(context, c_app_id, c_app_componet_name, null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_navi);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_DVR:
                c_app_name = context.getString(R.string.app_name_dvr);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spddvr", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_360);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_AUX_IN:
                c_app_name = context.getString(R.string.app_name_aux_in);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.auxui", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_SCREEN_SET:
                c_app_name = context.getString(R.string.app_name_screen_saver_set);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.screen.saver", "com.spd.screen.saver.ScreenSaverSetActivity", c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CAR_SETTING:
                c_app_name = context.getString(R.string.app_name_car_setting);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.carinfo.ui", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_car_switch);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_GOOGLE_PLAY:
                c_app_name = context.getString(R.string.app_name_google_play);
                c_child_app = new AppInfo(context, c_app_id, "com.android.vending", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_YOUTUBE:
                c_app_name = context.getString(R.string.app_name_youtube);
                c_child_app = new AppInfo(context, c_app_id, "com.google.android.youtube", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CHROME:
                c_app_name = context.getString(R.string.app_name_chrome);
                c_child_app = new AppInfo(context, c_app_id, "com.android.chrome", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_ES_BROWNS:
                c_app_name = context.getString(R.string.app_name_es);
                c_child_app = new AppInfo(context, c_app_id, "com.estrongs.android.pop", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CAR_PLAY:
                c_app_name = context.getString(R.string.app_name_car_play);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.carplay.ui", "com.spd.carplay.ui.MainActivity", c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_ANDROID_AUTO:
                c_app_name = context.getString(R.string.app_name_android_auto);
                c_child_app = new AppInfo(context, c_app_id, "", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_MUSIC:
                c_app_name = context.getString(R.string.app_name_music);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdmedia", "com.spd.spdmedia.MusicActivity", c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_music);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_RADIO:
                c_app_name = context.getString(R.string.app_name_radio);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdradio", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_radio);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_EQ:
                c_app_name = context.getString(R.string.app_name_eq);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdeq", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_360:
//                int c_avm_hardware = android.provider.Settings.System.getInt(context.getContentResolver(), "HW_AVM_SURPPORT", 0);
//                int c_avm_type = android.provider.Settings.System.getInt(context.getContentResolver(), "SETTING_AVM_ENABLE", 0);
//                if (getAvmShowFlag(c_avm_hardware, c_avm_type)) {
//                    c_app_name = context.getString(R.string.app_name_360);
//                    if (c_avm_hardware == 0 || c_avm_hardware == 4 || c_avm_type == 2) {
//                        //c_child_app = new AppInfo(context,c_app_id , "com.spd.dvr","com.spd.activity.External360Activity" , c_app_name,R.drawable.app_icon_part_bg_n,R.drawable.app_icon_part_360);
//                        c_child_app = new AppInfo(context, c_app_id, "com.senptec.carvideoapp", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_360);
//                    } else {
//                        c_child_app = new AppInfo(context, c_app_id, get360packageName(context), null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_360);
//                    }
//                    c_result = new ShortCut(c_child_app);
//                }
                c_app_name = context.getString(R.string.app_name_360);
                c_child_app = new AppInfo(context, c_app_id, "com.senptec.carvideoapp", "com.senptec.carvideo.MainActivity", c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_360);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_PHONE:
                c_app_name = context.getString(R.string.app_name_phone);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.bt", "com.spd.bt.FullscreenActivity", c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_phone);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_WECHAT_AND_BROWNS:
                if (isWechatEnable(context)) {
                    c_app_name = context.getString(R.string.app_name_car_wechat);
                    c_child_app = new AppInfo(context, c_app_id, "com.spd.test", "", c_app_name, R.drawable.app_icon_part_bg_n, 0);
                    c_result = new ShortCut(c_child_app);
                } else {
                    c_app_name = context.getString(R.string.app_name_net_browser);
                    c_child_app = new AppInfo(context, c_app_id, "com.android.chrome", "com.google.android.apps.chrome.Main", c_app_name, R.drawable.app_icon_part_bg_n, 0);
                    c_result = new ShortCut(c_child_app);
                }
                break;
            case SYSTEM_APP_ID_VIDEO:
                c_app_name = context.getString(R.string.app_name_video);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdmedia", "com.spd.spdmedia.VideoActivity", c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_video);
                c_result = new ShortCut(c_child_app);

                break;
            case SYSTEM_APP_ID_PHOTO:
                c_app_name = context.getString(R.string.app_name_photo);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdmedia", "com.spd.spdmedia.PhotoActivity", c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CARLIFE:
                c_app_name = context.getString(R.string.app_name_car_life);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.carlife.ui", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CARPLAY_AND_AUX_IN:
                if (isCarPlayEnable(context)) {
                    c_app_name = context.getString(R.string.app_name_car_play);
                    c_child_app = new AppInfo(context, c_app_id, "com.spd.carplay.ui", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                    c_result = new ShortCut(c_child_app);
                } else {
                    c_app_name = context.getString(R.string.app_name_aux_in);
                    c_child_app = new AppInfo(context, c_app_id, "com.spd.auxui", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                    c_result = new ShortCut(c_child_app);
                }
                break;
            case SYSTEM_APP_ID_CAR_SETTING_AND_SCREEN_SAVER_SET:
                if (isCarSettingsEnable(context)) {
                    c_app_name = context.getString(R.string.app_name_car_setting);
                    c_child_app = new AppInfo(context, c_app_id, "com.spd.carinfo.ui", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                    c_result = new ShortCut(c_child_app);
                } else {
                    c_app_name = context.getString(R.string.app_name_screen_saver_set);
                    c_child_app = new AppInfo(context, c_app_id, "com.spd.screen.saver", "com.spd.screen.saver.ScreenSaverSetActivity", c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_screen_saver_set);
                    c_result = new ShortCut(c_child_app);
                }
                break;
            case SYSTEM_APP_ID_SETTING:
                c_app_name = context.getString(R.string.app_name_settings);
                c_child_app = new AppInfo(context, c_app_id, "com.spd.spdsettings", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_setting);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_AIQU:
                c_app_name = context.getString(R.string.app_name_ai_qu);
                c_child_app = new AppInfo(context, c_app_id, "com.tencent.wecarflow", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_QQ_MUSIC:
                c_app_name = context.getString(R.string.app_name_qq_music);
                c_child_app = new AppInfo(context, c_app_id, "com.tencent.qqmusiccar", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_KAOLA:
                c_app_name = context.getString(R.string.app_name_tingban);
                c_child_app = new AppInfo(context, c_app_id, "com.edog.car", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_XIMALAYA:
                c_app_name = context.getString(R.string.app_name_ximalaya);
                c_child_app = new AppInfo(context, c_app_id, "com.ximalaya.ting.android.car", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_TENGXUN_VIDEO:
                c_app_name = context.getString(R.string.app_name_tencent_video);
                c_child_app = new AppInfo(context, c_app_id, "com.ktcp.video", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_KUWO:
                c_app_name = context.getString(R.string.app_name_ku_wo);
                c_child_app = new AppInfo(context, c_app_id, "cn.kuwo.kwmusiccar", null, c_app_name, R.drawable.app_icon_part_bg_n, 0);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CAR_SWITCH:
                c_app_name = context.getString(R.string.app_name_CAR_SWITCH);
                c_child_app = new AppInfo(context, c_app_id, "com.example.carswitcherapp", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_car_switch);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_CAR_INFO:
                c_app_name = context.getString(R.string.app_name_CAR_INFO);
                c_child_app = new AppInfo(context, c_app_id, "com.example.carinfoapp", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_car_info);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_AIR_CONDITIONER:
                c_app_name = context.getString(R.string.app_name_air_conditioner);
                c_child_app = new AppInfo(context, c_app_id, "com.example.caracapp", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_air_conditioner);
                c_result = new ShortCut(c_child_app);
                break;
            case SYSTEM_APP_ID_APPS: // 应用列表
                c_app_name = context.getString(R.string.app_name_apps);
                c_child_app = new AppInfo(context, c_app_id, "", null, c_app_name, R.drawable.app_icon_part_bg_n, R.drawable.app_icon_part_apps);
                c_result = new ShortCut(c_child_app);
                break;
        }
        return c_result;
    }

    private static List<ShortCut> getDefaultAppList(Context context) {
        List<ShortCut> c_result_short_cut = new ArrayList<>();
        addList(context, c_result_short_cut, SYSTEM_APP_ID_NAVI);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_MUSIC);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_RADIO);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_VIDEO);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_360);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_CAR_SWITCH);
        //addList(context, c_result_short_cut, SYSTEM_APP_ID_PHONE);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_CAR_INFO);
        //addList(context, c_result_short_cut, SYSTEM_APP_ID_AIR_CONDITIONER);
        addList(context, c_result_short_cut, SYSTEM_APP_ID_APPS);
        return c_result_short_cut;
    }

    private static void addList(Context context, List<ShortCut> c_result_short_cut, int c_system_app_id) {
        ShortCut c_short_cut = getAppShortcut(context, c_system_app_id);
        if (c_short_cut != null) {
            c_short_cut.setShortCutPostion(c_result_short_cut.size());
            c_result_short_cut.add(c_short_cut);
        }
    }

    public static List<ShortCut> getDesktopAppList(Context context) {
        List<ShortCut> c_result = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(DESK_TOP_SHORT_CUT, Context.MODE_PRIVATE);
        String c_count_str = sp.getString(DESK_TOP_SHORT_CUT_COUNT, null);
        Log.d(TAG, "getDesktopAppList: c_count_str = " + c_count_str);
        if (c_count_str == null) {
            c_result = getDefaultAppList(context);
        } else {
            int c_count = Integer.parseInt(c_count_str);
            int c_fix_index = 0;
            for (int i = 0; i < c_count; i++) {
                String c_app_id_str = sp.getString(DESK_TOP_SHORT_CUT_ID_ + i, null);
                ShortCut c_app_shortcut = null;
                int c_system_app_id = Integer.parseInt(c_app_id_str);
                Log.d(TAG, "getDesktopAppList: i = " + i + " c_system_app_id = " + c_system_app_id);
                if (c_system_app_id != 0) {
                    c_app_shortcut = getAppShortcut(context, c_system_app_id);
                    if (c_app_shortcut != null) {
                        c_app_shortcut.setShortCutPostion(i + c_fix_index);
                        c_result.add(c_app_shortcut);
                    }
                } else {
                    String c_app_pack_name = sp.getString(DESK_TOP_SHORT_CUT_PKG_ + i, null);
                    String c_app_class_name = sp.getString(DESK_TOP_SHORT_CUT_CLA_ + i, null);
                    try {
                        PackageManager packageManager = context.getPackageManager();
                        packageManager.getPackageInfo(c_app_pack_name, PackageManager.GET_ACTIVITIES);
                        String c_app_name = getAppName(context, c_app_pack_name, c_app_class_name);
                        AppInfo c_child_app = new AppInfo(context, c_system_app_id, c_app_pack_name, c_app_class_name, c_app_name, 0, 0);
                        c_app_shortcut = new ShortCut(c_child_app);
                        c_app_shortcut.setShortCutPostion(i + c_fix_index);
                        c_result.add(c_app_shortcut);
                    } catch (Exception e) {
                        c_fix_index--;
                    }
                }
            }
        }
        Log.d(TAG, "getDesktopAppList: c_result=" + c_result.size());
        return c_result;
    }

    public static synchronized String getAppName(Context context, String c_package_name, String c_class_name) {
        String c_result = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            ActivityInfo appInfo = packageManager.getActivityInfo(new ComponentName(c_package_name, c_class_name), PackageManager.MATCH_ALL);
            c_result = appInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    /*public static synchronized String getAppName(Context context,LauncherActivityInfo appInfo) {
        String applicationName = null;
        try {
            String packageName =appInfo.getComponentName().getPackageName();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            ResolveInfo c;
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            //filter system app
            if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 ||
                    (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
            }
            if (TextUtils.isEmpty(applicationName))
            {
                applicationName = appInfo.getLabel().toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            applicationName = appInfo.getName();
        }

        return applicationName;
    }*/
    public static void clearDestopAppList(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DESK_TOP_SHORT_CUT, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }

    public static void saveDesktopAppList(Context context, List<ShortCut> c_short_cut_list) {
        Log.d(TAG, "saveDesktopAppList: c_short_cut_list=" + c_short_cut_list.size());
        SharedPreferences sp = context.getSharedPreferences(DESK_TOP_SHORT_CUT, Context.MODE_PRIVATE);
        String c_count_str = sp.getString(DESK_TOP_SHORT_CUT_COUNT, null);
        SharedPreferences.Editor edit = sp.edit();
        if (c_count_str != null) {
            int c_count = Integer.parseInt(c_count_str);
            if (c_count > c_short_cut_list.size()) {
                for (int i = c_short_cut_list.size(); i < c_count; i++) {
                    edit.remove(DESK_TOP_SHORT_CUT_ID_ + i);
                    edit.remove(DESK_TOP_SHORT_CUT_PKG_ + i);
                    edit.remove(DESK_TOP_SHORT_CUT_CLA_ + i);
                    edit.remove(DESK_TOP_SHORT_CUT_APP_ + i);
                }
            }
        }
        edit.putString(DESK_TOP_SHORT_CUT_COUNT, c_short_cut_list.size() + "");
        for (int i = 0; i < c_short_cut_list.size(); i++) {
            ShortCut c_short_cut_info = c_short_cut_list.get(i);
            AppInfo c_app_info = c_short_cut_info.getAppInfo();
            edit.putString(DESK_TOP_SHORT_CUT_ID_ + i, c_app_info.m_app_id + "");
            edit.putString(DESK_TOP_SHORT_CUT_PKG_ + i, c_app_info.componentName);
            edit.putString(DESK_TOP_SHORT_CUT_CLA_ + i, c_app_info.className);
            edit.putString(DESK_TOP_SHORT_CUT_APP_ + i, c_app_info.mAppName);
        }
        edit.commit();
    }

    private static final String DESK_TOP_SHORT_CUT = "desk_short_cut";
    private static final String DESK_TOP_SHORT_CUT_COUNT = "desk_short_cut_count";
    private static final String DESK_TOP_SHORT_CUT_ID_ = "desk_short_cut_id_";
    private static final String DESK_TOP_SHORT_CUT_PKG_ = "desk_short_cut_package_";
    private static final String DESK_TOP_SHORT_CUT_CLA_ = "desk_short_cut_class_";
    private static final String DESK_TOP_SHORT_CUT_APP_ = "desk_short_cut_app_";

    private static String m_settings_save = "settings.save";
    private static String m_settings_factory_mode = "factory_mode";
    public static String m_settings_develop_mode = "SETTING_DEVELOPER_MODE";
    private static String m_clear_secret = "clear_secret";
    private static String[] m_system_app_list = new String[]{
            "com.android.calendar/com.android.calendar.AllInOneActivity",
            "com.android.contacts/com.android.contacts.activities.PeopleActivity",
            "com.android.deskclock/com.android.deskclock.DeskClock",
            "com.android.email/com.android.email.activity.Welcome",
            "com.android.mms/com.android.mms.ui.ConversationList",
            "com.android.music/com.android.music.MusicBrowserActivity",
            "com.android.music/com.android.music.VideoBrowserActivity",
            "com.android.soundrecorder/com.android.soundrecorder.SoundRecorder",
            "org.codeaurora.gallery/com.android.gallery3d.app.GalleryActivity",
            "org.codeaurora.snapcam/com.android.camera.CameraLauncher",
            "com.android.calculator2/com.android.calculator2.Calculator",
            "com.android.quicksearchbox/com.android.quicksearchbox.SearchActivity",
            "com.caf.fmradio/com.caf.fmradio.FMRadio",
            "com.example.connmgr/com.example.connmgr.MainActivity",
            "com.qualcomm.qct.dlt/com.qualcomm.qct.dlt.MainActivity",
            "com.qualcomm.qti.carrierswitch/com.qualcomm.qti.carrierswitch.MainActivity",
            "com.qualcomm.qti.presenceapp/com.qualcomm.qti.presenceapp.MainActivity",
            "com.qualcomm.qti.presenceappSub2/com.qualcomm.qti.presenceappSub2.MainActivity",
            "com.qualcomm.qti.sensors.qsensortest/com.qualcomm.qti.sensors.ui.qsensortest.TabControl",
            "com.qualcomm.qti.server.wigigapp/com.qualcomm.qti.server.wigigapp.WigigSettingsActivity",
            "com.qualcomm.qti.sva/com.qualcomm.qti.sva.MainActivity",
            "com.quicinc.cne.settings/com.quicinc.cne.settings.CneSettings",
            "com.spd.radio/com.spd.radio.TestActivity",
            "com.spd.settingui/com.spd.settingui.SettingActivity",
            "com.spd.tasAdapter/com.tencent.app.MainActivity",
            "com.spd.tools/com.spd.tools.MainActivity",
            "org.chromium.webview_shell/org.chromium.webview_shell.WebViewBrowserActivity",
            "org.codeaurora.bluetooth.batestapp/org.codeaurora.bluetooth.batestapp.BroadcastAudioAppActivity",
            "org.codeaurora.bluetooth.bttestapp/org.codeaurora.bluetooth.bttestapp.MainActivity",
            "org.codeaurora.bluetooth.hidtestapp/org.codeaurora.bluetooth.hidtestapp.HidTestApp",
            "org.codeaurora.qti.nrNetworkSettingApp/org.codeaurora.qti.nrNetworkSettingApp.MainActivity",
            //"com.spd.spdsettings/com.spd.spdsettings.FullscreenActivity",
            "com.spd.screen.saver/com.spd.screen.saver.FullscreenActivity",
            "com.spd.home/com.spd.home.FullscreenActivity",
            "com.spd.dvr/com.spd.activity.BackingCameraActivity",
            "com.qualcomm.qti.modemtestmode/com.qualcomm.qti.modemtestmode.MbnFileActivate",
            "com.android.inputmethod.latin/com.android.inputmethod.latin.setup.SetupActivity",
            "com.google.android.inputmethod.pinyin/com.google.android.apps.inputmethod.libs.framework.core.LauncherActivity"
    };

    public interface LoadInterrupt {
        boolean isThreadAlive();
    }

    // 需要过滤的包名列表
    private static HashSet<String> notShowPackages = new HashSet<>();

    static {
        notShowPackages.add("com.spd.carinfo.ui");// 原车信息
        notShowPackages.add("com.mapgoo.diruite");// 记路者
        notShowPackages.add("com.spd.bt");// 蓝牙电话
        notShowPackages.add("com.google.android.googlequicksearchbox");// Google
        notShowPackages.add("com.example.carswitcherapp");//车辆开关
        notShowPackages.add("com.example.carinfoapp");//车辆信息
        notShowPackages.add("com.senptec.carvideoapp");//车辆监控
        notShowPackages.add("com.spd.avm");//全景环视
        notShowPackages.add("com.spd.dvr");//全景环视
        notShowPackages.add("com.spd.auxui");//AUX IN
        notShowPackages.add("com.spd.start_log");//日志上传
    }

    private static HashMap<String, SoftReference<Bitmap>> m_icon_buffer = new HashMap<>();

    public static List<AppInfo> getUserAppList(Context context, LoadInterrupt c_load_interrupt) {
        ContentResolver cv = context.getContentResolver();
        int c_develop_mode_value = android.provider.Settings.System.getInt(cv, m_settings_develop_mode, 0);
        Log.d("Test009", "getUserAppList: c_develop_mode_value=" + c_develop_mode_value);
        boolean c_develop_mode = c_develop_mode_value == 1;
        int c_avm_hardware = android.provider.Settings.System.getInt(context.getContentResolver(), "HW_AVM_SURPPORT", 0);
        int c_avm_type = android.provider.Settings.System.getInt(context.getContentResolver(), "SETTING_AVM_ENABLE", 0);

        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = manager.queryIntentActivities(intent, 0);
        List<AppInfo> c_result = new ArrayList<>();
        for (ResolveInfo c_info : appList) {
            String c_package_name = c_info.activityInfo.packageName;
            if (notShowPackages.contains(c_package_name)) {
                continue;
            }
            String c_class_name = c_info.activityInfo.name;
            String c_app_name = c_info.activityInfo.loadLabel(manager).toString();
            if (c_app_name.equals("车载KTV") || c_package_name.equals("com.aske.carktv.xz"))
                c_app_name = context.getString(R.string.app_name_ktv);
            ;
            Log.d("Test009", "getUserAppList1: =========" + c_app_name + "=========");

            String c_test_name = c_package_name + "/" + c_class_name;
            boolean c_add_flag = true;
            if (c_develop_mode) {
                c_add_flag = true;
            } else {
                if (!getAvmShowFlag(c_avm_hardware, c_avm_type)) {
                    if (c_package_name.equals(get360packageName(context))) {
                        c_add_flag = false;
                    }
                }
                for (String c_system_app : m_system_app_list) {
                    if (c_system_app.equals(c_test_name)) {
                        c_add_flag = false;
                        break;
                    }
                }
            }
            Log.d("Test009", "getUserAppList1: " + c_package_name + "/" + c_class_name + " == " + c_add_flag);
            if (c_add_flag) {
                String c_icon_key = c_package_name + "/" + c_class_name;
                Bitmap c_icon_bitmap = null;
                if (m_icon_buffer.containsKey(c_icon_key)) {
                    SoftReference<Bitmap> c_child_obj = m_icon_buffer.get(c_icon_key);
                    if (c_child_obj != null && c_child_obj.get() != null && !c_child_obj.get().isRecycled()) {
                        c_icon_bitmap = c_child_obj.get();
                    }
                }
                if (c_icon_bitmap == null) {
                    Drawable c_icon_drawable;
                    try {
                        Resources r = context.getPackageManager().getResourcesForApplication(
                                c_info.activityInfo.applicationInfo);
                        int iconId = c_info.getIconResource();
                        try {
                            int density = (int) (context.getResources().getDisplayMetrics().density * 160 * 2);
                            c_icon_drawable = r.getDrawableForDensity(iconId, density);
                        } catch (Resources.NotFoundException e) {
                            c_icon_drawable = r.getDrawable(iconId);
                        }
                    } catch (Exception e) {
                        c_icon_drawable = c_info.activityInfo.loadIcon(manager);

                    }
                    c_icon_bitmap = createMyIconBitmap(c_icon_drawable, context);
                    m_icon_buffer.put(c_icon_key, new SoftReference<Bitmap>(c_icon_bitmap));
                }
                AppInfo c_child_app = new AppInfo(context, c_package_name, c_class_name, c_app_name, c_icon_bitmap);
                c_result.add(c_child_app);
            }
            if (!c_load_interrupt.isThreadAlive()) {
                c_result = null;
                break;
            }
        }
        return c_result;
    }


    /*public static List<AppInfo> getUserAppList(Context context)
    {
        List<AppInfo> c_result = new ArrayList<>();
        LauncherApps mLauncherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        UserManager mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        List<UserHandle> users = mUserManager.getUserProfiles();
        List<UserHandle> profiles= users == null ? Collections.<UserHandle>emptyList() : users;
        Log.d("MyLuncher","users size = "+users.size());
        Log.d("MyLuncher","profiles size = "+profiles.size());
        for (UserHandle user : profiles) {
            final List<LauncherActivityInfo> apps = mLauncherApps.getActivityList(null, user);

            if (apps == null || apps.isEmpty()) {
                Log.d("MyLuncher","users app = Empty");
                continue;
            }
            else
            {
                Log.d("MyLuncher","users app = "+apps.size());
            }

            for (int i = 0; i < apps.size(); i++) {
                LauncherActivityInfo app = apps.get(i);
                AppInfo c_child_app = new AppInfo(context,app);
                c_result.add(c_child_app);
            }
        }
        return c_result;
    }*/

    /*public static synchronized Intent getAppIntent(Context context, LauncherActivityInfo appInfo)
    {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
        Intent c_intent = new Intent();
        c_intent.setFlags(launchFlags);
        c_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        c_intent.setComponent(appInfo.getComponentName());
        return c_intent;
    }*/

    /*public static synchronized Bitmap getAppIcon(Context context,LauncherActivityInfo appInfo) {
        Bitmap c_result = null;
        try {
            String packageName =appInfo.getComponentName().getPackageName();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo  = packageManager.getApplicationInfo(packageName, 0);
            Drawable icon = packageManager.getApplicationIcon(applicationInfo);
            c_result = Utilities.createIconBitmap(icon,context);
        } catch (PackageManager.NameNotFoundException e) {

        }
        return c_result;
    }*/
/*    public static synchronized Bitmap getAppIcon1(Context context,LauncherActivityInfo appInfo) {
        Drawable icon = appInfo.getIcon(360);
        Log.d("IconTest", "getAppIcon1: buildIcon =0="+appInfo.getName());
        Bitmap bitmap = createMyIconBitmap(icon,context);
        //bitmap = buildIconBitmap(bitmap,120,120);
        //bitmap = buildIconBitmap1(bitmap);
        return bitmap;
    }*/
    public static synchronized Bitmap getAppIcon(Context context, String c_package_name, String c_class_name) {
        Bitmap c_result = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            ActivityInfo appInfo = packageManager.getActivityInfo(new ComponentName(c_package_name, c_class_name), PackageManager.MATCH_ALL);
            int density = 360;
            final int iconRes = appInfo.getIconResource();
            Drawable icon = null;
            // Get the preferred density icon from the app's resources
            if (density != 0 && iconRes != 0) {
                try {
                    final Resources resources
                            = packageManager.getResourcesForApplication(appInfo.applicationInfo);
                    icon = resources.getDrawableForDensity(iconRes, density);
                } catch (PackageManager.NameNotFoundException | Resources.NotFoundException exc) {
                }
            }
            if (icon == null) {
                icon = appInfo.loadIcon(packageManager);
            }
            Log.d("IconTest", "getAppIcon1: buildIcon =1=" + appInfo.name);
            c_result = createMyIconBitmap(icon, context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //c_result = buildIconBitmap(c_result,120,120);
        //c_result = buildIconBitmap1(c_result);
        return c_result;
    }
    /*private static Bitmap buildIconBitmap1(Bitmap c_src_bmp)
    {
        Bitmap c_draw_bitmap = Bitmap.createBitmap(c_src_bmp.getWidth(),c_src_bmp.getHeight(), Bitmap.Config.ARGB_8888);
        c_draw_bitmap.eraseColor(0x32ff0000);
        Canvas c_canvas = new Canvas(c_draw_bitmap);
        c_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        Matrix c_matrix = new Matrix();
        c_canvas.drawBitmap(c_src_bmp,c_matrix,null);
        c_src_bmp.recycle();
        c_src_bmp = c_draw_bitmap;
        return c_src_bmp;
    }*/


    public static String get360packageName(Context context) {
        String name = "com.spd.dvr";
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo("com.spd.avm", PackageManager.GET_ACTIVITIES);
            Log.i("360", pi.activities[0].toString());
            if (pi != null) {
                name = "com.spd.avm";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("360", "NameNotFoundException: com.spd.avm");
            pi = null;
        }

        if (pi == null) {
            try {
                pi = pm.getPackageInfo("com.atelectronic.atavm3d", PackageManager.GET_ACTIVITIES);
                Log.i("360", pi.activities[0].toString());
                if (pi != null) {
                    name = "com.atelectronic.atavm3d";
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("360", "NameNotFoundException: com.atelectronic.atavm3d");
                pi = null;
            }
        }
        return name;
    }
}
