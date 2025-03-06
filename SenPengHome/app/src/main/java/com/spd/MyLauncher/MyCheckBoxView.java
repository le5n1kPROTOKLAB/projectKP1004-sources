package com.spd.MyLauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class MyCheckBoxView extends View {
    private int m_point_count = 36;
    private float[] m_org_draw_point_array , m_org_controll_point_array;
    private float[] m_real_draw_point_array , m_real_controll_point_array;

    private Path m_draw_path;
    private MotionParameter m_motion_offset = new MotionParameter();
    private PaintFlagsDrawFilter m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);

    private Paint m_paint;
    private float m_check_box_width = 80 , m_check_box_height = 40 , m_check_box_r = 20;
    public MyCheckBoxView(Context context) {
        super(context);
        m_draw_path = new Path();
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_org_draw_point_array = new float[m_point_count*2];
        m_org_controll_point_array = new float[m_point_count*2];
        m_real_draw_point_array = new float[m_point_count*2];
        m_real_controll_point_array = new float[m_point_count*2];
        buildRectNormalEye(0,m_org_draw_point_array,m_org_controll_point_array,m_check_box_width,m_check_box_height,m_check_box_r,0 ,0);
    }

    public MyCheckBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_draw_path = new Path();
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_org_draw_point_array = new float[m_point_count*2];
        m_org_controll_point_array = new float[m_point_count*2];
        m_real_draw_point_array = new float[m_point_count*2];
        m_real_controll_point_array = new float[m_point_count*2];
        buildRectNormalEye(0,m_org_draw_point_array,m_org_controll_point_array,m_check_box_width,m_check_box_height,m_check_box_r,0 ,0);
    }
    private int m_width , m_height;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int c_width = right-left;
        int c_height = bottom-top;
        if (c_width>0 && c_height>0 && (m_width != c_width || m_height != c_height))
        {
            m_width = c_width;
            m_height = c_height;
            buildPath();
        }
    }

    private void buildPath() {
        Matrix c_matrix = new Matrix();
        m_draw_path.reset();
        c_matrix.postTranslate(m_width/2f,m_height/2f);
        c_matrix.mapPoints(m_real_draw_point_array,m_org_draw_point_array);
        c_matrix.mapPoints(m_real_controll_point_array,m_org_controll_point_array);
        m_draw_path.moveTo(m_real_draw_point_array[0],m_real_draw_point_array[1]);
        int c_point_size = m_real_draw_point_array.length/2;
        for (int i = 0 ; i < c_point_size-1 ; i++)
        {
            m_draw_path.quadTo(m_real_controll_point_array[i*2],m_real_controll_point_array[i*2+1],
                    m_real_draw_point_array[i*2+2],m_real_draw_point_array[i*2+3]);
        }
        m_draw_path.quadTo(m_real_controll_point_array[(c_point_size-1)*2],m_real_controll_point_array[(c_point_size-1)*2+1],
                m_real_draw_point_array[0],m_real_draw_point_array[1]);
        m_draw_path.close();
    }
    private boolean m_toggle = false;
    public boolean getCheckBoxToggled()
    {
        return m_toggle;
    }
    public void setCheckBoxToggled(boolean c_toggle , boolean c_motion)
    {
        if (m_toggle != c_toggle)
        {
            m_toggle = c_toggle;
            if (m_toggle)
            {
                float c_offset = m_check_box_width-m_check_box_r*2;
                if (c_motion)
                {
                    m_motion_offset.gotoValue(c_offset);
                }
                else
                {
                    m_motion_offset.setValue(c_offset);
                }
            }
            else
            {
                if (c_motion)
                {
                    m_motion_offset.gotoValue(0);
                }
                else
                {
                    m_motion_offset.setValue(0);
                }
            }
            this.invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(m_default_filter);
        boolean c_motion = m_motion_offset.motionValue();

        float c_current_offset = canvas.getWidth()/2f-m_check_box_width/2f+m_check_box_r+m_motion_offset.getCurrentValue();
        float c_current_value = m_motion_offset.getCurrentValue()/(m_check_box_width-m_check_box_r*2);
        int c_disable_color = 0xb0cccccc;
        int c_enable_color = 0xff00e41b;
        int c_color_n_r = c_disable_color>>16 &255;
        int c_color_n_g = c_disable_color>>8 &255;
        int c_color_n_b = c_disable_color &255;
        int c_color_n_a = c_disable_color>>24 &255;

        int c_color_p_r = c_enable_color>>16 &255;
        int c_color_p_g = c_enable_color>>8 &255;
        int c_color_p_b = c_enable_color &255;
        int c_color_p_a = c_enable_color>>24 &255;

        int c_color_use_r = (int)(c_color_n_r*(1f-c_current_value)+c_color_p_r*c_current_value);
        int c_color_use_g = (int)(c_color_n_g*(1f-c_current_value)+c_color_p_g*c_current_value);
        int c_color_use_b = (int)(c_color_n_b*(1f-c_current_value)+c_color_p_b*c_current_value);
        int c_color_use_a = (int)(c_color_n_a*(1f-c_current_value)+c_color_p_a*c_current_value);
        int c_bn_color = (c_color_use_a << 24) | (c_color_use_r << 16) | (c_color_use_g << 8) | c_color_use_b;
        m_paint.setColor(c_bn_color);
        canvas.drawPath(m_draw_path,m_paint);
        m_paint.setColor(0xffffffff);
        canvas.drawCircle(c_current_offset,canvas.getHeight()/2f,m_check_box_r,m_paint);
        //canvas.save();
        //canvas.clipPath(m_draw_path);
        //m_paint.setTextSize(18);
        //canvas.drawText("ON",c_current_offset-51,canvas.getHeight()/2f+7,m_paint);
        //m_paint.setColor(0xffaaaaaa);
        //canvas.drawText("OFF",c_current_offset+22,canvas.getHeight()/2f+7,m_paint);
        //canvas.restore();
        if (c_motion)
        {
            this.invalidate();
        }
    }

    private static boolean isOdd(int c_value)
    {
        boolean c_result = true;
        if (c_value%2==0)
            c_result = false;
        return c_result;
    }
    private boolean isAngleRound(int c_rect_position , int c_angle_index)
    {
        boolean c_result = false;
        if (c_rect_position == 0)
        {
            c_result = true;
        }
        else if (c_angle_index == 0 && (c_rect_position == 2 || c_rect_position == 3 || c_rect_position == 6))
        {
            c_result = true;
        }
        else if (c_angle_index == 1 && (c_rect_position == 4 || c_rect_position == 1 || c_rect_position == 2))
        {
            c_result = true;
        }
        else if (c_angle_index == 2 && (c_rect_position == 8 || c_rect_position == 7 || c_rect_position == 4))
        {
            c_result = true;
        }
        else if (c_angle_index == 3 && (c_rect_position == 6 || c_rect_position == 9 || c_rect_position == 8))
        {
            c_result = true;
        }
        return c_result;
    }

    private void buildRectNormalEye(int c_rect_position , float[] c_draw_point_array, float[] c_controll_point_array, float c_width , float c_height , float c_round_r , float c_offset_x , float c_offset_y)
    {
        int c_round_line_use_every_side = (int)(c_round_r/(c_width+c_height)*m_point_count);
        if (isOdd(c_round_line_use_every_side))
        {
            c_round_line_use_every_side--;
            if (c_round_line_use_every_side == 0)
            {
                c_round_line_use_every_side = 2;
            }
        }
        //int c_round_line_use_every_side = 4;
        int c_line_use_remain = m_point_count-c_round_line_use_every_side*4;
        int c_line_use_remain_one = c_line_use_remain/2;
        int c_line_use_height = (int)(c_height/(c_height+c_width)*c_line_use_remain_one);
        if (c_line_use_height == 0)
        {
            c_line_use_height = 2;
        }
        else if (isOdd(c_line_use_height))
        {
            c_line_use_height ++;
            if (c_line_use_height >= c_line_use_remain_one)
            {
                c_line_use_height -= 2;
            }
        }
        int c_line_use_width = c_line_use_remain_one-c_line_use_height;
        float c_height_remain_length = c_height-c_round_r*2f;
        float c_height_step = c_height_remain_length/c_line_use_height;
        float c_disangle_step = c_round_r/c_round_line_use_every_side;
        float c_width_remain_length = c_width-c_round_r*2f;
        float c_width_step = c_width_remain_length/c_line_use_width;
        int c_count_index = 0;
        for (int c_i = 0 ; c_i < c_line_use_height/2 ; c_i ++)
        {
            float c_current_point_x = c_width/2f;
            float c_current_point_y = c_i*c_height_step;
            float c_next_point_x = c_width/2f;
            float c_next_point_y = (c_i+1)*c_height_step;
            c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
            c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
            c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
            c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
            c_count_index++;
        }
        if(isAngleRound(c_rect_position,0))
        {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side ; c_i ++) {
                float c_round_center_x = c_width/2f-c_round_r;
                float c_round_center_y = c_height/2f-c_round_r;
                double c_current_angle = Math.PI/2d/c_round_line_use_every_side*c_i;
                double c_next_angle = Math.PI/2d/c_round_line_use_every_side*(c_i+1);
                double c_controll_angle = (c_current_angle+c_next_angle)/2d;
                float c_current_point_x_round = (float)(c_round_r* Math.cos(c_current_angle))+c_round_center_x;
                float c_current_point_y_round = (float)(c_round_r* Math.sin(c_current_angle))+c_round_center_y;
                float c_controll_point_x_round = (float)(c_round_r* Math.cos(c_controll_angle))+c_round_center_x;
                float c_controll_point_y_round = (float)(c_round_r* Math.sin(c_controll_angle))+c_round_center_y;
                c_draw_point_array[c_count_index*2]=c_current_point_x_round+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y_round+c_offset_y;
                c_controll_point_array[c_count_index*2]=c_controll_point_x_round+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=c_controll_point_y_round+c_offset_y;
                c_count_index++;
            }
        }
        else
        {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = c_width/2f;
                float c_current_point_y = c_height_remain_length/2f+c_i*c_disangle_step;
                float c_next_point_x = c_width/2f;
                float c_next_point_y = c_height_remain_length/2f+(c_i+1)*c_disangle_step;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = c_width/2f-c_i*c_disangle_step;
                float c_current_point_y = c_height/2f;
                float c_next_point_x = c_width/2f-(c_i+1)*c_disangle_step;
                float c_next_point_y = c_height/2f;;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
        }
        for (int c_i = c_line_use_width/2 ; c_i > -c_line_use_width/2 ; c_i --)
        {
            float c_current_point_x = c_i*c_width_step;
            float c_current_point_y = c_height/2f;;
            float c_next_point_x = (c_i-1)*c_width_step;
            float c_next_point_y = c_height/2f;;
            c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
            c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
            c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
            c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
            c_count_index++;
        }
        if(isAngleRound(c_rect_position,1)) {
            for (int c_i = 0; c_i < c_round_line_use_every_side; c_i++) {
                float c_round_center_x = -c_width / 2f + c_round_r;
                float c_round_center_y = c_height / 2f - c_round_r;
                double c_current_angle = Math.PI / 2d / c_round_line_use_every_side * c_i + Math.PI / 2d;
                double c_next_angle = Math.PI / 2d / c_round_line_use_every_side * (c_i + 1) + Math.PI / 2d;
                double c_controll_angle = (c_current_angle + c_next_angle) / 2d;
                float c_current_point_x_round = (float) (c_round_r * Math.cos(c_current_angle)) + c_round_center_x;
                float c_current_point_y_round = (float) (c_round_r * Math.sin(c_current_angle)) + c_round_center_y;
                float c_controll_point_x_round = (float) (c_round_r * Math.cos(c_controll_angle)) + c_round_center_x;
                float c_controll_point_y_round = (float) (c_round_r * Math.sin(c_controll_angle)) + c_round_center_y;
                c_draw_point_array[c_count_index*2]=c_current_point_x_round+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y_round+c_offset_y;
                c_controll_point_array[c_count_index*2]=c_controll_point_x_round+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=c_controll_point_y_round+c_offset_y;
                c_count_index++;
            }
        }
        else
        {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = c_width_remain_length/2f-c_i*c_disangle_step;
                float c_current_point_y = c_height/2f;
                float c_next_point_x = c_width_remain_length/2f-(c_i+1)*c_disangle_step;
                float c_next_point_y = c_height/2f;;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = -c_width/2f;
                float c_current_point_y = c_height/2f-c_i*c_disangle_step;
                float c_next_point_x = -c_width/2f;
                float c_next_point_y = c_height/2f-(c_i-1)*c_disangle_step;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
        }
        for (int c_i = c_line_use_height/2 ; c_i > -c_line_use_height/2 ; c_i --)
        {
            float c_current_point_x = -c_width/2f;
            float c_current_point_y = c_i*c_height_step;
            float c_next_point_x = -c_width/2f;
            float c_next_point_y = (c_i-1)*c_height_step;
            c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
            c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
            c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
            c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
            c_count_index++;
        }
        if(isAngleRound(c_rect_position,2)) {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side ; c_i ++) {
                float c_round_center_x = -c_width/2f+c_round_r;
                float c_round_center_y = -c_height/2f+c_round_r;
                double c_current_angle = Math.PI/2d/c_round_line_use_every_side*c_i+ Math.PI;
                double c_next_angle = Math.PI/2d/c_round_line_use_every_side*(c_i+1)+ Math.PI;
                double c_controll_angle = (c_current_angle+c_next_angle)/2d;
                float c_current_point_x_round = (float)(c_round_r* Math.cos(c_current_angle))+c_round_center_x;
                float c_current_point_y_round = (float)(c_round_r* Math.sin(c_current_angle))+c_round_center_y;
                float c_controll_point_x_round = (float)(c_round_r* Math.cos(c_controll_angle))+c_round_center_x;
                float c_controll_point_y_round = (float)(c_round_r* Math.sin(c_controll_angle))+c_round_center_y;
                c_draw_point_array[c_count_index*2]=c_current_point_x_round+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y_round+c_offset_y;
                c_controll_point_array[c_count_index*2]=c_controll_point_x_round+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=c_controll_point_y_round+c_offset_y;

                c_count_index++;
            }
        }
        else
        {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = -c_width/2f;
                float c_current_point_y = -c_height_remain_length/2f-c_i*c_disangle_step;
                float c_next_point_x = -c_width/2f;
                float c_next_point_y = -c_height_remain_length/2f-(c_i-1)*c_disangle_step;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = -c_width/2f+c_i*c_disangle_step;
                float c_current_point_y = -c_height/2f;;
                float c_next_point_x = -c_width/2f+(c_i+1)*c_disangle_step;
                float c_next_point_y = -c_height/2f;;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
        }
        for (int c_i = -c_line_use_width/2 ; c_i < c_line_use_width/2 ; c_i ++)
        {
            float c_current_point_x = c_i*c_width_step;
            float c_current_point_y = -c_height/2f;;
            float c_next_point_x = (c_i+1)*c_width_step;
            float c_next_point_y = -c_height/2f;;
            c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
            c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
            c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
            c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
            c_count_index++;
        }
        if(isAngleRound(c_rect_position,3)) {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side ; c_i ++) {
                float c_round_center_x = c_width/2f-c_round_r;
                float c_round_center_y = -c_height/2f+c_round_r;
                double c_current_angle = Math.PI/2d/c_round_line_use_every_side*c_i+ Math.PI*1.5d;
                double c_next_angle = Math.PI/2d/c_round_line_use_every_side*(c_i+1)+ Math.PI*1.5d;
                double c_controll_angle = (c_current_angle+c_next_angle)/2d;
                float c_current_point_x_round = (float)(c_round_r* Math.cos(c_current_angle))+c_round_center_x;
                float c_current_point_y_round = (float)(c_round_r* Math.sin(c_current_angle))+c_round_center_y;
                float c_controll_point_x_round = (float)(c_round_r* Math.cos(c_controll_angle))+c_round_center_x;
                float c_controll_point_y_round = (float)(c_round_r* Math.sin(c_controll_angle))+c_round_center_y;
                c_draw_point_array[c_count_index*2]=c_current_point_x_round+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y_round+c_offset_y;
                c_controll_point_array[c_count_index*2]=c_controll_point_x_round+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=c_controll_point_y_round+c_offset_y;

                c_count_index++;
            }
        }
        else
        {
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = c_width_remain_length/2f+c_i*c_disangle_step;
                float c_current_point_y = -c_height/2f;;
                float c_next_point_x = c_width_remain_length/2f+(c_i+1)*c_disangle_step;
                float c_next_point_y = -c_height/2f;;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
            for (int c_i = 0 ; c_i < c_round_line_use_every_side/2 ; c_i ++)
            {
                float c_current_point_x = c_width/2f;
                float c_current_point_y = -c_height/2f+c_i*c_disangle_step;
                float c_next_point_x = c_width/2f;
                float c_next_point_y = -c_height/2f+(c_i+1)*c_disangle_step;
                c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
                c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
                c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
                c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
                c_count_index++;
            }
        }
        for (int c_i = -c_line_use_height/2 ; c_i < 0 ; c_i ++)
        {
            float c_current_point_x = c_width/2f;
            float c_current_point_y = c_i*c_height_step;
            float c_next_point_x = c_width/2f;
            float c_next_point_y = (c_i+1)*c_height_step;
            c_draw_point_array[c_count_index*2]=c_current_point_x+c_offset_x;
            c_draw_point_array[c_count_index*2+1]=c_current_point_y+c_offset_y;
            c_controll_point_array[c_count_index*2]=(c_next_point_x+c_current_point_x)/2f+c_offset_x;
            c_controll_point_array[c_count_index*2+1]=(c_next_point_y+c_current_point_y)/2f+c_offset_y;
            c_count_index++;
        }
    }
}
