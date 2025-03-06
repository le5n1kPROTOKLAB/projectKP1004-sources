package com.spd.WallpaperChooser;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Display;

public class Util {
    public static DisplayMetrics setWallpaperManagerFitScreen(Activity context) {
        // 使桌面适应屏幕尺寸
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        final int screenWidth  = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        wallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight);

        // 获取壁纸硬设尺寸
        DisplayMetrics ret = new DisplayMetrics();
        ret.widthPixels = wallpaperManager.getDesiredMinimumWidth();
        ret.heightPixels = wallpaperManager.getDesiredMinimumHeight();
        return ret;
    }
    public static Bitmap centerCrop(Bitmap bitmap, DisplayMetrics screenMetrics) {
        Bitmap containScreen = scaleBitmapToContainScreen(bitmap, screenMetrics);
        return cropCenter(containScreen, screenMetrics);
    }

    /**
     * 将bitmap放大到包含屏幕尺寸的大小。
     * @param bitmap 要放大的图片
     * @param screenMetrics 屏幕的尺寸
     * @return 放大后的图片
     */
    private static Bitmap scaleBitmapToContainScreen(Bitmap bitmap, DisplayMetrics screenMetrics) {
        int height = screenMetrics.heightPixels;
        int width = screenMetrics.widthPixels;

        double wallpaperScale = (double)bitmap.getHeight() / (double)bitmap.getWidth();
        double screenScale = (double) height / (double) width;
        int targetWidth;
        int targetHeight;
        if (wallpaperScale < screenScale) {
            targetHeight = height;
            targetWidth = (int)(targetHeight / wallpaperScale);
        } else {
            targetWidth = width;
            targetHeight = (int)(targetWidth * wallpaperScale);
        }
        return  Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    /**
     * 对刚好包含屏幕的图片进行中心裁剪。
     * @param bitmap 宽或高刚好包含屏幕的图片
     * @param screenMetrics 屏幕的尺寸
     * @return 若高的部分多余，裁剪掉上下两边多余部分并返回。
     * 若宽的部分多余，裁减掉左右两边多于部分并返回。
     */
    private static Bitmap cropCenter(Bitmap bitmap, DisplayMetrics screenMetrics) {
        int h1 = bitmap.getHeight();
        int w1 = bitmap.getWidth();
        int h2 = screenMetrics.heightPixels;
        int w2 = screenMetrics.widthPixels;

        if (w1 > w2){
            return Bitmap.createBitmap(bitmap, (w1 - w2) / 2, 0, w2, h2);
        }else{
            return Bitmap.createBitmap(bitmap, 0, (h1 - h2) / 2, w2, h2);
        }
    }
}
