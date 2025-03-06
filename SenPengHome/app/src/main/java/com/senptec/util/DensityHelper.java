package com.senptec.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DensityHelper {

    /**
     * Android App不跟随系统显示大小变化
     * <a href="https://www.jianshu.com/p/bb3c23a3cf1e">...</a>
     */
    public static void setCustomDensity(Context context) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();
        if (res.getDisplayMetrics().densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
            configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
    }

}
