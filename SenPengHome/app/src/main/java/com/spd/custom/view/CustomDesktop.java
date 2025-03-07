package com.spd.custom.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.spd.home.FullscreenActivity;
import com.spd.home.R;

import java.util.ArrayList;
import java.util.List;

public class CustomDesktop extends FrameLayout implements View.OnLongClickListener, ShortCutClickListener, PageGuideView.PageGuideOrder {
    public static final int DESKTOP_POSTION_PREV = -50;
    public static final int DESKTOP_POSTION_NEXT = -51;
    public static final int DESKTOP_POSTION_DELETE = -52;


    protected StepMotionParameter m_desktop_offset = new StepMotionParameter();

    public CustomDesktop(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        m_desktop_offset.setStepIndexBounds(0, 0);
        Log.d("Test010", "CustomDesktop: =3= startLoadUserApp");
        startLoadUserAppList();
        resetDesktop(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        m_switch_motion_length = h;
    }

    private BroadcastReceiver m_app_add_listener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals("android.intent.action.PACKAGE_ADDED")) {
                resetDesktop(context);
                Log.d("Test010", "onReceive: =2= startLoadUserApp");
                startLoadUserAppList();
            }
        }
    };
    private BroadcastReceiver m_app_remove_listener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals("android.intent.action.PACKAGE_REMOVED")) {
                resetDesktop(context);
                Log.d("Test010", "onReceive: =1= startLoadUserApp");
                startLoadUserAppList();
            }
        }
    };

    DevelopModeListener m_develop_listener = new DevelopModeListener();
    AvmEnableListener m_avm_enable_listener = new AvmEnableListener();
    NaviAppListener m_navi_app_listener = new NaviAppListener();
    CarinfoModeListener m_car_info_mode_listener = new CarinfoModeListener();


    public void registerListener() {
        ContentResolver resolver = getContext().getContentResolver();
        resolver.registerContentObserver(Settings.System.getUriFor(Utilities.m_settings_develop_mode), true, m_develop_listener);
        resolver.registerContentObserver(Settings.System.getUriFor("SETTING_NAVI_APP"), true, m_navi_app_listener);
        resolver.registerContentObserver(Settings.System.getUriFor("SETTING_CARINFO_SETTING_ENABLE"), true, m_car_info_mode_listener);
        resolver.registerContentObserver(Settings.System.getUriFor("SETTING_AVM_ENABLE"), true, m_avm_enable_listener);

        IntentFilter c_add_intent_filter = new IntentFilter(
                "android.intent.action.PACKAGE_ADDED");
        c_add_intent_filter.addDataScheme("package");
        getContext().registerReceiver(m_app_add_listener, c_add_intent_filter);

        IntentFilter c_remove_intent_filter = new IntentFilter(
                "android.intent.action.PACKAGE_REMOVED");
        c_remove_intent_filter.addDataScheme("package");
        getContext().registerReceiver(m_app_remove_listener, c_remove_intent_filter);
    }


    public void unregisterListener() {
        ContentResolver resolver = getContext().getContentResolver();
        resolver.unregisterContentObserver(m_develop_listener);
        resolver.unregisterContentObserver(m_avm_enable_listener);
        resolver.unregisterContentObserver(m_navi_app_listener);
        resolver.unregisterContentObserver(m_car_info_mode_listener);
        getContext().unregisterReceiver(m_app_add_listener);
        getContext().unregisterReceiver(m_app_remove_listener);
        stopLoadUserAppList();
    }

    class DevelopModeListener extends ContentObserver {

        public DevelopModeListener() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d("Test010", "onChange: =0= startLoadUserApp");
            startLoadUserAppList();
        }
    }

    class NaviAppListener extends ContentObserver {

        public NaviAppListener() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            resetDesktop(getContext());
        }
    }

    class CarinfoModeListener extends ContentObserver {

        public CarinfoModeListener() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            resetDesktop(getContext());
        }
    }

    class AvmEnableListener extends ContentObserver {

        public AvmEnableListener() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d("Test010", "[AvmEnableListener] onChange");
            resetDesktop(getContext());
            startLoadUserAppList();
        }
    }

    public void resetDesktop(Context context) {
        Utilities.clearDestopAppList(context);
        List<ShortCut> c_app_info = Utilities.getDesktopAppList(context);
        setShortCutList(c_app_info);
    }

    private float m_last_touch_postion_x = 0;
    private float m_last_touch_postion_y = 0;
    private float m_drag_gate_offset = 30f;
    private boolean m_drag_flag = false;

    @Override
    public boolean onLongClick(View view) {
        Log.d("SimonCheck001", "Desktop onLongClick: ");
        return false;
    }

    MotionEvent m_last_ev;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        m_last_ev = ev;
        if (m_switch_mode == SWITCH_MODE_APP_LIST) {
            super.dispatchTouchEvent(ev);
        } else {
            if (m_short_drag_flag != 0) {
                m_drag_flag = false;
                if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                    m_short_drag_flag = 0;
                    m_last_touch_postion_x = ev.getX();
                    m_last_touch_postion_y = ev.getY();
                    m_drag_short_cut.stopDragShortcut(m_last_touch_postion_x - FullscreenActivity.DESKTOP_WIDTH / 2, m_last_touch_postion_y - FullscreenActivity.DESKTOP_HEIGHT / 2);
                } else {
                    m_last_touch_postion_x = ev.getX();
                    m_last_touch_postion_y = ev.getY();
                    m_drag_short_cut.startDragShortcut(m_last_touch_postion_x - FullscreenActivity.DESKTOP_WIDTH / 2, m_last_touch_postion_y - FullscreenActivity.DESKTOP_HEIGHT / 2);
                }

                if (m_short_drag_flag == 1) {
                    m_short_drag_flag = 2;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(ev);
                }
            } else {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    m_drag_flag = false;
                    m_last_touch_postion_x = ev.getX();
                    m_last_touch_postion_y = ev.getY();
                    super.dispatchTouchEvent(ev);
                } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!m_drag_flag) {
                        float c_new_postion_x = ev.getX();
                        float c_new_postion_y = ev.getY();
                        float c_offset_x = c_new_postion_x - m_last_touch_postion_x;
                        float c_offset_y = c_new_postion_y - m_last_touch_postion_y;
                        if (Math.abs(c_offset_x) > m_drag_gate_offset || Math.abs(c_offset_y) > m_drag_gate_offset) {
                            m_drag_flag = true;
                            m_desktop_offset.startDrag();
                            m_desktop_offset.dragValue(-c_offset_x);
                            m_last_touch_postion_x = c_new_postion_x;
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(ev);
                            this.invalidate();
                        } else {
                            super.dispatchTouchEvent(ev);
                        }
                    } else {
                        float c_new_postion_x = ev.getX();
                        float c_offset_x = c_new_postion_x - m_last_touch_postion_x;
                        m_desktop_offset.dragValue(-c_offset_x);
                        m_last_touch_postion_x = c_new_postion_x;
                        this.invalidate();
                    }
                } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (m_drag_flag) {
                        m_desktop_offset.stopDrag();
                        m_drag_flag = false;
                        this.invalidate();
                    } else {
                        super.dispatchTouchEvent(ev);
                    }
                }
            }
        }


        return true;
    }

    public float getDeskTopOffset() {
        return m_desktop_offset.getCurrentIndex() * FullscreenActivity.DESKTOP_WIDTH + m_desktop_offset.getCurrentOffset();
    }

    private ShortCutMusicView m_music_short_cut;
    //private ShortCutRadioView m_radio_short_cut;

    private List<ShortCutBaseView> m_buffer_shortcut_view = new ArrayList<>();
    private List<ShortCutBaseView> m_working_shortcut_view = new ArrayList<>();

    public ShortCutBaseView getShortCutView(ShortCut c_short_cut_info) {
        ShortCutBaseView c_result = null;
        /*if (c_short_cut_info.getAppInfo().m_app_id == Utilities.SYSTEM_APP_ID_MUSIC)
        {
            if (m_music_short_cut == null)
            {
                m_music_short_cut = new ShortCutMusicView(getContext());
                FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                m_music_short_cut.setLayoutParams(c_para);
            }
            c_result = m_music_short_cut;
        }
        else*/
        if (c_short_cut_info.getAppInfo().m_app_id != 0) {
            //TODO 固定图标的的设置
            c_result = getFreeShortCutView(c_short_cut_info.getAppInfo().m_short_cut_bg, c_short_cut_info.getAppInfo().m_short_cut_icon);
        } else {
            c_result = getFreeShortCutView(c_short_cut_info.getAppInfo());
        }

        return c_result;
    }

    private ShortCutBaseView getFreeShortCutView(AppInfo c_appinfo) {
        ShortCutBaseView c_result;
        if (m_buffer_shortcut_view.size() != 0) {
            c_result = m_buffer_shortcut_view.get(0);
            m_buffer_shortcut_view.remove(0);
            c_result.resetResource(c_appinfo);
        } else {
            c_result = new ShortCutBaseView(getContext(), c_appinfo);
            FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            c_result.setLayoutParams(c_para);
        }
        c_result.setViewInitFlag(ShortCutBaseView.SHORT_CUT_INIT_FLAG_SHOW, false, 0);
        return c_result;
    }

    private ShortCutBaseView getFreeShortCutView(int c_bg_id, int c_icon_id) {
        ShortCutBaseView c_result;
        if (m_buffer_shortcut_view.size() != 0) {
            c_result = m_buffer_shortcut_view.get(0);
            m_buffer_shortcut_view.remove(0);
            c_result.resetResource(c_bg_id, c_icon_id);
        } else {
            c_result = new ShortCutBaseView(getContext(), c_bg_id, c_icon_id);
            FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            c_result.setLayoutParams(c_para);
        }
        c_result.setViewInitFlag(ShortCutBaseView.SHORT_CUT_INIT_FLAG_SHOW, false, 0);
        return c_result;
    }

    @Override
    public void addView(View child) {
        if (!m_working_shortcut_view.contains(child)) {
            if (child instanceof ShortCutBaseView) {
                ShortCutBaseView c_app_view = (ShortCutBaseView) child;
                //ShortCut c_short_cut = c_app_view.getShortCutInfo();
                m_working_shortcut_view.add(c_app_view);
            }
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (!m_working_shortcut_view.contains(child)) {
            if (child instanceof ShortCutBaseView) {
                ShortCutBaseView c_app_view = (ShortCutBaseView) child;
                //ShortCut c_short_cut = c_app_view.getShortCutInfo();
                m_working_shortcut_view.add(c_app_view);
            }
        }
        super.addView(child, index);
    }

    @Override
    public void removeView(View view) {
        if (m_working_shortcut_view.contains(view)) {
            if (view instanceof ShortCutBaseView) {
                ShortCutBaseView c_app_view = (ShortCutBaseView) view;
                //ShortCut c_short_cut = c_app_view.getShortCutInfo();
                m_working_shortcut_view.remove(c_app_view);
                if (c_app_view != m_music_short_cut) {
                    m_buffer_shortcut_view.add(c_app_view);
                }

            }
        }
        super.removeView(view);
    }

    public void removeAllShortCutView() {
        for (ShortCutBaseView c_child : m_working_shortcut_view) {
            super.removeView(c_child);
            if (c_child != m_music_short_cut) {
                m_buffer_shortcut_view.add(c_child);
            }
        }
        m_working_shortcut_view.clear();
    }

    private List<ShortCut> m_current_short_cut_list = new ArrayList<>();

    public void setShortCutList(List<ShortCut> c_short_cut_list) {
        if (c_short_cut_list != null) {
            m_current_short_cut_list.clear();
            m_current_short_cut_list.addAll(c_short_cut_list);
        }
        removeAllShortCutView();
        for (int i = 0; i < m_current_short_cut_list.size(); i++) {
            ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
            c_child_short_cut.setDesktopOffsetXY(getDeskTopOffset(), -m_short_cut_motion_y * m_switch_motion_length);
            c_child_short_cut.setBaseAlpha(1f - m_short_cut_motion_y);
            c_child_short_cut.setShortCutPostion(i);
            c_child_short_cut.setDeskTop(this);
        }
        int c_short_cut_num = m_current_short_cut_list.size();
        int c_page_count = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
        int c_desktop_page_num = (c_short_cut_num / c_page_count) + ((c_short_cut_num % c_page_count) > 0 ? 1 : 0) - 1;
        m_desktop_offset.setStepIndexBounds(0, c_desktop_page_num);
    }

    public void addShortCut(int c_postion, ShortCut c_short_cut) {
        if (c_postion > m_current_short_cut_list.size()) {
            c_postion = m_current_short_cut_list.size();
        }
        c_short_cut.setDesktopOffsetXY(getDeskTopOffset(), -m_short_cut_motion_y * m_switch_motion_length);
        c_short_cut.setBaseAlpha(1f - m_short_cut_motion_y);
        c_short_cut.setShortCutPostion(c_postion);
        c_short_cut.setDeskTop(this);
        for (int i = 0; i < m_current_short_cut_list.size(); i++) {
            ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
            int c_tag_postion = c_child_short_cut.getShortCutPostion();
            if (c_tag_postion >= c_postion) {
                c_tag_postion++;
                c_child_short_cut.gotoShortCutPostion(c_tag_postion, false);
            }
        }
        m_current_short_cut_list.add(c_postion, c_short_cut);
        int c_short_cut_num = m_current_short_cut_list.size();
        int c_page_count = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
        int c_desktop_page_num = (c_short_cut_num / c_page_count) + ((c_short_cut_num % c_page_count) > 0 ? 1 : 0) - 1;
        m_desktop_offset.setStepIndexBounds(0, c_desktop_page_num);
        Utilities.saveDesktopAppList(getContext(), m_current_short_cut_list);
        this.invalidate();
    }

    private int m_short_drag_flag = 0;
    private ShortCut m_drag_short_cut;

    public void redragShortCut(ShortCut c_short_cut) {
        m_drag_short_cut = c_short_cut;
        c_short_cut.startDragShortcut(m_last_touch_postion_x - FullscreenActivity.DESKTOP_WIDTH / 2, m_last_touch_postion_y - FullscreenActivity.DESKTOP_HEIGHT / 2);
        m_short_drag_flag = 1;
        m_last_ev.setAction(MotionEvent.ACTION_MOVE);
        dispatchTouchEvent(m_last_ev);
        m_desktop_offset.stopDrag();
        if (m_listener != null) {
            m_listener.onDragShortCutStart();
        }
        this.invalidate();
    }

    public void pushShortCut(ShortCut c_short_cut) {
        Log.d("SimonCheck003", "pushShortCut: ");
        m_drag_short_cut = c_short_cut;
        int c_postion = m_current_short_cut_list.size();
        c_short_cut.setDesktopOffsetXY(getDeskTopOffset(), -m_short_cut_motion_y * m_switch_motion_length);
        c_short_cut.setBaseAlpha(1f - m_short_cut_motion_y);
        c_short_cut.setShortCutPostion(c_postion);
        c_short_cut.setDeskTop(this);
        c_short_cut.startDragShortcut(m_last_touch_postion_x - FullscreenActivity.DESKTOP_WIDTH / 2, m_last_touch_postion_y - FullscreenActivity.DESKTOP_HEIGHT / 2);
        m_current_short_cut_list.add(c_postion, c_short_cut);
        m_short_drag_flag = 1;
        m_last_ev.setAction(MotionEvent.ACTION_MOVE);
        dispatchTouchEvent(m_last_ev);
        int c_short_cut_num = m_current_short_cut_list.size();
        int c_page_count = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
        int c_desktop_page_num = (c_short_cut_num / c_page_count) + ((c_short_cut_num % c_page_count) > 0 ? 1 : 0) - 1;
        m_desktop_offset.setStepIndexBounds(0, c_desktop_page_num);
        m_desktop_offset.stopDrag();
        if (m_listener != null) {
            m_listener.onDragShortCutStart();
        }
        //MotionEvent c_event = new MotionEvent();
        this.invalidate();
    }

    public void finishTestEmptyShortCut(int c_new_postion) {
        Log.d(Utilities.TAG, "finishTestEmptyShortCut: " + c_new_postion);
        if (c_new_postion >= 0) {
            if (c_new_postion >= m_current_short_cut_list.size()) {
                c_new_postion = m_current_short_cut_list.size() - 1;
            }
            m_current_short_cut_list.remove(m_drag_short_cut);
            m_current_short_cut_list.add(c_new_postion, m_drag_short_cut);
            for (int i = 0; i < m_current_short_cut_list.size(); i++) {
                ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
                c_child_short_cut.gotoShortCutPostion(i, false);
            }
        } else if (c_new_postion == DESKTOP_POSTION_DELETE) {
            if (m_drag_short_cut != null) {
                AppInfo c_app_info = m_drag_short_cut.getAppInfo();
                if (c_app_info.m_app_id == 0) {
                    removeShortCut(m_drag_short_cut);
                } else {
                    for (int i = 0; i < m_current_short_cut_list.size(); i++) {
                        ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
                        c_child_short_cut.gotoShortCutPostion(i, false);
                    }
                }
            }
        }
        if (m_listener != null) {
            m_listener.onDragShortCutFinished();
        }
        Utilities.saveDesktopAppList(getContext(), m_current_short_cut_list);
    }

    private void removeShortCut(ShortCut c_short) {
        c_short.releaseShortCut();
        if (m_current_short_cut_list.contains(c_short)) {
            m_current_short_cut_list.remove(c_short);
        }
        for (int i = 0; i < m_current_short_cut_list.size(); i++) {
            ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
            c_child_short_cut.gotoShortCutPostion(i, false);
        }
        int c_short_cut_num = m_current_short_cut_list.size();
        int c_page_count = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
        int c_desktop_page_num = (c_short_cut_num / c_page_count) + ((c_short_cut_num % c_page_count) > 0 ? 1 : 0) - 1;
        m_desktop_offset.setStepIndexBounds(0, c_desktop_page_num);
        int c_current_index = m_desktop_offset.getCurrentIndex();
        if (c_current_index > c_desktop_page_num) {
            c_current_index = c_desktop_page_num;
            m_desktop_offset.gotoTargetIndex(c_current_index);
            this.invalidate();
        }
    }

    public void testShortCut(int c_new_postion, int c_org_postion) {
        if (c_new_postion >= 0) {
            if (c_org_postion != c_new_postion) {
                int c_offset_index = (c_org_postion - c_new_postion) / Math.abs(c_org_postion - c_new_postion);
                int c_max_value = c_org_postion > c_new_postion ? c_org_postion : c_new_postion;
                int c_min_value = c_org_postion < c_new_postion ? c_org_postion : c_new_postion;
                for (int i = 0; i < m_current_short_cut_list.size(); i++) {
                    ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
                    if (i >= c_min_value && i <= c_max_value) {
                        c_child_short_cut.gotoShortCutPostion(i + c_offset_index, true);
                    } else {
                        c_child_short_cut.gotoShortCutPostion(i, true);
                    }
                }
            } else {
                for (int i = 0; i < m_current_short_cut_list.size(); i++) {
                    ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
                    c_child_short_cut.gotoShortCutPostion(i, true);
                }
            }
        } else {
            if (c_new_postion == DESKTOP_POSTION_PREV) {
                int c_target_index = m_desktop_offset.getCurrentIndex() - 1;
                if (c_target_index >= m_desktop_offset.getMinIndex()) {
                    if (!m_drag_change_page_handler.hasMessages(0)) {
                        m_drag_change_page_handler.sendEmptyMessageDelayed(0, 1000);
                        m_desktop_offset.gotoTargetIndex(c_target_index);
                        this.invalidate();
                    }
                }
            } else if (c_new_postion == DESKTOP_POSTION_NEXT) {
                int c_target_index = m_desktop_offset.getCurrentIndex() + 1;
                if (c_target_index <= m_desktop_offset.getMaxIndex()) {
                    if (!m_drag_change_page_handler.hasMessages(0)) {
                        m_drag_change_page_handler.sendEmptyMessageDelayed(0, 1000);
                        m_desktop_offset.gotoTargetIndex(c_target_index);
                        this.invalidate();
                    }
                }
            }
        }
    }

    private Handler m_drag_change_page_handler = new Handler();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean c_motioned = m_desktop_offset.motionValue();
        for (int i = 0; i < m_current_short_cut_list.size(); i++) {
            ShortCut c_child_short_cut = m_current_short_cut_list.get(i);
            c_child_short_cut.setDesktopOffsetXY(getDeskTopOffset(), -m_short_cut_motion_y * m_switch_motion_length);
            c_child_short_cut.setBaseAlpha(1f - m_short_cut_motion_y);
        }
        int c_new_center_index = m_desktop_offset.getCurrentIndex();
        int c_new_page_count = m_desktop_offset.getMaxIndex();
        if ((c_new_center_index != m_last_center_index || c_new_page_count != m_last_page_count) && m_switch_mode == SWITCH_MODE_DESKTOP) {
            m_last_center_index = c_new_center_index;
            m_last_page_count = c_new_page_count;
            onPageGuideChanged(m_last_center_index, m_last_page_count + 1);
        }
        if (c_motioned) {
            this.invalidate();
        }
    }

    private int m_last_center_index = -1;
    private int m_last_page_count = 0;

    private ShortCutClickListener m_listener;

    public void setShortCutClickListener(ShortCutClickListener c_listener) {
        m_listener = c_listener;
    }

    public void onShortCutClicked(ShortCut c_short_cut) {
        if (m_listener != null) {
            m_listener.onAppClicked(c_short_cut.getAppInfo());
        }
    }

    @Override
    public void onPageGuideChanged(int c_posiont, int c_page_num) {
        if (m_page_guide_view != null) {
            m_page_guide_view.setPageGuide(c_posiont, c_page_num);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (m_page_guide_view == null) {
            m_page_guide_view = findViewById(R.id.id_page_guide_view);
            m_app_grid_view = findViewById(R.id.id_app_list_view);
            m_app_grid_view.setShortCutClickListener(this);
            m_bn_group = findViewById(R.id.id_app_bottom_bn_group);
            checkUserIcon();
        }
        CustomDesktopParam.checkPosition(m_page_guide_view);
        m_page_guide_view.setVisibility(View.VISIBLE);
    }

    private List<AppInfo> m_app_list;

    private void startLoadUserAppList() {
        if (m_load_handler == null) {
            m_load_thread = new HandlerThread("LoadThread");
            m_load_thread.start();
            m_load_handler = new Handler(m_load_thread.getLooper());

        }
        Log.d("TEST010", "startLoadUserAppList: ");
        if (m_load_runnable != null && m_load_runnable.m_thread_alive_flag) {
            m_load_runnable.m_thread_alive_flag = false;
            m_load_handler.removeCallbacks(m_load_runnable);
        }
        m_load_runnable = new LoadRunnable();
        m_load_handler.post(m_load_runnable);
    }

    private void stopLoadUserAppList() {
        if (m_load_thread != null) {
            m_load_thread.quitSafely();
            m_load_thread = null;
        }
        if (m_load_handler != null) {
            if (m_load_runnable != null && m_load_runnable.m_thread_alive_flag) {
                m_load_handler.removeCallbacks(m_load_runnable);
                m_load_runnable = null;
            }
            m_load_handler = null;
        }
    }

    private Handler m_load_handler;
    private HandlerThread m_load_thread;
    private LoadRunnable m_load_runnable;
    private Handler m_send_app_list_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            m_app_list = (List<AppInfo>) msg.obj;
            checkUserIcon();
        }
    };

    private class LoadRunnable implements Runnable, Utilities.LoadInterrupt {
        private boolean m_thread_alive_flag = true;

        @Override
        public void run() {
            Log.d("TEST20200704", "CustomDesktop getUserAppList: =start=");
            List<AppInfo> c_child_app_list = Utilities.getUserAppList(getContext(), this);
            if (m_thread_alive_flag) {
                if (m_send_app_list_handler.hasMessages(0)) {
                    m_send_app_list_handler.removeMessages(0);
                }
                Message msg = new Message();
                msg.what = 0;
                msg.obj = c_child_app_list;
                m_send_app_list_handler.sendMessage(msg);
                m_thread_alive_flag = false;
            }
            Log.d("TEST20200704", "CustomDesktop getUserAppList: =finished=");
        }

        @Override
        public boolean isThreadAlive() {
            return m_thread_alive_flag;
        }
    }

    private void checkUserIcon() {
        Log.d("TEST010", "checkUserIcon: ");
        if (m_app_grid_view != null) {
            m_app_grid_view.setAppInfoList(m_app_list);
            if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                m_page_guide_view.setPageGuide(0, m_app_grid_view.getChildItemCount());
            }
        }
    }

    public boolean onBackPressed() {
        boolean c_result = false;
        if (m_switch_mode == SWITCH_MODE_APP_LIST) {
            c_result = true;
            setSwitchMode(SWITCH_MODE_DESKTOP, true);
        }

        return c_result;
    }

    private LinearLayout m_bn_group;
    private AppPagerView m_app_grid_view;
    private PageGuideView m_page_guide_view;
    private float m_short_cut_motion_y = 0;
    private int m_switch_mode = 0;
    public final static int SWITCH_MODE_WALLPAPER = -1;
    public final static int SWITCH_MODE_DESKTOP = 0;
    public final static int SWITCH_MODE_APP_LIST = 1;

    private float m_switch_motion_length = 420;
    private ObjectAnimator m_switch_anim_0, m_switch_anim_1, m_switch_short_value_anim, m_page_guide_anim;

    public void setShortCutMotionY(float c_value) {
        m_short_cut_motion_y = c_value;
        this.invalidate();
    }

    public void setSwitchMode(int c_mode, boolean c_motion) {
        Log.d("SimonCheck003", "setSwitchMode: " + c_mode + " " + c_motion);
        if (c_mode == SWITCH_MODE_APP_LIST && m_app_list == null) {
            return;
        }
        if (m_switch_mode != c_mode) {
            m_switch_mode = c_mode;
            if (c_motion) {
                if (m_page_guide_anim != null && m_page_guide_anim.isRunning()) {
                    m_page_guide_anim.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    CustomDesktopParam.getTranslationYHeight(m_app_grid_view, new CustomDesktopParam.OnValueListener() {
                        @Override
                        public void onValue(int... value) {
                            m_page_guide_anim = ObjectAnimator.ofFloat(m_page_guide_view, "TranslationY", m_page_guide_view.getTranslationY(), value[0]);
                            m_page_guide_anim.setDuration(400);
                            m_page_guide_anim.start();
                        }
                    });
                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    m_page_guide_anim = ObjectAnimator.ofFloat(m_page_guide_view, "TranslationY", m_page_guide_view.getTranslationY(), 0f);
                    m_page_guide_anim.setDuration(400);
                    m_page_guide_anim.start();
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    m_page_guide_anim = ObjectAnimator.ofFloat(m_page_guide_view, "TranslationY", m_page_guide_view.getTranslationY(), 70);
                    m_page_guide_anim.setDuration(400);
                    m_page_guide_anim.start();
                }
                if (m_switch_short_value_anim != null && m_switch_short_value_anim.isRunning()) {
                    m_switch_short_value_anim.cancel();
                }

                if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    m_switch_short_value_anim = ObjectAnimator.ofFloat(this, "ShortCutMotionY", m_short_cut_motion_y, 1f);
                    m_switch_short_value_anim.setDuration(400);
                    m_switch_short_value_anim.start();

                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    m_switch_short_value_anim = ObjectAnimator.ofFloat(this, "ShortCutMotionY", m_short_cut_motion_y, 0);
                    m_switch_short_value_anim.setDuration(400);
                    m_switch_short_value_anim.start();
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    m_switch_short_value_anim = ObjectAnimator.ofFloat(this, "ShortCutMotionY", m_short_cut_motion_y, 1f);
                    m_switch_short_value_anim.setDuration(400);
                    m_switch_short_value_anim.start();
                }
                if (m_switch_anim_0 != null && m_switch_anim_0.isRunning()) {
                    m_switch_anim_0.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_APP_LIST) {

                    m_app_grid_view.setVisibility(View.VISIBLE);
                    PropertyValuesHolder c_app_list_layout_show_alpha = PropertyValuesHolder.ofFloat("Alpha", m_app_grid_view.getAlpha(), 1f);
                    PropertyValuesHolder c_app_list_layout_show_y = PropertyValuesHolder.ofFloat("TranslationY", m_app_grid_view.getTranslationY(), 0);
                    m_switch_anim_0 = ObjectAnimator.ofPropertyValuesHolder(m_app_grid_view, c_app_list_layout_show_alpha, c_app_list_layout_show_y);
                    m_switch_anim_0.setDuration(400);
                    m_switch_anim_0.start();
                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    PropertyValuesHolder c_app_list_layout_hide_alpha = PropertyValuesHolder.ofFloat("Alpha", m_app_grid_view.getAlpha(), 0f);
                    PropertyValuesHolder c_app_list_layout_hide_visibility = PropertyValuesHolder.ofInt("Visibility", View.VISIBLE, View.GONE);
                    PropertyValuesHolder c_app_list_layout_hide_y = PropertyValuesHolder.ofFloat("TranslationY", m_app_grid_view.getTranslationY(), m_switch_motion_length);
                    m_switch_anim_0 = ObjectAnimator.ofPropertyValuesHolder(m_app_grid_view, c_app_list_layout_hide_alpha, c_app_list_layout_hide_visibility, c_app_list_layout_hide_y);
                    m_switch_anim_0.setDuration(400);
                    m_switch_anim_0.start();
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    //m_app_grid_view.setAppInfoList(m_app_list);
                    //m_app_grid_view.setVisibility(View.VISIBLE);
                    //PropertyValuesHolder c_app_list_layout_show_alpha = PropertyValuesHolder.ofFloat("Alpha",m_app_grid_view.getAlpha() , 1f);
                    //PropertyValuesHolder c_app_list_layout_show_y = PropertyValuesHolder.ofFloat("TranslationY",m_app_grid_view.getTranslationY() , 0);
                    //m_switch_anim_0 = ObjectAnimator.ofPropertyValuesHolder(m_app_grid_view,c_app_list_layout_show_alpha,c_app_list_layout_show_y);
                    //m_switch_anim_0.setDuration(400);
                    //m_switch_anim_0.start();
                }
                if (m_switch_anim_1 != null && m_switch_anim_1.isRunning()) {
                    m_switch_anim_1.cancel();
                }
                /*if (m_switch_mode == SWITCH_MODE_DESKTOP)
                {
                    m_bn_group.setVisibility(View.VISIBLE);
                    PropertyValuesHolder c_app_list_layout_show_alpha = PropertyValuesHolder.ofFloat("Alpha",m_bn_group.getAlpha() , 1f);
                    PropertyValuesHolder c_app_list_layout_show_y = PropertyValuesHolder.ofFloat("TranslationY",m_bn_group.getTranslationY() , 10);
                    m_switch_anim_1 = ObjectAnimator.ofPropertyValuesHolder(m_bn_group,c_app_list_layout_show_alpha,c_app_list_layout_show_y);
                    m_switch_anim_1.setDuration(400);
                    m_switch_anim_1.start();
                }
                else if (m_switch_mode == SWITCH_MODE_APP_LIST)
                {
                    PropertyValuesHolder c_app_list_layout_hide_alpha = PropertyValuesHolder.ofFloat("Alpha",m_bn_group.getAlpha() , 0f);
                    PropertyValuesHolder c_app_list_layout_hide_visibility = PropertyValuesHolder.ofInt("Visibility",View.VISIBLE , View.GONE);
                    PropertyValuesHolder c_app_list_layout_hide_y = PropertyValuesHolder.ofFloat("TranslationY",m_bn_group.getTranslationY() , 100);
                    m_switch_anim_1 = ObjectAnimator.ofPropertyValuesHolder(m_bn_group,c_app_list_layout_hide_alpha,c_app_list_layout_hide_visibility,c_app_list_layout_hide_y);
                    m_switch_anim_1.setDuration(400);
                    m_switch_anim_1.start();
                }
                else if (m_switch_mode == SWITCH_MODE_WALLPAPER)
                {
                    PropertyValuesHolder c_app_list_layout_hide_alpha = PropertyValuesHolder.ofFloat("Alpha",m_bn_group.getAlpha() , 0f);
                    PropertyValuesHolder c_app_list_layout_hide_visibility = PropertyValuesHolder.ofInt("Visibility",View.VISIBLE , View.GONE);
                    PropertyValuesHolder c_app_list_layout_hide_y = PropertyValuesHolder.ofFloat("TranslationY",m_bn_group.getTranslationY() , 100);
                    m_switch_anim_1 = ObjectAnimator.ofPropertyValuesHolder(m_bn_group,c_app_list_layout_hide_alpha,c_app_list_layout_hide_visibility,c_app_list_layout_hide_y);
                    m_switch_anim_1.setDuration(400);
                    m_switch_anim_1.start();
                }*/
            } else {
                if (m_page_guide_anim != null && m_page_guide_anim.isRunning()) {
                    m_page_guide_anim.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    m_page_guide_view.setTranslationY(70);
                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    m_page_guide_view.setTranslationY(0);
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    m_page_guide_view.setTranslationY(70);
                }
                if (m_switch_short_value_anim != null && m_switch_short_value_anim.isRunning()) {
                    m_switch_short_value_anim.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    setShortCutMotionY(1f);
                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    setShortCutMotionY(0);
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    setShortCutMotionY(1f);
                }
                if (m_switch_anim_0 != null && m_switch_anim_0.isRunning()) {
                    m_switch_anim_0.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    m_app_grid_view.setVisibility(View.VISIBLE);
                    m_app_grid_view.setAlpha(1f);
                    m_app_grid_view.setTranslationY(0);
                } else if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    m_app_grid_view.setVisibility(View.GONE);
                    m_app_grid_view.setAlpha(0);
                    m_app_grid_view.setTranslationY(m_switch_motion_length);
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    //m_app_grid_view.setAppInfoList(m_app_list);
                    //m_app_grid_view.setVisibility(View.VISIBLE);
                    //m_app_grid_view.setAlpha(1f);
                    //m_app_grid_view.setTranslationY(0);
                }

                if (m_switch_anim_1 != null && m_switch_anim_1.isRunning()) {
                    m_switch_anim_1.cancel();
                }
                if (m_switch_mode == SWITCH_MODE_DESKTOP) {
                    m_bn_group.setVisibility(View.VISIBLE);
                    m_bn_group.setAlpha(1);
                    m_bn_group.setTranslationY(10);
                } else if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                    m_bn_group.setVisibility(View.GONE);
                    m_bn_group.setAlpha(0);
                    m_bn_group.setTranslationY(100);
                } else if (m_switch_mode == SWITCH_MODE_WALLPAPER) {
                    m_bn_group.setVisibility(View.GONE);
                    m_bn_group.setAlpha(0);
                    m_bn_group.setTranslationY(100);
                }
            }
            if (m_switch_mode == SWITCH_MODE_APP_LIST) {
                m_app_grid_view.setPageGuideOrder(this);
            } else {
                m_app_grid_view.setPageGuideOrder(null);
                onPageGuideChanged(m_last_center_index, m_last_page_count + 1);
            }
        }
    }

    @Override
    public void onAppLongClicked(AppInfo c_app_info) {
        Log.d("SimonCheck003", "onAppLongClicked: ");
        boolean c_push_flag = true;
        for (ShortCut c_short_cut : m_current_short_cut_list) {
            AppInfo c_old_info = c_short_cut.getAppInfo();
            Log.d("TEST010", "onAppLongClicked: c_old_info " + c_old_info.className);
            Log.d("TEST010", "onAppLongClicked: c_app_info " + c_app_info.className);

            if (c_old_info.className != null && c_old_info.className.equals(c_app_info.className)) {
                c_push_flag = false;
                break;
            }
        }
        if (c_push_flag) {
            setSwitchMode(SWITCH_MODE_DESKTOP, true);
            ShortCut c_new_short_cut = new ShortCut(c_app_info);
            this.pushShortCut(c_new_short_cut);
        }
    }

    @Override
    public void onAppClicked(AppInfo c_app_info) {
        m_listener.onAppClicked(c_app_info);
    }

    @Override
    public void onDragShortCutStart() {
        m_listener.onDragShortCutStart();
    }

    @Override
    public void onDragShortCutFinished() {
        m_listener.onDragShortCutFinished();
    }


}
