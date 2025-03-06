package com.spd.window;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.spd.home.R;

public class BTNotifyWindow extends BaseNotifyWindow{
    private ObjectAnimator m_dialog_view_anim;
    private FilletFrameLayout m_current_dialog_layout;
    private MyCircleButton m_bn_dial, m_bn_contact, m_bn_bt_music;
    private TextView m_title_text;
    public BTNotifyWindow(Context context) {
        super(context);
    }

    private String m_device_name;
    public void showBtNotifyWindow(String c_device_name)
    {
        m_device_name = c_device_name;
        if (m_title_text != null && m_device_name != null)
        {
            m_title_text.setText(m_device_name);
        }
        showWindow(5000);
    }
    @Override
    protected void onDismiss() {

    }
    @Override
    protected View getShowWindowView() {
        if (m_current_dialog_layout == null)
        {

            m_current_dialog_layout = (FilletFrameLayout)LayoutInflater.from(m_context).inflate(R.layout.window_layout_bt, null);
            FrameLayout.LayoutParams c_para = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,220);
            c_para.gravity = Gravity.BOTTOM;
            c_para.leftMargin = 2;
            c_para.rightMargin = 2;
            m_current_dialog_layout.setLayoutParams(c_para);
            m_bn_dial = m_current_dialog_layout.findViewById(R.id.id_bn_dial);
            m_bn_contact = m_current_dialog_layout.findViewById(R.id.id_bn_contact);
            m_bn_bt_music = m_current_dialog_layout.findViewById(R.id.id_bn_bt_music);
            m_bn_dial.setOnClickListener(this);
            m_bn_contact.setOnClickListener(this);
            m_bn_bt_music.setOnClickListener(this);
            m_title_text = m_current_dialog_layout.findViewById(R.id.id_window_title_text);
        }
        if (m_device_name != null)
        {
            m_title_text.setText(m_device_name);
        }
        return m_current_dialog_layout;
    }

    @Override
    protected void onDialogViewShow(View c_view) {
        if (m_dialog_view_anim != null && m_dialog_view_anim.isRunning())
        {
            m_dialog_view_anim.cancel();
        }
        PropertyValuesHolder c_anim_alpha = PropertyValuesHolder.ofFloat("Alpha",m_current_dialog_layout.getAlpha() , 1f);
        PropertyValuesHolder c_anim_translation_y = PropertyValuesHolder.ofFloat("TranslationY",m_current_dialog_layout.getTranslationY() , 0);
        m_dialog_view_anim = ObjectAnimator.ofPropertyValuesHolder(m_current_dialog_layout,c_anim_alpha,c_anim_translation_y);
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
        PropertyValuesHolder c_anim_translation_y = PropertyValuesHolder.ofFloat("TranslationY",m_current_dialog_layout.getTranslationY() , 180);
        m_dialog_view_anim = ObjectAnimator.ofPropertyValuesHolder(m_current_dialog_layout,c_anim_alpha,c_anim_translation_y);
        m_dialog_view_anim.setDuration(300);
        m_dialog_view_anim.start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == m_bn_dial)
        {
            int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
            Intent c_intent = new Intent("com.spd.bt.START");
            c_intent.putExtra("DIAL", 1);
            c_intent.setFlags(launchFlags);
            m_context.startActivity(c_intent);
            hideWindow();
        }
        else if (v == m_bn_contact)
        {
            int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
            Intent c_intent = new Intent("com.spd.bt.START");
            c_intent.putExtra("DIAL", 0);
            c_intent.setFlags(launchFlags);
            m_context.startActivity(c_intent);
            hideWindow();
        }
        else if (v == m_bn_bt_music)
        {
            int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
            Intent c_intent = new Intent("com.spd.spdmedia.music");
            c_intent.putExtra("deviceID", MEDIA_DEVICE_BT_MUSIC);
            c_intent.setFlags(launchFlags);
            m_context.startActivity(c_intent);
            hideWindow();
        }
    }
}
