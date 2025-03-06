package com.senptec.control;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.orhanobut.logger.BuildConfig;
import com.senptec.adapter.CanDataManager;
import com.senptec.adapter.CarSerialPortManager;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0218Bean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0318Bean;
import com.senptec.can.ICanClientInterface;
import com.senptec.can.ICanServerInterface;
import com.senptec.control.record.DbBean;
import com.senptec.control.record.RecordResponse;
import com.senptec.control.record.RecorderInstance;
import com.senptec.control.scene.DVRHelper;
import com.senptec.control.scene.ISpdServiceCallbackAdapter;
import com.senptec.control.scene.SettingsHelper;
import com.senptec.control.scene.SettingsHelperCallBackAdapter;
import com.senptec.control.util.LogUtils;
import com.senptec.control.util.ThreadUtil;
import com.spd.setting.util.SettingUtils;
import com.spd.system.aidl.ISpdServiceCallback;
import com.spd.system.service.SpdManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ControlService extends Service {

    private final String TAG = "screenControlService";

    private final SettingsHelper.SettingsHelperCallBack c_callback = new SettingsHelperCallBackAdapter() {

        @Override
        public void onServiceConnected() {
            getBrightness();
        }

        @Override
        public void onIntChanged(int i, int i1) {
            if (i == SettingUtils.Display.SCREEN_BRIGHTNESS) {
                getBrightness();
            }
        }
    };

    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == CMD) {
                getBrightness();
                CarSerialPortManager.getInstance().sendCarCommand(screenInfoEntity.getCommand());
                sendEmptyMessageDelayed(CMD, 500);
            } else {
                throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };
    private final int CMD = 111111;
    private final String CANSWITCHER0318_TAG = "CanSwitcher0318";
    private final String CANSWITCHER0218_TAG = "CanSwitcher0218";
    private CanSwitcher0318Bean mCanSwitcher0318Bean = new CanSwitcher0318Bean();
    private CanSwitcher0218Bean mCanSwitcher0218Bean = new CanSwitcher0218Bean();
    ScreenInfoEntity screenInfoEntity;
    private Timer timer0318;
    private Timer timer0218;
    private static final String KEY_FROM = "from";

    @Override
    public void onCreate() {
        super.onCreate();
        creatNotification();

        String tempJsonStr = SpUtils.getInstance().getString(TAG, "");

        if (tempJsonStr.isEmpty()) {
            screenInfoEntity = new ScreenInfoEntity();
        } else {
            screenInfoEntity = new Gson().fromJson(tempJsonStr, ScreenInfoEntity.class);
        }
        DVRHelper.getInstance().getSpdService().registerSpdServiceCallback(getPackageName(), spdServiceCallback);
        SettingsHelper.get().initSettingsHelper(getApplicationContext());
        SettingsHelper.get().regsiterCallBack(c_callback);
        mHandler.sendEmptyMessageDelayed(CMD, 100);

        timer0318 = new Timer();
        timer0318.schedule(new TimerTask() {
            @Override
            public void run() {
                CanDataManager canDataManager = CanDataManager.getInstance();
                mCanSwitcher0318Bean.setINAmbientLamp(SpUtils.getInstance().getBoolean(CANSWITCHER0318_TAG, false));
                mCanSwitcher0318Bean.setFCUDegas(mCanSwitcher0318Bean.getFCUDegas());
                mCanSwitcher0318Bean.setEVModel(mCanSwitcher0318Bean.getEVModel());
                mCanSwitcher0318Bean.setSteeringWheelAdjust(mCanSwitcher0318Bean.getSteeringWheelAdjust());
                mCanSwitcher0318Bean.setAcceleratorInterlock(mCanSwitcher0318Bean.getAcceleratorInterlock());
                mCanSwitcher0318Bean.setICMButtonUP(mCanSwitcher0318Bean.getICMButtonUp());
                mCanSwitcher0318Bean.setICMButtonDown(mCanSwitcher0318Bean.getICMButtonDown());
                mCanSwitcher0318Bean.setICMButtonBack(mCanSwitcher0318Bean.getICMButtonBack());
                mCanSwitcher0318Bean.setICMButtonMenu(mCanSwitcher0318Bean.getICMButtonMenu());
                mCanSwitcher0318Bean.setAEBS(mCanSwitcher0318Bean.getAEBS());
                mCanSwitcher0318Bean.setADAS(mCanSwitcher0318Bean.getADAS());
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                String json = canDataManager.gson.toJson(byteCanBaseBean.toByteArray());
                LogUtils.e("====putSendData=======  " + byteCanBaseBean.getId() + "--json--" + json);
                canDataManager.putSendData(byteCanBaseBean.getId(), json);
            }
        }, 0, 500);

        timer0218 = new Timer();
        timer0218.schedule(new TimerTask() {
            @Override
            public void run() {
                CanDataManager canDataManager = CanDataManager.getInstance();
                mCanSwitcher0218Bean.setExAmbientLamp(SpUtils.getInstance().getBoolean(CANSWITCHER0218_TAG, false));
                mCanSwitcher0218Bean.setDomeLamp(mCanSwitcher0218Bean.getDomeLamp());
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0218Bean;
                String json = canDataManager.gson.toJson(byteCanBaseBean.toByteArray());
                LogUtils.e("====putSendData=======  " + byteCanBaseBean.getId() + "--json--" + json);
                canDataManager.putSendData(byteCanBaseBean.getId(), json);
            }
        }, 0, 500);
    }

    private void creatNotification() {
        String CHANNEL_ID_STRING = BuildConfig.APPLICATION_ID + "1";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_STRING, "车辆服务", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING)
                .setContentTitle("车辆服务")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("提供车辆相关服务")
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        int notificationId = 1;
        startForeground(notificationId, notification);
        notificationManager.notify(notificationId, notification);
    }

    public ISpdServiceCallback spdServiceCallback = new ISpdServiceCallbackAdapter() {
        public void sendCommonToSouce(int i, int i1, int i2) {
            if (i == 8) {
                if (i1 == 0) {
                    log("休眠唤醒");
                    // 对应关钥匙后，再开钥匙，主机从休眠恢复
                    if (MyApplication.isRecordOpen) {
                        RecorderInstance.getInstance().startRecorder();
                    }
                } else if (i1 == 6 || i1 == 7) {
                    // 休眠时6、7均会回调一次
                    if (i1 == 6) {
                        log("系统休眠");
                        if (MyApplication.isRecordOpen) {
                            RecorderInstance.getInstance().stopRecorder();
                        }
                    }
                }
            }
        }
    };

    private String getPkgNameExt() {
        String pkgName = this.getPackageName();
        int callbackHashCode = this.hashCode();
        return pkgName + "." + callbackHashCode;
    }

    //背光检测
    private void getBrightness() {
        int c_brightness_max = SettingsHelper.get().getIntValue(SettingUtils.Display.SCREEN_MAX_BRIGHTNESS, 1);
        int c_brightness = SettingsHelper.get().getIntValue(SettingUtils.Display.SCREEN_BRIGHTNESS, 1);//获取背光等级1-255
        int c_brightness_min = SettingsHelper.get().getIntValue(SettingUtils.Display.SCREEN_MIN_BRIGHTNESS, 1);//获取背光等级1-255
        if (c_brightness < 1) {
            c_brightness = 1;
        } else if (c_brightness > 255) {
            c_brightness = 255;
        }
        screenInfoEntity.dayLightLevel = (int) Math.ceil(c_brightness * 1f / c_brightness_max * 100);
//        log("getBrightness" + String.format(" %s_%s_%s_%s ", c_brightness_min, c_brightness, c_brightness_max, screenInfoEntity.dayLightLevel));
        saveEntityAndSendCommands();
    }

    public void saveEntityAndSendCommands() {
        SpUtils.getInstance().save(TAG, new Gson().toJson(screenInfoEntity));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer0318.cancel();
        timer0218.cancel();
        mHandler.removeCallbacksAndMessages(null);
        SettingsHelper.get().unregsiterCallBack(c_callback);
        DVRHelper.getInstance().setHelpCallback(null);
        DVRHelper.getInstance().unregisterReverseInfoCallback("backing.avm" + this.hashCode());
        DVRHelper.getInstance().getSpdService().unRegisterSpdServiceCallback(getPackageName());
        SpdManager c_manager = SpdManager.getInstance(this.getApplicationContext());
        if (c_manager != null) {
            c_manager.unRegisterSpdServiceCallback(getPkgNameExt());
        }
    }

    private void openVedioPage() {
        if (isAppInstalled(this, "com.senptec.carvideoapp")) {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName componentName = new ComponentName("com.senptec.carvideoapp", "com.senptec.carvideo.MainActivity");
                intent.setComponent(componentName);
                intent.putExtra(KEY_FROM, "home");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void log(String msg) {
        VideoRecorder.log(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        log("server端：onBind");
        return iBinder;
    }

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private final IBinder iBinder = new ICanServerInterface.Stub() {

        @Override
        public void setCallback(String packageName, ICanClientInterface callBack) {
            log("收到客户端的回调， packageName: " + packageName);
            // 客户端向服务端注册回调
            CanDataManager.getInstance().putCallback(packageName, callBack);
        }

        @Override
        public void onSendData(String id, String gsonData) {
            log("收到客户端发来的消息：id: " + id + " " + gsonData);
            if (CanSwitcher0318Bean.ID.equals(id)) {
                // 定时发送，需要单独处理
                byte[] bytes = CanDataManager.getInstance().gson.fromJson(gsonData, byte[].class);
                mCanSwitcher0318Bean.parse(bytes);
                if (mCanSwitcher0318Bean.getICMButtonBack() || mCanSwitcher0318Bean.getICMButtonUp() ||
                        mCanSwitcher0318Bean.getICMButtonDown() || mCanSwitcher0318Bean.getICMButtonMenu()) {
                    CanDataManager canDataManager = CanDataManager.getInstance();
                    ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                    String json = canDataManager.gson.toJson(byteCanBaseBean.toByteArray());
                    canDataManager.putSendData(byteCanBaseBean.getId(), json);
                }
                SpUtils.getInstance().save(CANSWITCHER0318_TAG, mCanSwitcher0318Bean.getINAmbientLamp());
            } else if (CanSwitcher0218Bean.ID.equals(id)) {
                // 定时发送，需要单独处理
                byte[] bytes = CanDataManager.getInstance().gson.fromJson(gsonData, byte[].class);
                mCanSwitcher0218Bean.parse(bytes);
                SpUtils.getInstance().save(CANSWITCHER0218_TAG, mCanSwitcher0218Bean.getExAmbientLamp());
            } else {
                // 客户端发送
                CanDataManager.getInstance().putSendData(id, gsonData);
            }
        }

        @Override
        public void requestRecordFileList(String packageName, String tag, int csi, int channel, long startTime, long endTime) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    log(String.format("收到客户端发来的录像列表请求：packageName: %s, " +
                                            "\r\ntag:%s " +
                                            "\r\ncsi:%s " +
                                            "\r\nchannel:%s" +
                                            "\r\nstartTime: %s" +
                                            "\r\nendTime: %s",
                                    packageName,
                                    tag,
                                    csi,
                                    channel,
                                    simpleDateFormat.format(new Date(startTime)),
                                    simpleDateFormat.format(new Date(endTime))
                            )
                    );
                    // 查询数据库
                    List<DbBean> list = DataSupport
                            .where("starttime >= ? and starttime < ? and csi = ? and channel = ? and recordstatus = 1 and storestatus = 1",
                                    String.valueOf(startTime),
                                    String.valueOf(endTime),
                                    String.valueOf(csi),
                                    String.valueOf(channel)
                            )
                            .order("starttime asc")
                            .find(DbBean.class);

                    // 10条一组发送
                    if (list != null && list.size() > 0) {
                        log("查询到的记录数量为：" + list.size());
                        int size = list.size();
                        int packageCount = size / 10;
                        int temp = size % 10;
                        if (temp != 0) {
                            // 有余数
                            packageCount += 1;
                        }
                        List<RecordResponse.Data> dataList = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            int tempI = (i + 1) % 10;
                            DbBean dbBean = list.get(i);
                            RecordResponse.Data data = new RecordResponse.Data();
                            data.path = dbBean.path;
                            data.csi = dbBean.csi;
                            data.channel = dbBean.channel;
                            data.startTime = dbBean.startTime;
                            data.endTime = dbBean.endTime;
                            dataList.add(data);
                            if (i > 0 && tempI == 0) {
                                onRecordFileList(packageName,
                                        tag,
                                        startTime,
                                        endTime, packageCount,
                                        i / 10 + 1,
                                        size, 10,
                                        dataList);
                                dataList.clear();
                            } else if ((i + 1) == size) {
                                // 说明有余数，非10的整数，否则最后一个会走上面的分支
                                onRecordFileList(packageName,
                                        tag,
                                        startTime,
                                        endTime,
                                        packageCount,
                                        i / 10 + 1,
                                        size,
                                        tempI,
                                        dataList);
                                dataList.clear();
                            }
                        }

                    } else {
                        log("未查询到记录");
                        onRecordFileList(packageName,
                                tag,
                                startTime,
                                endTime,
                                1,
                                1,
                                0,
                                0,
                                null);
                    }
                }
            });
        }

        private void onRecordFileList(String packageName,
                                      String tag,
                                      long startTime,
                                      long endTime,
                                      int packageCount,
                                      int packageNumber,
                                      int totalCount,
                                      int count,
                                      List<RecordResponse.Data> list) {
            RecordResponse recordResponse = new RecordResponse();
            recordResponse.tag = tag;
            recordResponse.startTime = startTime;
            recordResponse.endTime = endTime;
            recordResponse.packageCount = packageCount;
            recordResponse.packageNumber = packageNumber;
            recordResponse.totalCount = totalCount;
            recordResponse.count = count;
            recordResponse.dataList = list;
            ICanClientInterface callBack = CanDataManager.getInstance().getCallBack(packageName);
            if (callBack != null) {
                try {
                    callBack.onRecordFileList(tag, CanDataManager.getInstance().getGsonData(recordResponse));
                } catch (RemoteException e) {
                    LogUtils.e(e);
                }
            }
        }
    };
}