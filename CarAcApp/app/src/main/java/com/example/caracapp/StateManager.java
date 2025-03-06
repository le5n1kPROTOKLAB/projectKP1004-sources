package com.example.caracapp;

import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.send.b1.MyByteB1CanAcBean;

public class StateManager {
    private final String TAG = "tempAcInstance";
    private MyByteB1CanAcBean byteB1CanAcBean;

    private StateManager() {
        String tempJsonStr = SpUtils.getInstance().getString(TAG, "");
        if (tempJsonStr.isEmpty()) {
            byteB1CanAcBean = new MyByteB1CanAcBean();
        } else {
            byteB1CanAcBean = new Gson().fromJson(tempJsonStr, MyByteB1CanAcBean.class);
        }
    }

    private static final class InstanceHolder {
        static final StateManager instance = new StateManager();
    }

    public static StateManager getInstance() {
        return InstanceHolder.instance;
    }

    public void setByteB1CanAcBean(MyByteB1CanAcBean byteB1CanAcBean) {
        this.byteB1CanAcBean = byteB1CanAcBean;
    }

    public MyByteB1CanAcBean getByteB1CanAcBean() {
        return byteB1CanAcBean;
    }

    public void saveEntityAndSendCommands() {
        SpUtils.getInstance().save(TAG, new Gson().toJson(byteB1CanAcBean));
    }
}
