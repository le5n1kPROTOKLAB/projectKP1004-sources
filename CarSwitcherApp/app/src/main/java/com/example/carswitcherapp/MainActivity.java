package com.example.carswitcherapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.media.AudioManager;

import com.example.carswitcherapp.common.utils.CanServerHelper;
import com.example.carswitcherapp.common.utils.StateManager;
import com.example.carswitcherapp.common.view.MyCheckImageView;
import com.example.carswitcherapp.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.senptec.adapter.HexByteStrUtils;
import com.senptec.adapter.LoggerUtil;
import com.senptec.adapter.bean.ByteCanBaseBean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0218Bean;
import com.senptec.adapter.bean.send.b1.CanSwitcher0318Bean;
import com.senptec.common.ui.util.DensityHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class MainActivity extends Activity {
    public HashMap<Integer, ActivityMainBinding> bindingHashMap = new HashMap<>();
    private ActivityMainBinding mBinding;
    private CanSwitcher0318Bean mCanSwitcher0318Bean;
    private CanSwitcher0218Bean mCanSwitcher0218Bean;
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initCanSwitcher0318Data();
        initCanSwitcher0218Data();
    }

    private void initView() {
        int orientation = getResources().getConfiguration().orientation;
        DensityHelper.setCustomDensity(this);
        ImmersionBar.with(this).fullScreen(true).init();
        mBinding = bindingHashMap.get(orientation);
        if (mBinding == null) {
            mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this), null, false);
            bindingHashMap.put(orientation, mBinding);
        }
        setContentView(mBinding.getRoot());
    }

    private void initCanSwitcher0318Data() {
        mCanSwitcher0318Bean = StateManager.getInstance().getCanSwitcher0318Bean();
        //车内氛围灯
        mBinding.ivInAmbient.setIconDrawable(getDrawable(R.drawable.selector_in_ambient));
        mBinding.ivInAmbient.setName(getString(R.string.in_ambient));
        mBinding.ivInAmbient.setLevel(mCanSwitcher0318Bean.getINAmbientLamp() ? 1 : 0);
        mBinding.ivInAmbient.setLongPressTime(0);
        mBinding.ivInAmbient.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setINAmbientLamp(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //仪表按键开关（上一步）
        mBinding.ivIcmButtonUp.setIconDrawable(getDrawable(R.drawable.selector_icm_button_up));
        mBinding.ivIcmButtonUp.setName(getString(R.string.icm_button_up));
        mBinding.ivIcmButtonUp.setLevel(mCanSwitcher0318Bean.getICMButtonUp() ? 1 : 0);
        mBinding.ivIcmButtonUp.setLongPressTime(0);
        mBinding.ivIcmButtonUp.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivIcmButtonUp.setLevel(0);
            }
        });
        mBinding.ivIcmButtonUp.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setICMButtonUP(newLevel != 0);
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(byteCanBaseBean.getId(), new Gson().toJson(byteCanBaseBean.toByteArray()));
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //氢堆加水除气
        mBinding.ivFcuDegas.setIconDrawable(getDrawable(R.drawable.selector_fcu_degas));
        mBinding.ivFcuDegas.setName(getString(R.string.fcu_degas));
        mBinding.ivFcuDegas.setLevel(mCanSwitcher0318Bean.getFCUDegas() ? 1 : 0);
        mBinding.ivFcuDegas.setLongPressTime(0);
        mBinding.ivFcuDegas.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setFCUDegas(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //方向盘调节
        mBinding.ivSteeringWheelAdjust.setIconDrawable(getDrawable(R.drawable.selector_steering_wheel_adjust));
        mBinding.ivSteeringWheelAdjust.setName(getString(R.string.steering_wheel_adjust));
        mBinding.ivSteeringWheelAdjust.setLevel(mCanSwitcher0318Bean.getSteeringWheelAdjust() ? 1 : 0);
        mBinding.ivSteeringWheelAdjust.setLongPressTime(0);
        mBinding.ivSteeringWheelAdjust.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivSteeringWheelAdjust.setLevel(0);
            }
        });
        mBinding.ivSteeringWheelAdjust.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setSteeringWheelAdjust(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //油门互锁
        mBinding.ivAcceleratorInterlock.setIconDrawable(getDrawable(R.drawable.selector_accelerator_interlock));
        mBinding.ivAcceleratorInterlock.setName(getString(R.string.accelerator_interlock));
        mBinding.ivAcceleratorInterlock.setLevel(mCanSwitcher0318Bean.getAcceleratorInterlock() ? 1 : 0);
        mBinding.ivAcceleratorInterlock.setLongPressTime(3000);
        mBinding.ivAcceleratorInterlock.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setAcceleratorInterlock(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //仪表按键开关（返回）
        mBinding.ivIcmButtonBack.setIconDrawable(getDrawable(R.drawable.selector_icm_button_back));
        mBinding.ivIcmButtonBack.setName(getString(R.string.icm_button_back));
        mBinding.ivIcmButtonBack.setLevel(mCanSwitcher0318Bean.getICMButtonBack() ? 1 : 0);
        mBinding.ivIcmButtonBack.setLongPressTime(0);
        mBinding.ivIcmButtonBack.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivIcmButtonBack.setLevel(0);
            }
        });
        mBinding.ivIcmButtonBack.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setICMButtonBack(newLevel != 0);
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(byteCanBaseBean.getId(), new Gson().toJson(byteCanBaseBean.toByteArray()));
                StateManager.getInstance().saveEntityAndSendCommands();
                LoggerUtil.e("====CanSwitcher0318==返回==" + HexByteStrUtils.getStringFromBytes(mCanSwitcher0318Bean.toByteArray()));
            }
        });
        //仪表按键开关（确认）
        mBinding.ivIcmButtonMenu.setIconDrawable(getDrawable(R.drawable.selector_icm_button_menu));
        mBinding.ivIcmButtonMenu.setName(getString(R.string.icm_button_menu));
        mBinding.ivIcmButtonMenu.setLevel(mCanSwitcher0318Bean.getICMButtonMenu() ? 1 : 0);
        mBinding.ivIcmButtonMenu.setLongPressTime(0);
        mBinding.ivIcmButtonMenu.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                LoggerUtil.e("=======onActionUp======");
                mBinding.ivIcmButtonMenu.setLevel(0);
            }
        });
        mBinding.ivIcmButtonMenu.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setICMButtonMenu(newLevel != 0);
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(byteCanBaseBean.getId(), new Gson().toJson(byteCanBaseBean.toByteArray()));
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //EV模式
        mBinding.ivEvModel.setIconDrawable(getDrawable(R.drawable.selector_ev_model));
        mBinding.ivEvModel.setName(getString(R.string.ev_model));
        mBinding.ivEvModel.setLevel(mCanSwitcher0318Bean.getEVModel() ? 1 : 0);
        mBinding.ivEvModel.setLongPressTime(3000);
        mBinding.ivEvModel.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setEVModel(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //仪表按键开关（下一步）
        mBinding.ivIcmButtonDown.setIconDrawable(getDrawable(R.drawable.selector_icm_button_down));
        mBinding.ivIcmButtonDown.setName(getString(R.string.icm_button_down));
        mBinding.ivIcmButtonDown.setLevel(mCanSwitcher0318Bean.getICMButtonDown() ? 1 : 0);
        mBinding.ivIcmButtonDown.setLongPressTime(0);
        mBinding.ivIcmButtonDown.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivIcmButtonDown.setLevel(0);
            }
        });
        mBinding.ivIcmButtonDown.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setICMButtonDown(newLevel != 0);
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(byteCanBaseBean.getId(), new Gson().toJson(byteCanBaseBean.toByteArray()));
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //ADAS
        mBinding.ivAdas.setIconDrawable(getDrawable(R.drawable.selector_adas));
        mBinding.ivAdas.setName(getString(R.string.adas));
        mBinding.ivAdas.setLevel(mCanSwitcher0318Bean.getADAS() ? 1 : 0);
        mBinding.ivAdas.setLongPressTime(3000);
        mBinding.ivAdas.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setADAS(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //AEBS
        mBinding.ivAebs.setIconDrawable(getDrawable(R.drawable.selector_aebs));
        mBinding.ivAebs.setName(getString(R.string.aebs));
        mBinding.ivAebs.setLevel(mCanSwitcher0318Bean.getAEBS() ? 1 : 0);
        mBinding.ivAebs.setLongPressTime(3000);
        mBinding.ivAebs.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0318Bean.setAEBS(newLevel == 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCanSwitcher0318Bean.setAEBS(newLevel != 0);
                        mBinding.ivAebs.setLevel(0);
                    }
                }, 1000);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //volume UP
        mBinding.ivVolumeUp.setIconDrawable(getDrawable(R.drawable.selector_volume_up));
        mBinding.ivVolumeUp.setName(getString(R.string.volume_up));
        mBinding.ivVolumeUp.setLevel(0);
        mBinding.ivVolumeUp.setLongPressTime(0);
        mBinding.ivVolumeUp.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivVolumeUp.setLevel(0);
            }
        });
        mBinding.ivVolumeUp.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                // CAN-команда
                mCanSwitcher0318Bean.setVolumeUp(newLevel != 0);
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(
                        byteCanBaseBean.getId(),
                        new Gson().toJson(byteCanBaseBean.toByteArray())
                );
                StateManager.getInstance().saveEntityAndSendCommands();

                // Системная громкость
                if(newLevel != 0) {
                    mAudioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI // Показывать системный ползунок
                    );
                }

                LoggerUtil.e("Volume UP: " + (newLevel != 0));
            }
        });

        // Volume DOWN
        mBinding.ivVolumeDown.setIconDrawable(getDrawable(R.drawable.selector_volume_down));
        mBinding.ivVolumeDown.setName(getString(R.string.volume_down));
        mBinding.ivVolumeDown.setLevel(0);
        mBinding.ivVolumeDown.setLongPressTime(0);
        mBinding.ivVolumeDown.setActionUpEvent(new MyCheckImageView.ActionUpCallback() {
            @Override
            public void onActionUp() {
                mBinding.ivVolumeDown.setLevel(0);
            }
        });
        mBinding.ivVolumeDown.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                // CAN-команда (если требуется)
                mCanSwitcher0318Bean.setVolumeDown(newLevel != 0); // Предполагая, что метод существует
                ByteCanBaseBean byteCanBaseBean = mCanSwitcher0318Bean;
                CanServerHelper.getCanServerHelper().sendData(
                        byteCanBaseBean.getId(),
                        new Gson().toJson(byteCanBaseBean.toByteArray())
                );
                StateManager.getInstance().saveEntityAndSendCommands();

                // Системная громкость
                if(newLevel != 0) {
                    mAudioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI
                    );
                }
                LoggerUtil.e("Volume DOWN: " + (newLevel != 0));
            }
        });




        LoggerUtil.e("====CanSwitcher0318====" + HexByteStrUtils.getStringFromBytes(mCanSwitcher0318Bean.toByteArray()));
    }

    private void initCanSwitcher0218Data() {
        mCanSwitcher0218Bean = StateManager.getInstance().getCanSwitcher0218Bean();
        //车内照明灯
        mBinding.ivDomeLamp.setIconDrawable(getDrawable(R.drawable.selector_dome_lamp));
        mBinding.ivDomeLamp.setName(getString(R.string.dome_lamp));
        mBinding.ivDomeLamp.setLevel(mCanSwitcher0218Bean.getDomeLamp() ? 1 : 0);
        mBinding.ivDomeLamp.setLongPressTime(0);
        mBinding.ivDomeLamp.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0218Bean.setDomeLamp(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        //车外氛围灯
        mBinding.ivExAmbient.setIconDrawable(getDrawable(R.drawable.selector_ex_ambient));
        mBinding.ivExAmbient.setName(getString(R.string.ex_ambient));
        mBinding.ivExAmbient.setLevel(mCanSwitcher0218Bean.getExAmbientLamp() ? 1 : 0);
        mBinding.ivExAmbient.setLongPressTime(0);
        mBinding.ivExAmbient.setOnLevelChangedListener(new MyCheckImageView.OnLevelChangedListener() {
            @Override
            public void onLevelChanged(int newLevel) {
                mCanSwitcher0218Bean.setExAmbientLamp(newLevel != 0);
                StateManager.getInstance().saveEntityAndSendCommands();
            }
        });
        LoggerUtil.e("====CanSwitcher0218====" + HexByteStrUtils.getStringFromBytes(mCanSwitcher0218Bean.toByteArray()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCanSwitcher0318Entity(CanSwitcher0318Bean canSwitcherBean) {
        LoggerUtil.e("====CanSwitcher0218==onCanSwitcher0318Entity==" + HexByteStrUtils.getStringFromBytes(canSwitcherBean.toByteArray()));
        StateManager.getInstance().setCanSwitcher0318Bean(canSwitcherBean);
        initCanSwitcher0318Data();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCanSwitcher0218Entity(CanSwitcher0218Bean canSwitcherBean) {
        LoggerUtil.e("====CanSwitcher0218==onCanSwitcher0218Entity==" + HexByteStrUtils.getStringFromBytes(canSwitcherBean.toByteArray()));
        StateManager.getInstance().setCanSwitcher0218Bean(canSwitcherBean);
        initCanSwitcher0218Data();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initView();
        initCanSwitcher0318Data();
        initCanSwitcher0218Data();
    }
}