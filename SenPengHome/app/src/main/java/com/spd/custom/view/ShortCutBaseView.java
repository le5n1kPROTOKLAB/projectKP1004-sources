package com.spd.custom.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.spd.Scene.SceneManager;
import com.spd.home.FullscreenActivity;

public class ShortCutBaseView extends FrameLayout {
    private String TAG = "ShortCutBaseView";
    protected static Paint m_paint = new Paint();

    static {
        m_paint.setTextSize(22);
    }

    protected static float[] m_bg_clip_point;
    protected static float[] m_bg_trans_potin;
    private ShortCut m_current_short_cut_info;
    private View m_touch_view;
    private float m_view_center_x, m_view_center_y;
    protected Camera m_camera = new Camera();
    protected Matrix m_matrix = new Matrix();
    protected static Path m_path = new Path();
    protected Bitmap m_bmp_part;
    protected Bitmap m_bmp_shadow;
    protected Bitmap m_bmp_bg;
    protected float m_icon_y_offset = 15;
    protected float m_text_y_offset = 80;

    public ShortCutBaseView(Context context) {
        super(context);
        initView(context);
        this.setTag(SceneManager.SCENE_STATE_TAG, true);
    }

    public ShortCutBaseView(Context context, AppInfo c_app_info) {
        super(context);
        resetResource(c_app_info);
        initView(context);
        this.setTag(SceneManager.SCENE_STATE_TAG, true);
    }

    public ShortCutBaseView(Context context, int c_bg_id, int c_part_id) {
        super(context);
        resetResource(c_bg_id, c_part_id);
        initView(context);
        this.setTag(SceneManager.SCENE_STATE_TAG, true);
    }

    protected ColorMatrixColorFilter m_bg_color_filter;
    protected int m_bg_color = 0;

    public void resetResource(AppInfo c_app_info) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "resetResource: =0=" + c_app_info);
        }


        m_bg_color = c_app_info.iconColor;
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(c_app_info.colorHue, c_app_info.colorHue, c_app_info.colorHue, 1); // 红、绿、蓝三分量按相同的比
        m_bg_color_filter = new ColorMatrixColorFilter(cm);
        m_bmp_bg = null;
        //m_bmp_bg = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.app_icon_bg_eq);
        m_bmp_part = c_app_info.iconBitmap;
        m_bmp_shadow = ShadowBuilder.buildBitmapShadow(getContext(), m_bmp_part, 8);
    }

    public void resetResource(int c_bg_id, int c_part_id) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "resetResource: =1=" + c_bg_id + " " + c_part_id);
        }


        m_bg_color = 0;
        m_bg_color_filter = null;
        m_bmp_bg = BitmapFactory.decodeResource(getContext().getResources(), c_bg_id);
        m_bmp_part = BitmapFactory.decodeResource(getContext().getResources(), c_part_id);
        m_bmp_shadow = ShadowBuilder.buildBitmapShadow(getContext(), m_bmp_part, 8);
    }

    private void initView(Context context) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "initView: =0=");
        }
        //Log.d(TAG, this.hashCode()+"initView: =0=");
        setWillNotDraw(false);
        m_touch_view = new View(context);
        FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(302, 174);
        c_para.gravity = Gravity.CENTER;
        m_touch_view.setLayoutParams(c_para);
        m_touch_view.setLongClickable(true);
        this.addView(m_touch_view);
        if (m_bg_clip_point == null) {
            m_bg_trans_potin = new float[24];
            float c_card_width = 304f;
            float c_card_height = 178f;
            float c_card_arc_r = 16f;
            m_bg_clip_point = new float[24];
            m_bg_clip_point[0] = -c_card_width / 2f;
            m_bg_clip_point[1] = -c_card_height / 2f + c_card_arc_r;
            m_bg_clip_point[2] = -c_card_width / 2f;
            m_bg_clip_point[3] = -c_card_height / 2f;
            m_bg_clip_point[4] = -c_card_width / 2f + c_card_arc_r;
            m_bg_clip_point[5] = -c_card_height / 2f;
            m_bg_clip_point[6] = c_card_width / 2f - c_card_arc_r;
            m_bg_clip_point[7] = -c_card_height / 2f;
            m_bg_clip_point[8] = c_card_width / 2f;
            m_bg_clip_point[9] = -c_card_height / 2f;
            m_bg_clip_point[10] = c_card_width / 2f;
            m_bg_clip_point[11] = -c_card_height / 2f + c_card_arc_r;
            m_bg_clip_point[12] = c_card_width / 2f;
            m_bg_clip_point[13] = c_card_height / 2f - c_card_arc_r;
            m_bg_clip_point[14] = c_card_width / 2f;
            m_bg_clip_point[15] = c_card_height / 2f;
            m_bg_clip_point[16] = c_card_width / 2f - c_card_arc_r;
            m_bg_clip_point[17] = c_card_height / 2f;
            m_bg_clip_point[18] = -c_card_width / 2f + c_card_arc_r;
            m_bg_clip_point[19] = c_card_height / 2f;
            m_bg_clip_point[20] = -c_card_width / 2f;
            m_bg_clip_point[21] = c_card_height / 2f;
            m_bg_clip_point[22] = -c_card_width / 2f;
            m_bg_clip_point[23] = c_card_height / 2f - c_card_arc_r;
        }
    }

    protected int m_init_flag = 0;
    private float m_init_value = 0;
    protected float m_init_value_bg = 0, m_init_value_part = 0, m_init_value_text = 0;

    private float m_init_offset = 0.15f;
    protected ObjectAnimator m_init_anim;
    private int m_init_duration = 500;
    public static final int SHORT_CUT_INIT_FLAG_SHOW = 0;
    public static final int SHORT_CUT_INIT_FLAG_HIDE = 1;
    private Handler m_motion_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int c_init_flag = msg.arg1;
            boolean c_motion = msg.arg2 == 1;
            setViewInitFlag(c_init_flag, c_motion);
        }
    };

    private void setViewInitFlag(int c_init_flag, boolean c_motion) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setViewInitFlag: " + c_init_flag + " " + c_motion);
        }
        //Log.d(TAG, this.hashCode()+"setViewInitFlag: =0="+c_init_flag+" "+c_motion);
        m_init_flag = c_init_flag;
        if (m_init_anim != null && m_init_anim.isRunning()) {
            m_init_anim.pause();
            m_init_anim.cancel();
        }
        if (!c_motion) {
            m_init_value = c_init_flag;
            countProgressValue();
            this.invalidate();
        } else {
            m_init_anim = ObjectAnimator.ofFloat(this, "ProgressValue", m_init_value, c_init_flag);
            m_init_anim.setInterpolator(m_linear_interpolator);
            m_init_anim.setDuration(m_init_duration);
            m_init_anim.start();
        }
    }

    public void setViewInitFlag(int c_init_flag, boolean c_motion, int c_delay_time) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setViewInitFlag: " + c_init_flag + " " + c_motion + " " + c_delay_time);
        }
        //Log.d(TAG, this.hashCode()+"setViewInitFlag: =1="+c_init_flag+" "+c_motion+" "+c_delay_time);
        if (m_motion_handler.hasMessages(0)) {
            m_motion_handler.removeMessages(0);
        }
        if (c_delay_time <= 0) {
            setViewInitFlag(c_init_flag, c_motion);
        } else {
            Message c_msg = new Message();
            c_msg.what = 0;
            c_msg.arg1 = c_init_flag;
            if (c_motion) {
                c_msg.arg2 = 1;
            } else {
                c_msg.arg2 = 0;
            }
            m_motion_handler.sendMessageDelayed(c_msg, c_delay_time);
        }
    }

    private LinearInterpolator m_linear_interpolator = new LinearInterpolator();
    private DecelerateInterpolator m_decelerate_interpolator = new DecelerateInterpolator();

    public void setProgressValue(float c_value) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setProgressValue: " + c_value);
        }
        //Log.d("ShortCutBaseView", "setProgressValue: "+c_value);
        m_init_value = c_value;
        countProgressValue();
        this.invalidate();
    }

    private void countProgressValue() {
        //Log.d("ShortCutBaseView", "countProgressValue: ");
        //Log.d(TAG, this.hashCode()+"countProgressValue: =0=");
        if (m_init_flag == SHORT_CUT_INIT_FLAG_SHOW) {
            float c_real_duration_length = (SHORT_CUT_INIT_FLAG_SHOW - SHORT_CUT_INIT_FLAG_HIDE + m_init_offset * 2f); // -0.7f
            float c_temp_value = (m_init_value - SHORT_CUT_INIT_FLAG_HIDE) / c_real_duration_length;
            c_temp_value = 1f - Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_bg = m_decelerate_interpolator.getInterpolation(c_temp_value);
            c_temp_value = (m_init_value + m_init_offset - SHORT_CUT_INIT_FLAG_HIDE) / c_real_duration_length;
            c_temp_value = 1f - Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_part = m_decelerate_interpolator.getInterpolation(c_temp_value);
            c_temp_value = (m_init_value + m_init_offset * 2f - SHORT_CUT_INIT_FLAG_HIDE) / c_real_duration_length;
            c_temp_value = 1f - Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_text = m_decelerate_interpolator.getInterpolation(c_temp_value);
        } else if (m_init_flag == SHORT_CUT_INIT_FLAG_HIDE) {
            float c_real_duration_length = (SHORT_CUT_INIT_FLAG_HIDE - SHORT_CUT_INIT_FLAG_SHOW - m_init_offset * 2f); // 0.7f
            float c_temp_value = (m_init_value - SHORT_CUT_INIT_FLAG_SHOW) / c_real_duration_length;
            c_temp_value = Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_text = m_decelerate_interpolator.getInterpolation(c_temp_value);
            c_temp_value = (m_init_value - m_init_offset - SHORT_CUT_INIT_FLAG_SHOW) / c_real_duration_length;
            c_temp_value = Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_part = m_decelerate_interpolator.getInterpolation(c_temp_value);
            c_temp_value = (m_init_value - m_init_offset * 2f - SHORT_CUT_INIT_FLAG_SHOW) / c_real_duration_length;
            c_temp_value = Math.min(Math.max(c_temp_value, 0f), 1f);
            m_init_value_bg = m_decelerate_interpolator.getInterpolation(c_temp_value);
        }
    }

    public ShortCut getShortCutInfo() {
        return m_current_short_cut_info;
    }

    public void setShortCutInfo(ShortCut c_short_cut) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setShortCutInfo: " + c_short_cut);
        }
        //Log.d(TAG, this.hashCode()+"setShortCutInfo: =0="+c_short_cut);
        m_current_short_cut_info = c_short_cut;
        m_touch_view.setOnClickListener(m_current_short_cut_info);
        m_touch_view.setOnTouchListener(m_current_short_cut_info);
        m_touch_view.setOnLongClickListener(m_current_short_cut_info);
    }

    public void setShortCutCoordinate(float c_x, float c_y) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setShortCutCoordinate: =0=" + c_x + " " + c_y);
        }
        //Log.d(TAG, this.hashCode()+"setShortCutCoordinate: =0="+c_x+" "+c_y);
        m_view_center_x = c_x;
        m_view_center_y = c_y;
        m_touch_view.setTranslationX(c_x);
        m_touch_view.setTranslationY(c_y);
        this.invalidate();
    }

    protected boolean m_delete_test_mode = false;

    public boolean getShortDeleteMode() {
        return m_delete_test_mode;
    }

    public void setShortDeleteMode(boolean b) {
        //Log.d(TAG, this.hashCode()+"setShortDeleteMode: =0="+b);
        m_delete_test_mode = b;
        this.invalidate();
    }

    private boolean m_drag_flag = false;
    protected float m_drag_value = 0.0f;
    protected float m_drag_length = 30;
    private ObjectAnimator m_drag_value_anim;

    public void setDragValue(float c_value) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setDragValue: =0=" + c_value);
        }
        //Log.d(TAG, this.hashCode()+"setDragValue: =0="+c_value);
        m_drag_value = c_value;
        this.invalidate();
    }

    public void setShortDragMode(boolean b) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setShortDragMode: =0=" + b);
        }
        //Log.d(TAG, this.hashCode()+"setShortDragMode: =0="+b);
        if (m_drag_flag != b) {
            m_drag_flag = b;
            float c_drag_value = m_drag_flag ? 1f : 0f;
            if (m_drag_value_anim != null && m_drag_value_anim.isRunning()) {
                m_drag_value_anim.pause();
                m_drag_value_anim.cancel();
            }
            m_drag_value_anim = ObjectAnimator.ofFloat(this, "DragValue", m_drag_value, c_drag_value);
            m_drag_value_anim.setInterpolator(m_decelerate_interpolator);
            m_drag_value_anim.setDuration(500);
            m_drag_value_anim.start();
        }
    }

    private boolean m_down_flag = false;
    protected float m_down_value = 0.0f;
    protected float m_down_circle_value = 0.0f;
    protected float m_down_circle_raduis = 70f;
    protected float m_down_length = 20;
    private ObjectAnimator m_down_value_anim, m_down_circle_value_anim;

    public void setDownCircleValue(float c_value) {
        //Log.d(TAG, this.hashCode()+"setDownCircleValue: =0="+c_value);
        //Log.d("SimonCheck002", "setDownCircleValue: "+c_value);
        m_down_circle_value = c_value;
        this.invalidate();
    }

    public void setDownValue(float c_value) {
        //Log.d(TAG, this.hashCode()+"setDownValue: =0="+c_value);
        m_down_value = c_value;
        this.invalidate();
    }


    public void setShortDownMode(boolean b) {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "setShortDownMode: =0=" + b);
        }
        //Log.d(TAG, this.hashCode()+"setShortDownMode: =0="+b);
        if (m_down_flag != b) {
            m_down_flag = b;
            float c_drag_value = m_down_flag ? 1f : 0f;
            int c_duration = m_down_flag ? 100 : 500;
            if (m_down_value_anim != null && m_down_value_anim.isRunning()) {
                m_down_value_anim.pause();
                m_down_value_anim.cancel();
            }
            m_down_value_anim = ObjectAnimator.ofFloat(this, "DownValue", m_down_value, c_drag_value);
            m_down_value_anim.setInterpolator(m_decelerate_interpolator);
            m_down_value_anim.setDuration(c_duration);
            m_down_value_anim.start();

            if (m_down_circle_value_anim != null && m_down_circle_value_anim.isRunning()) {
                m_down_circle_value_anim.pause();
                m_down_circle_value_anim.cancel();
            }
            if (m_down_flag) {
                m_down_circle_value = 0;
                m_down_circle_value_anim = ObjectAnimator.ofFloat(this, "DownCircleValue", m_down_circle_value, 1f);
            } else {
                m_down_circle_value_anim = ObjectAnimator.ofFloat(this, "DownCircleValue", m_down_circle_value, 2f);
            }
            m_down_circle_value_anim.setInterpolator(m_decelerate_interpolator);
            m_down_circle_value_anim.setDuration(c_duration);
            m_down_circle_value_anim.start();
        }
    }

    protected String getDrawTitle() {
        if (this instanceof ShortCutMusicView) {
            Log.d(ShortCutMusicView.TAG, this.hashCode() + "getDrawTitle: " + getShortCutInfo().getAppInfo().mAppName);
        }
        //Log.d(TAG, this.hashCode()+"getDrawTitle: ");
        return getShortCutInfo().getAppInfo().mAppName;
    }

    protected static Rect m_test_text_rect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        ////Log.d(TAG, this.hashCode()+"onDraw: ");
        super.onDraw(canvas);
        if (m_base_alpha < 0.01f) {
            return;
        }
        ShortCut c_app_info = getShortCutInfo();
        canvas.setDrawFilter(ShadowBuilder.m_default_filter);
        float c_length_z_offset = 140;
        m_camera.save();
        m_camera.translate(0, 0, FullscreenActivity.m_view_draw_scale - m_init_value_bg * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
        m_camera.getMatrix(m_matrix);
        m_camera.restore();
        m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY());
        m_matrix.mapPoints(m_bg_trans_potin, m_bg_clip_point);
        m_path.reset();
        m_path.moveTo(m_bg_trans_potin[0], m_bg_trans_potin[1]);
        m_path.quadTo(m_bg_trans_potin[2], m_bg_trans_potin[3], m_bg_trans_potin[4], m_bg_trans_potin[5]);
        m_path.lineTo(m_bg_trans_potin[6], m_bg_trans_potin[7]);
        m_path.quadTo(m_bg_trans_potin[8], m_bg_trans_potin[9], m_bg_trans_potin[10], m_bg_trans_potin[11]);
        m_path.lineTo(m_bg_trans_potin[12], m_bg_trans_potin[13]);
        m_path.quadTo(m_bg_trans_potin[14], m_bg_trans_potin[15], m_bg_trans_potin[16], m_bg_trans_potin[17]);
        m_path.lineTo(m_bg_trans_potin[18], m_bg_trans_potin[19]);
        m_path.quadTo(m_bg_trans_potin[20], m_bg_trans_potin[21], m_bg_trans_potin[22], m_bg_trans_potin[23]);
        m_path.lineTo(m_bg_trans_potin[0], m_bg_trans_potin[1]);
        m_path.close();

        if (m_bmp_bg != null) {
            m_camera.save();
            m_camera.translate(-m_bmp_bg.getWidth() / 2f, m_bmp_bg.getHeight() / 2f, FullscreenActivity.m_view_draw_scale - m_init_value_bg * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
            m_camera.getMatrix(m_matrix);
            m_camera.restore();
            m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY());
            m_paint.setColor(0xffffffff);
            m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_bg) * 255 * (1f - m_drag_value * 0.5f))));
            if (m_bg_color_filter != null) {
                m_paint.setColorFilter(m_bg_color_filter);
            }
            canvas.drawBitmap(m_bmp_bg, m_matrix, m_paint);
            if (m_bg_color_filter != null) {
                m_paint.setColorFilter(null);
            }
        } else if (m_bg_color != 0) {
            m_paint.setColor(m_bg_color);
            m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_bg) * 255 * (1f - m_drag_value * 0.5f))));
            canvas.drawPath(m_path, m_paint);
        }


        if (m_down_circle_value > 0.001f && m_down_circle_value < 2f) {
            canvas.save();
            canvas.clipPath(m_path);
            m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_bg) * 255 * m_down_value) * 0.3f));
            canvas.drawCircle(canvas.getWidth() / 2f + getShortCutInfo().getShortRealPostionX(), canvas.getHeight() / 2f + getShortCutInfo().getShortRealPostionY(), m_down_circle_value * m_down_circle_raduis, m_paint);
            canvas.restore();
        }

        if (m_bmp_part != null && m_init_value_part < 1f) {
            if (m_bmp_shadow != null) {
                m_camera.save();
                m_camera.translate(-m_bmp_shadow.getWidth() / 2f + (5f + 40f * m_init_value_part), m_bmp_shadow.getHeight() / 2f - (5f + 40f * m_init_value_part) + m_icon_y_offset, FullscreenActivity.m_view_draw_scale - m_init_value_part * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
                m_camera.getMatrix(m_matrix);
                m_camera.restore();
                m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY());
                m_paint.setColor(0xffffffff);
                m_paint.setAlpha((int) (m_base_alpha * Math.max(10, (1f - m_init_value_part) * 50f * (1f - m_drag_value * 0.5f))));
                canvas.save();
                canvas.clipPath(m_path);
                canvas.drawBitmap(m_bmp_shadow, m_matrix, m_paint);
                canvas.restore();
            }

            m_camera.save();
            m_camera.translate(-m_bmp_part.getWidth() / 2f, m_bmp_part.getHeight() / 2f + m_icon_y_offset, FullscreenActivity.m_view_draw_scale - m_init_value_part * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
            m_camera.getMatrix(m_matrix);
            m_camera.restore();
            m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY());
            m_paint.setColor(0xffffffff);
            m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_part) * 255 * (1f - m_drag_value * 0.5f))));
            canvas.drawBitmap(m_bmp_part, m_matrix, m_paint);
        }
        if (m_init_value_text < 1f) {
            String c_draw_text = getDrawTitle();
            if (c_draw_text != null) {
                m_paint.setShadowLayer(3, 1, 1, 0xff000000);
                m_paint.getTextBounds(c_draw_text, 0, c_draw_text.length(), m_test_text_rect);
                m_camera.save();
                m_camera.translate(-m_test_text_rect.width() / 2f + (2f + 40f * m_init_value_text), m_test_text_rect.height() / 2f - (2f + 40f * m_init_value_text) - m_text_y_offset / 2, FullscreenActivity.m_view_draw_scale - m_init_value_text * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
                m_camera.getMatrix(m_matrix);
                m_camera.restore();
                m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY() + m_text_y_offset / 2);
                m_paint.setColor(0xff000000);
                m_paint.setAlpha((int) (m_base_alpha * Math.max(10, (1f - m_init_value_part) * 50f * (1f - m_drag_value * 0.5f))));
                canvas.save();
                canvas.clipPath(m_path);
                canvas.setMatrix(m_matrix);
                canvas.drawText(c_draw_text, 0, 0, m_paint);
                canvas.restore();

                m_paint.setShadowLayer(2, 0, 0, 0x50000000);
                m_camera.save();
                m_camera.translate(-m_test_text_rect.width() / 2f, m_test_text_rect.height() / 2f - m_text_y_offset / 2, FullscreenActivity.m_view_draw_scale - m_init_value_text * c_length_z_offset - m_drag_value * m_drag_length + m_down_value * m_down_length);
                m_camera.getMatrix(m_matrix);
                m_camera.restore();
                m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY() + m_text_y_offset / 2);
                m_paint.setColor(0xffffffff);
                m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_part) * 255 * (1f - m_drag_value * 0.5f))));
                canvas.save();
                canvas.setMatrix(m_matrix);
                canvas.drawText(c_draw_text, 0, 0, m_paint);
                canvas.restore();
                m_paint.clearShadowLayer();
            }
        }
        if (m_delete_test_mode) {
            m_paint.setColor(0x80ff0000);
            canvas.drawPath(m_path, m_paint);
        }
    }


    protected float m_base_alpha = 1f;

    public void setBaseAlpha(float c_alpha) {
        //Log.d(TAG, this.hashCode()+"setBaseAlpha: "+c_alpha);
        m_base_alpha = c_alpha;
        this.invalidate();
    }
}
