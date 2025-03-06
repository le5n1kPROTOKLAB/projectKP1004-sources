package com.spd.window;

public class MotionParameter {
    private final static int PARA_STATE_STATIC = 0;
    private final static int PARA_STATE_GOTO = 1;
    private final static int PARA_STATE_DRAG = 2;
    private final static int PARA_STATE_SLIDE = 3;
    private int m_motion_mode = PARA_STATE_STATIC;
    private float m_currentm_freq_value = 0.0f;
    private float m_targetm_freq_value = 0.0f;
    private float m_static_gate = 0.0f;
    private float m_dump = 0.3f;

    public float getCurrentValue()
    {
        return m_currentm_freq_value;
    }

    public void setValue(float cm_freq_value)
    {
        m_currentm_freq_value = cm_freq_value;
        m_targetm_freq_value = cm_freq_value;
        m_motion_mode = PARA_STATE_STATIC;
    }
    public void gotoValue(float cm_freq_value , float c_dump)
    {
        m_targetm_freq_value = cm_freq_value;
        m_static_gate = Math.abs(cm_freq_value - m_currentm_freq_value)*0.001f+0.00001f;
        m_dump = c_dump;
        m_motion_mode = PARA_STATE_GOTO;
    }
    public void gotoValue(float cm_freq_value)
    {
        gotoValue(cm_freq_value,m_dump);
    }
    public boolean motionValue()
    {
        boolean c_result = false;
        if (m_motion_mode == PARA_STATE_GOTO)
        {
            m_currentm_freq_value += (m_targetm_freq_value - m_currentm_freq_value)*m_dump;
            if (Math.abs(m_targetm_freq_value - m_currentm_freq_value)<m_static_gate)
            {
                m_currentm_freq_value = m_targetm_freq_value;
                m_motion_mode = PARA_STATE_STATIC;
            }
            else
            {
                c_result = true;
            }
        }
        return c_result;
    }


}
