package com.senptec.adapter.bean.send.b1;

import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.Utils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.ByteKbpsChannelCmdBean;

/**
 * 描述：车内照明灯/车外氛围灯
 * 作者：hxl 2024/4/19 8:54
 * 修改描述：
 * 修改人：xxx 2024/4/19 8:54
 * 修改版本：
 */
public class CanSwitcher0218Bean extends ByteCanBaseBean {

    private static final byte DOME_LAMP_FLAG_ON = (byte) 0b00000001;
    private static final byte EX_AMBIENT_LAMP_FLAG_ON = (byte) 0b00000001 << 4;

    public static final String ID = "18FA0218";

    @Override
    public byte getCanChannel() {
        return ByteKbpsChannelCmdBean.CAN_CHANNEL_1;
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * 车内照明灯
     *
     * @param isOn
     */
    public void setDomeLamp(boolean isOn) {
        if (isOn) {
            bytes[2] = (byte) (getByte2() | DOME_LAMP_FLAG_ON);
        } else {
            bytes[2] = (byte) (getByte2() & ~DOME_LAMP_FLAG_ON);
        }
    }

    /**
     * 车内照明灯
     *
     * @return
     */
    public boolean getDomeLamp() {
        return DOME_LAMP_FLAG_ON == (byte) (getByte2() & DOME_LAMP_FLAG_ON);
    }

    /**
     * 车外氛围灯
     *
     * @param isOn
     */
    public void setExAmbientLamp(boolean isOn) {
        if (isOn) {
            bytes[6] = (byte) (getByte6() | EX_AMBIENT_LAMP_FLAG_ON);
        } else {
            bytes[6] = (byte) (getByte6() & ~EX_AMBIENT_LAMP_FLAG_ON);
        }
    }

    /**
     * 车外氛围灯
     *
     * @return
     */
    public boolean getExAmbientLamp() {
        return EX_AMBIENT_LAMP_FLAG_ON == (byte) (getByte6() & EX_AMBIENT_LAMP_FLAG_ON);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "byte2: " + HexByteStrUtils.hexString2binaryString(bytes[2]) + "\n" +
                "byte6: " + HexByteStrUtils.hexString2binaryString(bytes[6]) + "\n" +
                "{\n" +
                "车内照明灯: " + Utils.getOnOff(getDomeLamp()) + "\n" +
                "车外氛围灯: " + Utils.getOnOff(getExAmbientLamp()) + "\n" +
                "}";
    }
}
