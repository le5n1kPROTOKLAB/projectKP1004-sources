package com.spentec.server_client.record;

public class RecordRequest {
    public String recordTag;
    public int csi;
    public int channel;
    public long startTime;
    public long endTime;
    public OnResponseListener onResponseListener;
}
