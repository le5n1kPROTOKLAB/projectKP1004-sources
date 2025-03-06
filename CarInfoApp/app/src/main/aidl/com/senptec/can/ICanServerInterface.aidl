// IMyAidlInterface.aidl
package com.senptec.can;
import com.senptec.can.ICanClientInterface;

// Declare any non-default types here with import statements

// 服务端在客户端的回调
interface ICanServerInterface {

    // 客户端通过绑定服务拿到类对象，并通过此方法向服务端注册自己的方法
    void setCallback(String packageName, ICanClientInterface callBack);

    // 客户端向服务端发送数据
    void onSendData(String id, String gsonData);
}
