package com.example.carinfoapp.can;

import com.senptec.adapter.bean.receive.d1.channel.ChannelData;

public class ChannelDataInput extends ChannelData {
    public ChannelDataInput(int index) {
        super(0, index);
    }

    private String name;

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
