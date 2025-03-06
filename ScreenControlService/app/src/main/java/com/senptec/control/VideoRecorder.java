package com.senptec.control;

import android.os.Build;

import com.senptec.control.record.GUtilMain;
import com.senptec.control.record.RecorderInstance;
import com.senptec.control.util.LogUtils;

public class VideoRecorder {
    private static final String TAG = "CameraRecorder";


    static {
        System.loadLibrary("mmqcar_qcar_jni");
        if (Build.VERSION.SDK_INT == 28) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 4;
        } else if (Build.VERSION.SDK_INT == 25) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 2;
        }
    }

    public static void init(MyApplication application) {
        GUtilMain.setqContext(application);
        RecorderInstance.getInstance().initRecorder();
    }

    public static void log(String s) {
        LogUtils.d(1, TAG, s);
    }
}
