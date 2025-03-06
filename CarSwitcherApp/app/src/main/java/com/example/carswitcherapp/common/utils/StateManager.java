package com.example.carswitcherapp.common.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.senptec.adapter.SpUtils;
import com.senptec.adapter.bean.send.b1.CanSwitcher0218Bean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0318Bean;

public class StateManager {
    private final String CANSWITCHER0318_TAG = "CanSwitcher0318";
    private final String CANSWITCHER0218_TAG = "CanSwitcher0218";
    private static StateManager instance;
    private CanSwitcher0318Bean mCanSwitcher0318Bean;
    private CanSwitcher0218Bean mCanSwitcher0218Bean;

    private StateManager() {
        String canSwitcher0318Str = SpUtils.getInstance().getString(CANSWITCHER0318_TAG, "");
        if (TextUtils.isEmpty(canSwitcher0318Str)) {
            mCanSwitcher0318Bean = new CanSwitcher0318Bean();
        } else {
            mCanSwitcher0318Bean = new Gson().fromJson(canSwitcher0318Str, CanSwitcher0318Bean.class);
        }

        String canSwitcher0218Str = SpUtils.getInstance().getString(CANSWITCHER0218_TAG, "");
        if (TextUtils.isEmpty(canSwitcher0218Str)) {
            mCanSwitcher0218Bean = new CanSwitcher0218Bean();
        } else {
            mCanSwitcher0218Bean = new Gson().fromJson(canSwitcher0218Str, CanSwitcher0218Bean.class);
        }
    }

    public static StateManager getInstance() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }
        return instance;
    }

    public void setCanSwitcher0318Bean(CanSwitcher0318Bean canSwitcher0318Bean) {
        this.mCanSwitcher0318Bean = canSwitcher0318Bean;
    }

    public CanSwitcher0318Bean getCanSwitcher0318Bean() {
        return mCanSwitcher0318Bean;
    }

    public void setCanSwitcher0218Bean(CanSwitcher0218Bean canSwitcher0218Bean) {
        this.mCanSwitcher0218Bean = canSwitcher0218Bean;
    }

    public CanSwitcher0218Bean getCanSwitcher0218Bean() {
        return mCanSwitcher0218Bean;
    }

    public void saveEntityAndSendCommands() {
        SpUtils.getInstance().save(CANSWITCHER0318_TAG, new Gson().toJson(mCanSwitcher0318Bean));
        SpUtils.getInstance().save(CANSWITCHER0218_TAG, new Gson().toJson(mCanSwitcher0218Bean));
    }

}
