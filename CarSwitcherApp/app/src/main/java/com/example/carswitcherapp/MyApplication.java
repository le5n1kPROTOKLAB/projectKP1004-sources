package com.example.carswitcherapp;

import android.app.Application;

import com.example.carswitcherapp.common.utils.CanServerHelper;
import com.example.carswitcherapp.common.utils.StateManager;
import com.google.gson.Gson;
import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.LoggerUtil;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.send.b1.CanSwitcher0218Bean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0318Bean;
import com.spentec.server_client.callback.impl.OnDataAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

public class MyApplication extends Application {
    private Timer mTimer0318;
    private TimerTask mTimerTask0318;
    private Timer mTimer0218;
    private TimerTask mTimerTask0218;

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerUtil.LOG_ON = BuildConfig.DEBUG;
        LoggerUtil.init();
        SpUtils.init("carSwitcher", this);
        //开启can服务
        CanServerHelper.getCanServerHelper().init(this);
        CanServerHelper.getCanServerHelper().addOnReceiveDataListener(new OnDataAdapter() {
            @Override
            public void onSend(String id, String gsonData) {
                super.onSend(id, gsonData);
                try {
                    // 可以读取到上次打开时最后发送的报文，回显用
                    if (CanSwitcher0318Bean.ID.equals(id)) {
                        byte[] bytes = new Gson().fromJson(gsonData, byte[].class);
                        LoggerUtil.e("====CanSwitcher0318==onSend==" + HexByteStrUtils.getStringFromBytes(bytes));
                        EventBus.getDefault().post(new CanSwitcher0318Bean().parse(bytes));
                    }
                    if (CanSwitcher0218Bean.ID.equals(id)) {
                        byte[] bytes = new Gson().fromJson(gsonData, byte[].class);
                        LoggerUtil.e("====CanSwitcher0218==onSend==" + HexByteStrUtils.getStringFromBytes(bytes));
                        EventBus.getDefault().post(new CanSwitcher0218Bean().parse(bytes));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (mTimer0218 == null) {
            mTimer0218 = new Timer();
        }
        if (mTimerTask0218 == null) {
            mTimerTask0218 = new TimerTask() {

                @Override
                public void run() {
                    CanSwitcher0218Bean canSwitcher0218Bean = StateManager.getInstance().getCanSwitcher0218Bean();
                    LoggerUtil.e("====CanSwitcher0218==sendData==" + HexByteStrUtils.getStringFromBytes(canSwitcher0218Bean.toByteArray()));
                    CanServerHelper.getCanServerHelper().sendData(canSwitcher0218Bean.getId(), new Gson().toJson(canSwitcher0218Bean.toByteArray()));
                }
            };
        }
        if (mTimer0218 != null && mTimerTask0218 != null) {
            mTimer0218.schedule(mTimerTask0218, 0, 500);
        }

        if (mTimer0318 == null) {
            mTimer0318 = new Timer();
        }
        if (mTimerTask0318 == null) {
            mTimerTask0318 = new TimerTask() {

                @Override
                public void run() {
                    CanSwitcher0318Bean canSwitcher0318Bean = StateManager.getInstance().getCanSwitcher0318Bean();
                    LoggerUtil.e("====CanSwitcher0318==sendData==" + HexByteStrUtils.getStringFromBytes(canSwitcher0318Bean.toByteArray()));
                    CanServerHelper.getCanServerHelper().sendData(canSwitcher0318Bean.getId(), new Gson().toJson(canSwitcher0318Bean.toByteArray()));
                }
            };
        }
        if (mTimer0318 != null && mTimerTask0318 != null) {
            mTimer0318.schedule(mTimerTask0318, 0, 500);
        }
    }

    /**
     * 定时器关闭
     */
    public void close() {
        if (mTimer0318 != null) {
            mTimer0318.cancel();
            mTimer0318 = null;
        }
        if (mTimerTask0318 != null) {
            mTimerTask0318.cancel();
            mTimerTask0318 = null;
        }
        if (mTimer0218 != null) {
            mTimer0218.cancel();
            mTimer0218 = null;
        }
        if (mTimerTask0218 != null) {
            mTimerTask0218.cancel();
            mTimerTask0218 = null;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            CanServerHelper.getCanServerHelper().closeCanServer();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
