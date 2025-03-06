package com.spentec.server_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.senptec.can.ICanClientInterface;
import com.senptec.can.ICanServerInterface;
import com.spentec.server_client.callback.IOnDataListener;
import com.spentec.server_client.record.RecordRequest;
import com.spentec.server_client.record.RecordResponse;
import com.spentec.server_client.util.ThreadUtil;

import java.util.ArrayList;

public class ControlServerHelper {
    //服务包名
    private static final String SERVICE_PKGNAME = "com.senptec.control";
    //服务class
    private static final String SERVICE_CLASS = "com.senptec.control.ControlService";

    private static final int CMD = 111111;

    private Context mContext;
    private final Gson mGson = new Gson();
    private String LIB_TAG;

    public void init(@NonNull Context context, String tag) {
        if (mContext == null) {
            this.mContext = context;
            LIB_TAG = tag;
            callService();
            mHandler.sendEmptyMessageDelayed(CMD, 100);
        } else {
            throw new RuntimeException("instance re-init!");
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == CMD) {
                if (mService != null) {
                    sendEmptyMessageDelayed(CMD, 100);
                } else {
                    sendEmptyMessageDelayed(CMD, 1000);
                    callService();
                }
            } else {
                sendEmptyMessageDelayed(CMD, 3000);
                log("in25值未获取");
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            log("服务端：连接 Service 成功");
            mService = ICanServerInterface.Stub.asInterface(service);
            // 向服务端注册自己的回调
            try {
                mService.setCallback(LIB_TAG, new ICanClientInterface.Stub() {

                    @Override
                    public void onReceiveData(String id, String gsonData) {
                        ControlServerHelper.this.onReceiveData(id, gsonData);
                    }

                    @Override
                    public void onSendData(String id, String gsonData) {
                        // 可以读取到上次打开时最后发送的报文，回显用
                        ControlServerHelper.this.onSendData(id, gsonData);
                    }

                    @Override
                    public void onRecordFileList(String tag, String gsonData) {
                        ThreadUtil.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (mRecordRequest != null && TextUtils.equals(tag, mRecordRequest.recordTag)) {
                                    try {
                                        RecordResponse recordResponse = mGson.fromJson(gsonData, RecordResponse.class);
                                        mRecordResponse.totalCount = recordResponse.totalCount;
                                        mRecordResponse.count += recordResponse.count;
                                        if (recordResponse.count > 0) {
                                            if (mRecordResponse.dataList == null) {
                                                mRecordResponse.dataList = new ArrayList<>();
                                            }
                                            mRecordResponse.dataList.addAll(recordResponse.dataList);
                                        }
                                        if (mRecordResponse.count >= recordResponse.totalCount) {
                                            if (mRecordRequest.onResponseListener != null) {
                                                mRecordRequest.onResponseListener.onResponse(mRecordResponse);
                                            }
                                            mRecordRequest = null;
                                            mRecordResponse = null;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        log(e.getMessage());
                                    }
                                }
                            }
                        });
                    }
                });
                if (mRecordRequest != null) {
                    requestRecordFileList(mRecordRequest);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                log("ServiceConnection-RemoteException--" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log("服务端：失去连接 Service");
            mService = null;
        }
    };

    private ICanServerInterface mService;

    public void callService() {
        Intent serverIntent = new Intent();
        ComponentName componentName = new ComponentName(SERVICE_PKGNAME, SERVICE_CLASS);
        serverIntent.setComponent(componentName);
        mContext.bindService(serverIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 发送空消息
     *
     * @param id can的id
     */
    public void sendNullData(String id) {
        sendData(id, mGson.toJson(new byte[8]));
    }

    /**
     * 发送消息
     *
     * @param id  can的id
     * @param str can的消息
     */
    public void sendData(String id, String str) {
        try {
            if (mService != null) {
                try {
                    mService.onSendData(id, str);
                } catch (RemoteException e) {
                    mService = null;
                    callService();
                }
            } else {
                callService();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("发送消息-Exception--" + e.getMessage());
        }
    }

    public void release() {
        try {
            mHandler.removeMessages(CMD);
            mContext.unbindService(mServiceConnection);
        } catch (Exception e) {
            e.printStackTrace();
            log("释放服务连接Exception--" + e.getMessage());
        }
    }

    private RecordRequest mRecordRequest;
    private RecordResponse mRecordResponse;

    public void requestRecordFileList(RecordRequest recordRequest) {
        mRecordRequest = recordRequest;
        mRecordResponse = new RecordResponse();
        if (mService != null) {
            try {
                mService.requestRecordFileList(LIB_TAG,
                        mRecordRequest.recordTag, mRecordRequest.csi, mRecordRequest.channel,
                        mRecordRequest.startTime, mRecordRequest.endTime);
            } catch (RemoteException e) {
                mService = null;
                callService();
            }
        } else {
            callService();
        }
    }

    private static void log(String msg) {
        Log.d("control_server", msg);
    }

    private final ArrayList<IOnDataListener> onReceiveDataListeners = new ArrayList<>();

    public void addOnReceiveDataListener(IOnDataListener listener) {
        if (listener != null) {
            onReceiveDataListeners.add(listener);
        }
    }

    public void removeOnReceiveDataListener(IOnDataListener listener) {
        if (listener != null) {
            onReceiveDataListeners.remove(listener);
        }
    }

    private void onReceiveData(String id, String gsonData) {
        for (IOnDataListener listener : onReceiveDataListeners) {
            listener.onReceive(id, gsonData);
        }
    }

    private void onSendData(String id, String gsonData) {
        for (IOnDataListener listener : onReceiveDataListeners) {
            listener.onSend(id, gsonData);
        }
    }
}
