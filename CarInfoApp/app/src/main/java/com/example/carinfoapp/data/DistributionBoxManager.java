package com.example.carinfoapp.data;

import android.os.SystemClock;
import android.text.TextUtils;

import com.example.carinfoapp.can.InputCanBean;
import com.senptec.adapter.bean.receive.d1.channel.ByteD1CanChannel;
import com.senptec.adapter.bean.receive.d1.channel.ByteD1CanChannelAmpere;
import com.senptec.adapter.bean.receive.d1.channel.ByteD1CanChannelStatus;
import com.senptec.adapter.bean.receive.d1.channel.ChannelData;
import com.senptec.adapter.bean.receive.d1.channel.ChannelDataShow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DistributionBoxManager {
    public final ArrayList<ChannelDataShow> mList = new ArrayList<>();
    private final HashMap<String, ChannelDataShow> channelDataShowHashMap = new HashMap<>();

    public DistributionBoxManager() {
        EventBus.getDefault().register(this);
        for (String s : ChannelData.dataNameArray) {
            ChannelDataShow channelDataShow = new ChannelDataShow(s);
            channelDataShowHashMap.put(s, channelDataShow);
            mList.add(channelDataShow);
        }
        for (String s : InputCanBean.dataNameExtras) {
            ChannelDataShow channelDataShow = new ChannelDataShow(s);
            channelDataShow.setAmpere("");
            channelDataShowHashMap.put(s, channelDataShow);
            mList.add(channelDataShow);
        }
    }

    private void test() {
        new Thread(new Runnable() {
            private final Random random = new Random();

            @Override
            public void run() {
                while (true) {
                    int i = random.nextInt(10);
                    System.out.println("=================" + i + "/" + mList.size());
                    ChannelDataShow channelDataShow = mList.get(i);
                    channelDataShow.setAmpere(String.valueOf(random.nextFloat()));
                    channelDataShow.setStatus(String.valueOf(random.nextFloat()));
                    for (OnCarInfoListener onCarInfoListener : onCarInfoListeners) {
                        onCarInfoListener.onChanged(i, channelDataShow);
                    }
                    SystemClock.sleep(1000);
                }
            }
        }).start();
    }

    public ArrayList<ChannelDataShow> getList() {
        return mList;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCarInfoEntity(InputCanBean inputCanBean) {
        updateChannelData(inputCanBean.getDataList());
    }

    private void updateChannelData(List<? extends ChannelData> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            ChannelData channelData = dataList.get(i);
            ChannelDataShow channelDataShow = channelDataShowHashMap.get(channelData.getName());
            if (channelDataShow != null) {
                boolean hasValueChanged = false;
                if (!TextUtils.equals(channelDataShow.getStatus(), channelData.getValue())) {
                    hasValueChanged = true;
                    channelDataShow.setStatus(channelData.getValue());
                }
                if (hasValueChanged) {
                    for (OnCarInfoListener onCarInfoListener : onCarInfoListeners) {
                        onCarInfoListener.onChanged(ChannelData.dataNameArray.length + i, channelDataShow);
                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCarInfoEntity(ByteD1CanChannelAmpere ampere) {
        updateChannelData(ampere);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCarInfoEntity(ByteD1CanChannelStatus status) {
        updateChannelData(status);
    }

    private void updateChannelData(ByteD1CanChannel<? extends ChannelData> canChannel) {
        ArrayList<? extends ChannelData> datalist = canChannel.getDatalist();
        for (int i = 0; i < datalist.size(); i++) {
            ChannelData channelData = datalist.get(i);
            ChannelDataShow channelDataShow = channelDataShowHashMap.get(channelData.getName());
            if (channelDataShow != null) {
                boolean hasValueChanged = false;
                if (canChannel instanceof ByteD1CanChannelStatus) {
                    if (!TextUtils.equals(channelDataShow.getStatus(), channelData.getValue())) {
                        hasValueChanged = true;
                        channelDataShow.setStatus(channelData.getValue());
                    }
                } else {
                    if (!TextUtils.equals(channelDataShow.getAmpere(), channelData.getValue())) {
                        hasValueChanged = true;
                        channelDataShow.setAmpere(channelData.getValue());
                    }
                }
                if (hasValueChanged) {
                    for (OnCarInfoListener onCarInfoListener : onCarInfoListeners) {
                        onCarInfoListener.onChanged(canChannel.getStartPosition() + i, channelDataShow);
                    }
                }
            }
        }
    }

    public interface OnCarInfoListener {
        void onChanged(int index, ChannelDataShow channelDataShow);
    }

    private final ArrayList<OnCarInfoListener> onCarInfoListeners = new ArrayList<>();

    public void addOnCarInfoListener(OnCarInfoListener listener) {
        if (listener != null) {
            onCarInfoListeners.add(listener);
        }
    }

    public void removeOnCarInfoListener(OnCarInfoListener listener) {
        if (listener != null) {
            onCarInfoListeners.remove(listener);
        }
    }
}
