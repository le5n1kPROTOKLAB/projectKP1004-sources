package com.spd.custom.view;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;


import com.spd.media.aidl.IMediaCallback;
import com.spd.media.aidl.IMediaService;
import com.spd.media.entity.aidl.DeviceListItem;
import com.spd.media.entity.aidl.DevicesList;
import com.spd.media.entity.aidl.MediaListItem;
import com.spd.media.entity.aidl.MediasList;
import com.spd.media.entity.aidl.NowPlaying;
import com.spd.media.util.MediaUtilDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MediaHelper {
    private IMediaService mMediaService;
    private Context m_context;
    private String m_name = "com.spd.home";
    private String TAG ="MediaHelper";
    private String TAG_SER_CALL_BACK ="SerCallBack";

    private static MediaHelper m_helper;
    private boolean m_bind_flag = false;
    private boolean m_bind_request_flag = false;
    public static MediaHelper get()
    {
        if (m_helper == null)
        {
            m_helper = new MediaHelper();
        }

        return m_helper;
    }
    private boolean m_temp_service_init_flag = false;
    public void initMediaHelper (Context context)
    {
        if (!m_temp_service_init_flag)
        {
            m_context = context;
            m_temp_service_init_flag = true;
            Intent intent = new Intent("android.spd.IMediaService");
            intent.setPackage("com.spd.media");
            m_context.startService(intent);
        }

    }

    public boolean regsiterCallBack(MediaHelperCallBack c_callback)
    {
        Log.d(TAG, "regsiterCallBack: "+c_callback+" call_back.size"+ m_media_ui_call_back.size());
        boolean c_result = false;
        if (m_media_ui_call_back.indexOf(c_callback) < 0)
        {
            m_media_ui_call_back.add(0,c_callback);
            c_result = true;
        }
        bindService(c_callback);
        return c_result;
    }

    public boolean unregsiterCallBack(MediaHelperCallBack c_callback)
    {
        Log.d(TAG, "unregsiterCallBack: "+c_callback+" call_back.size"+ m_media_ui_call_back.size());
        boolean c_result = false;
        if (m_media_ui_call_back.indexOf(c_callback) >= 0)
        {
            m_media_ui_call_back.remove(c_callback);
            c_result = true;
        }
        if (m_media_ui_call_back.size() == 0)
        {
            unbindService();
        }
        return c_result;
    }
    public static final int MEDIA_TABLE_ALL = 0xffffffff;
    public static final int MEDIA_PHOTO_AND_VIDEO = 0xFFFF80FF;

    public static final int MEDIA_TABLE_USB0 = 0xffff0000 | MediaUtilDef.MEDAI_DEVICE_MASK_USB0;
    public static final int MEDIA_TABLE_USB1 = 0xffff0000 | MediaUtilDef.MEDIA_DEVICE_MASK_USB1;
    public static final int MEDIA_TABLE_SD = 0xffff0000 | MediaUtilDef.MEDIA_DEVICE_MASK_SD;
    public static final int MEDIA_TABLE_DISC = 0xffff0008 | MediaUtilDef.MEDIA_DEVICE_MASK_DISC;
    public static final int MEDIA_TABLE_FAVOURITE = 0xffffffff;
    public static final int MEDIA_TABLE_LOCAL = 0xffff0000 | MediaUtilDef.MEDIA_DEVICE_MASK_LOCAL;
    public static final int MEDIA_TABLE_BT = 0xffff0016;

    public void enterSource(String c_app_name , int c_device_id ,int list_type)
    {
        Log.d("TEST010", "enterSource: "+c_device_id+" "+list_type);
        if (m_bind_flag)
        {
            try {
                Log.d("TEST011", "enterSource: ");
                mMediaService.enterSource(c_app_name,c_device_id,list_type,1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void bindService(MediaHelperCallBack c_callback)
    {
        Log.d("TEST005","bindService: "+c_callback+ " "+ m_bind_flag +" "+m_bind_request_flag);
        if (m_media_ui_call_back.size()>0)
        {
            if (!m_bind_flag)
            {
                if (!m_bind_request_flag)
                {
                    ComponentName componentName = new ComponentName("com.spd.media","com.spd.media.service.MediaService");
                    try {
                        ServiceInfo serviceInfo = m_context.getPackageManager().getServiceInfo(componentName, 0);
                        if (serviceInfo != null)
                        {
                            m_bind_request_flag = true;
                            Intent intent = new Intent("android.spd.IMediaService");
                            intent.setPackage("com.spd.media");
                            Log.d(TAG, "bindService: ");
                            m_context.bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
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
            if (mMediaService != null)
            {
                try {
                    checkBrowserList(false);
                    mMediaService.unregisterCallback(m_name);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                m_context.unbindService(mServiceConnection);
                mMediaService = null;
            }
            m_bind_flag = false;
            m_bind_request_flag = false;
        }
    }
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected:");
            mMediaService = IMediaService.Stub.asInterface(service);
            String thread = Thread.currentThread().getName();
            Log.d(TAG,"Thread name1:"+thread);
            try {
                mMediaService.asBinder().linkToDeath(mDeath,0);
                Log.d("TEST011", "onServiceConnected: registerCallback");
                mMediaService.registerCallback(m_name,mCallback);
                m_bind_flag = true;
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
            if (mMediaService != null) {
                mMediaService.asBinder().unlinkToDeath(this, 0);
                mMediaService = null;
            }
            m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_DISCONNECTED);
            bindService(null);
        }
    };

    public IMediaCallback mCallback = new IMediaCallback.Stub() {
        @Override
        public void onFavoritesProcess(String s, int i, int i1) throws RemoteException {
            Log.d("TEST005", "onFavoritesProcess: ");
        }

        @Override
        public void onDevicesChanged(int i, DeviceListItem deviceListItem){
            Log.d("TEST005", "onDevicesChanged: "+i+" "+deviceListItem);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_DEVICE_CHANGED;
            c_msg.arg1 = i;
            c_msg.obj = deviceListItem;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onPlayStatusChanged(int i, int i1){
            Log.d("TEST005", "onPlayStatusChanged: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAY_STATUS_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onPlayTimeChanged(int i, int i1){
            //Log.d("TEST005", "onPlayTimeChanged: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAY_TIME_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onPlayModeChanged(int i, int i1){
            Log.d("TEST005", "onPlayModeChanged: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAY_MODE_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onNowPlayingChanged(NowPlaying nowPlaying){
            Log.d("TEST005", "onNowPlayingChanged: "+nowPlaying);
            Log.d("TEST011", "onNowPlayingChanged: "+nowPlaying);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_NOW_PLAYING_CHANGED;
            c_msg.obj = nowPlaying;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onPlayerInfoChanged(int i, int i1){
            Log.d("TEST005", "onPlayerInfoChanged: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAY_INFO_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onBrowserListChanged(int i, int i1){
            Log.d("TEST005", "onBrowserListChanged: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_BROWSER_LIST_CHANGED;
            c_msg.arg1 = i;
            c_msg.arg1 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }


        @Override
        public void onPlayingListChanged(int i){
            Log.d("TEST005", "onPlayingListChanged: "+i);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAYING_LIST_CHANGED;
            c_msg.arg1 = i;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void sendCommonControl(int i, int i1){
            Log.d("TEST005", "sendCommonControl: "+i+" "+i1);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_COMMON;
            c_msg.arg1 = i;
            c_msg.arg2 = i1;
            m_call_back_handler.sendMessage(c_msg);
        }
    };

    private static final int CALL_BACK_EVENT_SERVICE_CONNECTED = 101;
    private static final int CALL_BACK_EVENT_SERVICE_DISCONNECTED = 102;
    private static final int CALL_BACK_EVENT_DEVICE_CHANGED = 103;
    private static final int CALL_BACK_EVENT_PLAY_STATUS_CHANGED = 104;
    private static final int CALL_BACK_EVENT_PLAY_TIME_CHANGED = 105;
    private static final int CALL_BACK_EVENT_PLAY_MODE_CHANGED = 106;
    private static final int CALL_BACK_EVENT_NOW_PLAYING_CHANGED = 107;
    private static final int CALL_BACK_EVENT_PLAY_INFO_CHANGED = 108;
    private static final int CALL_BACK_EVENT_BROWSER_LIST_CHANGED = 109;
    private static final int CALL_BACK_EVENT_PLAYING_LIST_CHANGED = 110;
    private static final int CALL_BACK_EVENT_FAVOURITE_LIST_CHANGED = 111;
    private static final int CALL_BACK_EVENT_COMMON = 112;
    @SuppressLint("HandlerLeak")
    private Handler m_call_back_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"handleMessage: =0=");
            if (m_media_ui_call_back != null)
            {
                //Log.d(TAG,"handleMessage: =1="+msg.what);
                switch (msg.what)
                {
                    case CALL_BACK_EVENT_SERVICE_CONNECTED:
                        if (mMediaService != null) {
                            Log.d(TAG, "handleMessage: CALL_BACK_EVENT_SERVICE_CONNECTED" + msg.obj);
                            try {
                                mNowPlayingList.clear();
                                DevicesList c_device_list = mMediaService.getDeviceStatus();
                                m_now_playing_list_count = 0;
                                if (msg.obj != null) {
                                    MediaHelperCallBack c_target_call_back = (MediaHelperCallBack) msg.obj;
                                    Log.d(TAG, "handleMessage: onMediaServiceConnected ===0===");
                                    c_target_call_back.onMediaDevicesListChanged(c_device_list);
                                    if (c_device_list.devices != null) {
                                        for (int c_device_index = 0; c_device_index < c_device_list.devices.length; c_device_index++) {
                                            DeviceListItem c_child_device = c_device_list.devices[c_device_index];
                                            if (c_child_device != null) {
                                                c_target_call_back.onMediaDevicesChanged(c_device_index, c_child_device);
                                            }
                                        }
                                    }
                                    c_target_call_back.onMediaNowPlayingChanged(null);
                                    Log.d("TEST005", "onMediaPlayingListChanged: ==0==");
                                    c_target_call_back.onMediaPlayingListChanged(m_now_playing_list_count);
                                    c_target_call_back.onMediaServiceConnected();


                                } else {
                                    for (int c_index = 0; c_index < m_media_ui_call_back.size(); c_index++) {
                                        MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                        c_call_back.onMediaDevicesListChanged(c_device_list);
                                    }
                                    if (c_device_list.devices != null) {
                                        for (int c_device_index = 0; c_device_index < c_device_list.devices.length; c_device_index++) {
                                            DeviceListItem c_child_device = c_device_list.devices[c_device_index];
                                            if (c_child_device != null) {
                                                for (int c_index = 0; c_index < m_media_ui_call_back.size(); c_index++) {
                                                    MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                                    c_call_back.onMediaDevicesChanged(c_device_index, c_child_device);
                                                }
                                            }
                                        }
                                    }
                                    for (int c_index = 0; c_index < m_media_ui_call_back.size(); c_index++) {
                                        MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                        c_call_back.onMediaNowPlayingChanged(null);
                                    }
                                    for (int c_index = 0; c_index < m_media_ui_call_back.size(); c_index++) {
                                        MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                        Log.d("TEST005", "onMediaPlayingListChanged: ==1==");
                                        c_call_back.onMediaPlayingListChanged(m_now_playing_list_count);
                                    }
                                    for (int c_index = 0; c_index < m_media_ui_call_back.size(); c_index++) {
                                        MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                        Log.d(TAG, "handleMessage: onMediaServiceConnected ===1===");
                                        c_call_back.onMediaServiceConnected();
                                    }

                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            checkBrowserList(true);
                        }
                        break;
                    case CALL_BACK_EVENT_SERVICE_DISCONNECTED:
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaServiceDisconnected();
                        }
                        checkBrowserList(false);
                        break;
                    case CALL_BACK_EVENT_DEVICE_CHANGED:
                        int device = msg.arg1;
                        DeviceListItem deviceItem = (DeviceListItem)msg.obj;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaDevicesChanged(device,deviceItem);
                        }
                    break;
                    case CALL_BACK_EVENT_PLAY_STATUS_CHANGED:
                        int status = msg.arg1;
                        int speed = msg.arg2;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaPlayStatusChanged(status,speed);
                        }
                        if (status<1)
                        {
                            for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                            {
                                MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                c_call_back.onMediaNowPlayingChanged(null);
                            }
                            m_now_playing_list_count = 0;
                            mNowPlayingList.clear();
                            for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                            {
                                MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                                Log.d("TEST005", "onMediaPlayingListChanged: ==2==");
                                c_call_back.onMediaPlayingListChanged(m_now_playing_list_count);
                            }
                        }
                        break;
                    case CALL_BACK_EVENT_PLAY_TIME_CHANGED:
                        int timeMs = msg.arg1;
                        int durationMs = msg.arg2;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaPlayTimeChanged(timeMs,durationMs);
                        }
                        break;
                    case CALL_BACK_EVENT_PLAY_MODE_CHANGED:
                        int repeatMode = msg.arg1;
                        int shuffleMode = msg.arg2;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaPlayModeChanged(repeatMode,shuffleMode);
                        }
                        break;
                    case CALL_BACK_EVENT_NOW_PLAYING_CHANGED:
                        NowPlaying musicInfo = (NowPlaying)msg.obj;
/*                        if (musicInfo != null)
                        {
                            m_now_playing_list_count = musicInfo.playCount;
                        }*/
                        Log.d("TEST011", "handleMessage: "+ m_media_ui_call_back.size());
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaNowPlayingChanged(musicInfo);
                        }
                        break;
                    case CALL_BACK_EVENT_PLAY_INFO_CHANGED:
                        int codec = msg.arg1;
                        status = msg.arg2;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaPlayerInfoChanged(codec,status);
                        }
                        break;
                    case CALL_BACK_EVENT_BROWSER_LIST_CHANGED:
                        int listType = msg.arg1;
                        int count = msg.arg2;
                        onManagerBrowserListChanged(listType,count);
                        break;
                    case CALL_BACK_EVENT_PLAYING_LIST_CHANGED:
                        count = msg.arg1;
                        m_now_playing_list_count = count;
                        mNowPlayingList.clear();
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            Log.d("TEST005", "onMediaPlayingListChanged: ==3==");
                            c_call_back.onMediaPlayingListChanged(count);
                        }
                        break;
                    case CALL_BACK_EVENT_FAVOURITE_LIST_CHANGED:
                        count = msg.arg1;
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.onMediaFavoriteListChanged(count);
                        }
                        break;
                    case CALL_BACK_EVENT_COMMON:
                        for (int c_index = 0; c_index < m_media_ui_call_back.size() ; c_index++)
                        {
                            MediaHelperCallBack c_call_back = m_media_ui_call_back.get(c_index);
                            c_call_back.sendMediaCommonControl(msg.arg1,msg.arg2);
                        }
                        break;
                    default:
                }
            }
        }
    };

    public DeviceListItem getDeviceInfo(int c_device_id)
    {
        DeviceListItem c_result = null;
        try {
            DevicesList c_device_list = mMediaService.getDeviceStatus();
            c_result = c_device_list.devices[c_device_id];
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }


    private List<MediaHelperCallBack> m_media_ui_call_back = new ArrayList<>();
    public interface MediaHelperCallBack
    {
        public void onMediaServiceConnected();
        public void onMediaServiceDisconnected();
        public void onMediaDevicesListChanged(DevicesList devicelist);
        public void onMediaDevicesChanged(int device, DeviceListItem deviceItem);
        public void onMediaPlayStatusChanged(int status, int speed);
        public void onMediaPlayTimeChanged(int timeMs, int durationMs);
        public void onMediaPlayModeChanged(int repeatMode, int shuffleMode);
        public void onMediaNowPlayingChanged(NowPlaying musicInfo);
        public void onMediaPlayerInfoChanged(int codec, int status);
        public void onMediaPlayingListChanged(int count);
        public void onMediaFavoriteListChanged(int count);
        public void sendMediaCommonControl(int msg, int arg1);
    }

    public interface MediaHelperListCallBack
    {
        public void onMediaBrowserListChanged(int listType, int count);
        public void onMediaListCatched();
        public void onMediaListRelease();
    }
    private class MediaHelperListCallBackRegister
    {
        public int mListType = -1;
        public boolean m_list_catch_flag = false;
        public String mWhere = null;
        public String[] mArgs = null;
        public int mDeviceMask = 0;
        public String mPath = null;
        public MediaHelperListCallBack mListCallBack = null;
        public int mListCount = -1;
        public HashMap<Integer, MediaListItem> mListItem = new HashMap<>();

    }
    private List<MediaHelperListCallBackRegister> m_register_call_back_key_stack = new ArrayList<>();
    private List<MediaHelperListCallBackRegister> m_buffer_call_back_key_stack = new ArrayList<>();
    //private MediaHelperListCallBackRegister m_current_call_back_obj = null;

    private MediaHelperListCallBackRegister findListCallBack (int c_list_type)
    {
        MediaHelperListCallBackRegister c_result = null;
        for (int i = 0 ; i < m_register_call_back_key_stack .size() ; i++)
        {
            MediaHelperListCallBackRegister c_child_call_back = m_register_call_back_key_stack.get(i);
            if (c_child_call_back.mListType == c_list_type)
            {
                c_result = c_child_call_back;
                break;
            }
        }
        return c_result;
    }
    private MediaHelperListCallBackRegister findCurrentCallBack()
    {
        MediaHelperListCallBackRegister c_result = null;
        if (m_register_call_back_key_stack.size()>0)
        {
            c_result = m_register_call_back_key_stack.get(m_register_call_back_key_stack.size()-1);
        }
        return c_result;
    }
    private MediaHelperListCallBackRegister findBufferListCallBack (int c_list_type)
    {
        MediaHelperListCallBackRegister c_result = null;
        for (int i = 0 ; i < m_buffer_call_back_key_stack .size() ; i++)
        {
            MediaHelperListCallBackRegister c_child_call_back = m_buffer_call_back_key_stack.get(i);
            if (c_child_call_back.mListType == c_list_type)
            {
                c_result = c_child_call_back;
                break;
            }
        }
        if (c_result == null)
        {
            c_result = new MediaHelperListCallBackRegister();
            m_buffer_call_back_key_stack.add(c_result);
        }
        return c_result;
    }
    private void checkBrowserList(boolean c_connect_flag)
    {
        Log.d("TEST005", "checkBrowserList: "+c_connect_flag);
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        if (c_current_call_back_obj != null)
        {
            if (c_connect_flag && !c_current_call_back_obj.m_list_catch_flag) {
                try {
                    int c_count = mMediaService.registerBrowserList(c_current_call_back_obj.mListType, c_current_call_back_obj.mWhere,
                            c_current_call_back_obj.mArgs, c_current_call_back_obj.mDeviceMask, c_current_call_back_obj.mPath);
                    if (c_count != c_current_call_back_obj.mListCount) {
                        c_current_call_back_obj.mListCount = c_count;
                        c_current_call_back_obj.mListItem.clear();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();

                }
                c_current_call_back_obj.m_list_catch_flag = true;
                c_current_call_back_obj.mListCallBack.onMediaListCatched();
                c_current_call_back_obj.mListCallBack.onMediaBrowserListChanged(c_current_call_back_obj.mListType,c_current_call_back_obj.mListCount);
            }
            else if (!c_connect_flag)
            {
                if (c_current_call_back_obj.m_list_catch_flag)
                {
                    c_current_call_back_obj.m_list_catch_flag = false;
                    c_current_call_back_obj.mListCallBack.onMediaListRelease();
                }
            }
        }
    }
    private void onManagerBrowserListChanged(int listType,int count)
    {
        Log.d("TEST005", "onManagerBrowserListChanged: "+listType+" "+count);
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        if (c_current_call_back_obj != null && c_current_call_back_obj.mListType == listType)
        {
            c_current_call_back_obj.mListCallBack.onMediaBrowserListChanged(listType,count);
        }
    }
    public int registerBrowserList(MediaHelperListCallBack c_call_back , int listType , String where , String[] args ,int deviceMask , String path)
    {
        Log.d("TEST005", "registerBrowserList: ==Start=="+listType+" "+m_bind_flag+" "+where);
        int c_result = -1;
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        if (c_current_call_back_obj == null || c_current_call_back_obj.mListType != listType)
        {
            Log.d("FileType", "registerBrowserList: ==01==");
            if (c_current_call_back_obj != null && c_current_call_back_obj.m_list_catch_flag)
            {
                Log.d("FileType", "registerBrowserList: ==02==");
                if (m_bind_flag)
                {
                    try {
                        mMediaService.ungisterBrowserList(c_current_call_back_obj.mListType);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                c_current_call_back_obj.m_list_catch_flag = false;
                c_current_call_back_obj.mListCallBack.onMediaListRelease();
            }
        }
        Log.d("FileType", "registerBrowserList: ==1==");
        boolean c_list_refresh_flag ;
        MediaHelperListCallBackRegister c_old_call_back_obj = findListCallBack(listType);
        if (c_old_call_back_obj != null)
        {
            Log.d("FileType", "registerBrowserList: ==2==");
            m_register_call_back_key_stack.remove(c_old_call_back_obj);
            m_register_call_back_key_stack.add(c_old_call_back_obj);
            if (!c_old_call_back_obj.mListCallBack.equals(c_call_back))
            {
                if (c_old_call_back_obj.m_list_catch_flag)
                {
                    c_old_call_back_obj.m_list_catch_flag = false;
                    c_old_call_back_obj.mListCallBack.onMediaListRelease();
                }
                c_old_call_back_obj.mListCallBack = c_call_back;
            }
            if ((c_old_call_back_obj.mPath != null && !c_old_call_back_obj.mPath.equals(path)) || (path != null && !path.equals(c_old_call_back_obj.mPath)))
            {
                c_list_refresh_flag = true;
            }
            else if ((c_old_call_back_obj.mWhere != null && !c_old_call_back_obj.mWhere.equals(where)) || (where != null && !where.equals(c_old_call_back_obj.mWhere)))
            {
                c_list_refresh_flag = true;
            }
            else if (c_old_call_back_obj.mDeviceMask != deviceMask)
            {
                c_list_refresh_flag = true;
            }
            else
            {
                c_list_refresh_flag = false;
            }
        }
        else
        {
            Log.d("FileType", "registerBrowserList: ==3==");
            c_old_call_back_obj = findBufferListCallBack(listType);
            c_old_call_back_obj.mListCallBack = c_call_back;
            c_old_call_back_obj.mListType = listType;
            if ((c_old_call_back_obj.mPath != null && !c_old_call_back_obj.mPath.equals(path)) || (path != null && !path.equals(c_old_call_back_obj.mPath)))
            {
                c_list_refresh_flag = true;
            }
            else if ((c_old_call_back_obj.mWhere != null && !c_old_call_back_obj.mWhere.equals(where)) || (where != null && !where.equals(c_old_call_back_obj.mWhere)))
            {
                c_list_refresh_flag = true;
            }
            else if (c_old_call_back_obj.mDeviceMask != deviceMask)
            {
                c_list_refresh_flag = true;
            }
            else
            {
                c_list_refresh_flag = false;
            }
            m_register_call_back_key_stack.add(c_old_call_back_obj);
        }
        Log.d("FileType", "registerBrowserList: ==4== " + m_bind_flag+" "+c_old_call_back_obj.m_list_catch_flag);
        Log.d("FileType", "registerBrowserList: ==4=="+listType+" "+where+" "+args+" "+deviceMask+" "+path);
        if (m_bind_flag)
        {
            try {
                Log.d("FileType", "registerBrowserList:==110=="+listType+" "+where);
                c_result = mMediaService.registerBrowserList(listType,where,args,deviceMask,path);
                Log.d("FileType", "registerBrowserList:==111=="+listType+" "+where+" "+c_result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (c_list_refresh_flag || c_old_call_back_obj.mListCount != c_result)
            {
                c_old_call_back_obj.mWhere=where;
                c_old_call_back_obj.mArgs=args;
                c_old_call_back_obj.mDeviceMask=deviceMask;
                c_old_call_back_obj.mPath = path;
                c_old_call_back_obj.mListItem.clear();
                c_old_call_back_obj.mListCount = c_result;
            }
            c_old_call_back_obj.m_list_catch_flag = true;
            c_old_call_back_obj.mListCallBack.onMediaListCatched();
            c_old_call_back_obj.mListCallBack.onMediaBrowserListChanged(c_old_call_back_obj.mListType,c_old_call_back_obj.mListCount);
        }
        else
        {
            if (c_list_refresh_flag)
            {
                c_old_call_back_obj.mWhere=where;
                c_old_call_back_obj.mArgs=args;
                c_old_call_back_obj.mDeviceMask=deviceMask;
                c_old_call_back_obj.mPath = path;
                c_old_call_back_obj.mListItem.clear();
                c_old_call_back_obj.mListCount = c_result;
            }
        }

        return c_result;
    }
    public void unregisterBrowserList(MediaHelperListCallBack c_call_back, int listType)
    {
        Log.d("FileType", "unregisterBrowserList: "+m_bind_flag+" "+listType);
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        MediaHelperListCallBackRegister c_old_call_back_obj = findListCallBack(listType);
        if (c_old_call_back_obj != null)
        {
            Log.d("FileType","unregisterBrowserList == 0 == ");

            if (c_old_call_back_obj.mListCallBack.equals(c_call_back))
            {
                Log.d("FileType","unregisterBrowserList == 1 == ");
                m_register_call_back_key_stack.remove(c_old_call_back_obj);
                if (c_old_call_back_obj.equals(c_current_call_back_obj))
                {
                    Log.d("FileType","unregisterBrowserList == 2 == ");
                    if (c_current_call_back_obj.m_list_catch_flag) {
                        Log.d("FileType","unregisterBrowserList == 3 == ");
                        if (m_bind_flag)
                        {
                            try {
                                mMediaService.ungisterBrowserList(c_current_call_back_obj.mListType);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        c_current_call_back_obj.m_list_catch_flag = false;
                        c_current_call_back_obj.mListCallBack.onMediaListRelease();
                    }
                    c_current_call_back_obj = findCurrentCallBack();
                    if (c_current_call_back_obj != null)
                    {
                        Log.d("FileType","unregisterBrowserList == 4 == ");
                        if (m_bind_flag) {
                            Log.d("FileType","unregisterBrowserList == 5 == ");
                            if (!c_current_call_back_obj.m_list_catch_flag) {
                                Log.d("FileType","unregisterBrowserList == 6 == ");
                                try {
                                    int c_count = mMediaService.registerBrowserList(c_current_call_back_obj.mListType, c_current_call_back_obj.mWhere,
                                            c_current_call_back_obj.mArgs, c_current_call_back_obj.mDeviceMask, c_current_call_back_obj.mPath);
                                    if (c_count != c_current_call_back_obj.mListCount) {
                                        c_current_call_back_obj.mListCount = c_count;
                                        c_current_call_back_obj.mListItem.clear();
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();

                                }
                                c_current_call_back_obj.m_list_catch_flag = true;
                                c_current_call_back_obj.mListCallBack.onMediaListCatched();
                                c_current_call_back_obj.mListCallBack.onMediaBrowserListChanged(c_current_call_back_obj.mListType,c_current_call_back_obj.mListCount);
                            }
                        }
                    }
                }
            }
        }
    }

    public int getBrowserListCount(int listType)
    {
        int c_result = -1;
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        if (c_current_call_back_obj != null && c_current_call_back_obj.mListType == listType)
        {
            c_result = c_current_call_back_obj.mListCount;
        }
        return c_result;
    }
    public int getBrowserListIndex(int c_id)
    {
        int c_result = -1;
        try {
            c_result = mMediaService.getBrowserPlayIndex(c_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }
    public void selectBrowserListItem(int listType , int c_index)
    {
        Log.d("FileType", "selectBrowserListItem: ");
        if(m_bind_flag)
        {
            MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
            if (c_current_call_back_obj != null && c_current_call_back_obj.mListType==listType)
            {
                try {
                    Log.d(TAG, "selectBrowserListItem: =0= "+listType+" "+c_index);
                    mMediaService.selectBrowserList(listType,c_index);
                    Log.d(TAG, "selectBrowserListItem: =1="+listType+" "+c_index);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public int getBrowserPositionByCharter(char c_str)
    {
        int c_result = -1;
        try {
            c_result = mMediaService.getBrowserPositionByCharter(c_str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }
    public char getBrowserCharaterByPosition(int c_index)
    {
        char c_result = '*';
        try {
            c_result = mMediaService.getBrowserCharaterByPosition(c_index);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    public MediaListItem getBrowserListItem(int listType , int c_index)
    {
        Log.d("FileType","getBrowserListItem ==0=="+listType+" "+c_index);
        MediaListItem c_result = null;
        MediaHelperListCallBackRegister c_current_call_back_obj = findCurrentCallBack();
        MediaHelperListCallBackRegister c_old_call_back_obj = findBufferListCallBack(listType);
        if (c_old_call_back_obj != null && c_index >=0 && c_index < c_old_call_back_obj.mListCount)
        {
            if (c_old_call_back_obj.mListItem.containsKey(c_index))
            {
                c_result = c_old_call_back_obj.mListItem.get(c_index);
            }
            else if (c_old_call_back_obj.equals(c_current_call_back_obj))
            {
                int c_check_side_count = 5;
                int c_min_postion = c_index - 1;
                while (c_min_postion>=0 && c_index - c_min_postion <= c_check_side_count && !c_old_call_back_obj.mListItem.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_index +1;
                while (c_max_postion < c_old_call_back_obj.mListCount && c_max_postion - c_index <= c_check_side_count && !c_old_call_back_obj.mListItem.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    MediasList musicList = mMediaService.getBrowserList(c_min_postion,c_check_count);
                    if (musicList != null)
                    {
                        List<MediaListItem> c_music_list = musicList.elements;
                        if (c_music_list!=null)
                        {
                            Log.d(TAG,"getBrowserListItem ==1=="+listType+" "+c_index+" "+c_music_list);
                            for (int i = 0 ; i< c_music_list.size() ; i++)
                            {
                                MediaListItem c_child_music = c_music_list.get(i);
                                c_old_call_back_obj.mListItem.put(c_min_postion+i,c_child_music);
                            }
                        }
                    }
                    c_result = c_old_call_back_obj.mListItem.get(c_index);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("FileType","getBrowserListItem ==1=="+listType+" "+c_index+" "+c_result);
        return  c_result;
    }
    public NowPlaying getNowPlaying()
    {
        NowPlaying c_result = null;
        if (mMediaService != null)
        {
            try {
                c_result = mMediaService.getNowPlaying();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return c_result;
    }
    private int m_now_playing_list_count = 0;
    public HashMap<Integer, MediaListItem> mNowPlayingList = new HashMap<>();
    public int getNowPlayingListCount()
    {
        return m_now_playing_list_count;
    }
    public MediaListItem getNowPlayingItem (int c_postion)
    {
        Log.d("TEST006", "getNowPlayingItem: "+c_postion);
        MediaListItem c_result = null;
        if (c_postion >=0 && c_postion < m_now_playing_list_count )
        {
            if (mNowPlayingList.containsKey(c_postion))
            {
                c_result = mNowPlayingList.get(c_postion);
                Log.d("TEST006", "getNowPlayingItem: ==0== c_result = "+c_result);
            }
            else
            {
                int c_check_side_count = 5;
                int c_min_postion = c_postion - 1;
                while (c_min_postion>=0 && c_postion - c_min_postion <= c_check_side_count && !mNowPlayingList.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_postion +1;
                while (c_max_postion < m_now_playing_list_count && c_max_postion - c_postion <= c_check_side_count && !mNowPlayingList.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    MediasList mediaslist = mMediaService.getPlayingList(c_min_postion,c_check_count);
                    Log.d("TEST006", "getNowPlayingItem: ==1== "+c_min_postion+" "+c_check_count+" "+mediaslist);
                    if (mediaslist != null)
                    {
                        List<MediaListItem> c_media_list = mediaslist.elements;
                        if(c_media_list != null)
                        {
                            for (int i = 0 ; i< c_media_list.size() ; i++)
                            {
                                MediaListItem c_child_media_item = c_media_list.get(i);
                                Log.d(TAG, "getNowPlayingItem: "+(c_min_postion+i)+"==1== child("+i+") = "+c_child_media_item);
                                mNowPlayingList.put(c_min_postion+i,c_child_media_item);
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                c_result = mNowPlayingList.get(c_postion);
                Log.d("TEST006", "getNowPlayingItem: ==1== c_result = "+c_result);
            }
        }
        Log.d("TEST006", "getNowPlayingItem: "+c_postion+" "+c_result);
        return c_result;
    }
    public void selectPlayingSong(int c_index)
    {
        Log.d("TEST005", "selectPlayingSong: "+c_index);
        if(m_bind_flag)
        {
            try {
                mMediaService.selectPlayingList(c_index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void setPlayingTime(int c_time_ms)
    {
        Log.d("TEST005", "setPlayingTime: "+c_time_ms);
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_CMD_SEEK_TO,0,c_time_ms);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void nextMedia()
    {
        Log.d("TEST005", "nextMedia: ");
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_NEXT,0,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void prevMedia()
    {
        Log.d("TEST005", "prevMedia: ");
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_PREV,0,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void playPauseMedia()
    {
        Log.d("TEST005", "playPauseMedia: ");
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_PLAYPAUSE,0,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void playMedia()
    {
        Log.d("TEST005", "playMedia: ");
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_PLAY,0,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void pauseMedia()
    {
        Log.d("TEST005", "pauseMedia: ");
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_PAUSE,0,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveFavourite(int c_index)
    {
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_CMD_ADD_FAVORITES,c_index,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFavourite(int c_index)
    {
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_CMD_DEL_FAVORITES,c_index,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancleFavourite(int c_index)
    {
        if(m_bind_flag)
        {
            try {
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_CMD_CANCEL_FAVORITES,c_index,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayMode(int c_repeat_mode,int c_random_mode)
    {
        Log.d("TEST005", "setPlayMode: ");
        if(m_bind_flag)
        {
            try {
                //Log.d("MediaMainSceneManager", "setPlayMode: "+c_repeat_mode+" "+c_random_mode);
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_REPEAT,0,c_repeat_mode);
                mMediaService.setCommand(MediaUtilDef.MEDIA_PLAYER_KEY_SHUFFLE,0,c_random_mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setVideoSurface(Surface c_surface)
    {
        Log.d("TEST005", "setVideoSurface: ");
        try {
            mMediaService.setVideoSurface(c_surface);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
