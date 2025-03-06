package com.senptec.control.record;

import android.content.Context;
import android.media.MediaMuxer;

import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GUtilMain {
    // Log tag
    private static final String TAG = "GUtil";

    // Define video bitrate
    public static int BITRATE_4M = 4 * 1024 * 1024;
    public static int BITRATE_2M = 2 * 1024 * 1024;
    public static int BITRATE_1M = 1024 * 1024;
    public static int BITRATE_512K = 1024 * 1024;
    //private static int BITRATE_DEFAULT = 1024 * 1024;

    private static Context qContext = null;

    // parameters for video recoding
    private static RecorderParams recorderParams = null;
    private static QCarRecorderVideoPathDemo qrvpDemo = null;
    private static final Map<Integer, QCarCamera> qCarCameraMap = new ConcurrentHashMap<>();
    public static int MEDIA_OUTPUT_FORMAT_TS = 2;

    public static Context getqContext() {
        return qContext;
    }

    public static void setqContext(Context qContext) {
        GUtilMain.qContext = qContext;
    }

    // create struct RecorderParams for recorderParams
    public static RecorderParams getRecorderParams() {
        if (recorderParams == null) {
            recorderParams = new RecorderParams();
        }

        return recorderParams;
    }

    // get the mine type for video codec
    public static String getMinetypeWithCodecPosition(int position) {
        if (position == 0) {
            return "video/avc";
        } else if (position == 1) {
            return "video/hevc";
        }

        return "video/hevc";
    }

    // get output format for video
    public static int getOutputFormatWidthContainerPosition(int position) {
        if (position == 0) {
            return MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;  // mp4
        } else if (position == 1) {
            return MEDIA_OUTPUT_FORMAT_TS; //Android7 该值是2  Android9该值是4 一定要注意
        }

        return MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4; // default is mp4
    }

    public static synchronized QCarCamera getQCamera(int csiphy_num) {
        if (csiphy_num >= 3) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiphy_num need small than 3");
            return null;
        }

        QCarCamera qCarCamera = qCarCameraMap.get(csiphy_num);
        if (qCarCamera == null) {
            qCarCamera = new QCarCamera(csiphy_num);
            qCarCameraMap.put(csiphy_num, qCarCamera);
        }

        return qCarCamera;
    }

    public static synchronized int removeQCamera(int csiphy_num) {
        if (csiphy_num >= 3 || csiphy_num < 0) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiphy_num need larger than 0 and small than 3");
            return -1;
        }

        QCarCamera qCarCamera = qCarCameraMap.get(csiphy_num);
        if (qCarCamera != null) {
            qCarCameraMap.remove(csiphy_num, qCarCamera);
        }

        return 0;
    }

    public static QCarRecorderVideoPathDemo getQRVPDemo() {
        if (qrvpDemo == null) {
            qrvpDemo = new QCarRecorderVideoPathDemo();
        }
        return qrvpDemo;
    }

}
