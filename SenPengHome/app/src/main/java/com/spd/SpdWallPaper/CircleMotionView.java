package com.spd.SpdWallPaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CircleMotionView{
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];

    private float[] mMVPMatrix=new float[16];
    private float[] mWorldMatrix=new float[16];
    private float[] mUseMatrix=new float[16];

    private CircleMotionParameter m_motion_x;
    public final static float mCircleGroupWidth = 12f;
    private String TAG = "CircleMotionView";
    private int m_circle_count = 40;
    private List<SingleCircle> m_circle_list = new ArrayList<>();
    private LinearGradient m_gradient;
    private Paint m_gradient_bg_paint;

    public CircleMotionView() {
        m_motion_x = new CircleMotionParameter();
        float c_max_side_y = 4f;
        float c_max_side_z = 4f;


        int c_big_circle_num = 0;
        for (int i = 0 ; i<m_circle_count-c_big_circle_num ; i++)
        {
            float c_child_radius = 105f+(float)Math.random()*45f;
            float c_child_radius_offset = c_child_radius/50f;
            SingleCircle c_child = new SingleCircle(c_child_radius);
            m_circle_list.add(c_child);
            float c_postion_x = (float)Math.random()*(mCircleGroupWidth+c_child_radius_offset)*2f-(mCircleGroupWidth+c_child_radius_offset);
            float c_postion_y = (float)Math.random()*(c_max_side_y+c_child_radius_offset)*2f-(c_max_side_y+c_child_radius_offset);
            float c_postion_z = (float)Math.random()*(c_max_side_z+c_child_radius_offset)*2f-(c_max_side_z+c_child_radius_offset);
            c_child.setOrgPostion(-c_postion_x,-c_postion_y,c_postion_z);
        }
        for (int i = 0 ; i<c_big_circle_num ; i++)
        {
            float c_child_radius = 100f+(float)Math.random()*(i)*100;
            float c_child_radius_offset = c_child_radius/50f;
            SingleCircle c_child = new SingleCircle(c_child_radius);
            m_circle_list.add(c_child);
            float c_postion_x = (float)Math.random()*(mCircleGroupWidth+c_child_radius_offset)*2f-(mCircleGroupWidth+c_child_radius_offset);
            float c_postion_y = (float)Math.random()*(c_max_side_y+c_child_radius_offset)*2f-(c_max_side_y+c_child_radius_offset);
            float c_postion_z = (float)Math.random()*(c_max_side_z+c_child_radius_offset)*2f-(c_max_side_z+c_child_radius_offset);
            c_child.setOrgPostion(-c_postion_x,-c_postion_y,c_postion_z);
        }
    }


    float m_view_width;
    float m_view_height;

    public void setLayoutParameter (int c_width , int c_height)
    {
        m_view_width = c_width;
        m_view_height = c_height;
        if (m_view_height < 1f) {
            m_view_height = 1f;
        }
        m_gradient = new LinearGradient(m_view_width-10,0,0,m_view_height-10,0xff101010,0xff444444, Shader.TileMode.CLAMP);
        m_gradient_bg_paint = new Paint();
        m_gradient_bg_paint.setColor(Color.BLACK);
        //m_gradient_bg_paint.setShader(m_gradient);
        float aspect = (float) m_view_width / m_view_height;
        Log.d(TAG, "onMeasure: "+m_view_width+" "+m_view_height+" "+aspect);
        Matrix.frustumM(mProjectMatrix, 0, -aspect, aspect, -1, 1, 3, 30);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 12.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    private float m_offset_count_x = 0f;
    private int m_offset_drag_flag = 0;
    private float m_last_offset_value = 0f;
    public void offsetCircleView(float c_offset_x)
    {
        m_offset_count_x += c_offset_x;
        m_offset_drag_flag = 1;
    }
    public void stopOffsetCircleView()
    {
        m_motion_x.stopDragValue(0.9f);
        m_offset_drag_flag = 2;
    }
    float m_world_rotaion = 0.0f;

    public void onDraw(Canvas canvas) {
        if (m_offset_drag_flag != 0)
        {
            if (m_offset_drag_flag == 1)
            {
                m_motion_x.dragOffsetValue(m_offset_count_x);
                for (int i = 0 ; i<m_circle_count ; i++) {
                    m_circle_list.get(i).motionCircleView(m_offset_count_x);
                }
                m_offset_count_x = 0f;
                m_motion_x.motionValue();
                m_last_offset_value = m_motion_x.getCurrentValue();
            }
            else if (m_offset_drag_flag == 2)
            {
                m_offset_count_x = 0f;
                boolean c_motion = m_motion_x.motionValue();
                float c_offset_x = m_motion_x.getCurrentValue() - m_last_offset_value;
                m_last_offset_value = m_motion_x.getCurrentValue();
                for (int i = 0 ; i<m_circle_count ; i++) {
                    m_circle_list.get(i).motionCircleView(c_offset_x);
                }
                if (!c_motion)
                {
                    m_offset_drag_flag = 0;
                }
            }
        }
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),m_gradient_bg_paint);
        m_world_rotaion += 0.2f;
        Matrix.setIdentityM(mWorldMatrix,0);
        Matrix.multiplyMM(mUseMatrix,0,mMVPMatrix,0,mWorldMatrix,0);
        for (int i = 0 ; i<m_circle_count ; i++) {
            m_circle_list.get(i).drawCircle(canvas, mUseMatrix);
        }
    }
}
