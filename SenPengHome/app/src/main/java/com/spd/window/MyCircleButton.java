package com.spd.window;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.spd.home.R;


public class MyCircleButton extends FrameLayout implements View.OnTouchListener{
    private Bitmap m_bg_bitmap;
    private Bitmap m_icon_bitmap;
    private boolean m_bg_bitmap_mode;
    private float m_fillet_radius;
    private float m_fillet_side_width;
    private int m_fillet_color_n;
    private int m_fillet_color_p;
    private int m_fillet_side_color;
    protected Paint m_paint;
    private float m_touch_radius_inside;
    private boolean m_circle_in_blank;
    private MotionParameter m_click_value = new MotionParameter();
    protected static PaintFlagsDrawFilter m_default_filter;
    private boolean m_toggle_flag = false;
    private MotionParameter m_togglem_freq_value = new MotionParameter();

    public MyCircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.CustomView
                , 0, 0);
        int c_bg_resource = array.getResourceId(R.styleable.CustomView_bg_drawable, 0);

        if (c_bg_resource != 0)
        {
            if (c_bg_resource != -1)
            {
                m_bg_bitmap = BitmapFactory.decodeResource(context.getResources(),c_bg_resource);
            }
            m_bg_bitmap_mode = true;
        }
        else
        {
            m_bg_bitmap_mode = false;
            m_fillet_radius = array.getFloat(R.styleable.CustomView_fillet_radius,5f);
            m_fillet_color_n = array.getColor(R.styleable.CustomView_blank_bg_color_n,0xff000000);
            m_fillet_color_p = array.getColor(R.styleable.CustomView_blank_bg_color_p,m_fillet_color_n);
            m_fillet_side_color = array.getColor(R.styleable.CustomView_fillet_side_color,0);
            m_fillet_side_width = array.getFloat(R.styleable.CustomView_fillet_side_width,1f);
            m_circle_in_blank = array.getBoolean(R.styleable.CustomView_circle_in_blank,false);
        }
        int c_icon_resource = array.getResourceId(R.styleable.CustomView_icon_drawable, 0);
        if (c_icon_resource != 0)
        {
            m_icon_bitmap = BitmapFactory.decodeResource(context.getResources(),c_icon_resource);
        }

        m_touch_radius_inside = array.getFloat(R.styleable.CustomView_touch_circle_radius,40f);
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        boolean c_touch_enable = array.getBoolean(R.styleable.CustomView_bn_touch,true);
        if (c_touch_enable)
        {
            this.setOnTouchListener(this);
        }
        m_paint.setColor(Color.WHITE);
        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
        setWillNotDraw(false);
    }

    public void setIconDrawableResource(int c_resource_id)
    {
        if (c_resource_id != 0)
        {
            m_icon_bitmap = BitmapFactory.decodeResource(getContext().getResources(),c_resource_id);
        }
        this.invalidate();
    }



    private Path m_path;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!m_bg_bitmap_mode)
        {
            int c_width = right-left;
            int c_height = bottom-top;
            m_path = new Path();
            m_path.addCircle(c_width/2,c_height/2,m_fillet_radius, Path.Direction.CCW);
        }
    }

    protected float getBgAlphaEffect()
    {
        return 1.0f;
    }
    protected float getIconAlphaEffect()
    {
        return 1.0f;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(m_default_filter);


        boolean c_motion = m_click_value.motionValue();
        c_motion |= m_togglem_freq_value.motionValue();
        if (m_bg_bitmap_mode)
        {
            if (m_bg_bitmap != null)
            {
                m_paint.setAlpha((int)(getBgAlphaEffect()*255));
                canvas.drawBitmap(m_bg_bitmap,(this.getWidth()-m_bg_bitmap.getWidth())/2f,(this.getHeight()-m_bg_bitmap.getHeight())/2f,m_paint);
            }
        }
        else
        {
            int c_bn_color = m_fillet_color_n;
            if (m_click_value.getCurrentValue() >0.01f && m_click_value.getCurrentValue() < 1.99f)
            {
                float c_down_para = Math.abs(1.0f-m_click_value.getCurrentValue());
                int c_color_n_r = m_fillet_color_n>>16 &255;
                int c_color_n_g = m_fillet_color_n>>8 &255;
                int c_color_n_b = m_fillet_color_n &255;
                int c_color_n_a = m_fillet_color_n>>24 &255;

                int c_color_p_r = m_fillet_color_p>>16 &255;
                int c_color_p_g = m_fillet_color_p>>8 &255;
                int c_color_p_b = m_fillet_color_p &255;
                int c_color_p_a = m_fillet_color_p>>24 &255;

                int c_color_use_r = (int)(c_color_p_r*(1f-c_down_para)+c_color_n_r*c_down_para);
                int c_color_use_g = (int)(c_color_p_g*(1f-c_down_para)+c_color_n_g*c_down_para);
                int c_color_use_b = (int)(c_color_p_b*(1f-c_down_para)+c_color_n_b*c_down_para);
                int c_color_use_a = (int)(c_color_p_a*(1f-c_down_para)+c_color_n_a*c_down_para);

                //Log.d("TEST", "onDraw: "+c_color_use_a+" "+c_color_use_r+" "+c_color_use_g+" "+c_color_use_b);
                c_bn_color = (c_color_use_a << 24) | (c_color_use_r << 16) | (c_color_use_g << 8) | c_color_use_b;
            }
            m_paint.setColor(c_bn_color);
            m_paint.setStyle(Paint.Style.FILL);
            //Log.d("TEST","draw ");
            canvas.drawPath(m_path,m_paint);

            if (m_fillet_side_width > 0)
            {
                m_paint.setStyle(Paint.Style.STROKE);
                m_paint.setStrokeWidth(m_fillet_side_width);
                if (m_fillet_side_color == 0)
                {
                    m_paint.setColor(0xffffffff);
                }
                else
                {
                    m_paint.setColor(m_fillet_side_color);
                }
                canvas.drawPath(m_path,m_paint);
                m_paint.setStyle(Paint.Style.FILL);
                m_paint.setColor(0xffffffff);
            }

        }

        if (m_icon_bitmap != null)
        {
            m_paint.setAlpha((int)(getIconAlphaEffect()*255));
            canvas.drawBitmap(m_icon_bitmap,(this.getWidth()-m_icon_bitmap.getWidth())/2f,(this.getHeight()-m_icon_bitmap.getHeight())/2f,m_paint);
        }

        if (m_click_value.getCurrentValue() >0.01f && m_click_value.getCurrentValue() < 1.99f)
        {
            if (m_circle_in_blank)
            {
                canvas.save();
                canvas.clipPath(m_path);
            }
            m_paint.setAlpha((int) (Math.min(255, (1f-Math.abs(1f - m_click_value.getCurrentValue())) * 128)));
            canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, m_click_value.getCurrentValue()*m_touch_radius_inside, m_paint);
            if (m_circle_in_blank)
            {
                canvas.restore();
            }
        }

        if (m_togglem_freq_value.getCurrentValue()>0.01)
        {
            m_paint.setColor(0xffffffff);
            m_paint.setAlpha((int)(64*m_togglem_freq_value.getCurrentValue()));
            canvas.drawRect(0,0,10,this.getHeight(),m_paint);
        }
        if (c_motion)
        {
            this.invalidate();
        }
    }
    public void setButtonToggled(boolean c_toggle)
    {
        if (m_toggle_flag != c_toggle)
        {
            m_toggle_flag = c_toggle;
            if (m_toggle_flag)
            {
                Log.d("LeftSideButton","setButtonToggled ==true==");
                m_togglem_freq_value.gotoValue(1.0f,0.3f);
            }
            else
            {
                Log.d("LeftSideButton","setButtonToggled ==false==");
                m_togglem_freq_value.gotoValue(0.0f,0.1f);
            }
            this.invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_click_value.setValue(0.0f);
            m_click_value.gotoValue(1.0f);
            this.invalidate();
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
        {
            m_click_value.gotoValue(2.0f);
            this.invalidate();
        }
        return !this.hasOnClickListeners();
    }
}
