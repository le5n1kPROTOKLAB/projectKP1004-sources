package com.senptec.control.record;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.SystemClock;
import android.os.storage.StorageVolume;
import android.text.TextUtils;

import com.quectel.qcarapi.osd.QCarOsd;
import com.quectel.qcarapi.recorder.QCarEncParam;
import com.quectel.qcarapi.recorder.QCarRecorder;
import com.quectel.qcarapi.recorder.QCarRecorderSetting;
import com.quectel.qcarapi.stream.QCarCamera;
import com.senptec.control.VideoRecorder;
import com.senptec.control.util.DeviceStatus;
import com.septec.umanager.util.StorageHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class RecorderInstance {
    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraRecord";
    public static final int TIME_SPACE = 2 * 60 * 1000;

    private final Object rLock = new Object();
    private final String USB2_PATH = "/storage/usb1-1-2-p1";
    private Vector<QCarRecorder> carRecorders;
    private final RecorderParams rParams = GUtilMain.getRecorderParams();
    private QCarRecorderSetting qCarRecorderSetting = null;
    private boolean isRecorderStarted = false;

    private int vid_Width = 0;
    private int vid_Height = 0;
    private int mainRate;

    private Timer removeFileTimer;

    private static final class InstanceHolder {
        static final RecorderInstance instance = new RecorderInstance();
    }

    public static RecorderInstance getInstance() {
        return InstanceHolder.instance;
    }

    public void initRecorder() {
        vid_Width = rParams.getWidth();
        vid_Height = rParams.getHeight();
        mainRate = GUtilMain.BITRATE_2M;
        VideoRecorder.log("record width " + vid_Width + " height " + vid_Height);
        File file = new File(PATH);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            VideoRecorder.log("文件夹：" + file.getAbsolutePath() + " 创建" + (success ? "成功" : "失败"));
        } else {
            VideoRecorder.log("文件夹：" + file.getAbsolutePath() + " 存在");
        }
        if (qCarRecorderSetting == null) {
            qCarRecorderSetting = new QCarRecorderSetting();
            qCarRecorderSetting.setRootStoragePath(PATH);
            qCarRecorderSetting.startScanDirectory(GUtilMain.getqContext()); //该接口只需要第一次的时候调用一次
//            qCarRecorderSetting.forceScanUpdateDirectory(GUtilMain.getqContext());
        }
        // 开始录制前，要将数据库中所有处于录制状态的数据，强制变更为录制异常
        List<DbBean> dbBeans = DataSupport.where("recordstatus = ?",
                String.valueOf(DbBean.RECORD_STATUS_DOING)).find(DbBean.class);
        for (DbBean dbBean : dbBeans) {
            dbBean.recordStatus = DbBean.RECORD_STATUS_ERROR;
            dbBean.save();
        }
        startRecorder();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private volatile boolean isRemoving = false;

    private void startRemoveTimer() {
        VideoRecorder.log("startRemoveTimer");
        stopRemoveTimer();
        removeFileTimer = new Timer();
        removeFileTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isVolumeUsable) {
                    if (!isRemoving) {
                        isRemoving = true;
                        try {
                            // 这时只转移录制完毕的视频，不加限制的话，会把录制中也转移掉，导致后续录制流程异常
                            DbBean dbBean = DataSupport.where("storestatus is null or storestatus = ? and recordstatus != ?",
                                    String.valueOf(DbBean.STORE_STATUS_IN), String.valueOf(DbBean.RECORD_STATUS_DOING)).findFirst(DbBean.class);
                            removeFileToU2(dbBean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            isRemoving = false;
                        }
                    }
                }
            }
        }, 0, 100);
    }

    private void stopRemoveTimer() {
        VideoRecorder.log("stopRemoveTimer");
        if (removeFileTimer != null) {
            removeFileTimer.cancel();
            removeFileTimer = null;
        }
    }

    private volatile boolean isVolumeUsable = false;

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceStatus(DeviceStatus status) {
        VideoRecorder.log("onDeviceStatus " + status.isUsable);
        // 这里需要检查 USB2_PATH 是否装载
        isUsbMou();
    }

    private void isUsbMou() {
        StorageVolume[] volumes = StorageHelper.getStorageVolumes(GUtilMain.getqContext(), true);
        VideoRecorder.log("卷数量：" + (volumes == null ? 0 : volumes.length));
        if (volumes == null) return;
        String uPath = null;
        StorageVolume uVolume = null;
        for (StorageVolume volume : volumes) {
            if (volume != null) {
                uPath = StorageHelper.getPathReflect(volume);
                if (USB2_PATH.equals(uPath)) {
                    uVolume = volume;
                    break;
                }
            }
        }
        if (uVolume != null) {
            VideoRecorder.log("检查到卷：" + USB2_PATH);
            String mountedState = uVolume.getState();
            if (!"mounted".equalsIgnoreCase(mountedState) && !"mounted_ro".equalsIgnoreCase(mountedState)) {
                VideoRecorder.log("卷未装载 " + USB2_PATH + " state " + mountedState);
                onVolumeUsable(false);
            } else {
                VideoRecorder.log("卷装载 " + USB2_PATH);
                onVolumeUsable(true);
            }
        } else {
            VideoRecorder.log("未检查到卷：" + USB2_PATH);
            onVolumeUsable(false);
        }
    }

    private void onVolumeUsable(boolean b) {
        isVolumeUsable = b;
        if (b) {
            startRemoveTimer();
        } else {
            stopRemoveTimer();
        }
    }


    public void removeFileToU2(DbBean dbBean) {
        if (dbBean == null) return;
        if (TextUtils.isEmpty(dbBean.path)) {
            dbBean.delete();
            return;
        } else {
            File file = new File(dbBean.path);
            if (!file.exists()) {
                dbBean.delete();
                return;
            }
        }
        if (isVolumeUsable) {
            try {
                VideoRecorder.log("path " + dbBean.path + " status " + dbBean.storeStatus);
                File file = new File(dbBean.path); //指定文件名及路径
                String newFilePath = dbBean.path.replace("/storage/emulated/0", USB2_PATH);
                VideoRecorder.log("newFilePath " + newFilePath);
                int i = newFilePath.lastIndexOf("/");
                String newFileDir = newFilePath.substring(0, i);
                String fileName = newFilePath.substring(i);
                createDirIfNotExist(newFileDir);
                String newFileDirTemp = newFileDir + "/Temp";
                createDirIfNotExist(newFileDirTemp);
                String newFilePathTemp = newFileDirTemp + fileName;
                // 复制之前先检查剩余空间，不够的话，需要删除最早的视频，释放空间
                File usbDirectory = new File(USB2_PATH); // U盘所在路径
                if (usbDirectory.exists() && usbDirectory.isDirectory()) {
                    long totalSpace = usbDirectory.getTotalSpace(); // 获取可用空间
                    long freeSpace = usbDirectory.getFreeSpace(); // 获取可用空间
                    long remainSpace = file.length() * 2; // 释放待转移文件2倍大小

                    // 在实际运用打开usb高度或者拔掉硬盘会出现 可用空间/全部空间 0/0 需要空间 559176
                    // 故 全部空间 == 0 时，认为硬盘已经不能用了，不再执行下方代码
                    if (totalSpace <= 0) {
                        return;
                    }
                    VideoRecorder.log("可用空间/全部空间 " + freeSpace + "/" + totalSpace + " 需要空间 " + remainSpace);
                    while (isVolumeUsable && freeSpace < remainSpace) {
                        DbBean first = DataSupport
                                .where("storestatus = ?",
                                        String.valueOf(DbBean.STORE_STATUS_OUT))
                                .findFirst(DbBean.class);
                        if (first != null) {
                            VideoRecorder.log(first.path + " 需要删除");
                            File deleteFile = new File(first.path);
                            if (deleteFile.exists()) {
                                if (deleteFile.delete()) {
                                    VideoRecorder.log(first.path + " 删除成功");
                                } else {
                                    VideoRecorder.log(first.path + " 删除失败");
                                }
                            }
                            first.delete();
                        } else {
                            VideoRecorder.log("未查询到需要删除");
                        }
                        freeSpace = usbDirectory.getFreeSpace();
                    }
                }
                File newFileTemp = new File(newFilePathTemp);
                doCopyFile(file, newFileTemp);
                File newFile = new File(newFilePath);
                if (newFileTemp.renameTo(newFile)) {
                    VideoRecorder.log("将文件从临时目录重命名至常规目录成功: " + newFilePath);
                    VideoRecorder.log("更新数据库开始");
                    dbBean.storeStatus = DbBean.STORE_STATUS_OUT;
                    dbBean.path = newFilePath;
                    dbBean.save();
                    VideoRecorder.log("更新数据库结束，开始删除文件");
                    if (file.delete()) {
                        VideoRecorder.log("文件删除成功 " + newFile.length());
                    } else {
                        VideoRecorder.log("文件删除失败");
                    }
                    VideoRecorder.log("文件转移成功");
                } else {
                    VideoRecorder.log("将文件从临时目录重命名至常规目录失败");
                    VideoRecorder.log("文件转移失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                VideoRecorder.log("文件转移失败");
            }
        }
    }

    private static void createDirIfNotExist(String fileDir) {
        File newFileDirFile = new File(fileDir);
        if (!newFileDirFile.exists()) {
            VideoRecorder.log(fileDir + " 不存在");
            if (newFileDirFile.mkdirs()) {
                VideoRecorder.log(fileDir + " 创建成功");
            } else {
                VideoRecorder.log(fileDir + " 创建失败");
            }
        } else {
            VideoRecorder.log(fileDir + " 存在");
        }
    }

    /**
     * android向U盘中拷贝文件成功后立刻断开，文件0字节[https://blog.csdn.net/SylG17/article/details/107816854]
     */
    private static void doCopyFile(File srcFile, File destFile) throws IOException {
        VideoRecorder.log("文件拷贝到临时目录开始");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            FileDescriptor fd = fos.getFD();
            int len;
            byte[] fileRemoveTemp = new byte[1024];
            while ((len = fis.read(fileRemoveTemp)) > 0) {
                fos.write(fileRemoveTemp, 0, len);
            }
            fos.flush();
            // 文件写完之后再同步，否则文件复制会很慢
            fd.sync();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }

        boolean b = destFile.setLastModified(srcFile.lastModified());
        VideoRecorder.log("设置文件最后修改时间" + (b ? "成功" : "失败"));
        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        VideoRecorder.log("文件拷贝到临时目录结束");
    }

    public void startRecorder() {
        synchronized (rLock) {
            if (!isRecorderStarted) {
                startRecord();
                isRecorderStarted = true;
            }
        }
    }

    private void startRecord() {
        carRecorders = new Vector<>();
        VideoRecorder.log("开始录制");
        QCarCamera qCarCamera = GUtilMain.getQCamera(1);
        if (qCarCamera != null) {
            while (true) {
                int ret = qCarCamera.cameraOpen(4, 0);
                if (ret == 0) {
                    addRecord(1, 0);
                    addRecord(1, 1);
                    addRecord(1, 2);
                    addRecord(1, 3);
                    break;
                } else {
                    SystemClock.sleep(500);
                }
            }
        }
    }

    public void addRecord(int csiNum, int channelNum) {
        initChannelRecord(csiNum, channelNum);
    }

    private void initChannelRecord(int csiNum, int channel) {
        // 针对每个通道的预览视频数据格式进行设置
        QCarCamera qCarCamera = GUtilMain.getQCamera(csiNum);
        if (qCarCamera != null) {
            qCarCamera.setVideoColorFormat(channel, QCarCamera.YUV420_NV12);
            QCarRecorder avcEncoder = new QCarRecorder(GUtilMain.getqContext());
            avcEncoder.setQCarCamera(qCarCamera);
            QCarEncParam.EncVideoParam encoderParam = new QCarEncParam.EncVideoParam(); //创建EncoderParam类
            encoderParam.setCsiNumAndVideoChannel(csiNum, channel);
            encoderParam.setResolution(vid_Width, vid_Height);
            encoderParam.setBitrate(mainRate);//设置主码流

            encoderParam.setKeyIFrameInterval(2);
            encoderParam.setKeyFrameRate(25);
            encoderParam.setEncoderMineType(GUtilMain.getMinetypeWithCodecPosition(0));
            encoderParam.setStreamOutputFormat(GUtilMain.getOutputFormatWidthContainerPosition(0));
            encoderParam.setBitrateMode(1); //VBR模式 可以手动设置
            avcEncoder.setMainEncVideoParam(encoderParam); //设置encoder参数
            avcEncoder.setFileSegmentThreshold(1, TIME_SPACE);
            avcEncoder.registerRecorderVideoPathCB(GUtilMain.getQRVPDemo());
            avcEncoder.startRecorder();
            addMainOsd();
            carRecorders.add(avcEncoder);
        }
    }

    public void stopRecorder() {
        synchronized (rLock) {
            if (isRecorderStarted) {
                stopRecord();
                isRecorderStarted = false;
            }
        }
    }

    private void stopRecord() {
        Vector<Thread> encoderThreads = new Vector<>();
        for (final QCarRecorder carRecorder : carRecorders) {
            Thread encoderCloseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    carRecorder.stopRecorder();
                }
            });
            encoderCloseThread.start();
            encoderThreads.add(encoderCloseThread);
        }
        for (Thread thread : encoderThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        encoderThreads.clear();
        carRecorders.clear();
    }

    private static void addMainOsd() {
        new Thread(new Runnable() {
            Date date;
            String dateStr;
            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int tIndex = -1;
            final String font_path = "/system/fonts/Song.ttf";
            final int font_size = 4;
            QCarOsd qCarOsd;
            boolean bOsdFlag = false;

            @Override
            public void run() {
                qCarOsd = new QCarOsd();
                qCarOsd.initOsd(font_path.getBytes(), font_size);
                qCarOsd.setOsdColor(72, 170, 120);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                bOsdFlag = true;
                QCarCamera qCarCamera = GUtilMain.getQCamera(1);
                if (qCarCamera != null) {
                    qCarCamera.setMainOsd(qCarOsd);
                }
                while (bOsdFlag) {
                    date = new Date();
                    dateStr = sdf.format(date);
                    tIndex = qCarOsd.setOsd(-1, dateStr.getBytes(), tIndex, 32, 96);
                    SystemClock.sleep(980); // 时间字幕刷新频率
                }
                qCarOsd.deinitOsd();
            }
        }).start();
    }
}
