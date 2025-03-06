package com.spd.Scene;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.spd.MyLauncher.LeafView;
import com.spd.MyLauncher.MyNormalButton;
import com.spd.WallpaperChooser.WallpaperChooserPopupWindow;
import com.spd.custom.view.AppInfo;
import com.spd.custom.view.CustomDesktop;
import com.spd.custom.view.ShortCutBaseView;
import com.spd.custom.view.ShortCutClickListener;
import com.spd.custom.view.Utilities;
import com.spd.home.R;

import java.util.List;

public class HomeSceneManager extends SceneManager implements View.OnClickListener, View.OnLongClickListener, ShortCutClickListener {
    private Context m_context;
    private LeafView m_leaf_icon;
    private MyNormalButton m_bottom_bn_voice, m_bottom_bn_phone, m_bottom_bn_camera, m_bottom_bn_wallpaper;
    private CustomDesktop m_destop;
    private Activity m_activity;
    /*    private BTNotifyWindow m_bt_window;
        private USBNotifyWindow m_usb_window;
        private VolumeNotifyWindow m_volume_window;*/
    private WallpaperChooserPopupWindow m_wallpaper_window;

    public HomeSceneManager(Activity c_contex, ViewGroup c_root_view) {
        super(c_contex, "com.spd.home", c_root_view);
        m_context = c_contex;
        m_destop = c_root_view.findViewById(R.id.id_app_desktop);
        m_destop.setShortCutClickListener(this);

        m_leaf_icon = c_root_view.findViewById(R.id.id_leaf_icon);
        m_leaf_icon.setTag(SceneManager.SCENE_STATE_TAG, true);
        m_bottom_bn_voice = c_root_view.findViewById(R.id.id_bottom_eq);
        m_bottom_bn_phone = c_root_view.findViewById(R.id.id_bottom_phone);
        m_bottom_bn_camera = c_root_view.findViewById(R.id.id_bottom_settings);
        m_bottom_bn_wallpaper = c_root_view.findViewById(R.id.id_bottom_wallpaper);
        m_bottom_bn_voice.setTag(SceneManager.SCENE_STATE_TAG, true);
        m_bottom_bn_phone.setTag(SceneManager.SCENE_STATE_TAG, true);
        m_bottom_bn_camera.setTag(SceneManager.SCENE_STATE_TAG, true);
        m_bottom_bn_wallpaper.setTag(SceneManager.SCENE_STATE_TAG, true);
        m_leaf_icon.setOnClickListener(this);
        m_bottom_bn_voice.setOnClickListener(this);
        m_bottom_bn_phone.setOnClickListener(this);
        m_bottom_bn_camera.setOnClickListener(this);
        m_bottom_bn_wallpaper.setOnClickListener(this);
        m_leaf_icon.setOnLongClickListener(this);
        //m_bt_window = new BTNotifyWindow(c_contex);
        //m_usb_window = new USBNotifyWindow(c_contex);
        //m_volume_window = new VolumeNotifyWindow(c_contex);
        //m_leaf_icon.setLongClickable(true);
        m_wallpaper_window = new WallpaperChooserPopupWindow(c_contex, c_root_view);
        m_wallpaper_window.setWallpaperWindowListener(new WallpaperChooserPopupWindow.WallpaperWindowListener() {
            @Override
            public void onWallpaperWindowShow() {
                m_destop.setSwitchMode(CustomDesktop.SWITCH_MODE_WALLPAPER, true);
            }

            @Override
            public void onWallpaperWindowHide() {
                m_destop.setSwitchMode(CustomDesktop.SWITCH_MODE_DESKTOP, true);
            }
        });
    }

    public boolean onBackPressed() {
        boolean c_result = false;
        if (m_wallpaper_window.isShowing()) {
            m_wallpaper_window.dismiss();
            c_result = true;
        } else {
            c_result = m_destop.onBackPressed();
        }
        return c_result;
    }

    @Override
    public boolean onLongClick(View view) {

        return false;
    }

    @Override
    public void onClick(View view) {
        Log.d("SimonCheck001", "onClick: ");
        switch (view.getId()) {
            case R.id.id_leaf_icon:
                m_destop.setSwitchMode(CustomDesktop.SWITCH_MODE_APP_LIST, true);
                break;
            case R.id.id_bottom_phone:
                int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
                Intent c_intent = new Intent("com.spd.bt.START");
                c_intent.setFlags(launchFlags);
                startActivity(c_intent);
                break;
            case R.id.id_bottom_eq:
                launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
                c_intent = new Intent();
                c_intent.setComponent(new ComponentName("com.spd.spdeq", "com.spd.spdeq.EQActivity"));
                c_intent.setFlags(launchFlags);
                startActivity(c_intent);
                break;
            case R.id.id_bottom_settings:
                //m_bt_window.showBtNotifyWindow("BlueToothDeviceName");
                launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
                c_intent = new Intent();
                c_intent.setComponent(new ComponentName("com.spd.spdsettings", "com.spd.spdsettings.FullscreenActivity"));
                c_intent.setFlags(launchFlags);
                startActivity(c_intent);
                break;
            case R.id.id_bottom_wallpaper:

                //m_usb_window.showVolumeNotifyWindow(BaseNotifyWindow.MEDIA_DEVICE_USB0);
                //zhangpeng
/*                m_volume_window.setVolumeWindowListener(new VolumeNotifyWindow.VolumeWindowListener() {
                    @Override
                    public void onVolumeChanged(int c_volume_type, int c_volume_value) {
                        Log.d("VOLUME", "onVolumeChanged: "+c_volume_type+" "+c_volume_value);
                    }
                });
                m_volume_window.showVolumeNotifyWindow(0,0,0,0,VolumeNotifyWindow.VOLUME_ID_MEDIA);
*/
                showWallpaperSet();
                break;
        }
    }

    public void showWallpaperSet() {
        m_wallpaper_window.showAtLocation(m_root_view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onAppLongClicked(AppInfo c_app_info) {

    }

    @Override
    public void onAppClicked(AppInfo c_app_info) {
        if (c_app_info != null) {
            if (c_app_info.m_app_id == Utilities.SYSTEM_APP_ID_APPS) {
                // 点击应用图标，打开应用列表
                m_destop.setSwitchMode(CustomDesktop.SWITCH_MODE_APP_LIST, true);
            } else {
                Intent c_intent = c_app_info.getLaunchIntent();
                startActivity(c_intent);
            }
        }
    }

    @Override
    public void onDragShortCutStart() {
        m_leaf_icon.setLeafMode(LeafView.LEAF_MODE_DELETE);
    }

    @Override
    public void onDragShortCutFinished() {
        m_leaf_icon.setLeafMode(LeafView.LEAF_MODE_FLOWER);
    }

    public void startActivity(Intent c_intent) {
        if (!m_activity_handler.hasMessages(0)) {
            this.exitScene();
            Message c_msg = new Message();
            c_msg.what = 0;
            c_msg.obj = c_intent;
            m_activity_handler.sendMessageDelayed(c_msg, getExitSceneDelayTime());
        }
    }

    private Handler m_activity_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent c_intent = (Intent) msg.obj;
            try {
                Log.d("NewText001", " =01=" + c_intent.getComponent());
                m_context.startActivity(c_intent);
                Log.d("NewText001", " =11= started");
            } catch (Exception e) {
                Log.d("NewText001", " =22= falure");
                HomeSceneManager.this.enterScene();
            }
        }
    };

    @Override
    public boolean getChildSceneMotionFlag(ViewGroup c_root_view, View c_child_view) {
        return false;
    }

    @Override
    public void onSceneChildChange(int c_count, ViewGroup c_root_view, int c_index, View c_child_view, int state) {
        int c_use_time = 400;
        Log.d("SimonCheck002", "onSceneChildChange: " + state + " " + c_child_view + " ");
        if (m_leaf_icon == c_child_view) {
            if (state == SCENE_STATE_REQUEST_CATCH) {
                ObjectAnimator c_anin = (ObjectAnimator) m_leaf_icon.getTag(SCENE_ANIN_TAG);
                if (c_anin != null) {
                    c_anin.pause();
                    c_anin.cancel();
                }
                m_leaf_icon.setAlpha(1f);
                m_leaf_icon.resetLeafMotion(true, c_use_time * c_index / c_count);
            } else {
                ObjectAnimator c_anin = (ObjectAnimator) m_leaf_icon.getTag(SCENE_ANIN_TAG);
                if (c_anin != null) {
                    c_anin.pause();
                    c_anin.cancel();
                }
                c_anin = ObjectAnimator.ofFloat(m_leaf_icon, "alpha", 1f, 0f);
                c_anin.setDuration(200);
                c_anin.setStartDelay(c_use_time * c_index / c_count);
                c_anin.start();
                m_leaf_icon.setTag(SCENE_ANIN_TAG, c_anin);
            }
        } else if (m_bottom_bn_voice == c_child_view || m_bottom_bn_phone == c_child_view || m_bottom_bn_camera == c_child_view || m_bottom_bn_wallpaper == c_child_view) {
            ObjectAnimator c_anin = (ObjectAnimator) c_child_view.getTag(SCENE_ANIN_TAG);
            if (c_anin != null) {
                c_anin.pause();
                c_anin.cancel();
            }
            if (state == SCENE_STATE_REQUEST_CATCH) {
                c_anin = ObjectAnimator.ofFloat(c_child_view, "alpha", 0f, 1f);
            } else {
                c_anin = ObjectAnimator.ofFloat(c_child_view, "alpha", 1f, 0f);
            }
            c_anin.setDuration(200);
            c_anin.setStartDelay(c_use_time * c_index / c_count);
            c_anin.start();
            c_child_view.setTag(SCENE_ANIN_TAG, c_anin);
        } else {
            ObjectAnimator c_anin = (ObjectAnimator) c_child_view.getTag(SCENE_ANIN_TAG);
            if (c_anin != null) {
                c_anin.pause();
                c_anin.cancel();
            }
            c_index += 1;
            int c_delay_time = c_use_time * c_index / c_count;
            if (c_child_view instanceof ShortCutBaseView) {
                ShortCutBaseView c_short_cut_view = (ShortCutBaseView) c_child_view;
                if (state == SCENE_STATE_REQUEST_CATCH) {
                    c_short_cut_view.setViewInitFlag(ShortCutBaseView.SHORT_CUT_INIT_FLAG_HIDE, false, 0);
                    c_short_cut_view.setViewInitFlag(ShortCutBaseView.SHORT_CUT_INIT_FLAG_SHOW, true, c_delay_time);
                } else if (state == SCENE_STATE_REQUEST_RELEASE) {
                    c_short_cut_view.setViewInitFlag(ShortCutBaseView.SHORT_CUT_INIT_FLAG_HIDE, true, c_delay_time);
                }
            }
        }
    }

    @Override
    public void reOrderChildMotion(ViewGroup c_root_view, List<View> c_motion_view_list) {

    }

    public void registerListener() {
        m_destop.registerListener();
    }


    public void unregisterListener() {
        m_destop.unregisterListener();
    }


}
