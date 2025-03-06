package com.spd.custom.view;

import android.util.Log;

import com.spd.home.FullscreenActivity;

public class StepMotionParameter {
    public final static int PARA_STATE_STATIC = 0;
    public final static int PARA_STATE_GOTO = 1;
    public final static int PARA_STATE_DRAG = 2;
    public final static int PARA_STATE_SLIDE = 3;
    private int m_motion_mode = PARA_STATE_STATIC;
    private float m_current_value = 0.0f;
    private int m_target_index = 0;
    private float m_static_gate = 0.0f;
    private float m_dump = 0.3f;
    private float m_step_offset;
    private int m_current_index = 0;
    private int m_min_index = 0, m_max_index = 10;
    private float m_drag_offset = 0;
    private float m_slide_speed = 0;
    private boolean m_drag_move_over_step = false;
    public StepMotionParameter()
    {
        m_step_offset = FullscreenActivity.DESKTOP_WIDTH;
    }
    public int getMotionMode()
    {
        return m_motion_mode;
    }
    public int getCurrentIndex()
    {
        return m_current_index;
    }
    public float getCurrentOffset()
    {
        return m_current_value;
    }
    public void setStepOffset(float c_step_offset)
    {
        m_step_offset = c_step_offset;
    }
    public float getStepOffset()
    {
        return m_step_offset;
    }
    public void setStepIndexBounds(int c_min_index , int c_max_index)
    {
        Log.d("SimonCheck002", "setStepIndexBounds: "+c_min_index+" "+c_max_index);
        m_min_index = c_min_index;
        m_max_index = c_max_index;
    }
    public int getMinIndex() {
        return m_min_index;
    }
    public int getMaxIndex() {
        return m_max_index;
    }
    public void setValue(int c_step_index ,float c_freq_value)
    {
        m_current_index = c_step_index;
        int c_step = (int)(c_freq_value/m_step_offset);
        m_current_index += c_step;
        c_freq_value -= m_step_offset*c_step;
        if (c_freq_value<0)
        {
            c_freq_value+=m_step_offset;
            m_current_index -= 1;
        }
        m_current_value = c_freq_value;
        m_motion_mode = PARA_STATE_STATIC;
        m_drag_offset = 0;
    }
    public void startDrag()
    {
        m_drag_offset = 0;
        m_slide_speed = 0;
        m_motion_mode = PARA_STATE_DRAG;
    }
    public void dragValue(float c_value_offset)
    {
        m_drag_offset += c_value_offset;
    }
    public void stopDrag()
    {
        if (m_motion_mode == PARA_STATE_DRAG)
        {
            m_motion_mode = PARA_STATE_SLIDE;
        }
    }
    private boolean isValueValid()
    {
        boolean c_result = true;
        if (m_current_index<m_min_index)
        {
            c_result=false;
        }
        else if (m_current_index>m_max_index)
        {
            c_result=false;
        }
        else if (m_current_index == m_max_index && m_current_value >0f )
        {
            c_result=false;
        }
        return c_result;
    }
    public void gotoTargetIndex(int c_target_index)
    {
        m_target_index = c_target_index;
        m_motion_mode = PARA_STATE_GOTO;
    }
    public boolean motionValue()
    {
        boolean c_result = false;
        if (m_motion_mode == PARA_STATE_DRAG)
        {
            m_current_value += m_drag_offset;
            m_slide_speed = m_slide_speed/2f+m_drag_offset/2f;
            m_drag_offset = 0;
            int c_step = (int)(m_current_value/m_step_offset);
            m_current_index += c_step;
            m_current_value -= m_step_offset*c_step;
            if (m_current_value<=-m_step_offset*0.5)
            {
                m_current_value+=m_step_offset;
                m_current_index -= 1;
            }
            else if (m_current_value>m_step_offset*0.5)
            {
                m_current_value-=m_step_offset;
                m_current_index += 1;
            }
            c_result = true;
        }
        else if (m_motion_mode == PARA_STATE_SLIDE)
        {
            if (m_drag_move_over_step)
            {
                m_current_value += m_slide_speed;
                m_slide_speed *= 0.87f;
                int c_step = (int)(m_current_value/m_step_offset);
                m_current_index += c_step;
                m_current_value -= m_step_offset*c_step;
                if (m_current_value<=-m_step_offset*0.5)
                {
                    m_current_value+=m_step_offset;
                    m_current_index -= 1;
                }
                else if (m_current_value>m_step_offset*0.5)
                {
                    m_current_value-=m_step_offset;
                    m_current_index += 1;
                }
                if (!isValueValid() || Math.abs(m_slide_speed) < 30f)
                {
                    float c_target_offset = m_slide_speed*15;
                    float c_target_value = m_current_value+c_target_offset;
                    int c_target_index = m_current_index;
                    c_step = (int)(c_target_value/m_step_offset);
                    c_target_index += c_step;
                    c_target_value -= m_step_offset*c_step;
                    if (c_target_value<0)
                    {
                        c_target_value+=m_step_offset;
                        c_target_index -= 1;
                    }
                    if (c_target_value > m_step_offset*0.5)
                    {
                        c_target_index++;
                    }
                    if (c_target_index<m_min_index)
                    {
                        c_target_index = m_min_index;
                    }
                    else if (c_target_index>m_max_index)
                    {
                        c_target_index = m_max_index;
                    }
                    gotoTargetIndex(c_target_index);
                }
            }
            else
            {
                float c_target_offset = m_slide_speed*15;
                if (c_target_offset>m_step_offset)
                {
                    c_target_offset = m_step_offset;
                }
                else if (c_target_offset<-m_step_offset)
                {
                    c_target_offset = -m_step_offset;
                }
                float c_target_value = m_current_value+c_target_offset;
                int c_target_index = m_current_index;
                int c_step = (int)(c_target_value/m_step_offset);
                c_target_index += c_step;
                c_target_value -= m_step_offset*c_step;
                if (c_target_value<0)
                {
                    c_target_value+=m_step_offset;
                    c_target_index -= 1;
                }
                if (c_target_value > m_step_offset*0.5)
                {
                    c_target_index++;
                }
                if (c_target_index<m_min_index)
                {
                    c_target_index = m_min_index;
                }
                else if (c_target_index>m_max_index)
                {
                    c_target_index = m_max_index;
                }
                gotoTargetIndex(c_target_index);
            }
            c_result = true;
        }
        else if (m_motion_mode == PARA_STATE_GOTO)
        {
            float c_offset_length = (m_target_index-m_current_index)*m_step_offset-m_current_value;
            if (Math.abs(c_offset_length)<1)
            {
                m_current_index = m_target_index;
                m_current_value = 0;
                m_motion_mode = PARA_STATE_STATIC;
            }
            else
            {
                m_current_value += c_offset_length*0.3f;
                int c_step = (int)(m_current_value/m_step_offset);
                m_current_index += c_step;
                m_current_value -= m_step_offset*c_step;
                if (m_current_value<=-m_step_offset*0.5)
                {
                    m_current_value+=m_step_offset;
                    m_current_index -= 1;
                }
                else if (m_current_value>m_step_offset*0.5)
                {
                    m_current_value-=m_step_offset;
                    m_current_index += 1;
                }
                c_result = true;
            }
        }
        return c_result;
    }



}
