package com.spd.SpdWallPaper;

public class CircleMotionParameter {
    private final static int PARA_STATE_STATIC = 0;
    private final static int PARA_STATE_GOTO = 1;
    private final static int PARA_STATE_DRAG = 2;
    private final static int PARA_STATE_SLIDE = 3;
    private int m_motion_mode = PARA_STATE_STATIC;
    private float m_current_value = 0.0f;
    private float m_target_value = 0.0f;
    private float m_static_gate = 0.0f;
    private float m_drag_speed = 0.0f;
    private float m_dump = 0.3f;
    private float m_circle_base_value = 1.0f;
    private float m_circle_rotation_speed = 0.015f;
    private float m_circle_rotation_phase = 1.0f;
    private boolean m_circle_effect = false;


    public float getCurrentValue()
    {
        float c_result = m_current_value;
        if (m_circle_effect)
        {
            c_result += m_circle_base_value*Math.sin(m_circle_rotation_phase);
        }
        return c_result;
    }

    public void setValue(float c_value)
    {
        m_current_value = c_value;
        m_target_value = c_value;
        m_motion_mode = PARA_STATE_STATIC;
    }
    public void gotoValue(float c_value , float c_dump)
    {
        m_target_value = c_value;
        m_static_gate = Math.abs(c_value - m_current_value)*0.001f+0.00001f;
        m_dump = c_dump;
        m_motion_mode = PARA_STATE_GOTO;
    }

    public void gotoValue(float c_value)
    {
        gotoValue(c_value,m_dump);
    }

    public void dragOffsetValue(float c_offset_value)
    {
        m_current_value += c_offset_value;
        m_motion_mode = PARA_STATE_DRAG;
        m_drag_speed = m_drag_speed*0.5f+c_offset_value*0.5f;
    }
    public void stopDragValue(float c_dump)
    {
        m_dump = c_dump;
        m_static_gate = Math.abs(m_drag_speed)*0.01f;
        m_motion_mode = PARA_STATE_SLIDE;
    }
    public void setCircleMotionEffect(float c_base_value , float c_rotation_speed , float c_rotation_phase)
    {
        m_circle_base_value = c_base_value;
        m_circle_rotation_speed = c_rotation_speed;
        m_circle_rotation_phase = c_rotation_phase;
        m_circle_effect = true;
    }
    public void closeCircleMotionEffect()
    {
        m_circle_effect = false;
    }

    public boolean motionValue()
    {
        boolean c_result = false;
        if (m_motion_mode == PARA_STATE_GOTO)
        {
            m_current_value += (m_target_value - m_current_value)*m_dump;
            if (Math.abs(m_target_value - m_current_value)<m_static_gate)
            {
                m_current_value = m_target_value;
                m_motion_mode = PARA_STATE_STATIC;
            }
            else
            {
                c_result = true;
            }
        }
        else if (m_motion_mode == PARA_STATE_SLIDE)
        {
            m_drag_speed *= m_dump;
            m_current_value += m_drag_speed;
            if (Math.abs(m_drag_speed) < m_static_gate)
            {
                m_drag_speed = 0f;
                m_motion_mode = PARA_STATE_STATIC;
            }
            else
            {
                c_result = true;
            }
        }
        if (m_circle_effect)
        {
            m_circle_rotation_phase += m_circle_rotation_speed;
            if (m_circle_rotation_phase>Math.PI*2f)
            {
                m_circle_rotation_phase -= Math.PI*2f;
            }
            else if (m_circle_rotation_phase<0f)
            {
                m_circle_rotation_phase += Math.PI*2f;
            }
            c_result = true;
        }
        return c_result;
    }


}
