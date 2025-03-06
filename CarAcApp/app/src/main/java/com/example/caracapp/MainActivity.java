package com.example.caracapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.caracapp.databinding.ActivityMainBinding;
import com.gyf.immersionbar.ImmersionBar;
import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.LoggerUtil;
import com.senptec.adapter.bean.receive.d1.MyByteD1CanAcBean;
import com.senptec.adapter.bean.send.b1.MyByteB1CanAcBean;
import com.senptec.common.ui.util.DensityHelper;
import com.senptec.common.ui.util.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends Activity {



    Handler mHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == CMD) {
                if (isColse) {
                    if (closeCount >= 3) {
                        isColse = false;
                        closeCount = 0;

                    } else {
                        // 记录发送
                        closeCount++;
                        // 交替发送一次关报文闭和释放报文
                        byteB1CanAcBean.release = false;
                        MyApplication.getInstance().sendData(byteB1CanAcBean.getId(),
                                MyApplication.getInstance().gson.toJson(byteB1CanAcBean.toByteArray()));
                        byteB1CanAcBean.release = true;
                        StateManager.getInstance().saveEntityAndSendCommands();
                    }
                } else {
                    closeCount = 0;
                }
                MyApplication.getInstance().sendData(byteB1CanAcBean.getId(),
                        MyApplication.getInstance().gson.toJson(byteB1CanAcBean.toByteArray()));
                sendEmptyMessageDelayed(CMD, 1000);
            }
        }
    };

    private final int CMD = 111111;
    MyByteB1CanAcBean byteB1CanAcBean;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        EventBus.getDefault().register(this);


        /*new Handler().postDelayed(() -> { //тест отправки
            MyByteD1CanAcBean bean = new MyByteD1CanAcBean();
            bean.parse(new byte[]{0, 0, 0, (byte) 0x64, (byte) 0x64, 0, 0, 0}); // Температура 20.0
            EventBus.getDefault().post(bean);
        }, 2000);*/
    }

    private ArrayList<CheckImageView> linkCheckImageViews = new ArrayList<>();
    private ArrayList<CheckImageView> linkCheckImageViews_Extra = new ArrayList<>();

    private boolean isColse = false;
    int closeCount = 0;

    @SuppressLint("DefaultLocale")
    private void updateUI() {
        DensityHelper.setCustomDensity(this);
        ImmersionBar.with(this).fullScreen(true).init();
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this), null, false);
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        linkCheckImageViews.add(mBinding.civAutoController);
        linkCheckImageViews.add(mBinding.civColdController);
        linkCheckImageViews.add(mBinding.civWindController);
        linkCheckImageViews.add(mBinding.civHeatController);

        linkCheckImageViews_Extra.add(mBinding.civEfficiencyController);
        linkCheckImageViews_Extra.add(mBinding.civInexpensiveController);
        linkCheckImageViews_Extra.add(mBinding.civEcoController);

        ScreenUtil.setWallPaper(rootView);
        setTemp(getString(R.string.default_value), getString(R.string.default_value));

        /*setTemp("25.0", "30.0"); // тест хардкода температуры*/

        // TODO 需要设置默认值
        byteB1CanAcBean = StateManager.getInstance().getByteB1CanAcBean();
        mBinding.npT.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.npT.setValue(1);
                byteB1CanAcBean.setAirTemperature(MyByteB1CanAcBean.getTempByValue(1));
                StateManager.getInstance().saveEntityAndSendCommands();
                mBinding.npT.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mBinding.seekbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.seekbar.setValue(1);
                byteB1CanAcBean.setAirWindSpeed(1);
                StateManager.getInstance().saveEntityAndSendCommands();
                mBinding.seekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mBinding.seekbar.setValue(byteB1CanAcBean.getAirWindSpeed());
        mBinding.npT.setValue(MyByteB1CanAcBean.getValueByTemp(byteB1CanAcBean.getAirTemperature()));
        mBinding.civPowerController.setOnCheckedChangeListener(isChecked -> {
            // 空调开关操作
            if (isChecked) {
                if (byteB1CanAcBean != null) {
                    byteB1CanAcBean.setPowerOn(true);
                    byteB1CanAcBean.release = false;
                    if (isColse) {
                        // 如果点完关闭后还再发关闭报文，未释放时，又点击打开，那么不取消后续释放标志
                        isColse = false;
                    }
                    StateManager.getInstance().saveEntityAndSendCommands();
                    excuteSendAtOnce();
                    updateSwitchUI(byteB1CanAcBean);
                }
                mBinding.civAutoController.setEnabled(true);
                mBinding.civColdController.setEnabled(true);
                mBinding.civWindController.setEnabled(true);
                mBinding.civHeatController.setEnabled(true);
                mBinding.civEfficiencyController.setEnabled(true);
                mBinding.civInexpensiveController.setEnabled(true);
                mBinding.civEcoController.setEnabled(true);
            } else {
                Log.i("mCivPowerController", "power is off");
                if (byteB1CanAcBean != null) {
                    byteB1CanAcBean.setPowerOn(false);
                    isColse = true;
                    StateManager.getInstance().saveEntityAndSendCommands();
                    excuteSendAtOnce();
                }
                colseSwitch(mBinding.civAutoController);
                colseSwitch(mBinding.civColdController);
                colseSwitch(mBinding.civWindController);
                colseSwitch(mBinding.civHeatController);
                colseSwitch(mBinding.civEfficiencyController);
                colseSwitch(mBinding.civInexpensiveController);
                colseSwitch(mBinding.civEcoController);
            }
            if (byteB1CanAcBean == null) {
                Toast.makeText(this, "设备未完成初始化", Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.civPowerController.setChecked(byteB1CanAcBean.isPowerOn());
        excuteSendAtOnce();
        LoggerUtil.e(HexByteStrUtils.getStringFromBytes(byteB1CanAcBean.create().toByteArray()));
        LoggerUtil.e(byteB1CanAcBean.toString());

        setOnCheckedChangeListener(mBinding.civAutoController, linkCheckImageViews);
        setOnCheckedChangeListener(mBinding.civColdController, linkCheckImageViews);
        setOnCheckedChangeListener(mBinding.civWindController, linkCheckImageViews);
        setOnCheckedChangeListener(mBinding.civHeatController, linkCheckImageViews);

        setOnCheckedChangeListener(mBinding.civEfficiencyController, linkCheckImageViews_Extra);
        setOnCheckedChangeListener(mBinding.civInexpensiveController, linkCheckImageViews_Extra);
        setOnCheckedChangeListener(mBinding.civEcoController, linkCheckImageViews_Extra);

        mBinding.ivbtTUp.setOnClickListener(v -> mBinding.npT.smoothScroll(false, 1));
        mBinding.ivbtDown.setOnClickListener(v -> mBinding.npT.smoothScroll(true, 1));
        mBinding.ivbtSUp.setOnClickListener(v -> mBinding.seekbar.smoothScroll(false, 1));
        mBinding.ivbtSDown.setOnClickListener(v -> mBinding.seekbar.smoothScroll(true, 1));

        //
        int maxValue = mBinding.npT.getMaxValue();
        int minValue = mBinding.npT.getMinValue();
        int valueNumber = maxValue - minValue + 1;
        String[] showTexts = new String[valueNumber];
        for (int i = 0; i < showTexts.length; i++) {
            showTexts[i] = String.format("%.1f", MyByteB1CanAcBean.getTempByValue(i));
        }
        mBinding.npT.setDisplayedValues(showTexts);

        mBinding.npT.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (byteB1CanAcBean == null) {
                Toast.makeText(this, "设备未完成初始化", Toast.LENGTH_SHORT).show();
                return;
            }
            byteB1CanAcBean.setAirTemperature(MyByteB1CanAcBean.getTempByValue(newVal));
            StateManager.getInstance().saveEntityAndSendCommands();
            updateSwitchUI(byteB1CanAcBean);
        });

        mBinding.seekbar.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (byteB1CanAcBean == null) {
                Toast.makeText(this, "设备未完成初始化", Toast.LENGTH_SHORT).show();
                return;
            }
            byteB1CanAcBean.setAirWindSpeed(newVal);
            StateManager.getInstance().saveEntityAndSendCommands();
        });
    }

    private void excuteSendAtOnce() {
        // 取消任务，再立即开启任务，相当于立刻执行一次
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(CMD);
    }

    private void setOnCheckedChangeListener(CheckImageView civController, ArrayList<CheckImageView> linkCheckImageViews) {
        civController.setOnCheckedChangeListener(isChecked -> {
            if (!isNeedUpdateData) return;
            // 空调自动
            if (!mBinding.civPowerController.isChecked()) return;
            LoggerUtil.d("setOnCheckedChangeListener start civAutoController " + isChecked);
            // 只有在空调开关为ON状态时才执行以下操作
            if (byteB1CanAcBean == null) {
                Toast.makeText(this, "设备未完成初始化", Toast.LENGTH_SHORT).show();
                return;
            }
            colseLinkSwitch(civController, linkCheckImageViews);
            if (civController == mBinding.civAutoController) {
                byteB1CanAcBean.setAutoOn(isChecked);
            } else if (civController == mBinding.civColdController) {
                byteB1CanAcBean.setCoolOn(isChecked);
            } else if (civController == mBinding.civWindController) {
                byteB1CanAcBean.setWindOn(isChecked);
            } else if (civController == mBinding.civHeatController) {
                byteB1CanAcBean.setHeatOn(isChecked);
            } else if (civController == mBinding.civEfficiencyController) {
                byteB1CanAcBean.setEfficiencyOn(isChecked);
            } else if (civController == mBinding.civInexpensiveController) {
                byteB1CanAcBean.setInexpensiveOn(isChecked);
            } else if (civController == mBinding.civEcoController) {
                byteB1CanAcBean.setEcoOn(isChecked);
            }
            StateManager.getInstance().saveEntityAndSendCommands();
            LoggerUtil.d("setOnCheckedChangeListener end civAutoController");
            updateSwitchUI(byteB1CanAcBean);
        });
    }

    private void setTemp(String in, String out) {
        updateWhenValueChange(mBinding.tempIn, String.format(getString(R.string.car_temp_degree), in));
        updateWhenValueChange(mBinding.tempOut, String.format(getString(R.string.car_temp_degree), out));
    }

    private void updateWhenValueChange(TextView textView, String newValue) {
        String oldValue = textView.getText().toString();
        if (!TextUtils.equals(oldValue, newValue)) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(newValue);
                }
            });
        }
    }

    private boolean isNeedUpdateData = true;

    private void updateSwitchUI(MyByteB1CanAcBean byteB1CanAcBean) {
        isNeedUpdateData = false;
        boolean isPowerOn = byteB1CanAcBean.isPowerOn();
        boolean isAutoOn = byteB1CanAcBean.isAutoOn();
        boolean isCoolOn = byteB1CanAcBean.isCoolOn();
        boolean isWindOn = byteB1CanAcBean.isWindOn();
        boolean isEfficiencyOn = byteB1CanAcBean.isEfficiencyOn();
        boolean isInexpensiveOn = byteB1CanAcBean.isInexpensiveOn();
        boolean isEcoOn = byteB1CanAcBean.isEcoOn();
        boolean isHeatOn = byteB1CanAcBean.isHeatOn();

        // 开关状态不能在此更新
        mBinding.civAutoController.setChecked(isPowerOn && isAutoOn);
        mBinding.civColdController.setChecked(isPowerOn && isCoolOn);
        mBinding.civWindController.setChecked(isPowerOn && isWindOn);
        mBinding.civHeatController.setChecked(isPowerOn && isHeatOn);
        mBinding.civEfficiencyController.setChecked(isPowerOn && isEfficiencyOn);
        mBinding.civInexpensiveController.setChecked(isPowerOn && isInexpensiveOn);
        mBinding.civEcoController.setChecked(isPowerOn && isEcoOn);
        isNeedUpdateData = true;
    }

    // 激活时的联动，按键互斥（只能一个按键有效）
    private void colseLinkSwitch(CheckImageView activeSwitch, ArrayList<CheckImageView> linkCheckImageViews) {
        if (linkCheckImageViews == null) return;
        // 只有控件激活时才会触发其他两个按钮的联动，否则会无限触发
        if (!activeSwitch.isChecked()) return;
        for (CheckImageView linkCheckImageView : linkCheckImageViews) {
            if (linkCheckImageView != activeSwitch) {
                linkCheckImageView.setChecked(false);
            }
        }
    }

    private void colseSwitch(CheckImageView controller) {
        if (controller.isChecked()) {
            controller.setChecked(false);
        }
        controller.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void airConditioner(MyByteD1CanAcBean byteD1CanAcBean) {
        /*runOnUiThread(() -> {
            mBinding.indicatorText.setVisibility(View.VISIBLE);  // Показать индикатор
        });*/
        setTemp(byteD1CanAcBean.getAirInTemperatureStr(), byteD1CanAcBean.getAirOutTemperatureStr());
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataReceived(DataReceivedEvent event) {
        mBinding.indicatorReceive.setVisibility(View.VISIBLE);  // Показать индикатор для onReceive
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCanIdReceived(CanIdReceivedEvent event) {
        String id = event.getId();
        mBinding.tvCanId.setText("CAN ID: " + id); // Обновляем TextView
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveCount(ReceiveCountEvent event) {
        int count = event.getCount();
        mBinding.tvReceiveCount.setText("Receive Count: " + count);
    }*/
}