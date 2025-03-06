package com.senptec.adapter;

import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.can.ICanClientInterface;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CanDataManager {

    public final Gson gson = new Gson();
    private final SendThread sendThread;

    private CanDataManager() {
        sendThread = new SendThread();
        sendThread.start();
    }

    private static final class InstanceHolder {
        static final CanDataManager instance = new CanDataManager();
    }

    public static CanDataManager getInstance() {
        return InstanceHolder.instance;
    }

    public Gson getGson() {
        return gson;
    }

    // <id, gsonData>
    public final ConcurrentHashMap<String, String> receiveCanMap = new ConcurrentHashMap<>();
    // <id, gsonData>
    public final ConcurrentHashMap<String, String> sendCanMap = new ConcurrentHashMap<>();

    // <packageName, clientCallback>
    private final ConcurrentHashMap<String, ICanClientInterface> callbackMap = new ConcurrentHashMap<>();

    public void putReceiveData(String id, String json) {
        // 保存数据
        synchronized (this) {
            receiveCanMap.put(id, json);
        }
        // 转发
        ArrayList<String> needDeleteList = new ArrayList<>();
        for (Map.Entry<String, ICanClientInterface> clientEntry : callbackMap.entrySet()) {
            try {
                clientEntry.getValue().onReceiveData(id, json);
            } catch (RemoteException e) {
                needDeleteList.add(clientEntry.getKey());
            }
        }
        // 删除失联的客户端程序
        synchronized (this) {
            for (String s : needDeleteList) {
                callbackMap.remove(s);
            }
        }
    }

    public ICanClientInterface getCallBack(String packageName) {
       return callbackMap.get(packageName);
    }


    public void putSendData(String id, String gsonData) {
        if (!TextUtils.isEmpty(gsonData)) {
            // 有效数据
            // 保存
            SendThread.SendData sendData = new SendThread.SendData(id, gsonData);
            boolean add = sendThread.sendQueue.add(sendData);
            if (!add) {
                sendThread.sendQueue.poll();
                sendThread.sendQueue.offer(sendData);
            }
            synchronized (this) {
                sendCanMap.put(id, gsonData);
            }
        }
    }

    @Nullable
    public String toJson(ByteCanBaseBean byteCanBaseBean) {
        return gson.toJson(byteCanBaseBean);
    }

    public void putCallback(String packageName, ICanClientInterface callback) {
        boolean isDead = false;
        if (callback != null) {
            // 回调保存的最新数据
            for (Map.Entry<String, String> entry : receiveCanMap.entrySet()) {
                try {
                    callback.onReceiveData(entry.getKey(), entry.getValue());
                } catch (RemoteException e) {
                    isDead = true;
                    break;
                }
            }
            // 回调最后发送的数据
            if (!isDead) {
                for (Map.Entry<String, String> entry : sendCanMap.entrySet()) {
                    try {
                        callback.onSendData(entry.getKey(), entry.getValue());
                    } catch (RemoteException e) {
                        isDead = true;
                        break;
                    }
                }
            }
        }
        if (!isDead) {
            // 注册回调
            synchronized (this) {
                callbackMap.put(packageName, callback);
            }
        }
    }

    public String getGsonData(Object obj) {
        return gson.toJson(obj);
    }
}
