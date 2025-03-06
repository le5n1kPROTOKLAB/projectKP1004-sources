package com.example.carinfoapp.can;

import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.receive.d1.channel.ChannelData;

import java.util.ArrayList;

public class InputCanBean extends ByteCanBaseBean {
    /**
     * 输入
     */
    public static final String[] dataNameExtras = new String[]{
            "CON3-M", "CON3-N", "CON3-P", "CON3-R",
            "CON6-F", "CON6-G", "CON7-F", "CON7-G",
            "CON7-H", "CON7-A",
    };
    public static final String ID = "181F001F";

    @Override
    public String getId() {
        return ID;
    }


    private final ArrayList<ChannelData> dataList = new ArrayList<>();

    @Override
    public ByteCanBaseBean parse(byte[] CANContent) {
        super.parse(CANContent);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                int index = i * 4 + j;
                if (index < dataNameExtras.length) {
                    ChannelDataInput channelData = new ChannelDataInput(index);
                    channelData.setName(dataNameExtras[index]);
                    channelData.setValue(getStatus((bytes[i] >> (2 * j)) & 0b11));
                    dataList.add(channelData);
                } else {
                    break;
                }
            }
        }

        return this;
    }

    private String getStatus(int i) {
        String result = "无效";
        if (0b00 == i) {
            result = "开路";
        } else if (0b01 == i) {
            result = "闭合";
        }
        return result;
    }

    public ArrayList<ChannelData> getDataList() {
        return dataList;
    }
}