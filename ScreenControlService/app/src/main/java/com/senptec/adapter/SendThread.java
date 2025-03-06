package com.senptec.adapter;

import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.ByteKbpsChannelCmdBean;
import com.senptec.adapter.bean.CommunicationBean;
import com.senptec.control.util.LogUtils;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Date:    2023/8/29
 * Author:  zhaixusheng
 * Info:
 */
public class SendThread extends Thread {

    public SendThread() {

    }

    public ArrayBlockingQueue<SendData> sendQueue = new ArrayBlockingQueue<>(256);

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                SendData poll = sendQueue.take();
                // 发送
                // 向串口发送数据
                byte[] bytes = CanDataManager.getInstance().gson.fromJson(poll.bytesContent, byte[].class);
                // 设置channel为can1
                ByteCanBaseBean byteCanBaseBean = new ByteCanBaseBeanWrapper().parse(bytes);
                // 设置id
                byteCanBaseBean.setId(poll.id);
                CommunicationBean communicationBean = byteCanBaseBean.create();
                LogUtils.d("向串口发送数据======" + communicationBean.toString());
                LogUtils.d("====向串口发送数据====" + HexByteStrUtils.getStringFromBytes(communicationBean.toByteArray()));
                CarSerialPortManager.getInstance().sendCarCommand(communicationBean.toByteArray());
            } catch (InterruptedException e) {

            }
        }
    }

    private static class ByteCanBaseBeanWrapper extends ByteCanBaseBean {
        @Override
        public byte getCanChannel() {
            return ByteKbpsChannelCmdBean.CAN_CHANNEL_1;
        }
    }

    public static class SendData {
        public String id;
        public String bytesContent;

        public SendData(String id, String bytesContent) {
            this.id = id;
            this.bytesContent = bytesContent;
        }
    }
}