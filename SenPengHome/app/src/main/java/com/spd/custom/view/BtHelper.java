package com.spd.custom.view;

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


import com.spd.bluetooth.aidl.IBluetoothCallback;
import com.spd.bluetooth.aidl.IBluetoothService;
import com.spd.bluetooth.entity.aidl.CallLog;
import com.spd.bluetooth.entity.aidl.CallLogList;
import com.spd.bluetooth.entity.aidl.Contacts;
import com.spd.bluetooth.entity.aidl.ContactsList;
import com.spd.bluetooth.entity.aidl.MusicInfo;
import com.spd.bluetooth.utils.BluetoothUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class BtHelper {
    private IBluetoothService mBluetoothService;
    private Context m_context;
    private String m_name = "com.spd.home";
    private String TAG ="BtHelper";

    private static BtHelper m_helper;
    private boolean m_bind_flag = false;
    private boolean m_bind_request_flag = false;
    public static BtHelper get()
    {
        if (m_helper == null)
        {
            m_helper = new BtHelper();
        }
        return m_helper;
    }
    private boolean m_temp_service_init_flag = false;
    public void initBtHelper (Context context)
    {
        if (!m_temp_service_init_flag)
        {
            m_context = context;
            m_temp_service_init_flag = true;
            Intent c_intent = new Intent("com.spd.bluetooth.service.BluetoothService");
            c_intent.setPackage("com.spd.bluetooth");
            m_context.startService(c_intent);
        }
    }

    public CallLog[] getTalkingObject()
    {
        CallLog[] c_result = null;
        try {
            c_result = mBluetoothService.getDialInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    public void sendDTMF(String c_str)
    {
        try {
            char c_key = c_str.charAt(0);
            mBluetoothService.sendDTMF((byte)c_key);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendDTMF(char c_key)
    {
        try {
            mBluetoothService.sendDTMF((byte)c_key);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void hangUp(CallLog callLog)
    {
        Log.d(TAG, "hangUp: "+callLog.getCallNumber());
        try {
            mBluetoothService.hangUpCall(callLog);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void switchTransfer()
    {
        Log.d(TAG, "switchTransfer: ");
        try {
            mBluetoothService.switchTransfer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void acceptCall()
    {
        try {
            mBluetoothService.acceptCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void micMute()
    {
        try {
            mBluetoothService.muteMic();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public boolean getDialState()
    {
        boolean c_result = false;
        try {
            c_result = mBluetoothService.getDialStatus() == 1;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }
    public void dialCall(String c_call_number)
    {
        try {
            mBluetoothService.dialCall(c_call_number);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    public void dialNoNumberCall(Contacts c_contacts)
    {
        try {
            mBluetoothService.dialNoNumberCall(c_contacts);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    public boolean addFavoriteContacts(Contacts contacts)
    {
        boolean c_result = false;
        try {
            c_result = mBluetoothService.addFavoriteContacts(contacts);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    public boolean deleteFavoriteContacts(Contacts contacts)
    {
        boolean c_result = false;
        try {
            Log.d(TAG, "deleteFavoriteContacts: "+contacts);
            c_result = mBluetoothService.deleteFavoriteContacts(contacts);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    public void sendCommandPlay()
    {
        try {
            mBluetoothService.sendMediaCommand(BluetoothUtil.MEDIA_PALAY);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendCommandPause()
    {
        try {
            mBluetoothService.sendMediaCommand(BluetoothUtil.MEDIA_PAUSE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendCommandNext()
    {
        try {
            mBluetoothService.sendMediaCommand(BluetoothUtil.MEDIA_NEXT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendCommandPrev()
    {
        try {
            mBluetoothService.sendMediaCommand(BluetoothUtil.MEDIA_PREV);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private boolean m_source_enter_flag = false;
    public void enterBTSource()
    {
        if (mBluetoothService != null)
        {
            try {
                if (!m_source_enter_flag)
                {
                    m_source_enter_flag = true;
                    mBluetoothService.enterSource("com.spd.spdbt");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void exitBTSource()
    {
        if (mBluetoothService != null)
        {
            try {
                if (m_source_enter_flag)
                {
                    m_source_enter_flag = false;
                    mBluetoothService.exitSource("com.spd.spdbt");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean regsiterCallBack(BtHelperPhoneCallBack c_callback)
    {
        Log.d(TAG, "regsiterCallBack: "+c_callback+" call_back.size"+m_bt_call_back.size());
        boolean c_result = false;
        if (m_bt_call_back.indexOf(c_callback) < 0)
        {
            m_bt_call_back.add(c_callback);
            c_result = true;
        }
        bindService(c_callback);
        return c_result;
    }

    public boolean unregsiterCallBack(BtHelperPhoneCallBack c_callback)
    {
        boolean c_result = false;
        if (m_bt_call_back.indexOf(c_callback) >= 0)
        {
            m_bt_call_back.remove(c_callback);
            c_result = true;
        }
        Log.d(TAG, "unregsiterCallBack: "+c_callback+" call_back.size"+m_bt_call_back.size());
        if (m_bt_call_back.size() == 0)
        {
            unbindService();
        }
        return c_result;
    }

    private void bindService(BtHelperPhoneCallBack c_callback)
    {
        Log.d(TAG,"bindService: "+c_callback +" "+m_bind_flag);
        if (m_bt_call_back.size() > 0)
        {
            if (!m_bind_flag)
            {
                if (!m_bind_request_flag)
                {
                    ComponentName componentName = new ComponentName("com.spd.bluetooth","com.spd.bluetooth.service.BluetoothService");
                    try {
                        ServiceInfo serviceInfo = m_context.getPackageManager().getServiceInfo(componentName, 0);
                        if (serviceInfo != null)
                        {
                            m_bind_request_flag = true;
                            Intent c_intent = new Intent("com.spd.bluetooth.service.BluetoothService");
                            c_intent.setPackage("com.spd.bluetooth");
                            m_context.bindService(c_intent,mServiceConnection,BIND_AUTO_CREATE);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
            else
            {
                Message c_msg = new Message();
                c_msg.what = CALL_BACK_EVENT_SERVICE_CONNECTED;
                c_msg.obj = c_callback;
                m_call_back_handler.sendMessage(c_msg);
                //enterBTSource();
            }
        }
    }
    private void unbindService()
    {
        Log.d(TAG,"unbindService:");
        if (m_bind_flag)
        {
            if (mBluetoothService != null)
            {
                try {
                    mBluetoothService.unregisterCallback(m_name);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                m_context.unbindService(mServiceConnection);
                Log.d(TAG, "m_bind_flag = false: == 0 ==  ");
                mBluetoothService = null;
                m_bind_flag = false;
                exitBTSource();
                m_bind_request_flag = false;
                m_contact_list_count = -1;
                m_contact_list.clear();
                m_contact_char = null;
                m_contact_count = null;
                m_favourite_list_count = -1;
                m_favourite_list.clear();
                m_favourite_char = null;
                m_favourite_count = null;
                m_history_list_count = -1;
                m_history_list.clear();
                m_search_list_count = 0;
                m_search_list.clear();
                m_last_search_str = "";
            }
        }
    }
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected:");
            mBluetoothService = IBluetoothService.Stub.asInterface(service);
            String thread = Thread.currentThread().getName();
            Log.d(TAG,"Thread name1:"+thread);
            try {
                mBluetoothService.asBinder().linkToDeath(mDeath,0);
                mBluetoothService.registerCallback(m_name,mCallback);
                Log.d(TAG, "m_bind_flag = true: == 1 ==  ");
                m_bind_flag = true;
                //enterBTSource();
                m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_CONNECTED);
            } catch (RemoteException e) {
                Log.d(TAG,"ERROR:"+e.toString());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
            Log.d(TAG, "m_bind_flag = false: == 2 ==  ");
            m_bind_flag = false;
            m_bind_request_flag = false;
            mBluetoothService = null;
            m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_DISCONNECTED);
            bindService(null);
        }
    };
    private IBinder.DeathRecipient mDeath = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG,"service binderDied");
            Log.d(TAG, "m_bind_flag = false: == 3 ==  ");
            m_bind_flag = false;
            m_bind_request_flag = false;
            try {
                mBluetoothService.unregisterCallback(m_name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBluetoothService.asBinder().unlinkToDeath(this,0);
            mBluetoothService = null;
            m_call_back_handler.sendEmptyMessage(CALL_BACK_EVENT_SERVICE_DISCONNECTED);
            bindService(null);
        }
    };

    public IBluetoothCallback mCallback = new IBluetoothCallback.Stub() {
        /*
        0:无连接
        1：只有a2dp连接
        2：只有hfp连接
        3：a2dp+hfp
        4：只有PBAP
         */
        @Override
        public void onConnectStatusChanged(int status){

            Log.d(TAG,"onConnectStatusChanged:"+status);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_CONNECT_STATUS_CHANGED;
            c_msg.arg1 = status;
            m_call_back_handler.sendMessage(c_msg);
        }



        @Override
        public void onPlayStatusChanged(int status,long postion){
            Log.d(TAG,"onPlayStatusChanged:"+status +" postion");
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PLAY_STATUS_CHANGED;
            c_msg.arg1 = status;
            c_msg.obj = postion;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onTrackInfoChanged(MusicInfo musicInfo){
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_TRACK_INFO_CHANGED;
            c_msg.obj = musicInfo;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onMicStatusChanged(int status){
            Log.d(TAG,"onMicStatusChanged"+status);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_MIC_STATUS_CHANGED;
            c_msg.arg1 = status;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onTransferStatusChanged(int status){
            Log.d(TAG,"onTransferStatusChanged:"+status);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_TRANSFER_STATUS_CHANGED;
            c_msg.arg1 = status;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onPhonebookCountChanged(int type, int count){
            Log.d(TAG,"onPhonebookCountChanged:"+type+" "+count);
            Log.d("BtListView", "BtHelper onPhonebookCountChanged: "+type+" "+count);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_PHONE_BOOK_COUNT_CHANGED;
            c_msg.arg1 = type;
            c_msg.arg2 = count;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void sendCommonControl(int msg, int arg1, int arg2){
            Log.d(TAG,"sendCommonControl:"+msg);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_COMMON;
            c_msg.obj = msg;
            c_msg.arg1 = arg1;
            c_msg.arg2 = arg2;
            m_call_back_handler.sendMessage(c_msg);
        }
        @Override
        public void onDialogListChanged(int i) {
            Log.d(TAG,"onDialogListChanged:"+i);
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_DIALOG_LIST_CHANGED;
            c_msg.arg1 = i;
            m_call_back_handler.sendMessage(c_msg);
        }

        @Override
        public void onCallStatusChanged(CallLog callLog){
            Message c_msg = new Message();
            c_msg.what = CALL_BACK_EVENT_CALL_STATUS_CHANGED;
            c_msg.obj = callLog;
            m_call_back_handler.sendMessage(c_msg);
        }
    };
    private static final int CALL_BACK_EVENT_SERVICE_CONNECTED = 101;
    private static final int CALL_BACK_EVENT_SERVICE_DISCONNECTED = 102;
    private static final int CALL_BACK_EVENT_CONNECT_STATUS_CHANGED = 103;
    private static final int CALL_BACK_EVENT_PLAY_STATUS_CHANGED = 104;
    private static final int CALL_BACK_EVENT_TRACK_INFO_CHANGED = 105;
    private static final int CALL_BACK_EVENT_MIC_STATUS_CHANGED = 106;
    private static final int CALL_BACK_EVENT_TRANSFER_STATUS_CHANGED = 107;
    private static final int CALL_BACK_EVENT_PHONE_BOOK_COUNT_CHANGED = 108;
    private static final int CALL_BACK_EVENT_CALL_STATUS_CHANGED = 109;
    private static final int CALL_BACK_EVENT_COMMON = 110;
    private static final int CALL_BACK_EVENT_DIALOG_LIST_CHANGED = 111;
    private int m_a2dp_status = 0,m_hfp_status = 0,m_pbap_status = 0;
    private Handler m_call_back_handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG,"handleMessage: =0=");
            if (mBluetoothService != null && m_bind_flag && m_bt_call_back != null)
            {
                Log.d(TAG,"handleMessage: =1="+msg.what);
                switch (msg.what)
                {
                    case CALL_BACK_EVENT_SERVICE_CONNECTED:
                        m_a2dp_status = 0;
                        m_hfp_status = 0;
                        m_pbap_status = 0;
                        Log.d(TAG,"handleMessage: CALL_BACK_EVENT_SERVICE_CONNECTED"+msg.obj);
                        try {

                            if (msg.obj != null)
                            {
                                BtHelperPhoneCallBack c_target_call_back = (BtHelperPhoneCallBack)msg.obj;
                                c_target_call_back.onServiceConnected();
                                Log.d(TAG, "handleMessage: "+mBluetoothService);
                                int connectState = mBluetoothService.getConnectionState();
                                Message c_msg = new Message();
                                c_msg.what = CALL_BACK_EVENT_CONNECT_STATUS_CHANGED;
                                c_msg.arg1 = connectState;
                                c_msg.obj = c_target_call_back;
                                m_call_back_handler.sendMessage(c_msg);
                            }
                            else
                            {
                                for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                {
                                    BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                    if (c_call_back.onServiceConnected())
                                        break;
                                }
                                int connectState = mBluetoothService.getConnectionState();
                                Message c_msg = new Message();
                                c_msg.what = CALL_BACK_EVENT_CONNECT_STATUS_CHANGED;
                                c_msg.arg1 = connectState;
                                m_call_back_handler.sendMessage(c_msg);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CALL_BACK_EVENT_SERVICE_DISCONNECTED:
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onServiceDisconnected())
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_CONNECT_STATUS_CHANGED:
                        int c_status = msg.arg1;
                        Log.d(TAG, "handleMessage: CALL_BACK_EVENT_CONNECT_STATUS_CHANGED "+c_status+" "+msg.obj);
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onConnectStatusChanged(c_status))
                                break;
                        }
                        try {
                            CallLog[] callState = mBluetoothService.getDialInfo();
                            int micState = mBluetoothService.getMicStatus();
                            int transferState = mBluetoothService.getTransferStatus();
                            int playState = mBluetoothService.getPlaybackStatus();
                            MusicInfo c_music_info = mBluetoothService.getMusicInfo();
                            if (msg.obj != null) {
                                BtHelperPhoneCallBack c_target_call_back = (BtHelperPhoneCallBack) msg.obj;
                                c_target_call_back.onPhonebookCountChanged(0,m_contact_list_count);
                                c_target_call_back.onPhonebookCountChanged(2,m_history_list_count);
                                c_target_call_back.onPhonebookCountChanged(1,m_favourite_list_count);

                                for (CallLog childCall : callState)
                                {
                                    if (childCall != null)
                                    {
                                        c_target_call_back.onCallStatusChanged(childCall);
                                    }
                                }
                                c_target_call_back.onMicStatusChanged(micState);
                                c_target_call_back.onTransferStatusChanged(transferState);
                                c_target_call_back.onPlayStatusChanged(playState,0);
                                c_target_call_back.onTrackInfoChanged(c_music_info);
                                c_target_call_back.onDialogListChanged(2);
                            }
                            else
                            {
                                int c_pbap_status = (c_status >> 2) & 1;
                                if (c_pbap_status == 1 && m_pbap_status != c_pbap_status)
                                {
                                    m_pbap_status = c_pbap_status;
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(0,m_contact_list_count))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(2,m_history_list_count))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(1,m_favourite_list_count))
                                            break;
                                    }
                                }
                                else if (c_pbap_status == 0 && m_pbap_status != c_pbap_status)
                                {
                                    m_pbap_status = c_pbap_status;
                                    m_contact_list_count = -1;
                                    m_history_list_count = -1;
                                    m_favourite_list_count = -1;
                                    m_search_list_count = -1;
                                    m_contact_list.clear();
                                    m_history_list.clear();
                                    m_favourite_list.clear();
                                    m_search_list.clear();
                                    m_contact_char = null;
                                    m_contact_count = null;
                                    m_favourite_char = null;
                                    m_favourite_count = null;
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(0,m_contact_list_count))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(2,m_history_list_count))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPhonebookCountChanged(1,m_favourite_list_count))
                                            break;
                                    }
                                }
                                int c_hfp_status = (c_status >> 1) & 1;
                                Log.d(TAG, "handleMessage: c_hfp_status = "+c_hfp_status + " "+ m_hfp_status);
                                if (c_hfp_status == 1 && m_hfp_status != c_hfp_status)
                                {
                                    m_hfp_status = c_hfp_status;
                                    for (CallLog childCall : callState)
                                    {
                                        if (childCall != null) {
                                            for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                            {
                                                BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                                if (c_call_back.onCallStatusChanged(childCall))
                                                    break;
                                            }
                                        }
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onMicStatusChanged(micState))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onTransferStatusChanged(transferState))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        Log.d(TAG, "handleMessage: onTrackInfoChanged ==0==");
                                        if (c_call_back.onDialogListChanged(2))
                                            break;
                                    }
                                }
                                else if (c_hfp_status == 0 && m_hfp_status != c_hfp_status)
                                {
                                    m_hfp_status = c_hfp_status;
                                }
                                int c_a2dp_status = c_status & 1;
                                if (c_a2dp_status == 1 && m_a2dp_status != c_a2dp_status)
                                {
                                    m_a2dp_status = c_a2dp_status;
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        if (c_call_back.onPlayStatusChanged(playState,0))
                                            break;
                                    }
                                    for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                                    {
                                        BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                                        Log.d(TAG, "handleMessage: onTrackInfoChanged ==0==");
                                        if (c_call_back.onTrackInfoChanged(c_music_info))
                                            break;
                                    }

                                }
                                else if (c_a2dp_status == 0 && m_a2dp_status != c_a2dp_status)
                                {
                                    m_a2dp_status = c_a2dp_status;

                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }




                        break;
                    case CALL_BACK_EVENT_PLAY_STATUS_CHANGED:
                        c_status = msg.arg1;
                        long c_postion = (long)msg.obj;
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onPlayStatusChanged(c_status,c_postion))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_TRACK_INFO_CHANGED:
                        MusicInfo c_musicInfo = null;
                        if (msg.obj != null && msg.obj instanceof MusicInfo)
                        {
                            c_musicInfo = (MusicInfo)msg.obj;
                        }
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            Log.d(TAG, "handleMessage: onTrackInfoChanged ==1==");
                            if (c_call_back.onTrackInfoChanged(c_musicInfo))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_CALL_STATUS_CHANGED:
                        CallLog callLog = (CallLog)msg.obj;
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onCallStatusChanged(callLog))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_MIC_STATUS_CHANGED:
                        c_status = msg.arg1;
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onMicStatusChanged(c_status))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_TRANSFER_STATUS_CHANGED:
                        c_status = msg.arg1;
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onTransferStatusChanged(c_status))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_PHONE_BOOK_COUNT_CHANGED:

                        int c_type = msg.arg1;
                        int c_count = msg.arg2;
                        Log.d("BtListView", "BtHelper onPhonebookCountChanged: "+c_type+" "+c_count);
                        if (c_type == BluetoothUtil.DATABASE_CONTACTS_TOKEN)
                        {
                            m_contact_list_count = c_count;
                            m_contact_list.clear();
                            m_contact_char = null;
                            m_contact_count = null;
                        }
                        else if (c_type == BluetoothUtil.DATABASE_FAVORITES_TOKEN)
                        {
                            m_favourite_list_count = c_count;
                            m_favourite_list.clear();
                            m_favourite_char = null;
                            m_favourite_count = null;
                        }
                        else if (c_type == BluetoothUtil.DATABASE_CALLLOGS_TOKEN)
                        {
                            m_history_list_count = c_count;
                            m_history_list.clear();
                        }
                        else if (c_type == BluetoothUtil.DATABASE_SEARCHS_TOKEN)
                        {
                            m_search_list_count = c_count;
                            m_search_list.clear();
                        }
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onPhonebookCountChanged(c_type,c_count))
                                break;
                        }
                        break;
                    case CALL_BACK_EVENT_COMMON:
                        int c_command = (int)msg.obj;
                        int c_arg1 = msg.arg1 ;
                        int c_arg2 = msg.arg2 ;
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.sendCommonControl(c_command,c_arg1,c_arg2))
                                break;
                        }
                        break;
                    case SYNC_PHONE_BOOK_COMMAND:
                        /*try {
                            mBluetoothService.startDownloadPhonebook();
                            mBluetoothService.startDownloadCallLog();
                            mBluetoothService.startDownloadFavorite();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }*/
                        break;
                    case CALL_BACK_EVENT_DIALOG_LIST_CHANGED:
                        c_status = msg.arg1;
                        Log.d(TAG, "handleMessage: CALL_BACK_EVENT_DIALOG_LIST_CHANGED "+c_status);
                        for (int c_index = 0 ; c_index < m_bt_call_back.size() ; c_index++)
                        {
                            BtHelperPhoneCallBack c_call_back = m_bt_call_back.get(c_index);
                            if (c_call_back.onDialogListChanged(c_status))
                                break;
                        }
                        break;
                        default:
                }
            }
        }
    };



    private static final int SYNC_PHONE_BOOK_COMMAND = 1000;

    private List<BtHelperPhoneCallBack> m_bt_call_back = new ArrayList<>();

    public interface BtHelperPhoneCallBack
    {
        public boolean onServiceConnected();
        public boolean onServiceDisconnected();
        public boolean onConnectStatusChanged(int status);
        public boolean onPlayStatusChanged(int status, long postion);
        public boolean onTrackInfoChanged(MusicInfo musicInfo);
        public boolean onCallStatusChanged(CallLog callLog);
        public boolean onMicStatusChanged(int status);
        public boolean onTransferStatusChanged(int status);
        public boolean onPhonebookCountChanged(int type, int count);
        public boolean sendCommonControl(int msg, int arg1, int arg2);
        public boolean onDialogListChanged(int i);
    }
    public int getLastListCount(int c_type , String c_arg_str)
    {
        int c_result = -1;
        if (c_type == BluetoothUtil.DATABASE_CONTACTS_TOKEN)
        {
            c_result = m_contact_list_count;
        }
        else if (c_type == BluetoothUtil.DATABASE_FAVORITES_TOKEN)
        {
            c_result = m_favourite_list_count;
        }
        else if (c_type == BluetoothUtil.DATABASE_CALLLOGS_TOKEN)
        {
            c_result = m_history_list_count;
        }
        else if (c_type == BluetoothUtil.DATABASE_DETAILS_TOKEN)
        {
            c_result = -1;
        }
        else if (c_type == BluetoothUtil.DATABASE_SEARCHS_TOKEN)
        {
            if ((m_last_search_str != null && m_last_search_str.equals(c_arg_str)) || (c_arg_str == null && m_last_search_str == null))
            {
                c_result = m_search_list_count;
            }
            else
            {
                c_result = 0;
            }
        }
        return c_result;
    }
    private String m_last_search_str = "";
    public void startLoadList(int c_type,String c_arg_str)
    {
        try
        {
            if (c_type == BluetoothUtil.DATABASE_CONTACTS_TOKEN)
            {
                m_contact_list_count = -1;
                m_contact_list.clear();
                m_contact_char = null;
                m_contact_count = null;
                Log.d("BtListView", "startLoadList: DATABASE_CONTACTS_TOKEN startDownloadPhonebook ==0==");
                mBluetoothService.startDownloadPhonebook(false);
                Log.d("BtListView", "startLoadList: DATABASE_CONTACTS_TOKEN startDownloadPhonebook ==1==");
            }
            else if (c_type == BluetoothUtil.DATABASE_FAVORITES_TOKEN)
            {
                m_favourite_list_count = -1;
                m_favourite_list.clear();
                m_favourite_char = null;
                m_favourite_count = null;
                mBluetoothService.startDownloadFavorite();
            }
            else if (c_type == BluetoothUtil.DATABASE_CALLLOGS_TOKEN)
            {
                m_history_list_count = -1;
                m_history_list.clear();
                mBluetoothService.startDownloadCallLog();
            }
            else if (c_type == BluetoothUtil.DATABASE_DETAILS_TOKEN)
            {
                //????
                mBluetoothService.startDownloadDetail(c_arg_str);
            }
            else if (c_type == BluetoothUtil.DATABASE_SEARCHS_TOKEN)
            {
                m_search_list_count = 0;
                m_search_list.clear();
                m_last_search_str = c_arg_str;
                mBluetoothService.searchList(0,c_arg_str);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String[] m_contact_char;
    private int[] m_contact_count;
    private String[] m_favourite_char;
    private int[] m_favourite_count;

    private HashMap<Integer,Contacts> m_contact_list = new HashMap<>();
    private HashMap<Integer,CallLog> m_history_list = new HashMap<>();
    private HashMap<Integer,Contacts> m_favourite_list = new HashMap<>();
    private HashMap<Integer,Contacts> m_search_list = new HashMap<>();
    private int m_contact_list_count = -1;
    private int m_history_list_count = -1;
    private int m_favourite_list_count = -1;
    private int m_search_list_count = 0;

    public String[] getContactCharSection()
    {
        if (m_contact_char == null)
        {
            getContactItem(0);
        }
        return m_contact_char;
    }
    public int[] getContactCharCount()
    {
        if (m_contact_count == null)
        {
            getContactItem(0);
        }
        return m_contact_count;
    }
    public String[] getFavouriteCharSection()
    {
        if (m_favourite_char == null)
        {
            getFavouriteItem(0);
        }
        return m_favourite_char;
    }
    public int[] getFavouriteCharCount()
    {
        if (m_favourite_count == null)
        {
            getFavouriteItem(0);
        }
        return m_favourite_count;
    }

    public Contacts getDetailContacts(String c_look_up)
    {
        Contacts c_result = null;
        try {
            c_result = mBluetoothService.getDetailContacts(c_look_up);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return c_result;
    }

    public int getContactCount()
    {
        return m_contact_list_count;
    }
    public Contacts getContactItem (int c_postion)
    {
        Contacts c_result = null;
        if (c_postion >=0 && c_postion < m_contact_list_count )
        {
            if (m_contact_list.containsKey(c_postion) && m_contact_list.get(c_postion) != null)
            {
                c_result = m_contact_list.get(c_postion);
            }
            else
            {
                int c_check_side_count = 5;
                int c_min_postion = c_postion - 1;
                while (c_min_postion>=0 && c_postion - c_min_postion <= c_check_side_count && !m_contact_list.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_postion +1;
                while (c_max_postion < m_contact_list_count && c_max_postion - c_postion <= c_check_side_count && !m_contact_list.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    ContactsList contactsList = mBluetoothService.getContactsList(c_min_postion,c_check_count);
                    m_contact_char = contactsList.getSections();
                    m_contact_count = contactsList.getCounts();
                    List<Contacts> c_contacts_list = contactsList.getElements();
                    for (int i = 0 ; i< c_contacts_list.size() ; i++)
                    {
                        Contacts c_child_contacts = c_contacts_list.get(i);
                        Log.d("BtListView", "getContactItem: " + c_child_contacts);
                        m_contact_list.put(c_min_postion+i,c_child_contacts);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                c_result = m_contact_list.get(c_postion);
            }
        }
        return c_result;
    }
    public int getHistoryCount()
    {
        return m_history_list_count;
    }
    public CallLog getHistoryItem (int c_postion)
    {

        boolean c_buffer_flag = false;
        CallLog c_result = null;
        if (c_postion >=0 && c_postion < m_history_list_count )
        {
            if (m_history_list.containsKey(c_postion) && m_history_list.get(c_postion) != null)
            {
                c_result = m_history_list.get(c_postion);
                c_buffer_flag = true;
            }
            else
            {
                c_buffer_flag = false;
                int c_check_side_count = 5;
                int c_min_postion = c_postion - 1;
                while (c_min_postion>=0 && c_postion - c_min_postion <= c_check_side_count && !m_history_list.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_postion +1;
                while (c_max_postion < m_history_list_count && c_max_postion - c_postion <= c_check_side_count && !m_history_list.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    CallLogList historyList = mBluetoothService.getCallLogList(c_min_postion,c_check_count);
                    List<CallLog> c_contacts_list = historyList.getElements();
                    for (int i = 0 ; i< c_contacts_list.size() ; i++)
                    {
                        CallLog c_child_history = c_contacts_list.get(i);
                        m_history_list.put(c_min_postion+i,c_child_history);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                c_result = m_history_list.get(c_postion);
            }
        }
        return c_result;
    }
    public int getFavouriteCount()
    {
        return m_favourite_list_count;
    }
    public Contacts getFavouriteItem (int c_postion)
    {
        Contacts c_result = null;
        if (c_postion >=0 && c_postion < m_favourite_list_count )
        {
            if (m_favourite_list.containsKey(c_postion) && m_favourite_list.get(c_postion) != null)
            {
                c_result = m_favourite_list.get(c_postion);
            }
            else
            {
                int c_check_side_count = 5;
                int c_min_postion = c_postion - 1;
                while (c_min_postion>=0 && c_postion - c_min_postion <= c_check_side_count && !m_favourite_list.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_postion +1;
                while (c_max_postion < m_favourite_list_count && c_max_postion - c_postion <= c_check_side_count && !m_favourite_list.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    ContactsList contactsList = mBluetoothService.getFavoriteContactsList(c_min_postion,c_check_count);
                    m_favourite_char = contactsList.getSections();
                    m_favourite_count = contactsList.getCounts();
                    List<Contacts> c_contacts_list = contactsList.getElements();
                    for (int i = 0 ; i< c_contacts_list.size() ; i++)
                    {
                        Contacts c_child_contacts = c_contacts_list.get(i);
                        m_favourite_list.put(c_min_postion+i,c_child_contacts);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                c_result = m_favourite_list.get(c_postion);
            }
        }
        return c_result;
    }
    public int getSearchCount()
    {
        return m_search_list_count;
    }
    public Contacts getSearchItem (int c_postion)
    {
        Contacts c_result = null;
        if (c_postion >=0 && c_postion < m_search_list_count )
        {
            if (m_search_list.containsKey(c_postion) && m_search_list.get(c_postion) != null)
            {
                c_result = m_search_list.get(c_postion);
            }
            else
            {
                int c_check_side_count = 5;
                int c_min_postion = c_postion - 1;
                while (c_min_postion>=0 && c_postion - c_min_postion <= c_check_side_count && !m_search_list.containsKey(c_min_postion))
                {
                    c_min_postion--;
                }
                c_min_postion++;
                int c_max_postion = c_postion +1;
                while (c_max_postion < m_search_list_count && c_max_postion - c_postion <= c_check_side_count && !m_search_list.containsKey(c_max_postion))
                {
                    c_max_postion ++ ;
                }
                c_max_postion--;
                int c_check_count = c_max_postion - c_min_postion +1;
                try {
                    ContactsList contactsList = mBluetoothService.getSearchList(c_min_postion,c_check_count);
                    List<Contacts> c_contacts_list = contactsList.getElements();
                    for (int i = 0 ; i< c_contacts_list.size() ; i++)
                    {
                        Contacts c_child_contacts = c_contacts_list.get(i);
                        m_search_list.put(c_min_postion+i,c_child_contacts);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                c_result = m_search_list.get(c_postion);
            }
        }
        return c_result;
    }
    public void syncContact()
    {
        if (mBluetoothService != null)
        {
            try {
                mBluetoothService.startDownloadPhonebook(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}
