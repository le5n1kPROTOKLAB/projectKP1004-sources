package com.senptec.control;

import androidx.annotation.NonNull;

import com.senptec.adapter.CanDataManager;
import com.senptec.adapter.CarInfoListener;
import com.senptec.adapter.CarSerialPortManager;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.ByteCmdBaseBean;
import com.senptec.adapter.bean.receive.d1.ByteD1CmdBean;
import com.senptec.adapter.bean.receive.d4.ByteD4CmdBean;
import com.senptec.control.scene.DVRHelper;
import com.senptec.control.util.DeviceStatus;
import com.senptec.control.util.LogUtils;
import com.septec.umanager.bean.AppInfo;
import com.septec.umanager.util.AppManager;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.LitePalApplication;


public class MyApplication extends LitePalApplication {

    public static boolean isRecordOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.Builder builder = new LogUtils.Builder();
        builder.setContext(this);
        builder.setLog2FileSwitch(BuildConfig.DEBUG);
        // 初始化LitePal数据库
        LitePal.initialize(this);
        SpUtils.init("carControl", this);
        DVRHelper.getInstance().initDVRHelper(this);
        CarSerialPortManager.getInstance().initPortManager(this)
                .addCarInfoListener(ByteD1CmdBean.D1_ID_FLAG, new CarInfoListener() {
                    @Override
                    public void onMessage(ByteCmdBaseBean cmdBean) {
                        if (cmdBean != null) {
                            CanDataManager canDataManager = CanDataManager.getInstance();
                            ByteCanBaseBean canBean = cmdBean.getCanBean();
                            // 自己处理
                            String id = canBean.getId();
                            LogUtils.e("====id=======  " + id);
                            String json = canDataManager.toJson(canBean);
                            canDataManager.putReceiveData(id, json);
                        }
                    }
                }).addVersionListener(new CarInfoListener() {
                    @Override
                    public void onMessage(ByteCmdBaseBean byteCmdBaseBean) {
                        if (byteCmdBaseBean instanceof ByteD4CmdBean) {
                            ByteCanBaseBean canBean = byteCmdBaseBean.getCanBean();
                            String id = CanIdTransfer.S_CAN_ID_BOTTOM_VERSION;
                            canBean.setId(id);
                            CanDataManager canDataManager = CanDataManager.getInstance();
                            String json = canDataManager.toJson(canBean);
                            canDataManager.putReceiveData(id, json);
                        }
                    }
                });
        // 初始化并开启录制功能
        if (isRecordOpen) {
            VideoRecorder.init(this);
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String msg = "thread: " + t.getName() + "(" + t.getId() + ") " + e.getMessage();
                LogUtils.needNewThread = false;
                LogUtils.d(msg);
                LogUtils.needNewThread = true;
            }
        });
        AppManager appManager = AppManager.getInstance();
        appManager.init(this);
        appManager.setOnEventListener(new AppManager.OnEventListener() {
            @Override
            public void onApkLoad(AppInfo appInfo) {
                appInfo.isNeedCheckVersion = true;
                appInfo.isOpen = true;
            }

            @Override
            public void onDeviceMounted(boolean b) {
                // 内置存储也会经过这个回调返回true
                VideoRecorder.log("onDeviceMounted " + b);
                if (b) {
                    // 完成装载
                    AppManager.getInstance().loadSdOrUsbStorage(AppManager.TYPE_INSTALL_MANAGER_APP);
                    EventBus.getDefault().post(new DeviceStatus(true));
                }
            }

            @Override
            public void onDeviceAttached(boolean b) {
                VideoRecorder.log("onDeviceAttached " + b);
                if (!b) {
                    // usb拔出
                    EventBus.getDefault().post(new DeviceStatus(false));
                }
            }

            @Override
            public void onApkInstallComplete() {

            }
        });
    }
}
