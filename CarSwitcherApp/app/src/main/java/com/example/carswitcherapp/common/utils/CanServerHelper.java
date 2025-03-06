package com.example.carswitcherapp.common.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.spentec.server_client.ControlServerHelper;
import com.spentec.server_client.callback.IOnDataListener;

/**
 * 描述：can通信相关
 * 作者：hxl 2024/1/17 8:54
 * 修改描述：
 * 修改人：xxx 2024/1/17 8:54
 * 修改版本：
 */
public class CanServerHelper {

    private final ControlServerHelper mControlServerHelper;

    private volatile static CanServerHelper mSingleton;

    public CanServerHelper() {
        mControlServerHelper = new ControlServerHelper();
    }

    public static CanServerHelper getCanServerHelper() {
        if (mSingleton == null) {
            synchronized (CanServerHelper.class) {
                if (mSingleton == null) {
                    mSingleton = new CanServerHelper();
                }
            }
        }
        return mSingleton;
    }

    public void init(@NonNull Context context) {
        String LIB_TAG = context.getPackageName();
        mControlServerHelper.init(context, LIB_TAG);
    }

    public void sendData(String canId, String json) {
        mControlServerHelper.sendData(canId, json);
    }

    public void addOnReceiveDataListener(IOnDataListener listener) {
        mControlServerHelper.addOnReceiveDataListener(listener);
    }

    public void closeCanServer() {
        mControlServerHelper.release();
    }
}
