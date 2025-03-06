package com.senptec.control;


import com.senptec.adapter.CRC16Util;
import com.senptec.adapter.HexByteStrUtils;

import java.io.Serializable;

/**
 * CAN ID:0x18FA0117
 * 背光状态
 */
public class ScreenInfoEntity implements Serializable {

    /**
     * byte0 bit0-3 背光调节模式  0:手动调节  1：自动调节  2：智能模式 其它：无效
     */
    public String screenControlMode = "0";
    /**
     * byte0 bit4-7 预留
     */
    public String byte0_4_7 = "0";

    /**
     * byte1 白天模式背光值 1-100  百分比
     */
    public int dayLightLevel = 100;
    /**
     * byte2  预留
     */
    public String byte2 = "00";
    /**
     * byte3 夜晚模式背光值   1-100  百分比
     */
    public int nightLightLevel = 100;
    /**
     * byte4 预留
     */
    public String byte4 = "00";
    /**
     * byte5 bit0-1 显示屏背光控制 0：显示屏开  1：显示屏关  其它无效
     */
    public int screenState = 0;
    /**
     * byte5 bit2-3 小灯信号状态:  00：小灯未开启  01：小灯开启 其它：无效
     */
    public int dayNightState = 0;

    /**
     * byte5 bit4-7 预留
     */
    public String byte5_4_7 = "0";

    /**
     * byte6 预留
     */
    public String byte6 = "00";
    /**
     * byte7 预留
     */
    public String byte7 = "00";

    /**
     * 其他信息，发送拼装使用
     */
    public String ID = "18FA0117";

    public byte[] getCommand() {
        String byte0Str = screenControlMode + byte0_4_7;
        String byte1Str = HexByteStrUtils.intNum2hexString(dayLightLevel);
        String byte2Str = byte2;
        String byte3Str = HexByteStrUtils.intNum2hexString(nightLightLevel);
        String byte4Str = byte4;
        String byte5Str = Integer.toHexString(screenState << 2 + dayNightState).toUpperCase() + byte5_4_7;
        String byte6Str = byte6;
        String byte7Str = byte7;
        String kbpfStr = "1";
        //背光的控制报文在CAN通道1. 底层在转发时，通道会自动转换1
        String canChanelStr = "1";
        String command = "7E" + "0F" + "B1" + kbpfStr + canChanelStr + ID + byte0Str + byte1Str + byte2Str + byte3Str + byte4Str + byte5Str + byte6Str + byte7Str + "EF";
        byte[] commandBytes = HexByteStrUtils.toBytes(command);
        byte[] calcBytes = CRC16Util.getCalcBytes(commandBytes);
        return HexByteStrUtils.getAddBytes(commandBytes, calcBytes);
    }

    public static void main(String[] args) {
        ScreenInfoEntity screenInfoEntity = new ScreenInfoEntity();
        byte[] command = screenInfoEntity.getCommand();
        System.out.println(HexByteStrUtils.getStringFromBytes(command));

    }

    @Override
    public String toString() {
        return "ScreenInfoEntity{" +
                "screenControlMode='" + screenControlMode + '\'' +
                ", byte0_4_7='" + byte0_4_7 + '\'' +
                ", dayLightLevel=" + dayLightLevel +
                ", byte2='" + byte2 + '\'' +
                ", nightLightLevel=" + nightLightLevel +
                ", byte4='" + byte4 + '\'' +
                ", screenState=" + screenState +
                ", dayNightState=" + dayNightState +
                ", byte5_4_7='" + byte5_4_7 + '\'' +
                ", byte6='" + byte6 + '\'' +
                ", byte7='" + byte7 + '\'' +
                ", ID='" + ID + '\'' +
                '}';
    }
}
