package com.spd.window;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.spd.home.R;

public class FilletFrameLayout extends FrameLayout {
    private float m_fillet_radius;
    private float m_fillet_width;
    private float m_fillet_height;
    private int m_fillet_postion;
    private float m_fillet_side_width;

    private int m_fillet_color_n;
    private int m_fillet_side_color;
    protected Paint m_paint;
    protected static PaintFlagsDrawFilter m_default_filter;
    SweepGradient m_sweep_gradient ;
    public FilletFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.CustomView
                , 0, 0);
        m_fillet_radius = array.getFloat(R.styleable.CustomView_fillet_radius,5f);
        m_fillet_color_n = array.getColor(R.styleable.CustomView_blank_bg_color_n,0xff000000);
        m_fillet_side_color = array.getColor(R.styleable.CustomView_fillet_side_color,0);

        m_fillet_width = array.getFloat(R.styleable.CustomView_fillet_width,-1);
        m_fillet_height = array.getFloat(R.styleable.CustomView_fillet_height,-1);
        m_fillet_postion = array.getInt(R.styleable.CustomView_fillet_postion,0);
        m_fillet_side_width = array.getFloat(R.styleable.CustomView_fillet_side_width,1f);
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_paint.setColor(Color.WHITE);
        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
        setWillNotDraw(false);
    }
    private float[] m_bg_org_point;
    private float[] m_bg_draw_point;
    private int[] m_angle_step = new int[4];
    private Matrix m_path_matrix;
    private Path m_path;
    private void buildBgFillet(float c_card_width , float c_card_height , float c_card_arc_r)
    {
        if (m_bg_org_point == null)
        {
            m_bg_org_point = new float[24];
            m_bg_draw_point = new float[24];
            int c_arc_uses_step = 1;
            if (m_fillet_postion == 0 || m_fillet_postion == 4 || m_fillet_postion == 7 || m_fillet_postion == 8)
            {
                c_arc_uses_step = 1;
            }
            else
            {
                c_arc_uses_step = 0;
            }
            m_angle_step[2] = c_arc_uses_step;
            m_bg_org_point[0] = -c_card_width/2f;
            m_bg_org_point[1] = -c_card_height/2f+c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[2] = -c_card_width/2f;
            m_bg_org_point[3] = -c_card_height/2f;
            m_bg_org_point[4] = -c_card_width/2f+c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[5] = -c_card_height/2f;
            if (m_fillet_postion == 0 || m_fillet_postion == 8 || m_fillet_postion == 9 || m_fillet_postion == 6)
            {
                c_arc_uses_step = 1;
            }
            else
            {
                c_arc_uses_step = 0;
            }
            m_angle_step[3] = c_arc_uses_step;
            m_bg_org_point[6] = c_card_width/2f-c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[7] = -c_card_height/2f;
            m_bg_org_point[8] = c_card_width/2f;
            m_bg_org_point[9] = -c_card_height/2f;
            m_bg_org_point[10] = c_card_width/2f;
            m_bg_org_point[11] = -c_card_height/2f+c_card_arc_r*c_arc_uses_step;
            if (m_fillet_postion == 0 || m_fillet_postion == 6 || m_fillet_postion == 3 || m_fillet_postion == 2)
            {
                c_arc_uses_step = 1;
            }
            else
            {
                c_arc_uses_step = 0;
            }
            m_angle_step[0] = c_arc_uses_step;
            m_bg_org_point[12] = c_card_width/2f;
            m_bg_org_point[13] = c_card_height/2f-c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[14] = c_card_width/2f;
            m_bg_org_point[15] = c_card_height/2f;
            m_bg_org_point[16] = c_card_width/2f-c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[17] = c_card_height/2f;
            if (m_fillet_postion == 0 || m_fillet_postion == 2 || m_fillet_postion == 1 || m_fillet_postion == 4)
            {
                c_arc_uses_step = 1;
            }
            else
            {
                c_arc_uses_step = 0;
            }
            m_angle_step[1] = c_arc_uses_step;
            m_bg_org_point[18] = -c_card_width/2f+c_card_arc_r*c_arc_uses_step;
            m_bg_org_point[19] = c_card_height/2f;
            m_bg_org_point[20] = -c_card_width/2f;
            m_bg_org_point[21] = c_card_height/2f;
            m_bg_org_point[22] = -c_card_width/2f;
            m_bg_org_point[23] = c_card_height/2f-c_card_arc_r*c_arc_uses_step;
        }
        m_path_matrix = new Matrix();
        m_path = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int c_width = right-left;
        int c_height = bottom-top;
        if (m_fillet_width == -1)
        {
            m_fillet_width = c_width;
        }
        if (m_fillet_height == -1)
        {
            m_fillet_height = c_height;
        }
        buildBgFillet(m_fillet_width,m_fillet_height,m_fillet_radius);

        m_path_matrix.postTranslate(c_width/2f,c_height/2f);
        m_path_matrix.mapPoints(m_bg_draw_point,m_bg_org_point);
        m_path.reset();
        m_path.moveTo(m_bg_draw_point[0],m_bg_draw_point[1]);
        m_path.quadTo(m_bg_draw_point[2],m_bg_draw_point[3],m_bg_draw_point[4],m_bg_draw_point[5]);
        m_path.lineTo(m_bg_draw_point[6],m_bg_draw_point[7]);
        m_path.quadTo(m_bg_draw_point[8],m_bg_draw_point[9],m_bg_draw_point[10],m_bg_draw_point[11]);
        m_path.lineTo(m_bg_draw_point[12],m_bg_draw_point[13]);
        m_path.quadTo(m_bg_draw_point[14],m_bg_draw_point[15],m_bg_draw_point[16],m_bg_draw_point[17]);
        m_path.lineTo(m_bg_draw_point[18],m_bg_draw_point[19]);
        m_path.quadTo(m_bg_draw_point[20],m_bg_draw_point[21],m_bg_draw_point[22],m_bg_draw_point[23]);
        m_path.lineTo(m_bg_draw_point[0],m_bg_draw_point[1]);
        m_path.close();
        float c_k = m_fillet_height/m_fillet_width;
        float c_angle_0 = (float)(Math.atan(c_k)/Math.PI*0.5f);
        float c_color_postion_angle = m_fillet_radius/(m_fillet_width+m_fillet_height)*0.25f;
        int[] c_line_color = new int[] {0x50000000,0x50000000,0xa0000000,0xa0000000,0x50ffffff,0x50ffffff,0xa0ffffff,0xa0ffffff,0x50000000,0x50000000};
        float[] c_color_postion = new float[]{0.0f,c_angle_0-c_color_postion_angle*m_angle_step[0],
                c_angle_0+c_color_postion_angle*m_angle_step[0],0.5f-c_angle_0-c_color_postion_angle*m_angle_step[1],
                0.5f-c_angle_0+c_color_postion_angle*m_angle_step[1],0.5f+c_angle_0-c_color_postion_angle*m_angle_step[2],
                0.5f+c_angle_0+c_color_postion_angle*m_angle_step[2],1f-c_angle_0-c_color_postion_angle*m_angle_step[3],
                1f-c_angle_0+c_color_postion_angle*m_angle_step[3],1f};
        m_sweep_gradient = new SweepGradient(c_width/2f, c_height/2f,c_line_color , c_color_postion);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(m_default_filter);
        m_paint.setColor(m_fillet_color_n);
        m_paint.setStyle(Paint.Style.FILL);
        //Log.d("TEST","draw ");
        canvas.drawPath(m_path,m_paint);

        if (m_fillet_side_width>0)
        {
            m_paint.setStyle(Paint.Style.STROKE);
            m_paint.setStrokeWidth(m_fillet_side_width);
            if (m_fillet_side_color == 0)
            {
                m_paint.setShader(m_sweep_gradient);
                m_paint.setColor(0xffffffff);
            }
            else
            {
                m_paint.setColor(m_fillet_side_color);
            }
            canvas.drawPath(m_path,m_paint);
            m_paint.setStyle(Paint.Style.FILL);
            m_paint.setShader(null);
            m_paint.setColor(0xffffffff);
        }
    }
}
