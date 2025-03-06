package com.senptec.control.record;

import androidx.annotation.NonNull;

import org.litepal.crud.DataSupport;

public class DbBean extends DataSupport {
    public int csi; // 路号
    public int channel; // 通道号
    public String path;
    public long startTime = 0;
    public long endTime = 0;
    public static final int STORE_STATUS_IN = 0; //0：内置存储
    public static final int STORE_STATUS_OUT = 1; //1：外置存储
    public int storeStatus = STORE_STATUS_IN; // 存储状态

    public static final int RECORD_STATUS_DOING = 0; //0：录制未完成
    public static final int RECORD_STATUS_DONE = 1; //1：录制完成
    public static final int RECORD_STATUS_ERROR = 2; //2：录制异常
    public int recordStatus = RECORD_STATUS_DOING; // 录制状态

    public long size;

    @NonNull
    @Override
    public String toString() {
        return "csi:" + csi + "\n" +
                "channel:" + channel + "\n" +
                "path:" + path + "\n" +
                "startTime:" + startTime + "\n" +
                "endTime:" + endTime + "\n" +
                "storeStatus:" + storeStatus + "\n" +
                "recordStatus:" + recordStatus + "\n"
                ;
    }
}
