package com.spd.custom.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.spd.home.FullscreenActivity;

public class ShortCut implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private AppInfo m_app_info;
    private ShortCutBaseView m_app_view;
    private int m_app_postion = -100;
    private float m_shortcut_current_coordinate_x, m_shortcut_current_coordinate_y;
    //private float m_shortcut_target_coordinate_x,m_shortcut_target_coordinate_y;
    private float m_shortcut_real_coordinate_x, m_shortcut_real_coordinate_y;
    private ObjectAnimator m_coordinate_anim;
    private DecelerateInterpolator m_anim_interpolator = new DecelerateInterpolator();
    private boolean m_short_cut_show = false;
    private CustomDesktop m_desktop;

    public ShortCut(AppInfo c_app_info) {
        m_app_info = c_app_info;
    }

    public void setDeskTop(CustomDesktop c_destop) {
        m_desktop = c_destop;
        testShortCutPostion();
    }

    public AppInfo getAppInfo() {
        return m_app_info;
    }

    public int getShortCutPostion() {
        return m_app_postion;
    }

    public void setShortCutPostion(int c_postion) {
        if (m_coordinate_anim != null && m_coordinate_anim.isRunning()) {
            m_coordinate_anim.pause();
            m_coordinate_anim.cancel();
        }
        m_app_postion = c_postion;
        if (m_app_view != null) {
            m_app_view.setShortCutInfo(this);
        }
        if (m_app_postion >= 0) {
            float[] c_local_coordinate = countLocalCoordinateXY(c_postion);
            m_shortcut_current_coordinate_x = c_local_coordinate[0];
            m_shortcut_current_coordinate_y = c_local_coordinate[1];
            m_shortcut_real_coordinate_x = m_shortcut_current_coordinate_x - m_desktop_offset_x;
            m_shortcut_real_coordinate_y = m_shortcut_current_coordinate_y + m_desktop_offset_y;
            testShortCutPostion();
        }
    }

    public void gotoShortCutPostion(int c_postion, boolean c_test_flag) {
        if (!c_test_flag) {
            m_app_postion = c_postion;
        }
        if (m_coordinate_anim != null && m_coordinate_anim.isRunning()) {
            m_coordinate_anim.pause();
            m_coordinate_anim.cancel();
        }
        if (m_app_view != null) {
            m_app_view.setShortCutInfo(this);
        }
        float[] c_local_coordinate = countLocalCoordinateXY(c_postion);
        float c_shortcut_target_coordinate_x = c_local_coordinate[0];
        float c_shortcut_target_coordinate_y = c_local_coordinate[1];
        if (Math.abs(m_shortcut_current_coordinate_x - c_shortcut_target_coordinate_x) > 0.1f || Math.abs(m_shortcut_current_coordinate_y - c_shortcut_target_coordinate_y) > 0.1f) {
            if (m_app_postion >= 0 && m_desktop != null) {
                PropertyValuesHolder c_coordinate_x = PropertyValuesHolder.ofFloat("CurrentCoordinateX", m_shortcut_current_coordinate_x, c_shortcut_target_coordinate_x);
                PropertyValuesHolder c_coordinate_y = PropertyValuesHolder.ofFloat("CurrentCoordinateY", m_shortcut_current_coordinate_y, c_shortcut_target_coordinate_y);
                m_coordinate_anim = ObjectAnimator.ofPropertyValuesHolder(this, c_coordinate_x, c_coordinate_y);
                m_coordinate_anim.setInterpolator(m_anim_interpolator);
                m_coordinate_anim.setDuration(500);
                m_coordinate_anim.start();
            } else {
                m_shortcut_current_coordinate_x = c_shortcut_target_coordinate_x;
                m_shortcut_current_coordinate_y = c_shortcut_target_coordinate_y;
            }
        }
    }

    public float getShortRealPostionX() {
        return m_shortcut_real_coordinate_x;
    }

    public float getShortRealPostionY() {
        return m_shortcut_real_coordinate_y;
    }

    public void setDesktopOffsetXY(float c_offset_x, float c_offset_y) {
        // m_desktop_offset_x, m_desktop_offset_y 与拖拽相关
        m_desktop_offset_x = c_offset_x;
        m_desktop_offset_y = c_offset_y;
        if (!m_drag_flag) {
            m_shortcut_real_coordinate_x = m_shortcut_current_coordinate_x - m_desktop_offset_x;
            m_shortcut_real_coordinate_y = m_shortcut_current_coordinate_y + m_desktop_offset_y;
            testShortCutPostion();
        }
    }

    public void setCurrentCoordinateX(float c_x) {
        if (!m_drag_flag) {
            m_shortcut_current_coordinate_x = c_x;
            m_shortcut_real_coordinate_x = m_shortcut_current_coordinate_x - m_desktop_offset_x;
            testShortCutPostion();
        }
    }

    public void setCurrentCoordinateY(float c_y) {
        if (!m_drag_flag) {
            m_shortcut_current_coordinate_y = c_y;
            m_shortcut_real_coordinate_y = m_shortcut_current_coordinate_y + m_desktop_offset_y;
            testShortCutPostion();
        }
    }

    private int m_last_test_postion = -1;

    public void startDragShortcut(float c_x, float c_y) {
        m_drag_flag = true;
        if (m_app_view != null) {
            m_app_view.setShortDragMode(m_drag_flag);
        }
        m_shortcut_real_coordinate_x = c_x;
        m_shortcut_real_coordinate_y = c_y;
        m_shortcut_current_coordinate_x = m_shortcut_real_coordinate_x + m_desktop_offset_x;
        m_shortcut_current_coordinate_y = m_shortcut_real_coordinate_y - m_desktop_offset_y;
        int c_test_postion = testDragPotion(c_x, c_y);
        if (c_test_postion == -1) {
            c_test_postion = m_app_postion;
        }
        if (c_test_postion >= 0) {
            if (m_last_test_postion != c_test_postion) {
                m_last_test_postion = c_test_postion;
                m_desktop.testShortCut(c_test_postion, m_app_postion);
            }
        } else if (c_test_postion != -1) {
            m_desktop.testShortCut(c_test_postion, m_app_postion);
        }
        testShortCutPostion();
    }

    public void stopDragShortcut(float c_x, float c_y) {
        m_drag_flag = false;
        if (m_app_view != null) {
            m_app_view.setShortDragMode(m_drag_flag);
        }
        m_shortcut_real_coordinate_x = c_x;
        m_shortcut_real_coordinate_y = c_y;
        m_shortcut_current_coordinate_x = m_shortcut_real_coordinate_x + m_desktop_offset_x;
        m_shortcut_current_coordinate_y = m_shortcut_real_coordinate_y - m_desktop_offset_y;
        int c_test_postion = testDragPotion(c_x, c_y);
        if (c_test_postion != CustomDesktop.DESKTOP_POSTION_DELETE) {
            if (m_last_test_postion >= 0) {
                m_desktop.finishTestEmptyShortCut(m_last_test_postion);
            }
        } else {
            m_desktop.finishTestEmptyShortCut(c_test_postion);
        }
        m_last_test_postion = -1;
        setTestDeleteFlag(false);
        testShortCutPostion();
    }

    private int testDragPotion(float c_x, float c_y) {
        int c_test_postion = -1;
        float c_safe_side = 20f;
        if (c_y > -FullscreenActivity.DESKTOP_HEIGHT / 2f + FullscreenActivity.DESKTOP_MARGIN_TOP + c_safe_side && c_y < FullscreenActivity.DESKTOP_HEIGHT / 2f - FullscreenActivity.DESKTOP_MARGIN_BOTTOM - c_safe_side) {
            if (c_x < -FullscreenActivity.DESKTOP_WIDTH / 2 + FullscreenActivity.DESKTOP_MARGIN_LEFT + c_safe_side) {
                c_test_postion = CustomDesktop.DESKTOP_POSTION_PREV;
            } else if (c_x > FullscreenActivity.DESKTOP_WIDTH / 2 - FullscreenActivity.DESKTOP_MARGIN_RIGHT - c_safe_side) {
                c_test_postion = CustomDesktop.DESKTOP_POSTION_NEXT;
            } else {
                int c_page_index = m_desktop.m_desktop_offset.getCurrentIndex();
                float c_test_coordinate_x = m_shortcut_real_coordinate_x + m_desktop_offset_x - c_page_index * FullscreenActivity.DESKTOP_WIDTH;
                float c_test_coordinate_y = m_shortcut_real_coordinate_y - m_desktop_offset_y;
                int c_item_every_page = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
                float c_real_layout_width = FullscreenActivity.DESKTOP_WIDTH - FullscreenActivity.DESKTOP_MARGIN_LEFT - FullscreenActivity.DESKTOP_MARGIN_RIGHT;
                float c_real_layout_height = FullscreenActivity.DESKTOP_HEIGHT - FullscreenActivity.DESKTOP_MARGIN_TOP - FullscreenActivity.DESKTOP_MARGIN_BOTTOM;
                float c_real_layout_width_cell = c_real_layout_width / FullscreenActivity.DESKTOP_PAGE_COLUMN;
                float c_real_layout_height_cell = c_real_layout_height / FullscreenActivity.DESKTOP_PAGE_ROW;
                float c_test_x_org = -c_real_layout_width / 2f;
                float c_test_y_org = -c_real_layout_height / 2f;
                int c_column = (int) ((c_test_coordinate_x - c_test_x_org) / c_real_layout_width_cell);
                int c_row = (int) ((c_test_coordinate_y - c_test_y_org) / c_real_layout_height_cell);
                c_test_postion = c_page_index * c_item_every_page + c_row * FullscreenActivity.DESKTOP_PAGE_COLUMN + c_column;
            }
            setTestDeleteFlag(false);
        } else if (c_y > FullscreenActivity.DESKTOP_HEIGHT / 2f - FullscreenActivity.DESKTOP_MARGIN_BOTTOM && Math.abs(c_x) < 100) {
            c_test_postion = CustomDesktop.DESKTOP_POSTION_DELETE;
            setTestDeleteFlag(true);
        } else {
            setTestDeleteFlag(false);
        }
        return c_test_postion;
    }


    private boolean m_drag_flag = false;
    private float[] m_count_coordinate = new float[2];

    private float[] countLocalCoordinateXY(int c_postion) {
        // 每个页面的 item 数量
        int c_item_every_page = FullscreenActivity.DESKTOP_PAGE_COLUMN * FullscreenActivity.DESKTOP_PAGE_ROW;
        // 计算 item 位于哪个页面
        int c_page_index = c_postion / c_item_every_page;
        // 计算 item 在页面中的 index
        int c_postion_page = c_postion % c_item_every_page;
        // 计算 grid 布局宽度
        float c_real_layout_width = FullscreenActivity.DESKTOP_WIDTH - FullscreenActivity.DESKTOP_MARGIN_LEFT - FullscreenActivity.DESKTOP_MARGIN_RIGHT;
        // 计算 grid 布局高度
        float c_real_layout_height = FullscreenActivity.DESKTOP_HEIGHT - FullscreenActivity.DESKTOP_MARGIN_TOP - FullscreenActivity.DESKTOP_MARGIN_BOTTOM;
        // 计算 item 宽度 包括间距
        float c_real_layout_width_cell = c_real_layout_width / FullscreenActivity.DESKTOP_PAGE_COLUMN;
        // 计算 item 高度 包括间距
        float c_real_layout_height_cell = c_real_layout_height / FullscreenActivity.DESKTOP_PAGE_ROW;
        // 计算位于当前页面第几列
        int c_postion_cell_x = c_postion_page % FullscreenActivity.DESKTOP_PAGE_COLUMN;
        // 计算位于当前页面第几行
        int c_postion_cell_y = c_postion_page / FullscreenActivity.DESKTOP_PAGE_COLUMN;
        // 计算左上角x坐标 坐标系原点位于屏幕中心点？
        m_count_coordinate[0] = c_postion_cell_x * c_real_layout_width_cell + c_real_layout_width_cell / 2f + FullscreenActivity.DESKTOP_MARGIN_LEFT + c_page_index * FullscreenActivity.DESKTOP_WIDTH - FullscreenActivity.DESKTOP_WIDTH / 2;
        // 计算左上角y坐标 坐标系原点位于屏幕中心点？
        m_count_coordinate[1] = c_postion_cell_y * c_real_layout_height_cell + c_real_layout_height_cell / 2f + FullscreenActivity.DESKTOP_MARGIN_TOP - FullscreenActivity.DESKTOP_HEIGHT / 2;
        return m_count_coordinate;
    }

    private float m_desktop_offset_x = 0;
    private float m_desktop_offset_y = 0;

    public void testShortCutPostion() {
        if (m_drag_flag) {
            if (!m_short_cut_show) {
                m_app_view = m_desktop.getShortCutView(this);
                m_app_view.setShortCutInfo(this);
                m_app_view.setShortCutCoordinate(m_shortcut_real_coordinate_x, m_shortcut_real_coordinate_y);
                m_app_view.setShortDeleteMode(false);
                m_app_view.setBaseAlpha(m_base_alpha);
                m_desktop.addView(m_app_view);
                m_short_cut_show = true;
            } else {
                m_app_view.setShortCutCoordinate(m_shortcut_real_coordinate_x, m_shortcut_real_coordinate_y);
            }
        } else if (m_app_postion >= 0 && m_desktop != null) {
            boolean c_show = false;
            if (m_shortcut_real_coordinate_x > -FullscreenActivity.DESKTOP_WIDTH / 2 - 140 && m_shortcut_real_coordinate_x < FullscreenActivity.DESKTOP_WIDTH / 2 + 140) {
                c_show = true;
            }
            if (!c_show && m_short_cut_show) {
                if (m_app_view != null) {
                    m_desktop.removeView(m_app_view);
                    m_app_view = null;
                }
            } else if (c_show && !m_short_cut_show) {
                m_app_view = m_desktop.getShortCutView(this);
                m_app_view.setShortCutInfo(this);
                m_app_view.setShortCutCoordinate(m_shortcut_real_coordinate_x, m_shortcut_real_coordinate_y);
                m_app_view.setShortDeleteMode(false);
                m_app_view.setBaseAlpha(m_base_alpha);
                m_desktop.addView(m_app_view);
            } else if (m_app_view != null) {
                m_app_view.setShortCutCoordinate(m_shortcut_real_coordinate_x, m_shortcut_real_coordinate_y);
            }
            m_short_cut_show = c_show;
        }
    }

    public void releaseShortCut() {
        if (m_desktop != null && m_app_view != null) {
            m_desktop.removeView(m_app_view);
            m_app_view = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (m_desktop != null && m_base_alpha > 0.99f) {
            m_desktop.onShortCutClicked(this);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (m_base_alpha > 0.99f) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (m_app_view != null) {
                    m_app_view.bringToFront();
                    m_app_view.setShortDownMode(true);
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (m_app_view != null) {
                    m_app_view.setShortDownMode(false);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        m_app_view.bringToFront();
        if (m_coordinate_anim != null && m_coordinate_anim.isRunning()) {
            m_coordinate_anim.pause();
            m_coordinate_anim.cancel();
        }
        if (m_desktop != null && m_base_alpha > 0.99f) {
            m_desktop.redragShortCut(this);
        }
        if (m_app_view != null && m_base_alpha > 0.99f) {
            m_app_view.setShortDownMode(false);
        }
        return false;
    }

    private void setTestDeleteFlag(boolean b) {
        if (m_app_view != null && m_app_view.getShortDeleteMode() != b) {
            m_app_view.setShortDeleteMode(b);
        }
    }

    private float m_base_alpha = 1f;

    public void setBaseAlpha(float c_alpha) {
        m_base_alpha = c_alpha;
        if (m_app_view != null) {
            m_app_view.setBaseAlpha(m_base_alpha);
        }
    }
}
