package com.spd.SpdWallPaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.opengl.Matrix;


public class SingleCircle {
    private float[] m_point_org_postion;
    private static float[] m_point_use_postion;
    private static float[] m_point_count_postion;
    private Paint m_paint;
    private CircleMotionParameter m_postion_x_motion;
    private CircleMotionParameter m_postion_y_motion;
    private CircleMotionParameter m_postion_z_motion;
    private CircleMotionParameter m_postion_alpha_motion;

    //private CircleMotionParameter m_alpha_motion;

    private float m_offset_x_xishu;
    private static PaintFlagsDrawFilter m_default_filter;
    private float mCircleRadius;
    private float[] mHsv = new float[3];
    private float[] mColor = new float[]{182,60,236};

    public SingleCircle(float c_radius) {
        //m_circle_color = new Color();

        mHsv[0] = mColor[(int)(Math.random()*mColor.length)];
        mHsv[1] = 1.0f;
        mHsv[2] = 1.0f;
        m_postion_x_motion = new CircleMotionParameter();
        m_postion_y_motion = new CircleMotionParameter();
        m_postion_z_motion = new CircleMotionParameter();
        m_postion_alpha_motion = new CircleMotionParameter();
        //m_alpha_motion = new CircleMotionParameter();
        m_postion_x_motion.setCircleMotionEffect(0.3f,(float)Math.random()*0.03f+0.03f,(float)Math.random()*(float)Math.PI*2f);
        m_postion_y_motion.setCircleMotionEffect(0.3f,(float)Math.random()*0.03f+0.03f,(float)Math.random()*(float)Math.PI*2f);
        m_postion_z_motion.setCircleMotionEffect(0.4f,(float)Math.random()*0.01f+0.02f,(float)Math.random()*(float)Math.PI*2f);
        m_postion_alpha_motion.setCircleMotionEffect(0.4f,(float)Math.random()*0.015f+0.07f,(float)Math.random()*(float)Math.PI*2f);
        //m_alpha_motion.setCircleMotionEffect(0.3f,(float)Math.random()*0.002f+0.0004f,(float)Math.random()*(float)Math.PI*2f);

        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
        m_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        m_paint.setColor(0xffffffff);
        //m_paint.setStrokeWidth(3);
        m_paint.setAntiAlias(true);
        m_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        m_offset_x_xishu = (float)Math.random()*0.02f+0.01f;
        mCircleRadius = c_radius;
        m_point_org_postion = new float[4];
        m_point_org_postion[3] = 1f;
        if (m_point_count_postion == null)
        {
            m_point_count_postion = new float[4];
        }
        if (m_point_use_postion == null)
        {
            m_point_use_postion = new float[4];
        }

        //m_paint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL));
    }

    public void setOrgPostion(float c_x ,float c_y ,float c_z)
    {
        m_point_org_postion[0] = c_x;
        m_point_org_postion[1] = c_y;
        m_point_org_postion[2] = c_z;
    }

    public void motionCircleView(float c_offset_x)
    {
        m_point_org_postion[0] += c_offset_x*m_offset_x_xishu;
        if (m_point_org_postion[0]> CircleMotionView.mCircleGroupWidth)
        {
            m_point_org_postion[0] -= CircleMotionView.mCircleGroupWidth*2f;
        }
        else if (m_point_org_postion[0]<-CircleMotionView.mCircleGroupWidth)
        {
            m_point_org_postion[0] += CircleMotionView.mCircleGroupWidth*2f;
        }
    }
    private boolean mColorReinitFlag = false;
    public void drawCircle(Canvas canvas,float[] cUseMatrix)
    {
        canvas.setDrawFilter(m_default_filter);
        m_postion_x_motion.motionValue();
        m_postion_y_motion.motionValue();
        m_postion_z_motion.motionValue();
        m_postion_alpha_motion.motionValue();
        float c_alpha = m_postion_alpha_motion.getCurrentValue();
        if (c_alpha < 0 && mColorReinitFlag)
        {
            //Log.d("mColorReinitFlag", "drawCircle: mColorReinitFlag");
            mColorReinitFlag = false;
            m_postion_x_motion.setCircleMotionEffect(0.3f,(float)Math.random()*0.003f+0.003f,(float)Math.random()*(float)Math.PI*2f);
            m_postion_y_motion.setCircleMotionEffect(0.3f,(float)Math.random()*0.003f+0.003f,(float)Math.random()*(float)Math.PI*2f);
            m_postion_z_motion.setCircleMotionEffect(0.4f,(float)Math.random()*0.01f+0.002f,(float)Math.random()*(float)Math.PI*2f);
            mHsv[0] = mColor[(int)(Math.random()*mColor.length)];
        }
        c_alpha = Math.max(0.0f,c_alpha);
        if (c_alpha > 0.0f)
        {
            mColorReinitFlag = true;
            m_point_count_postion[0] = m_point_org_postion[0]+m_postion_x_motion.getCurrentValue();
            m_point_count_postion[1] = m_point_org_postion[1]+m_postion_y_motion.getCurrentValue();
            m_point_count_postion[2] = m_point_org_postion[2]+m_postion_z_motion.getCurrentValue();
            m_point_count_postion[3] = m_point_org_postion[3];
            Matrix.multiplyMV(m_point_count_postion,0,cUseMatrix,0,m_point_count_postion,0);
            float c_point_x = m_point_count_postion[0]/m_point_count_postion[3];
            c_point_x = c_point_x*0.5f+0.5f;
            float c_point_y = m_point_count_postion[1]/m_point_count_postion[3];
            c_point_y = c_point_y*0.5f+0.5f;
            float c_point_z = m_point_count_postion[2]/m_point_count_postion[3];
            //canvas.drawCircle(c_point_x*canvas.getWidth(),c_point_y*canvas.getHeight(),80f-c_point_z*10,m_paint);

            m_paint.setAlpha((int)(128*c_alpha));
            float c_glow_radius = mCircleRadius*1.3f+20f;
            RadialGradient c_radia_gradient = new RadialGradient(c_point_x*canvas.getWidth(),c_point_y*canvas.getHeight(),c_glow_radius,
                    new int[]{Color.HSVToColor(30,mHsv),Color.HSVToColor(228,mHsv),Color.HSVToColor(0,mHsv)},new float[]{0.0f,0.7f,1.0f}, Shader.TileMode.CLAMP);
            m_paint.setShader(c_radia_gradient);
            canvas.drawCircle(c_point_x*canvas.getWidth(),c_point_y*canvas.getHeight(),c_glow_radius,m_paint);
            m_paint.setAlpha((int)(150*c_alpha));
            float c_blur_radius = Math.abs(m_postion_z_motion.getCurrentValue())*10f*20f;
            c_blur_radius = Math.max(c_blur_radius-30f,0f)+5f;
            float c_circle_radius = mCircleRadius+c_blur_radius;
            float c_offset_native = (mCircleRadius-c_blur_radius)/c_circle_radius;
            c_radia_gradient = new RadialGradient(c_point_x*canvas.getWidth(),c_point_y*canvas.getHeight(),c_circle_radius,
                    new int[]{Color.HSVToColor(230,mHsv),Color.HSVToColor(0,mHsv)},new float[]{c_offset_native,1.0f}, Shader.TileMode.CLAMP);
            m_paint.setShader(c_radia_gradient);
            canvas.drawCircle(c_point_x*canvas.getWidth(),c_point_y*canvas.getHeight(),c_circle_radius,m_paint);
            //m_paint.clearShadowLayer();
        }

    }

}
