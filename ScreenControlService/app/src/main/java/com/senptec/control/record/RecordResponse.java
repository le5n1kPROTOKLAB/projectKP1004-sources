package com.senptec.control.record;

import java.util.List;

public class RecordResponse {
    public String tag;
    public long startTime;
    public long endTime;
    public int packageCount; // 总包数
    public int packageNumber; // 当前包号
    public int totalCount; // 总数量
    public int count; // 本次数量
    public List<Data> dataList;

    public static class Data {
        public int csi; // 路号
        public int channel; // 通道号
        public String path;
        public long startTime = 0;
        public long endTime = 0;
    }
}
