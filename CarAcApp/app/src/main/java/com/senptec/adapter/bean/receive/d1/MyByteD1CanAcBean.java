package com.senptec.adapter.bean.receive.d1;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.bean.ByteCanBaseBean;

public class MyByteD1CanAcBean extends ByteCanBaseBean {
    public static final String ID = "18FAC081";

    /**
     * 回风温度(车内温度)
     */
    private double airInTemperature;

    public double getAirInTemperature() {
        return airInTemperature;
    }

    public String getAirInTemperatureStr() {
        return getNeedStr(airInTemperature);
    }

    private double airOutTemperature;

    public double getAirOutTemperature() {
        return airOutTemperature;
    }

    public String getAirOutTemperatureStr() {
        return getNeedStr(airOutTemperature);
    }


    @SuppressLint("DefaultLocale")
    private String getNeedStr(double airTemperature) {
        if (airTemperature < -30 || airTemperature > 97)
            return "--";
        return String.format("%.1f", airTemperature);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ByteCanBaseBean parse(byte[] CANContent) {
        super.parse(CANContent);
        airInTemperature = HexByteStrUtils.bytes2Int(new byte[]{bytes[3]}) * 0.5 - 30;
        airOutTemperature = HexByteStrUtils.bytes2Int(new byte[]{bytes[4]}) * 0.5 - 30;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "{\n" +
                "车内温度: " + airInTemperature + "\n" +
                "车外温度: " + airOutTemperature + "\n" +
                "}";
    }
}
