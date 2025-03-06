package com.spd.custom.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.spd.home.R;
import com.spd.radio.Radio;
import com.spd.radio.entity.aidl.RadioFreqInfo;
import com.spd.radio.entity.aidl.RadioStatus;

import java.util.List;

public class ShortCutRadioView extends ShortCutBaseView implements Radio.Callback{
    //private Bitmap m_bmp_part_sub,m_bmp_shadow_sub;
    public ShortCutRadioView(Context context) {
        super(context);
        m_bmp_bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bg_03);
        //m_bmp_part_sub = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon_part_radio);
        //m_bmp_shadow_sub = ShadowBuilder.buildBitmapShadow(getContext(),m_bmp_part_sub,8);
        Radio.get().init(context,"home.MainActivity",this);
        m_freq_draw_paint = new Paint();
        m_freq_draw_paint.setAntiAlias(true);
        m_freq_draw_paint.setColor(0xffffffff);
        m_freq_draw_paint.setTextSize(64);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Radio.get().init(getContext(),"home.MainActivity",this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Radio.get().uninit();
    }
/*    @Override
    protected String getDrawTitle() {
        return null;
    }*/
    private float m_freq_y_offset= 9f;
    private float m_icon_y_offset = 55f;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //m_freq_draw_paint.getTextBounds(m_main_string , 0,m_main_string.length(),m_freq_test_rect);
        ShortCut c_app_info = getShortCutInfo();
        if (m_init_value_text <1f) {
            String c_draw_text = m_main_string;
            if (c_draw_text == null) {
                c_draw_text = "87.5";
                m_draw_part_offset = -125f;
            }
            m_freq_draw_paint.setShadowLayer(3, 1, 1, 0xff000000);
            m_freq_draw_paint.getTextBounds(c_draw_text, 0, c_draw_text.length(), m_test_text_rect);
            m_camera.save();
            m_camera.translate(-m_test_text_rect.width() / 2f + (2f + 40f * m_init_value_text), m_test_text_rect.height() / 2f - (2f + 40f * m_init_value_text) - m_freq_y_offset / 2, -m_init_value_text * 300 - m_drag_value * m_drag_length + m_down_value * m_down_length);
            m_camera.getMatrix(m_matrix);
            m_camera.restore();
            m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY() + m_freq_y_offset / 2);
            m_freq_draw_paint.setColor(0xff000000);
            m_freq_draw_paint.setAlpha((int) (m_base_alpha * Math.max(10, (1f - m_init_value_part) * 50f * (1f - m_drag_value * 0.5f))));
            canvas.save();
            canvas.clipPath(m_path);
            canvas.setMatrix(m_matrix);
            canvas.drawText(c_draw_text, 0, 0, m_freq_draw_paint);
            canvas.restore();

            m_freq_draw_paint.setShadowLayer(2, 0, 0, 0x50000000);
            m_camera.save();
            m_camera.translate(-m_test_text_rect.width() / 2f, m_test_text_rect.height() / 2f - m_freq_y_offset / 2, -m_init_value_text * 300 - m_drag_value * m_drag_length + m_down_value * m_down_length);
            m_camera.getMatrix(m_matrix);
            m_camera.restore();
            m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY() + m_freq_y_offset / 2);
            m_freq_draw_paint.setColor(0xffffffff);
            m_freq_draw_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_part) * 255 * (1f - m_drag_value * 0.5f))));
            canvas.save();
            canvas.setMatrix(m_matrix);
            canvas.drawText(c_draw_text, 0, 0, m_freq_draw_paint);
            canvas.restore();
            m_freq_draw_paint.clearShadowLayer();
        }
        /*if (m_bmp_part_sub != null && m_init_value_part<1f)
        {
            if (m_bmp_shadow_sub != null)
            {
                m_camera.save();
                m_camera.translate(-m_bmp_shadow_sub.getWidth()/2f+(5f+40f*m_init_value_part)+m_draw_part_offset,m_bmp_shadow_sub.getHeight()/2f-(5f+40f*m_init_value_part)+m_icon_y_offset,-m_init_value_part*300-m_drag_value*m_drag_length+m_down_value*m_down_length);
                m_camera.getMatrix(m_matrix);
                m_camera.restore();
                m_matrix.postTranslate(canvas.getWidth()/2f+c_app_info.getShortRealPostionX(),canvas.getHeight()/2f+c_app_info.getShortRealPostionY());
                m_paint.setColor(0xffffffff);
                m_paint.setAlpha((int)(m_base_alpha*Math.min(60,(1f-m_init_value_part)*160*(1f-m_drag_value*0.5f))));
                canvas.save();
                canvas.clipPath(m_path);
                canvas.drawBitmap(m_bmp_shadow_sub,m_matrix,m_paint);
                canvas.restore();
            }

            m_camera.save();
            m_camera.translate(-m_bmp_part_sub.getWidth()/2f+m_draw_part_offset,m_bmp_part_sub.getHeight()/2f+m_icon_y_offset,-m_init_value_part*300-m_drag_value*m_drag_length+m_down_value*m_down_length);
            m_camera.getMatrix(m_matrix);
            m_camera.restore();
            m_matrix.postTranslate(canvas.getWidth()/2f+c_app_info.getShortRealPostionX(),canvas.getHeight()/2f+c_app_info.getShortRealPostionY());
            m_paint.setColor(0xffffffff);
            m_paint.setAlpha((int)(m_base_alpha*Math.min(255,(1f-m_init_value_part)*255*(1f-m_drag_value*0.5f))));
            canvas.drawBitmap(m_bmp_part_sub,m_matrix,m_paint);
        }*/
    }

    @Override
    public void onRadioConnectChanged(boolean b) {
        if (b)
        {
            checkFreqInfo();
        }
    }
    private String m_main_string;
    private Paint m_freq_draw_paint;
    private Rect m_freq_test_rect;
    private float m_draw_part_offset = -125f;
    private void checkFreqInfo()
    {
        RadioFreqInfo c_freq_info = Radio.get().getFreqInfo();
        int c_freq = c_freq_info.freq;
        if (c_freq >= 10000)
        {
            m_draw_part_offset = ((c_freq-87500f)/(108000f-87500f)-0.5f)*250f;
            int c_main_num = c_freq/1000;
            int c_sub_num = (c_freq%1000)/100;
            m_main_string = c_main_num+"."+c_sub_num;
            this.invalidate();
        }
        else
        {
            m_draw_part_offset = ((c_freq-522)/(1620f-522f)-0.5f)*270f;
            m_main_string = ""+c_freq;
            this.invalidate();
        }
    }
    @Override
    public void onRadioFreqChanged(int i) {

    }

    @Override
    public void onRadioBandChanged(String s) {
        checkFreqInfo();
    }

    @Override
    public void onRadioRdsRTChanged(String s) {

    }

    @Override
    public void onRadioRdsPSChanged(String s) {

    }

    @Override
    public void onRadioCurrentStatusChanged(RadioStatus radioStatus) {

    }

    @Override
    public void onRadioListChanged(String s, List<RadioFreqInfo> list, int i) {

    }

}
