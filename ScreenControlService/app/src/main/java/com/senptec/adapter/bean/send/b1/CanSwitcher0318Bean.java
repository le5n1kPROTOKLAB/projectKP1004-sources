package com.senptec.adapter.bean.send.b1;

import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.Utils;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.ByteKbpsChannelCmdBean;

/**
 * 描述：氢堆加水除气/EV模式/方向盘调节/车内氛围灯/油门互锁/仪表按键开关（上一步）/仪表按键开关（下一步）/仪表按键开关（返回）/仪表按键开关（确认）/AEBS/ADAS
 * 作者：hxl 2024/4/19 8:54
 * 修改描述：
 * 修改人：xxx 2024/4/19 8:54
 * 修改版本：
 */
public class CanSwitcher0318Bean extends ByteCanBaseBean {

    private static final byte FCUDEGAS_FLAG_ON = (byte) 0b00000001 << 4;
    private static final byte EV_MODEL_FLAG_ON = (byte) 0b00000001;
    private static final byte STEERING_WHEEL_ADJUST_FLAG_ON = (byte) 0b00000001 << 2;
    private static final byte IN_AMBIENT_LAMP_FLAG_ON = (byte) 0b00000001;
    private static final byte ACCELERATOR_INTERLOCK_FLAG_ON = (byte) 0b00000001 << 2;
    private static final byte ICM_BUTTON_UP_FLAG_ON = (byte) 0b00000001 << 2;
    private static final byte ICM_BUTTON_DOWN_FLAG_ON = (byte) 0b00000001 << 4;
    private static final byte ICM_BUTTON_BACK_FLAG_ON = (byte) 0b00000001;
    private static final byte ICM_BUTTON_MENU_FLAG_ON = (byte) 0b01000000;
    private static final byte AEBS_FLAG_ON = (byte) 0b00000001 ;
    private static final byte ADAS_FLAG_ON = (byte) 0b01000000;

    public static final String ID = "18FA0318";

    @Override
    public byte getCanChannel() {
        return ByteKbpsChannelCmdBean.CAN_CHANNEL_1;
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * 氢堆加水除气
     *
     * @param isOn
     */
    public void setFCUDegas(boolean isOn) {
        if (isOn) {
            bytes[2] = (byte) (getByte2() | FCUDEGAS_FLAG_ON);
        } else {
            bytes[2] = (byte) (getByte2() & ~FCUDEGAS_FLAG_ON);
        }
    }

    /**
     * 氢堆加水除气
     *
     * @return
     */
    public boolean getFCUDegas() {
        return FCUDEGAS_FLAG_ON == (byte) (getByte2() & FCUDEGAS_FLAG_ON);
    }

    /**
     * EV模式
     *
     * @param isOn
     */
    public void setEVModel(boolean isOn) {
        if (isOn) {
            bytes[4] = (byte) (getByte4() | EV_MODEL_FLAG_ON);
        } else {
            bytes[4] = (byte) (getByte4() & ~EV_MODEL_FLAG_ON);
        }
    }

    /**
     * EV模式
     *
     * @return
     */
    public boolean getEVModel() {
        return EV_MODEL_FLAG_ON == (byte) (getByte4() & EV_MODEL_FLAG_ON);
    }

    /**
     * 方向盘调节
     *
     * @param isOn
     */
    public void setSteeringWheelAdjust(boolean isOn) {
        if (isOn) {
            bytes[4] = (byte) (getByte4() | STEERING_WHEEL_ADJUST_FLAG_ON);
        } else {
            bytes[4] = (byte) (getByte4() & ~STEERING_WHEEL_ADJUST_FLAG_ON);
        }
    }

    /**
     * 方向盘调节
     *
     * @return
     */
    public boolean getSteeringWheelAdjust() {
        return STEERING_WHEEL_ADJUST_FLAG_ON == (byte) (getByte4() & STEERING_WHEEL_ADJUST_FLAG_ON);
    }

    /**
     * 车内氛围灯
     *
     * @param isOn
     */
    public void setINAmbientLamp(boolean isOn) {
        if (isOn) {
            bytes[2] = (byte) (getByte2() | IN_AMBIENT_LAMP_FLAG_ON);
        } else {
            bytes[2] = (byte) (getByte2() & ~IN_AMBIENT_LAMP_FLAG_ON);
        }
    }

    /**
     * 车内氛围灯
     *
     * @return
     */
    public boolean getINAmbientLamp() {
        return IN_AMBIENT_LAMP_FLAG_ON == (byte) (getByte2() & IN_AMBIENT_LAMP_FLAG_ON);
    }

    /**
     * 油门互锁
     *
     * @param isOn
     */
    public void setAcceleratorInterlock(boolean isOn) {
        if (isOn) {
            bytes[5] = (byte) (getByte5() | ACCELERATOR_INTERLOCK_FLAG_ON);
        } else {
            bytes[5] = (byte) (getByte5() & ~ACCELERATOR_INTERLOCK_FLAG_ON);
        }
    }

    /**
     * 油门互锁
     *
     * @return
     */
    public boolean getAcceleratorInterlock() {
        return ACCELERATOR_INTERLOCK_FLAG_ON == (byte) (getByte5() & ACCELERATOR_INTERLOCK_FLAG_ON);
    }

    /**
     * 仪表按键开关（上一步）
     *
     * @param isOn
     */
    public void setICMButtonUP(boolean isOn) {
        if (isOn) {
            bytes[3] = (byte) (getByte3() | ICM_BUTTON_UP_FLAG_ON);
        } else {
            bytes[3] = (byte) (getByte3() & ~ICM_BUTTON_UP_FLAG_ON);
        }
    }

    /**
     * 仪表按键开关（上一步）
     *
     * @return
     */
    public boolean getICMButtonUp() {
        return ICM_BUTTON_UP_FLAG_ON == (byte) (getByte3() & ICM_BUTTON_UP_FLAG_ON);
    }

    /**
     * 仪表按键开关（下一步）
     *
     * @param isOn
     */
    public void setICMButtonDown(boolean isOn) {
        if (isOn) {
            bytes[3] = (byte) (getByte3() | ICM_BUTTON_DOWN_FLAG_ON);
        } else {
            bytes[3] = (byte) (getByte3() & ~ICM_BUTTON_DOWN_FLAG_ON);
        }
    }

    /**
     * 仪表按键开关（下一步）
     *
     * @return
     */
    public boolean getICMButtonDown() {
        return ICM_BUTTON_DOWN_FLAG_ON == (byte) (getByte3() & ICM_BUTTON_DOWN_FLAG_ON);
    }

    /**
     * 仪表按键开关（返回）
     *
     * @param isOn
     */
    public void setICMButtonBack(boolean isOn) {
        if (isOn) {
            bytes[3] = (byte) (getByte3() | ICM_BUTTON_BACK_FLAG_ON);
        } else {
            bytes[3] = (byte) (getByte3() & ~ICM_BUTTON_BACK_FLAG_ON);
        }
    }

    /**
     * 仪表按键开关（返回）
     *
     * @return
     */
    public boolean getICMButtonBack() {
        return ICM_BUTTON_BACK_FLAG_ON == (byte) (getByte3() & ICM_BUTTON_BACK_FLAG_ON);
    }

    /**
     * 仪表按键开关（确认）
     *
     * @param isOn
     */
    public void setICMButtonMenu(boolean isOn) {
        if (isOn) {
            bytes[3] = (byte) (getByte3() | ICM_BUTTON_MENU_FLAG_ON);
        } else {
            bytes[3] = (byte) (getByte3() & ~ICM_BUTTON_MENU_FLAG_ON);
        }
    }

    /**
     * 仪表按键开关（确认）
     *
     * @return
     */
    public boolean getICMButtonMenu() {
        return ICM_BUTTON_MENU_FLAG_ON == (byte) (getByte3() & ICM_BUTTON_MENU_FLAG_ON);
    }

    /**
     * AEBS
     *
     * @param isOn
     */
    public void setAEBS(boolean isOn) {
        if (isOn) {
            bytes[5] = (byte) (getByte5() | AEBS_FLAG_ON);
        } else {
            bytes[5] = (byte) (getByte5() & ~AEBS_FLAG_ON);
        }
    }

    /**
     * AEBS
     *
     * @return
     */
    public boolean getAEBS() {
        return AEBS_FLAG_ON == (byte) (getByte5() & AEBS_FLAG_ON);
    }

    /**
     * ADAS
     *
     * @param isOn
     */
    public void setADAS(boolean isOn) {
        if (isOn) {
            bytes[4] = (byte) (getByte4() | ADAS_FLAG_ON);
        } else {
            bytes[4] = (byte) (getByte4() & ~ADAS_FLAG_ON);
        }
    }

    /**
     * ADAS
     *
     * @return
     */
    public boolean getADAS() {
        return ADAS_FLAG_ON == (byte) (getByte4() & ADAS_FLAG_ON);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "byte0: " + HexByteStrUtils.hexString2binaryString(bytes[0]) + "\n" +
                "byte2: " + HexByteStrUtils.hexString2binaryString(bytes[2]) + "\n" +
                "byte3: " + HexByteStrUtils.hexString2binaryString(bytes[3]) + "\n" +
                "byte4: " + HexByteStrUtils.hexString2binaryString(bytes[4]) + "\n" +
                "byte5: " + HexByteStrUtils.hexString2binaryString(bytes[5]) + "\n" +
                "{\n" +
                "氢堆加水除气: " + Utils.getOnOff(getFCUDegas()) + "\n" +
                "EV模式: " + Utils.getOnOff(getEVModel()) + "\n" +
                "方向盘调节: " + Utils.getOnOff(getSteeringWheelAdjust()) + "\n" +
                "车内氛围灯: " + Utils.getOnOff(getINAmbientLamp()) + "\n" +
                "油门互锁: " + Utils.getOnOff(getAcceleratorInterlock()) + "\n" +
                "仪表按键开关（上一步）: " + Utils.getOnOff(getICMButtonUp()) + "\n" +
                "仪表按键开关（下一步）: " + Utils.getOnOff(getICMButtonDown()) + "\n" +
                "仪表按键开关（返回）: " + Utils.getOnOff(getICMButtonBack()) + "\n" +
                "仪表按键开关（确认）: " + Utils.getOnOff(getICMButtonMenu()) + "\n" +
                "AEBS: " + Utils.getOnOff(getAEBS()) + "\n" +
                "ADAS: " + Utils.getOnOff(getADAS()) + "\n" +
                "}";
    }
}
