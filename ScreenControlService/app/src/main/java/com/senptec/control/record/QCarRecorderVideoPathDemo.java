package com.senptec.control.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StatFs;

import com.quectel.qcarapi.cb.IQCarRecorderVideoPathCB;
import com.quectel.qcarapi.recorder.QCarEncParam;
import com.quectel.qcarlib.db.RecorderSQLiteOpenHelper;
import com.quectel.qcarlib.utils.RecorderUtil;
import com.quectel.qcarlib.utils.StorageUtil;
import com.senptec.control.VideoRecorder;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class QCarRecorderVideoPathDemo implements IQCarRecorderVideoPathCB {
    public static final int MEDIA_VIDEO_TYPE_MIAN_STRAM = 0;
    public static final int MEDIA_VIDEO_TYPE_MERGE_STREAM = 2;
    public static final int MEDIA_VIDEO_TYPE_SUB_STREAM = 2;
    public static final int MEDIA_VIDEO_TYPE_COLLISION_STREAM = 3;
    public static final int MEDIA_VIDEO_TYPE_PICTURE_JPEG = 4;

    private RecorderSQLiteOpenHelper sqLiteOpenHelper;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    static {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public String stampToDate(long timeMillis) {
        return simpleDateFormat.format(new Date(timeMillis));
    }

    public static long geFreeCapacity(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSizeLong();
        long feeBlockCount = stat.getAvailableBlocksLong();
        return blockSize * feeBlockCount / 1024 / 1024;
    }

    @Override
    public String getRecorderVideoPath(Context mContext, int mediaType, QCarEncParam.EncVideoParam encoderParam) {
        String path;
        String rootPath; //default
        sqLiteOpenHelper = RecorderUtil.getRsqliteHelper(mContext);
        rootPath = StorageUtil.getStoragePath(mContext, true);
        int csiphyNum = encoderParam.getCsiphyNum();
        int channel = encoderParam.getChannel();
        if (mediaType == MEDIA_VIDEO_TYPE_MIAN_STRAM) {
            path = getDir(rootPath, csiphyNum, channel, "main") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_MERGE_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "merge") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_COLLISION_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "collision") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_SUB_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "child") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 1, encoderParam.getStreamOutputFormat());
        } else
            path = "/sdcard/DCIM/err.stream";


        VideoRecorder.log(" path = " + path);
        if (geFreeCapacity(rootPath) < StorageUtil.getCapacityLeftInSdcard()) {
            while (geFreeCapacity(rootPath) < (StorageUtil.getCapacityLeftInSdcard() + StorageUtil.getCapacityRemoveLimit())) {
                String deletePath = sqLiteOpenHelper.queryFirstRecorderPathAndDelete();
                if (deletePath == null) {
                    VideoRecorder.log("内存卡满，录像不能启动");
                    return null;
                } else {
                    List<DbBean> dbBeans = DataSupport.where("path = ?", deletePath).find(DbBean.class);
                    if (dbBeans != null) {
                        for (DbBean dbBean : dbBeans) {
                            dbBean.delete();
                        }
                    }
                }
            }
        }
        sqLiteOpenHelper.insertRecorder(path, new Date().getTime());  //需要使用单例模式
        DbBean dbBean = new DbBean();
        dbBean.path = path;
        dbBean.save();
        return path;
    }

    public static String getDir(String rootPath, int csiNmu, int channelNum, String type) {
        String path = rootPath + String.format("/%s/%s_%s/", type, csiNmu, channelNum);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public String getRecorderLockVideoPath(Context mContext, int mediaType, QCarEncParam.EncVideoParam encoderParam) {
        String path;
        String rootPath; //default

        sqLiteOpenHelper = RecorderUtil.getRsqliteHelper(mContext);
        rootPath = StorageUtil.getStoragePath(mContext, true);
        rootPath += "/LockVideo";
        int csiphyNum = encoderParam.getCsiphyNum();
        int channel = encoderParam.getChannel();
        if (mediaType == MEDIA_VIDEO_TYPE_MIAN_STRAM) {
            path = getDir(rootPath, csiphyNum, channel, "main") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_MERGE_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "merge") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_COLLISION_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "collision") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        } else if (mediaType == MEDIA_VIDEO_TYPE_SUB_STREAM) {
            path = getDir(rootPath, csiphyNum, channel, "child") + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 1, encoderParam.getStreamOutputFormat());
        } else
            path = "/sdcard/DCIM/err.stream";

        VideoRecorder.log(" path = " + path);
        if (geFreeCapacity(rootPath) < StorageUtil.getCapacityLeftInSdcard()) {
            while (geFreeCapacity(rootPath) < (StorageUtil.getCapacityLeftInSdcard() + StorageUtil.getCapacityRemoveLimit())) {
                String deletePath = sqLiteOpenHelper.queryFirstRecorderPathAndDelete();
                if (deletePath == null) {
                    VideoRecorder.log("内存卡满，录像不能启动");
                    return null;
                }
            }
        }
        return path;
    }

    public void notifyRecoderVideoResult(QCarEncParam.EncVideoParam encoderParam, String path) {
        VideoRecorder.log("record file success! \n " + "path: " + path);
        long endTime = System.currentTimeMillis();
        File file = new File(path); //指定文件名及路径
        // 更新数据库信息
        DbBean dbBean = DataSupport
                .where("path = ?", path)
                .findLast(DbBean.class);
        if (dbBean == null) return;
        dbBean.csi = encoderParam.getCsiphyNum();
        dbBean.channel = encoderParam.getChannel();
        dbBean.recordStatus = DbBean.RECORD_STATUS_DONE;
        dbBean.size = file.length();
        String timeStr = file.getName().split("\\.")[0];
        try {
            Date parse = simpleDateFormat.parse(timeStr);
            if (parse != null) {
                dbBean.startTime = parse.getTime();
                dbBean.endTime = endTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            VideoRecorder.log(e.getMessage());
        } finally {
            dbBean.save();
        }
        // 在内置存储中向文件名加入结束时间
        String endTimeStr = stampToDate(endTime);
        String newFilePath = createNewPath(path, endTimeStr);
        File newFile = new File(newFilePath);
        if (file.renameTo(newFile)) {
            VideoRecorder.log("rename file success! \n " + "newFilePath: " + newFilePath);
            dbBean.path = newFilePath;
            dbBean.save();
        } else {
            VideoRecorder.log("Rename file failed!");
        }
    }

    private String createNewPath(String oldPath, String endTimeStr) {
        String newPath = oldPath;
        int i = oldPath.lastIndexOf('/');
        if (i > 0) {
            String dirPath = oldPath.substring(0, i + 1);
            int j = oldPath.lastIndexOf('.');
            String startTimeStr = oldPath.substring(i + 1, j);
            String endStr = oldPath.substring(j);
            newPath = dirPath + startTimeStr + "_" + endTimeStr + endStr;
        }
        return newPath;
    }
}
