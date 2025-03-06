package com.senptec.control.scene;

import static android.content.Context.BIND_AUTO_CREATE;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.spd.ISpdHalService;
import android.spd.ISpdTouchCallBack;
import android.spd.TouchKeyInfo;
import android.spd.TouchResolution;
import android.util.Log;

import com.spd.setting.aidl.ISettingService;
import com.spd.setting.aidl.ISettingServiceCallback;
import com.spd.setting.entity.aidl.AccessPoint;
import com.spd.setting.util.SettingUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsHelper {
    private ISettingService mSettingsService;
    private Context m_context;
    private String m_name = "com.spd.spdsettings";
    private String TAG ="SettingsHelper";

    private static SettingsHelper m_helper;
    private boolean m_bind_flag = false;
    private boolean m_bind_request_flag = false;
    public static SettingsHelper get()
    {
        if (m_helper == null)
        {
            m_helper = new SettingsHelper();
        }
        return m_helper;
    }
    private boolean m_temp_service_init_flag = false;
    public void initSettingsHelper(Context context)
    {
        if (!m_temp_service_init_flag)
        {
            m_context = context;
            m_temp_service_init_flag = true;
            Intent intent = new Intent("com.spd.setting.service.SettingService");
            intent.setPackage("com.spd.setting");
            m_context.startService(intent);
        }

    }

    public boolean regsiterCallBack(SettingsHelperCallBack c_callback)
    {
        Log.d(TAG, "regsiterCallBack: "+c_callback+" call_back.size"+ m_dvr_ui_call_back.size());
        boolean c_result = false;
        if (m_dvr_ui_call_back.indexOf(c_callback) < 0)
        {
            m_dvr_ui_call_back.add(0,c_callback);
            c_result = true;
        }
        bindService(c_callback);
        return c_result;
    }

    public boolean unregsiterCallBack(SettingsHelperCallBack c_callback)
    {
        Log.d(TAG, "unregsiterCallBack: "+c_callback+" call_back.size"+ m_dvr_ui_call_back.size());
        boolean c_result = false;
        if (m_dvr_ui_call_back.indexOf(c_callback) >= 0)
        {
            m_dvr_ui_call_back.remove(c_callback);
            c_result = true;
        }
        if (m_dvr_ui_call_back.size() == 0)
        {
            unbindService();
        }
        return c_result;
    }
    public void exitSource(String pkName) {
        Log.d(TAG, "exitSource: "+pkName);
        if(m_bind_flag && mSettingsService != null){
            try {
                mSettingsService.exitSource(pkName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private String m_request_enter_source_package_name = null;
    public boolean enterSource(String c_app_name)
    {
        Log.d(TAG, "enterSource: "+c_app_name);
        if (m_bind_flag && mSettingsService != null)
        {
            try {
                Log.d(TAG, "enterSource: real");
                mSettingsService.enterSource(c_app_name);
            } catch (RemoteException e) {
                m_bind_flag = false;
                m_bind_request_flag = false;
                e.printStackTrace();
            }
        }
        else
        {
            m_request_enter_source_package_name = c_app_name;
        }
        return m_bind_flag;
    }

    private void bindService(SettingsHelperCallBack c_callback)
    {
        if(m_dvr_ui_call_back.size() >0)
        {
            if (!m_bind_flag)
            {
                if (!m_bind_request_flag)
                {
                    m_bind_request_flag = true;
                    Intent intent = new Intent("com.spd.setting.service.SettingService");
                    intent.setPackage("com.spd.setting");
                    Log.d(TAG, "bindService: ");
                    m_context.bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
                }
            }
            else
            {
                if (!m_call_back_handler.hasMessages(CALL_BACK_EVENT_SERVICE_CONNECTED))
                {
                    Message c_msg = new Message();
                    c_msg.what = CALL_BACK_EVENT_SERVICE_CONNECTED;
                    c_msg.obj = c_callback;
                    m_call_back_handler.sendMessage(c_msg);
                }
            }
        }
    }
    private void unbindService()
    {
        Log.d(TAG,"unbindService:");
        if (m_bind_flag)
        {
            if (mSettingsService != null)
            {
                try {
                    //checkBrowserList(false);
                    mSettingsService.unRegisterSettingCallback(m_name);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                m_context.unbindService(mServiceConnection);
                mSettingsService = null;
            }
            m_bind_flag = false;
            m_bind_request_flag = false;
        }
    }
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected:");
            mSettingsService = ISettingService .Stub.asInterface(service);
            String thread = Thread.currentThread().getName();
            Log.d(TAG,"Thread name1:"+thread);
            try {
                mSettingsService.asBinder().linkToDeath(mDeath,0);
                Log.d(TAG, "onServiceConnected: registerCallback");
                mSettingsService.registerSettingServiceCallback(m_name,mCallback);
                m_bind_flag = true;
                if (m_request_enter_source_package_name != null)
                {
                    mSettingsService.enterSource(m_request_enter_source_package_name);
                    m_request_enter_source_package_name = null;
                }
                m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_CONNECTED);
            } catch (RemoteException e) {
                Log.d(TAG,"ERROR:"+e.toString());
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
            m_bind_flag = false;
            m_bind_request_flag = false;
            m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_DISCONNECTED);
            bindService(null);
        }
    };
    private IBinder.DeathRecipient mDeath = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG,"service binderDied");
            m_bind_flag = false;
            m_bind_request_flag = false;
            if (mSettingsService != null) {
                mSettingsService.asBinder().unlinkToDeath(this, 0);
                mSettingsService = null;
            }
            m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_DISCONNECTED);
            bindService(null);
        }
    };

    public ISettingServiceCallback mCallback = new ISettingServiceCallback.Stub() {
        @Override
        public void onIntChanged(int i, int i1) throws RemoteException {
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_INT_VALUE_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onFloatChanged(int i, float v) throws RemoteException {
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_FLOAT_VALUE_CHANGED;
            c_msg.arg1 = i;
            c_msg.obj = v;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onStringChanged(int i, String s) throws RemoteException {
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_STRING_VALUE_CHANGED;
            c_msg.arg1 = i;
            c_msg.obj = s;
            m_call_back_handler.sendMessage(c_msg);
        }
    };

    private static final int CALL_BACK_EVENT_SERVICE_CONNECTED = 101;
    private static final int CALL_BACK_EVENT_SERVICE_DISCONNECTED = 102;
    private static final int CALL_BACK_EVENT_INT_VALUE_CHANGED = 110;
    private static final int CALL_BACK_EVENT_FLOAT_VALUE_CHANGED = 111;
    private static final int CALL_BACK_EVENT_STRING_VALUE_CHANGED = 112;

    private Handler m_call_back_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"handleMessage: =0=");
            if (m_dvr_ui_call_back != null)
            {
                //Log.d(TAG,"handleMessage: =1="+msg.what);
                switch (msg.what)
                {
                    case CALL_BACK_EVENT_SERVICE_CONNECTED:
                        if (msg.obj != null)
                        {
                            SettingsHelperCallBack c_call_back = (SettingsHelperCallBack)msg.obj;
                            c_call_back.onServiceConnected();
                        }
                        else
                        {
                            for (SettingsHelperCallBack c_call_back : m_dvr_ui_call_back)
                            {
                                c_call_back.onServiceConnected();
                            }
                        }
                        break;
                    case CALL_BACK_EVENT_SERVICE_DISCONNECTED:
                        for (SettingsHelperCallBack c_call_back : m_dvr_ui_call_back)
                        {
                            c_call_back.onServiceDisconnected();
                        }
                        break;
                    case CALL_BACK_EVENT_INT_VALUE_CHANGED:
                        for (SettingsHelperCallBack c_call_back : m_dvr_ui_call_back)
                        {
                            c_call_back.onIntChanged(msg.arg1,msg.arg2);
                        }
                        break;
                    case CALL_BACK_EVENT_FLOAT_VALUE_CHANGED:
                        for (SettingsHelperCallBack c_call_back : m_dvr_ui_call_back)
                        {
                            c_call_back.onFloatChanged(msg.arg1,(float)msg.obj);
                        }
                        break;
                    case CALL_BACK_EVENT_STRING_VALUE_CHANGED:
                        for (SettingsHelperCallBack c_call_back : m_dvr_ui_call_back)
                        {
                            c_call_back.onStringChanged(msg.arg1,(String)msg.obj);
                        }
                        break;

                    default:
                }
            }
        }
    };
    public static final int DVR_LIST_MASK_ALL = 0;
    public static final int DVR_LIST_MASK_FRONT = 1 << 16;
    public static final int DVR_LIST_TYPE_VIDEO = 0;
    public static final int DVR_LIST_TYPE_PHOTO = 1;

    private List<SettingsHelperCallBack> m_dvr_ui_call_back = new ArrayList<>();


    public interface SettingsHelperCallBack
    {
        void onServiceConnected();
        void onServiceDisconnected();
        void onIntChanged(int i, int i1);
        void onFloatChanged(int i, float v);
        void onStringChanged(int i, String s);
    }

    public void putIntValue(int type , int c_value)
    {
        if (mSettingsService != null)
        {
            try {
                mSettingsService.putInt(type,c_value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getIntValue(int type , int c_default_value)
    {
        int c_result = c_default_value;
        if (mSettingsService != null)
        {
            try {
                c_result = mSettingsService.getInt(type,c_default_value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return c_result;
    }

    public void putFloatValue(int type , float c_value)
    {
        if (mSettingsService != null)
        {
            try {
                mSettingsService.putFloat(type,c_value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public float getFloatValue(int type , float c_default_value)
    {
        float c_result = c_default_value;
        if (mSettingsService != null)
        {
            if (mSettingsService != null)
            {
                try {
                    c_result = mSettingsService.getFloat(type,c_default_value);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return c_result;
    }

    public void putStringValue(int type , String c_value)
    {
        if (mSettingsService != null)
        {
            try {
                mSettingsService.putString(type,c_value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringValue(int type , String c_default_value)
    {
        String c_result = c_default_value;
        if (mSettingsService != null)
        {
            if (mSettingsService != null)
            {
                try {
                    c_result = mSettingsService.getString(type,c_default_value);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return c_result;
    }

    public List<AccessPoint> getAccessPoints() {
        if(mSettingsService != null){
            try {
                return mSettingsService.getAccessPoints();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void connectWifi(AccessPoint point) {
        Log.d(TAG, "connectWifi: "+point);
        if(mSettingsService != null){
            try {
                mSettingsService.connectWifi(point);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteWifi(int networkId) {
        Log.d(TAG, "deleteWifi: "+networkId);
        if(mSettingsService != null){
            try {
                mSettingsService.deleteWifi(networkId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public List<BluetoothDevice> getConnectedDevices() {
        if(mSettingsService != null){
            try {
                return mSettingsService.getConnectedDevices();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<BluetoothDevice>();
    }

    public List<BluetoothDevice> getBondedDevices() {
        if(mSettingsService != null){
            try {
                return mSettingsService.getBondedDevices();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<BluetoothDevice>();
    }

    public List<BluetoothDevice> getAvailableDevices() {
        if(mSettingsService != null){
            try {
                return mSettingsService.getAvailableDevices();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<BluetoothDevice>();
    }


    public void setBluetoothName(String name) {
        Log.d(TAG, "setBluetoothName: "+name);
        if(mSettingsService != null){
            try {
                mSettingsService.putString(SettingUtils.Bluetooth.BLUETOOTH_NAME,name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBluetoothAutoAnswer(int timeType) {
        Log.d(TAG, "setBluetoothAutoAnswer: "+timeType);
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.Bluetooth.BLUETOOTH_AUTO_ANSWER,timeType);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void startBluetoothDiscovery() {
        Log.d(TAG, "startBluetoothDiscovery: ");
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.Bluetooth.BLUETOOTH_START_DISCOVERY,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelBluetoothDiscovery() {
        Log.d(TAG, "cancelBluetoothDiscovery: ");
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.Bluetooth.BLUETOOTH_CANCEL_DISCOVERY,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    public void setLocalApName(String name) {
        Log.d(TAG, "setLocalApName: "+name);
        if(mSettingsService != null){
            try {
                mSettingsService.putString(SettingUtils.Network.WIFI_AP_NAME,name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocalApPasswd(String passwd) {
        Log.d(TAG, "setLocalApPasswd: "+passwd);
        if(mSettingsService != null){
            try {
                mSettingsService.putString(SettingUtils.Network.WIFI_AP_PASSWORD,passwd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getLocaleLanguages() {
        if(mSettingsService != null){
            try {
                return mSettingsService.getLocaleLanguages();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return new String[0];
    }

    public boolean setSystemLanguage(int languageType) {
        Log.d(TAG, "setSystemLanguage: "+languageType);
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.General.SYSTEM_LANGUAGE,languageType);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean setNaviApp(String appPackageName) {
        Log.d(TAG, "setNaviApp: "+appPackageName);
        if(mSettingsService != null){
            try {
                mSettingsService.putString(SettingUtils.General.NAVI_APP_PACKAGE_NAME,appPackageName);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getVoiceApp() {
        String appPackageName = getStringValue(SettingUtils.General.VOICE_APP_PACKAGE_NAME, "");
        Log.d(TAG, "getVoiceApp: " + appPackageName);
        return appPackageName;
    }

    public void setVoiceApp(String appPackageName) {
        Log.d(TAG, "setVoiceApp: " + appPackageName);
        putStringValue(SettingUtils.General.VOICE_APP_PACKAGE_NAME, appPackageName);
    }

    public void resetDefault(String passwd) {
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.General.ID,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void recoveryFactorySetting() {
        if(mSettingsService != null){
            try {
                mSettingsService.putInt(SettingUtils.General.RESET_FACTORY,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectBluetooth(BluetoothDevice device) {
        if(mSettingsService != null){
            try {
                mSettingsService.disconnectBluetooth(device);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void connectBluetooth(BluetoothDevice device) {
        if(mSettingsService != null){
            try {
                mSettingsService.connectBluetooth(device);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeBond(BluetoothDevice device) {
        if(mSettingsService != null){
            try {
                mSettingsService.removeBond(device);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void installApkSilently(String pathName) {
        if(mSettingsService != null){
            try {
                mSettingsService.installApkSilently(pathName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ISpdHalService mSpdHalService;
    public ISpdHalService getHalService(){
        if(mSpdHalService == null){
            IBinder binder = ServiceManager.getService("spd.Hal");
            mSpdHalService = ISpdHalService.Stub.asInterface(binder);
            if(mSpdHalService == null){
                Log.i("key","mSpdHalService is null");
            }
        }
        return mSpdHalService;
    }

    public void setHalCallback(ISpdTouchCallBack callback){
        try {
            getHalService().SetTouchCallBack(callback);
            Log.i("key","setHalCallback");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startStudyTouchKey(){
        try {
            getHalService().StartStudyTouchKey();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopStudyTouchKey(){
        try {
            getHalService().StopStudyTouchKey();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setTouchKeyInfo(TouchKeyInfo info){
        try {
            getHalService().SetTouchKeyInfo(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getTouchScreen(TouchResolution resolution){
        try {
            mSpdHalService.GetTouchResolution(resolution);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

