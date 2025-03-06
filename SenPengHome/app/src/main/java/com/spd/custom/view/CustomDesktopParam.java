package com.spd.custom.view;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.spd.home.FullscreenActivity;

/**
 * Date:    2023/3/29
 * Info: 桌面参数
 */
public class CustomDesktopParam {
    public static int DESKTOP_PAGE_COLUMN = 1;
    public static int DESKTOP_PAGE_ROW = 1;

    public static float DESKTOP_MARGIN_LEFT = 0;
    public static float DESKTOP_MARGIN_RIGHT = 0;
    public static float DESKTOP_MARGIN_TOP = 80;
    public static float DESKTOP_MARGIN_BOTTOM = 120;

    public static float DESKTOP_WIDTH = 1280;
    public static float DESKTOP_HEIGHT = 720;

    public static float DESKTOP_ITEM_WIDTH = 320;
    public static float DESKTOP_ITEM_HEIGHT = 212;

    public static float DESKTOP_ITEM_WIDTH_SPACE = 32;
    public static float DESKTOP_ITEM_HEIGHT_SPACE = 21;

    public static float DESKTOP_PAGE_GUIDE_BOTTOM = DESKTOP_HEIGHT;

    public static void init(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        DESKTOP_WIDTH = metrics.widthPixels;
        DESKTOP_HEIGHT = metrics.heightPixels;
        if (DESKTOP_HEIGHT > DESKTOP_WIDTH) {
            DESKTOP_ITEM_WIDTH_SPACE = 21;
            DESKTOP_ITEM_HEIGHT_SPACE = 32;
        } else {
            DESKTOP_ITEM_WIDTH_SPACE = 32;
            DESKTOP_ITEM_HEIGHT_SPACE = 21;
        }

        // TODO 根据屏幕宽高，动态分配行列数及边距
        // 固定桌面图标Item大小， 通过屏幕宽高计算边距、间距及行列数
        // 1，首先每个的宽高通过配置确定
        float itemWidth = DESKTOP_ITEM_WIDTH + DESKTOP_ITEM_WIDTH_SPACE * 2;
        float pageWidth = DESKTOP_WIDTH - DESKTOP_MARGIN_LEFT - DESKTOP_MARGIN_RIGHT;
        float itemHeight = DESKTOP_ITEM_HEIGHT + DESKTOP_ITEM_HEIGHT_SPACE * 2;
        float pageHeight = DESKTOP_HEIGHT - DESKTOP_MARGIN_TOP - DESKTOP_MARGIN_BOTTOM;
        // 2. 通过宽度计算最适合列数，横向边距及间距
        DESKTOP_PAGE_COLUMN = (int) (pageWidth / itemWidth);
        // 将多余的空间分配到左右两边
        float WsideMarginNew = (pageWidth - DESKTOP_PAGE_COLUMN * itemWidth) / 2;
        DESKTOP_MARGIN_LEFT += WsideMarginNew;
        DESKTOP_MARGIN_RIGHT += WsideMarginNew;
        // 3. 通过高度计算最适合行数，纵向边距及间距
        DESKTOP_PAGE_ROW = (int) (pageHeight / itemHeight);
        float countHeight = DESKTOP_PAGE_ROW * itemHeight;
        float HsideMarginNew = (pageHeight - countHeight) / 2;
        DESKTOP_MARGIN_TOP += HsideMarginNew;
        DESKTOP_MARGIN_BOTTOM += HsideMarginNew;

        DESKTOP_PAGE_GUIDE_BOTTOM = DESKTOP_MARGIN_TOP + countHeight;

        FullscreenActivity.DESKTOP_WIDTH = CustomDesktopParam.DESKTOP_WIDTH;
        FullscreenActivity.DESKTOP_HEIGHT = CustomDesktopParam.DESKTOP_HEIGHT;
        FullscreenActivity.DESKTOP_PAGE_COLUMN = CustomDesktopParam.DESKTOP_PAGE_COLUMN;
        FullscreenActivity.DESKTOP_PAGE_ROW = CustomDesktopParam.DESKTOP_PAGE_ROW;
        FullscreenActivity.DESKTOP_MARGIN_LEFT = CustomDesktopParam.DESKTOP_MARGIN_LEFT;
        FullscreenActivity.DESKTOP_MARGIN_RIGHT = CustomDesktopParam.DESKTOP_MARGIN_RIGHT;
        FullscreenActivity.DESKTOP_MARGIN_TOP = CustomDesktopParam.DESKTOP_MARGIN_TOP;
        FullscreenActivity.DESKTOP_MARGIN_BOTTOM = CustomDesktopParam.DESKTOP_MARGIN_BOTTOM;
    }

    public static void checkPosition(PageGuideView m_page_guide_view) {
        // TODO 重新定位页页面指示的位置
        CustomDesktop.LayoutParams layoutParams = (FrameLayout.LayoutParams) m_page_guide_view.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.bottomMargin = 0;
        layoutParams.topMargin = (int) DESKTOP_PAGE_GUIDE_BOTTOM;
    }

    public static void getTranslationYHeight(final AppPagerView appPagerView, final OnValueListener listener) {
        int height = appPagerView.getHeight();
        if (height == 0) {
            appPagerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = appPagerView.getHeight();
                    onTranslationY(height, appPagerView, listener);
                    if (height != 0) {
                        appPagerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        } else {
            onTranslationY(height, appPagerView, listener);
        }
    }

    private static void onTranslationY(final int height, final AppPagerView appPagerView, final OnValueListener listener) {
        int location = appPagerView.getTop() + height + 20;
        int translationY = (int) (location - DESKTOP_PAGE_GUIDE_BOTTOM);
        if (listener != null) {
            listener.onValue(translationY);
        }
    }

    public interface OnValueListener {
        void onValue(int... value);
    }

    public static int DESKTOP_PAGE_ICON_WIDTH = 188;
    public static int DESKTOP_PAGE_APP_LIST_COLUMNS = -1;

    public static int DESKTOP_PAGE_ICON_HEIGHT = 200;
    public static int DESKTOP_PAGE_APP_LIST_ROWS = -1;

    public static void getIconItemColumns(final AppPagerView appPagerView, final OnValueListener listener) {
        int height = appPagerView.getHeight();
        if (height == 0) {
            appPagerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = appPagerView.getWidth();
                    int height = appPagerView.getHeight();
                    if (height != 0) {
                        DESKTOP_PAGE_APP_LIST_COLUMNS  = width / DESKTOP_PAGE_ICON_WIDTH;
                        DESKTOP_PAGE_APP_LIST_ROWS  = height / DESKTOP_PAGE_ICON_HEIGHT;
                        onAppItemColumns(listener);
                        appPagerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        } else {
            onAppItemColumns(listener);
        }
    }

    private static void onAppItemColumns(OnValueListener listener) {
        if (listener != null) {
            listener.onValue(DESKTOP_PAGE_APP_LIST_COLUMNS, DESKTOP_PAGE_APP_LIST_ROWS);
        }
    }
}