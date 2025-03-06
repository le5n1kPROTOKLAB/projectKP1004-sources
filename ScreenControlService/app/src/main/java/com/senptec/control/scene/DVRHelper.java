package com.senptec.control.scene;

import static com.spd.dvr.utils.DataUtil.DVR_AVMSTATE_CMD;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import com.spd.carinfo.CarInfo;
import com.spd.dvr.aidl.IDVRService;
import com.spd.dvr.aidl.IReverseInfoCallback;
import com.spd.dvr.entity.aidl.ReverseAVMInfo;
import com.spd.system.entity.aidl.DataWrapper;
import com.spd.system.entity.aidl.DeviceStatusInfo;
import com.spd.system.service.SpdManager;

/**
 * Created by spd.
 * User: hxfeng
 */
public class DVRHelper {

    private static DVRHelper mDVRHelper;
    private Context mContext;
    private IDVRService mDvrService;

    private DVRHelper(){
    }

    public static synchronized DVRHelper getInstance(){
        if(mDVRHelper == null){
            mDVRHelper = new DVRHelper();
        }
        return mDVRHelper;
    }

    public void initDVRHelper(Context context){
        mContext = context.getApplicationContext();

        Intent intent = new Intent("com.spd.service.dvrservice");
        intent.setPackage("com.spd.dvr");
        mContext.startService(intent);
        binderService();
    }

    public boolean isServiceReady(){
        if(mDvrService != null){
            return true;
        }else{
            return false;
        }
    }
    private void binderService(){
        Intent intent = new Intent("com.spd.service.dvrservice");
        intent.setPackage("com.spd.dvr");
        mContext.bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private HelperCallback mHelperCallback;

    public interface HelperCallback{
        void serviceConnected(boolean state);
    }

    public void setHelpCallback(HelperCallback callback){
        mHelperCallback = callback;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mDvrService = IDVRService.Stub.asInterface(iBinder);
            if(mHelperCallback != null){
                mHelperCallback.serviceConnected(true);
            }
            try {
                mDvrService.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        mDvrService.asBinder().unlinkToDeath(this,0);
                        mDvrService = null;
                        binderService();

                    }
                },0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

//            DVRHelper.getInstance().setAvmConfigParam(DataUtil.SETTING_AVM_2D_3D_SWITCH_MODE,1);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(mHelperCallback != null){
                mHelperCallback.serviceConnected(false);
            }
        }
    };

    public void startPreviewByChannel(int channel,Surface surface){
        if(mDvrService != null){
            try {
                mDvrService.startPreviewByChannel(channel,surface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlaybackSurface(Surface surface) {
        if(mDvrService != null){
            try {
                mDvrService.setPlaybackSurface(surface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPlayCommand(int command, int arg0, int arg1){
        if(command == DVR_AVMSTATE_CMD){

        }else{
            if(mDvrService != null){
                try {
                    mDvrService.dvrControlCmd(command,arg0,arg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void unBinderService(){
        if(mDvrService != null){
            mContext.unbindService(mServiceConnection);
            mDvrService = null;
        }

    }

    public void releaseHelper(){
        unBinderService();
    }

    private String PARA_TAG = "PARA_TAG";
    public int getCameraDeviceParam(int csi, int channel, int type){
        if(mDvrService != null){
            try {
                int value = mDvrService.getCameraDeviceParam(csi, channel, type);
                Log.d(PARA_TAG, "getCameraDeviceParam: csi="+csi+" channel="+channel+" type="+type+" value = "+value);
                return value;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void setCameraDeviceParam(int csi, int channel, int type , int value){
        if(mDvrService != null){
            try {
                Log.d(PARA_TAG, "setCameraDeviceParam: csi="+csi+" channel="+channel+" type="+type+" value = "+value);
                mDvrService.setCameraDeviceParam(csi, channel, type,value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getAvmConfigParam(String c_key)
    {
        int c_result = 0;
        if(mDvrService != null){
            try {
                c_result = mDvrService.getAvmConfigParam(c_key, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return c_result;
    }

    public int getAvmConfigParam(String c_key, int defaultValue)
    {
        int c_result = 0;
        if(mDvrService != null){
            try {
                c_result = mDvrService.getAvmConfigParam(c_key, defaultValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return c_result;
    }

    public void setAvmConfigParam(String c_key,int c_value)
    {
        if(mDvrService != null){
            try {
                mDvrService.setAvmConfigParam(c_key,c_value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerReverseInfoCallback(String pkName, IReverseInfoCallback callback)
    {
        if(mDvrService != null){
            try {
                mDvrService.registerReverseInfoCallback(pkName,callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void unregisterReverseInfoCallback(String pkName)
    {
        if(mDvrService != null){
            try {
                mDvrService.unregisterReverseInfoCallback(pkName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public ReverseAVMInfo getReverseAVMInfo()
    {
        ReverseAVMInfo c_result = null;
        if(mDvrService != null){
            try {
                c_result = mDvrService.getReverseInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return c_result;
    }

    public void startDialActivity(){
        try {
            DataWrapper dataWrapper = getSpdService().getSourceInfo("android");
            if(dataWrapper != null){
                DeviceStatusInfo deviceStatusInfo = (DeviceStatusInfo) dataWrapper.getSourceInfo();
                if(deviceStatusInfo != null){
                    if(deviceStatusInfo.callStatus != 0){
                        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
                        Intent c_intent = new Intent("com.spd.bt.TALKING");
                        c_intent.setFlags(launchFlags);
                        mContext.startActivity(c_intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SpdManager mSpdManager;

    public SpdManager getSpdService() {
        if (mSpdManager == null && mContext != null) {
            mSpdManager = SpdManager.getInstance(mContext);
        }
        return mSpdManager;
    }

    public void sendTouch(int c_x , int c_y , int c_action)
    {
        Bundle c_bundle = new Bundle();
        c_bundle.putInt(CarInfo.KEY_X,c_x);
        c_bundle.putInt(CarInfo.KEY_Y,c_y);
        c_bundle.putInt(CarInfo.KEY_ACTION,c_action);
        if(mDvrService != null){
            try {
                mDvrService.sendData(CarInfo.General.SCREEN_X_Y,c_bundle,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAutoParkingMode(int c_value)
    {
        if(mDvrService != null){
            CarInfo.Base.set(CarInfo.ReverseAndAVM.PARKING_ASSIST_SYSTEM,c_value,0);
        }
    }
}
