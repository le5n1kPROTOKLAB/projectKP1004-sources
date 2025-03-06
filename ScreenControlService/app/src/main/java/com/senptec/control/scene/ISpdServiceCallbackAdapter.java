package com.senptec.control.scene;

import com.spd.system.aidl.ISpdServiceCallback;

import java.util.Map;

public class ISpdServiceCallbackAdapter extends ISpdServiceCallback.Stub {
    @Override
    public void onServiceConnect(boolean b) {

    }

    @Override
    public void onParkingStatusChange(int i) {

    }

    @Override
    public void onPowerStatusChange(int i) {

    }

    @Override
    public void onBluetoothStatusChange(int i) {

    }

    @Override
    public void onCallStatusChange(int i, int i1) {

    }

    @Override
    public void onUsbDeviceStatusChange(int i, int i1) {

    }

    @Override
    public void onLightStatusChange(int i) {

    }

    @Override
    public void onReversingStatusChange(int i) {

    }

    @Override
    public void onTurnStatusChange(int i) {

    }

    @Override
    public void onAuthenticateStatusChange(int i) {

    }

    @Override
    public void onAudioFocusChanage(int i, int i1) {

    }

    @Override
    public void onKeyLearn(Map map) {

    }

    @Override
    public void sendCommonToSouce(int i, int i1, int i2) {

    }

    @Override
    public int sendKeyCode(int i, int i1, int i2) {
        return 0;
    }
}
