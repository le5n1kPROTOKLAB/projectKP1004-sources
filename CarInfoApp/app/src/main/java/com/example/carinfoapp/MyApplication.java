package com.example.carinfoapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.example.carinfoapp.can.InputCanBean;
import com.google.gson.Gson;
import com.senptec.adapter.CarSerialPortManager;
import com.senptec.adapter.LoggerUtil;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.receive.d1.ByteD1CmdBean;
import com.senptec.adapter.bean.receive.d1.channel.ByteD1CanChannelAmpere;
import com.senptec.adapter.bean.receive.d1.channel.ByteD1CanChannelStatus;
import com.senptec.can.ICanClientInterface;
import com.senptec.can.ICanServerInterface;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.HashMap;


public class MyApplication extends Application {

    public Gson gson = new Gson();

    private final int CMD = 111111;

    Handler mHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == CMD) {
                if (mService != null) {
                    sendEmptyMessageDelayed(CMD, 500); // 主要是根据
                } else {
                    sendEmptyMessageDelayed(CMD, 1000);
                    callService();
                }
            } else {
                sendEmptyMessageDelayed(CMD, 3000);
                log("in25值未获取");
            }
        }
    };

    private ICanServerInterface mService;

    private void callService() {
        ServiceConnection mConn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                log("服务端：连接 Service 成功");
                mService = ICanServerInterface.Stub.asInterface(service);
                // 向服务端注册自己的回调
                try {
                    mService.setCallback(getPackageName(), new ICanClientInterface.Stub() {

                        @Override
                        public void onReceiveData(String id, String gsonData) {
                            //  接收报文
                            try {
                                // 串口Can数据
                                Class<? extends ByteCanBaseBean> aClass = canClazzMap.get(id);
                                if (aClass != null) {
                                    parseStringToCanBeanById(gson, gsonData, aClass);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSendData(String id, String gsonData) {
                            // 可以读取到上次打开时最后发送的报文，回显用
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                log("服务端：失去连接 Service");
                mService = null;
            }
        };

        log("callSwitcherService");

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.senptec.control", "com.senptec.control.ControlService");
        intent.setComponent(componentName);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    // <id, clazz>
    private static final HashMap<String, Class<? extends ByteCanBaseBean>> canClazzMap = new HashMap<>();

    static {
        canClazzMap.put(ByteD1CmdBean.D1_ID_CHANNEL_OUT_STATUS, ByteD1CanChannelStatus.class);
        canClazzMap.put(ByteD1CmdBean.D1_ID_CHANNEL_OUT_AMPERE, ByteD1CanChannelAmpere.class);
        canClazzMap.put(InputCanBean.ID, InputCanBean.class);
    }

    private static void parseStringToCanBeanById(Gson gson, String gsonData, Class<? extends ByteCanBaseBean> clazz) {
        ByteCanBaseBean canBaseBean = gson.fromJson(gsonData, ByteCanBaseBean.class);
        if (canBaseBean != null) {
            try {
                EventBus.getDefault().post(clazz.newInstance().parse(canBaseBean.toByteArray()));
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String TAG = MyApplication.class.getSimpleName();

    private static void log(String msg) {
        LoggerUtil.d(TAG + " " + msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerUtil.LOG_ON = BuildConfig.DEBUG;
        LoggerUtil.init();
        SpUtils.init("carInfo", this);
        CarSerialPortManager carSerialPortManager = CarSerialPortManager.getInstance();
        Class<? extends CarSerialPortManager> aClass = carSerialPortManager.getClass();
        try {
            Field contextField = aClass.getDeclaredField("context");
            contextField.setAccessible(true);
            contextField.set(carSerialPortManager, getApplicationContext());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        callService();
        mHandler.sendEmptyMessageDelayed(CMD, 100);
    }
}
