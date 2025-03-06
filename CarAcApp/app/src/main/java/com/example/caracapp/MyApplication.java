package com.example.caracapp;

import android.app.Application;

import com.google.gson.Gson;
import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.LoggerUtil;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.receive.d1.MyByteD1CanAcBean;
import com.senptec.adapter.bean.send.b1.MyByteB1CanAcBean;
import com.spentec.server_client.ControlServerHelper;
import com.spentec.server_client.callback.IOnDataListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MyApplication extends Application implements IOnDataListener {
    private static MyApplication instance;
    private ControlServerHelper mControlServerHelper;
    public Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        instance= this;
        LoggerUtil.LOG_ON = BuildConfig.DEBUG;
        LoggerUtil.init();
        SpUtils.init("carAc", this);
        mControlServerHelper = new ControlServerHelper();
        mControlServerHelper.addOnReceiveDataListener(this);
        mControlServerHelper.init(this, getPackageName());
    }

    private static int receiveCount = 0;

    public void sendData(String canId, String json) {
        mControlServerHelper.sendData(canId, json);
    }

    @Override
    public void onReceive(String id, String gsonData) {

        /*if ("10FFC2AE".equals(id)){
            EventBus.getDefault().post(new DataReceivedEvent());
        }*/

        /*receiveCount++;
        EventBus.getDefault().post(new CanIdReceivedEvent(gsonData));
        EventBus.getDefault().post(new ReceiveCountEvent(receiveCount));*/


        //  接收报文
        try {
            // 串口Can数据
            Class<? extends ByteCanBaseBean> aClass = canClazzMap.get(id);
            if (aClass != null) {
                parseStringToCanBeanById(gson, gsonData, aClass);
            }
        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    @Override
    public void onSend(String id, String gsonData) {
    }

    private static final HashMap<String, Class<? extends ByteCanBaseBean>> canClazzMap = new HashMap<>();

    static {
        canClazzMap.put(MyByteD1CanAcBean.ID, MyByteD1CanAcBean.class);
    }

    private static void parseStringToCanBeanById(Gson gson, String gsonData, Class<? extends ByteCanBaseBean> clazz) {
        ByteCanBaseBean canBaseBean = gson.fromJson(gsonData, ByteCanBaseBean.class);
        if (canBaseBean != null) {
            try {
                EventBus.getDefault().post(clazz.newInstance().parse(canBaseBean.toByteArray()));
            } catch (IllegalAccessException | InstantiationException e) {
                log(e.getMessage());
            }
        }
    }

    private static final String TAG = MyApplication.class.getSimpleName();

    private static void log(String msg) {
        LoggerUtil.d(TAG + " " + msg);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
