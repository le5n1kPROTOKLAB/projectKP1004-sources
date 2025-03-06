package com.spd.window;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spd.MyLauncher.MyCheckBoxView;
import com.spd.MyLauncher.MyNormalButton;
import com.spd.MyLauncher.MyProgressBarView;
import com.spd.home.R;

import java.util.HashMap;

public class VolumeNotifyWindow extends BaseNotifyWindow implements MyProgressBarView.ProgressBarEventListener{
    public static final int VOLUME_ID_ALL = 0xef000001;
    public static final int VOLUME_ID_MEDIA = VOLUME_ID_ALL+1;
    public static final int VOLUME_ID_PHONE = VOLUME_ID_ALL+2;
    public static final int VOLUME_ID_NAVI = VOLUME_ID_ALL+3;
    public static final int VOLUME_ID_MUTE = VOLUME_ID_ALL+4;

    private ObjectAnimator m_dialog_view_anim;
    private HashMap<Integer,ObjectAnimator> m_item_anim_map = new HashMap<>();
    private HashMap<Integer,LinearLayout> m_item_layout_map = new HashMap<>();
    private HashMap<Integer,Boolean> m_item_show_map = new HashMap<>();
    private TextView m_text_view_value_media,m_text_view_value_phone,m_text_view_value_navi;
    private MyProgressBarView m_progress_bar_media,m_progress_bar_phone,m_progress_bar_navi;
    private MyCheckBoxView m_bn_mute;
    private MyNormalButton m_bn_extends;
    private FilletLinearLayout m_current_dialog_layout;
    public VolumeNotifyWindow(Context context) {
        super(context);

    }
    private int m_volume_max_media = 100;
    private int m_volume_max_phone = 100;
    private int m_volume_max_navi = 100;
    private int m_volume_media = 0;
    private int m_volume_phone = 0;
    private int m_volume_navi = 0;
    private int m_volume_mute = 0;
    private int m_volume_show_type = -1;
    private int m_volume_init_type = -1;

    private boolean m_init_item_motion_flag = false;
    public void setVolumeMaxValue(int c_volume_max_media,int c_volume_max_phone,int c_volume_max_navi)
    {
        m_volume_max_media = c_volume_max_media;
        m_volume_max_phone = c_volume_max_phone;
        m_volume_max_navi = c_volume_max_navi;
    }
    /*
    前4个值是分别设置音量值
    最后一个type类型是设置将要显示的音量类型。
    注意，如果当前音量已经显示并且类型和你即将要显示的类型不同，则立刻显示所有类型。
     */

    @Override
    protected void onDismiss() {
        Log.d("VOLUME", "onDismiss: ");
        m_volume_show_type = -1;
    }
    private int m_show_delay_time = 5000;
    public void showVolumeNotifyWindow(int c_volume_media, int c_volume_phone, int c_volume_navi, int c_mute , int c_show_type)
    {
        m_volume_media = c_volume_media;
        m_volume_phone = c_volume_phone;
        m_volume_navi = c_volume_navi;
        m_volume_mute = c_mute;
        m_volume_init_type = c_show_type;
        if (m_volume_show_type == -1)
        {
            m_init_item_motion_flag = false;
            showWindow(m_show_delay_time);
        }
        else if (m_volume_show_type != c_show_type)
        {
            m_volume_show_type = VOLUME_ID_ALL;
            m_init_item_motion_flag = true;
            checkVolumeValue();
            checkItemLayout(m_init_item_motion_flag);
            delayWindow(m_show_delay_time);
        }
        else
        {
            m_volume_show_type = c_show_type;
            m_init_item_motion_flag = true;
            checkVolumeValue();
            delayWindow(m_show_delay_time);
        }


    }

    public void setVolumeItemShow(int c_item_id , boolean c_show , boolean c_motion)
    {
        boolean c_old_show = m_item_show_map.get(c_item_id);
        if (c_old_show != c_show)
        {
            m_item_show_map.put(c_item_id,c_show);
            LinearLayout c_item_layout;
            c_item_layout = m_item_layout_map.get(c_item_id);
            ObjectAnimator c_old_anim = m_item_anim_map.get(c_item_id);
            if (c_old_anim != null && c_old_anim.isRunning())
            {
                c_old_anim.cancel();
            }
            if (c_motion)
            {
                Object c_obj = c_item_layout.getTag();
                TypeEvaluator<ViewGroup.LayoutParams> evaluator;
                if (c_obj == null)
                {
                    evaluator = new HeightEvaluator(c_item_layout);
                    c_item_layout.setTag(evaluator);
                }
                else
                {
                    evaluator = (TypeEvaluator<ViewGroup.LayoutParams>)c_obj;
                }
                if (c_show)
                {
                    ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, c_item_layout.getHeight());
                    ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                    PropertyValuesHolder c_anim_layout_para = PropertyValuesHolder.ofObject("layoutParams",evaluator,start,end);
                    PropertyValuesHolder c_anim_alpha = PropertyValuesHolder.ofFloat("Alpha",c_item_layout.getAlpha() , 1f);
                    c_old_anim = ObjectAnimator.ofPropertyValuesHolder(c_item_layout,c_anim_alpha,c_anim_layout_para);
                    c_old_anim.setDuration(500);
                    c_old_anim.start();
                    m_item_anim_map.put(c_item_id,c_old_anim);
                }
                else
                {
                    ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, c_item_layout.getHeight());
                    ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    PropertyValuesHolder c_anim_layout_para = PropertyValuesHolder.ofObject("layoutParams",evaluator,start,end);
                    PropertyValuesHolder c_anim_alpha = PropertyValuesHolder.ofFloat("Alpha",c_item_layout.getAlpha() , 0f);
                    c_old_anim = ObjectAnimator.ofPropertyValuesHolder(c_item_layout,c_anim_alpha,c_anim_layout_para);
                    c_old_anim.setDuration(500);
                    c_old_anim.start();
                    m_item_anim_map.put(c_item_id,c_old_anim);
                }
            }
            else
            {
                if (c_show)
                {
                    c_item_layout.setAlpha(1f);
                    LinearLayout.LayoutParams c_para = (LinearLayout.LayoutParams)c_item_layout.getLayoutParams();
                    c_para.height = 80;
                    c_item_layout.requestLayout();
                }
                else
                {
                    c_item_layout.setAlpha(0f);
                    LinearLayout.LayoutParams c_para = (LinearLayout.LayoutParams)c_item_layout.getLayoutParams();
                    c_para.height = 0;
                    c_item_layout.requestLayout();
                }
            }
        }

    }

    @Override
    public void onProgressBarValueChanged(int c_id, float c_value) {
        switch (c_id)
        {
            case R.id.id_progress_media:
                m_volume_media = Math.round(m_volume_max_media*c_value);
                m_text_view_value_media.setText(m_volume_media+"");
                if (m_listener != null)
                {
                    m_listener.onVolumeChanged(VOLUME_ID_MEDIA,m_volume_media);
                }
                break;
            case R.id.id_progress_phone:
                m_volume_phone = Math.round(m_volume_max_phone*c_value);
                m_text_view_value_phone.setText(m_volume_phone+"");
                if (m_listener != null)
                {
                    m_listener.onVolumeChanged(VOLUME_ID_PHONE,m_volume_phone);
                }
                break;
            case R.id.id_progress_navi:
                m_volume_navi = Math.round(m_volume_max_navi*c_value);
                m_text_view_value_navi.setText(m_volume_navi+"");
                if (m_listener != null)
                {
                    m_listener.onVolumeChanged(VOLUME_ID_NAVI,m_volume_navi);
                }
                break;
            default:
        }
        delayWindow(m_show_delay_time);
    }

    @Override
    public void onProgressBarTouchChanged(int c_id, float c_value) {
        switch (c_id)
        {
            case R.id.id_progress_media:
                m_volume_media = Math.round(m_volume_max_media*c_value);
                m_text_view_value_media.setText(m_volume_media+"");
                break;
            case R.id.id_progress_phone:
                m_volume_phone = Math.round(m_volume_max_phone*c_value);
                m_text_view_value_phone.setText(m_volume_phone+"");
                break;
            case R.id.id_progress_navi:
                m_volume_navi = Math.round(m_volume_max_navi*c_value);
                m_text_view_value_navi.setText(m_volume_navi+"");
                break;
            default:
        }
        delayWindow(m_show_delay_time);
    }



    class HeightEvaluator implements TypeEvaluator<ViewGroup.LayoutParams> {
        private ViewGroup m_view_group;
        public HeightEvaluator (ViewGroup c_view_group)
        {
            m_view_group = c_view_group;
        }
        @Override
        public ViewGroup.LayoutParams evaluate(float fraction, ViewGroup.LayoutParams startValue, ViewGroup.LayoutParams endValue) {
            ViewGroup.LayoutParams params = m_view_group.getLayoutParams();
            params.height = (int) (startValue.height + fraction * (endValue.height - startValue.height));
            return params;
        }

    }

    @Override
    protected View getShowWindowView() {
        if (m_current_dialog_layout == null)
        {
            m_current_dialog_layout = (FilletLinearLayout) LayoutInflater.from(m_context).inflate(R.layout.window_layout_volume, null);
            m_current_dialog_layout.setOnClickListener(this);
            FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(520,FrameLayout.LayoutParams.WRAP_CONTENT);
            c_para.gravity = Gravity.CENTER;
            m_current_dialog_layout.setLayoutParams(c_para);
            LinearLayout c_item_layout_media = m_current_dialog_layout.findViewById(R.id.id_volume_item_media);
            LinearLayout c_item_layout_phone = m_current_dialog_layout.findViewById(R.id.id_volume_item_phone);
            LinearLayout c_item_layout_navi = m_current_dialog_layout.findViewById(R.id.id_volume_item_navi);
            m_item_layout_map.put(VOLUME_ID_MEDIA,c_item_layout_media);
            m_item_layout_map.put(VOLUME_ID_PHONE,c_item_layout_phone);
            m_item_layout_map.put(VOLUME_ID_NAVI,c_item_layout_navi);
            m_item_show_map.put(VOLUME_ID_MEDIA,true);
            m_item_show_map.put(VOLUME_ID_PHONE,true);
            m_item_show_map.put(VOLUME_ID_NAVI,true);
            m_text_view_value_media = m_current_dialog_layout.findViewById(R.id.id_text_view_media);
            m_text_view_value_phone = m_current_dialog_layout.findViewById(R.id.id_text_view_phone);
            m_text_view_value_navi = m_current_dialog_layout.findViewById(R.id.id_text_view_navi);
            m_progress_bar_media = m_current_dialog_layout.findViewById(R.id.id_progress_media);
            m_progress_bar_phone = m_current_dialog_layout.findViewById(R.id.id_progress_phone);
            m_progress_bar_navi = m_current_dialog_layout.findViewById(R.id.id_progress_navi);
            m_progress_bar_media.setProgressBarEventListener(this);
            m_progress_bar_phone.setProgressBarEventListener(this);
            m_progress_bar_navi.setProgressBarEventListener(this);
            m_bn_mute = m_current_dialog_layout.findViewById(R.id.id_check_box_mute);
            m_bn_mute.setOnClickListener(this);
            m_bn_extends = m_current_dialog_layout.findViewById(R.id.id_bn_extends);
            m_bn_extends.setOnClickListener(this);
        }
        m_volume_show_type = m_volume_init_type;
        checkVolumeValue();
        checkItemLayout(m_init_item_motion_flag);
        return m_current_dialog_layout;
    }

    private void checkVolumeValue()
    {
        m_text_view_value_media.setText(m_volume_media+"");
        m_text_view_value_phone.setText(m_volume_phone+"");
        m_text_view_value_navi.setText(m_volume_navi+"");
        float c_value_float = (float)m_volume_media/(float)m_volume_max_media;
        m_progress_bar_media.setCurrentValue(c_value_float);
        c_value_float = (float)m_volume_phone/(float)m_volume_max_phone;
        m_progress_bar_phone.setCurrentValue(c_value_float);
        c_value_float = (float)m_volume_navi/(float)m_volume_max_navi;
        m_progress_bar_navi.setCurrentValue(c_value_float);
        m_bn_mute.setCheckBoxToggled(m_volume_mute==1,false);
    }
    @Override
    protected void onDialogViewShow(View c_view) {
        if (m_dialog_view_anim != null && m_dialog_view_anim.isRunning())
        {
            m_dialog_view_anim.cancel();
        }
        PropertyValuesHolder c_anim_alpha = PropertyValuesHolder.ofFloat("Alpha",m_current_dialog_layout.getAlpha() , 1f);
        PropertyValuesHolder c_anim_scale_x = PropertyValuesHolder.ofFloat("ScaleX",m_current_dialog_layout.getScaleX() , 1f);
        PropertyValuesHolder c_anim_scale_y = PropertyValuesHolder.ofFloat("ScaleY",m_current_dialog_layout.getScaleY() , 1f);

        m_dialog_view_anim = ObjectAnimator.ofPropertyValuesHolder(m_current_dialog_layout,c_anim_alpha,c_anim_scale_x,c_anim_scale_y);
        m_dialog_view_anim.setDuration(300);
        m_dialog_view_anim.start();
    }

    @Override
    protected void onDialogViewHide(View c_view) {
        if (m_dialog_view_anim != null && m_dialog_view_anim.isRunning())
        {
            m_dialog_view_anim.cancel();
        }
        PropertyValuesHolder c_anim_alpha = PropertyValuesHolder.ofFloat("Alpha",m_current_dialog_layout.getAlpha() , 0f);
        PropertyValuesHolder c_anim_scale_x = PropertyValuesHolder.ofFloat("ScaleX",m_current_dialog_layout.getScaleX() , 0.5f);
        PropertyValuesHolder c_anim_scale_y = PropertyValuesHolder.ofFloat("ScaleY",m_current_dialog_layout.getScaleY() , 0.5f);
        m_dialog_view_anim = ObjectAnimator.ofPropertyValuesHolder(m_current_dialog_layout,c_anim_alpha,c_anim_scale_x,c_anim_scale_y);
        m_dialog_view_anim.setDuration(300);
        m_dialog_view_anim.start();
    }

    private void checkItemLayout(boolean c_motion)
    {
        Log.d("VOLUME", "checkItemLayout: "+m_volume_show_type);
        if (m_volume_show_type == VOLUME_ID_ALL)
        {
            Log.d("VOLUME", "checkItemLayout: VOLUME_ID_ALL");
            setVolumeItemShow(VOLUME_ID_MEDIA,true,c_motion);
            setVolumeItemShow(VOLUME_ID_PHONE,true,c_motion);
            setVolumeItemShow(VOLUME_ID_NAVI,true,c_motion);
            m_bn_extends.setIconDrawable(R.drawable.extends_arrow_up);
        }
        else if (m_volume_show_type == VOLUME_ID_MEDIA)
        {
            Log.d("VOLUME", "checkItemLayout: VOLUME_ID_MEDIA");
            setVolumeItemShow(VOLUME_ID_MEDIA,true,c_motion);
            setVolumeItemShow(VOLUME_ID_PHONE,false,c_motion);
            setVolumeItemShow(VOLUME_ID_NAVI,false,c_motion);
            m_bn_extends.setIconDrawable(R.drawable.extends_arrow_down);
        }
        else if (m_volume_show_type == VOLUME_ID_PHONE)
        {
            Log.d("VOLUME", "checkItemLayout: VOLUME_ID_PHONE");
            setVolumeItemShow(VOLUME_ID_MEDIA,false,c_motion);
            setVolumeItemShow(VOLUME_ID_PHONE,true,c_motion);
            setVolumeItemShow(VOLUME_ID_NAVI,false,c_motion);
            m_bn_extends.setIconDrawable(R.drawable.extends_arrow_down);
        }
        else if (m_volume_show_type == VOLUME_ID_NAVI)
        {
            Log.d("VOLUME", "checkItemLayout: VOLUME_ID_NAVI");
            setVolumeItemShow(VOLUME_ID_MEDIA,false,c_motion);
            setVolumeItemShow(VOLUME_ID_PHONE,false,c_motion);
            setVolumeItemShow(VOLUME_ID_NAVI,true,c_motion);
            m_bn_extends.setIconDrawable(R.drawable.extends_arrow_down);
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.id_check_box_mute)
        {
            m_volume_mute = 1-m_volume_mute;
            m_bn_mute.setCheckBoxToggled(m_volume_mute==1,true);
            if (m_listener != null)
            {
                m_listener.onVolumeChanged(VOLUME_ID_MUTE,m_volume_mute);
            }
            delayWindow(m_show_delay_time);
        }
        else if (v.getId() == R.id.id_bn_extends)
        {
            if (m_volume_show_type == VOLUME_ID_ALL)
            {
                if (m_volume_init_type == VOLUME_ID_ALL)
                {
                    m_volume_show_type = VOLUME_ID_MEDIA;
                }
                else
                {
                    m_volume_show_type = m_volume_init_type;
                }
                checkItemLayout(true);
            }
            else if (m_volume_show_type == VOLUME_ID_MEDIA || m_volume_show_type == VOLUME_ID_PHONE || m_volume_show_type == VOLUME_ID_NAVI)
            {
                m_volume_show_type = VOLUME_ID_ALL;
                checkItemLayout(true);
            }
            delayWindow(m_show_delay_time);
        }
    }
    private VolumeWindowListener m_listener;
    public void setVolumeWindowListener(VolumeWindowListener c_listener)
    {
        m_listener = c_listener;
    }
    public interface VolumeWindowListener
    {
        void onVolumeChanged(int c_volume_type, int c_volume_value);

    }
}
