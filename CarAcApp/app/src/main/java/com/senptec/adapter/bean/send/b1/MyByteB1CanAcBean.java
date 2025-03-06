package com.senptec.adapter.bean.send.b1;

import android.support.annotation.IntDef;

import androidx.annotation.NonNull;

import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.Utils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.ByteKbpsChannelCmdBean;

public class MyByteB1CanAcBean extends ByteCanBaseBean {
    public static final String ID = "10FFC2AE";

    @Override
    public byte getCanChannel() {
        return ByteKbpsChannelCmdBean.CAN_CHANNEL_1;
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * 空调电源开关 BYTE[3]
     */
    public static final byte AIR_POWER_OFF = (byte) 0b00000000;
    public static final byte AIR_POWER_ON = (byte) 0b00000001;

    @IntDef({AIR_POWER_OFF, AIR_POWER_ON})
    public @interface AirPower {
    }

    private byte airPower = AIR_POWER_OFF;

    /**
     * 空调模式设定
     */
    public static final byte AIR_MODE_NONE = (byte) 0b00000000;
    public static final byte AIR_MODE_AUTO = (byte) 0b00000001;
    public static final byte AIR_MODE_COOL = (byte) 0b00000010;
    public static final byte AIR_MODE_WIND = (byte) 0b00000011;
    public static final byte AIR_MODE_HEAT = (byte) 0b00000100;
    public static final byte AIR_MODE_COOL_A = (byte) 0b00000101;
    public static final byte AIR_MODE_COOL_B = (byte) 0b00000110;
    public static final byte AIR_MODE_COOL_C = (byte) 0b00000111;

    public static final byte AIR_MODE_HEAT_A = (byte) 0b00001000;
    public static final byte AIR_MODE_HEAT_B = (byte) 0b00001001;
    public static final byte AIR_MODE_HEAT_C = (byte) 0b00001010;

    public static final byte AIR_MODE_NONE_1 = (byte) 0;
    public static final byte AIR_MODE_AUTO_1 = (byte) 1;
    public static final byte AIR_MODE_COOL_1 = (byte) 2;
    public static final byte AIR_MODE_WIND_1 = (byte) 3;
    public static final byte AIR_MODE_HEAT_1 = (byte) 4;

    @IntDef({
            AIR_MODE_NONE_1, AIR_MODE_AUTO_1, AIR_MODE_COOL_1, AIR_MODE_WIND_1, AIR_MODE_HEAT_1
    })
    public @interface AirMode {
    }

    // 记录模式模式 byte[6]
    private byte airMode = AIR_MODE_NONE_1;

    public static final byte AIR_MODE_EXTRA_NONE = (byte) 0;
    public static final byte AIR_MODE_EXTRA_A = (byte) 1;
    public static final byte AIR_MODE_EXTRA_B = (byte) 2;
    public static final byte AIR_MODE_EXTRA_C = (byte) 3;

    @IntDef({
            AIR_MODE_EXTRA_NONE, AIR_MODE_EXTRA_A, AIR_MODE_EXTRA_B, AIR_MODE_EXTRA_C
    })
    public @interface AirModeExtra {
    }

    private byte airModeExtra = AIR_MODE_EXTRA_NONE;

    public void setAirPower(@AirPower byte airPower) {
        this.airPower = airPower;
    }

    public byte getAirPower() {
        return airPower;
    }

    public void setAirMode(@AirMode byte airMode) {
        this.airMode = airMode;
    }

    public void setAirModeExtra(@AirModeExtra byte airModeExtra) {
        this.airModeExtra = airModeExtra;
    }

    private static final byte WIND_SPEED_FLAG = 0b00000111;

    private int airWindSpeed;

    public void setAirWindSpeed(int airWindSpeed) {
        this.airWindSpeed = airWindSpeed;
    }

    public int getAirWindSpeed() {
        return airWindSpeed;
    }

    private double airTemperature;

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public static double getTempByValue(int i) {
        return 16 + i * 0.5;
    }

    public static int getValueByTemp(double airTemperature) {
        return (int) ((airTemperature - 16) * 2);
    }

    public void setPowerOn(boolean on) {
        setAirPower(on ? AIR_POWER_ON : AIR_POWER_OFF);
    }

    public void setAutoOn(boolean on) {
        if (on) {
            setAirMode(AIR_MODE_AUTO_1);
        } else {
            if (isAutoOn()) { // 只有是当前模式时才接受false的处理
                setAirMode(AIR_MODE_NONE_1);
            }
        }
    }

    public void setCoolOn(boolean on) {
        if (on) {
            setAirMode(AIR_MODE_COOL_1);
        } else {
            if (isCoolOn()) { // 只有是当前模式时才接受false的处理
                setAirMode(AIR_MODE_NONE_1);
            }
        }
    }

    public void setWindOn(boolean on) {
        if (on) {
            setAirMode(AIR_MODE_WIND_1);
        } else {
            if (isWindOn()) { // 只有是当前模式时才接受false的处理
                setAirMode(AIR_MODE_NONE_1);
            }
        }
    }

    public void setHeatOn(boolean on) {
        if (on) {
            setAirMode(AIR_MODE_HEAT_1);
        } else {
            if (isHeatOn()) { // 只有是当前模式时才接受false的处理
                setAirMode(AIR_MODE_NONE_1);
            }
        }
    }

    public void setEfficiencyOn(boolean on) {
        if (on) {
            setAirModeExtra(AIR_MODE_EXTRA_A);
        } else {
            if (isEfficiencyOn()) { // 只有是当前模式时才接受false的处理
                setAirModeExtra(AIR_MODE_EXTRA_NONE);
            }
        }
    }


    public void setInexpensiveOn(boolean on) {
        if (on) {
            setAirModeExtra(AIR_MODE_EXTRA_B);
        } else {
            if (isInexpensiveOn()) { // 只有是当前模式时才接受false的处理
                setAirModeExtra(AIR_MODE_EXTRA_NONE);
            }
        }
    }

    public void setEcoOn(boolean on) {
        if (on) {
            setAirModeExtra(AIR_MODE_EXTRA_C);
        } else {
            if (isEcoOn()) { // 只有是当前模式时才接受false的处理
                setAirModeExtra(AIR_MODE_EXTRA_NONE);
            }
        }
    }

    public boolean isPowerOn() {
        return airPower == AIR_POWER_ON;
    }

    public boolean isAutoOn() {
        return airMode == AIR_MODE_AUTO_1;
    }

    public boolean isCoolOn() {
        return airMode == AIR_MODE_COOL_1;
    }

    public boolean isWindOn() {
        return airMode == AIR_MODE_WIND_1;
    }

    public boolean isHeatOn() {
        return airMode == AIR_MODE_HEAT_1;
    }

    public boolean isEfficiencyOn() {
        return airModeExtra == AIR_MODE_EXTRA_A;
    }

    public boolean isInexpensiveOn() {
        return airModeExtra == AIR_MODE_EXTRA_B;
    }

    public boolean isEcoOn() {
        return airModeExtra == AIR_MODE_EXTRA_C;
    }

    public boolean release = false;

    @Override
    protected byte getByte0() {
        if (!isPowerOn()) {
            return AIR_MODE_NONE;
        }
        if (isAutoOn()) {
            return AIR_MODE_AUTO;
        } else if (isWindOn()) {
            return AIR_MODE_WIND;
        } else if (isCoolOn()) {
            if (isEfficiencyOn()) {
                return AIR_MODE_COOL_A;
            } else if (isInexpensiveOn()) {
                return AIR_MODE_COOL_C;
            } else if (isEcoOn()) {
                return AIR_MODE_COOL_B;
            } else {
                return AIR_MODE_COOL;
            }
        } else if (isHeatOn()) {
            // 认为是制热
            if (isEfficiencyOn()) {
                return AIR_MODE_HEAT_A;
            } else if (isInexpensiveOn()) {
                return AIR_MODE_HEAT_C;
            } else if (isEcoOn()) {
                return AIR_MODE_HEAT_B;
            } else {
                return AIR_MODE_HEAT;
            }
        } else {
            return AIR_MODE_NONE;
        }
    }

    @Override
    protected byte getByte1() {
        // double转byte
        return (byte) ((airTemperature + 30) * 2);
    }

    @Override
    protected byte getByte2() {
        return (byte) (airWindSpeed & WIND_SPEED_FLAG);
    }

    @Override
    protected byte getByte3() {
        return airPower;
    }

    @Override
    protected byte getByte4() {
        // 调远程控制有效 使用中控控制，无效使用实体按钮控制, 释放前应先发关闭报文
        if (isPowerOn()) {
            return (byte) 0b00001111;
        } else {

            return (byte) (!release ? 0b00001111 : 0b00000000);
        }

    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() +
                "{\n" +
                "power: " + Utils.getOnOff(isPowerOn()) + "\n" +
                "AUTO: " + Utils.getOnOff(isAutoOn()) + "\n" +
                "cool: " + Utils.getOnOff(isCoolOn()) + "\n" +
                "wind: " + Utils.getOnOff(isWindOn()) + "\n" +
                "heal: " + Utils.getOnOff(isHeatOn()) + "\n" +
                "高效: " + Utils.getOnOff(isEfficiencyOn()) + "\n" +
                "经济: " + Utils.getOnOff(isInexpensiveOn()) + "\n" +
                "ECO: " + Utils.getOnOff(isEcoOn()) + "\n" +
                "temperature: " + (HexByteStrUtils.bytes2Int(new byte[]{bytes[1]}) * 0.5 - 30) + "\n" +
                "windSpeed: " + airWindSpeed + "\n" +
                "}";
    }
}
