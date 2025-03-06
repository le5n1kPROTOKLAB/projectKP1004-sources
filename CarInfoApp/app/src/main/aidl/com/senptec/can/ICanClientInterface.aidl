// IMyAidlInterface.aidl
package com.senptec.can;
import com.senptec.can.ICanServerInterface;

// Declare any non-default types here with import statements

// 被设置到服务端
interface ICanClientInterface {

    // 服务端向客户端转发数据
    void onReceiveData(String id, String gsonData);

    // 服务端向客户端返回最后发送的数据
    void onSendData(String id, String gsonData);
}
