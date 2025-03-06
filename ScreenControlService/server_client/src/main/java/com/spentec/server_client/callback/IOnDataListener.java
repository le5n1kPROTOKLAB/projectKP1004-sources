package com.spentec.server_client.callback;

public interface IOnDataListener {
    void onReceive(String id, String gsonData);
    void onSend(String id, String gsonData);
}
