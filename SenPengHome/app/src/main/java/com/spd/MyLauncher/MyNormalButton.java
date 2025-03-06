package com.spd.MyLauncher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.spd.home.R;

public class MyNormalButton extends View implements View.OnTouchListener{
    private Bitmap m_bg_bitmap;
    private Bitmap m_icon_bitmap;
    protected Paint m_paint;
    private float m_touch_radius_inside;

    private MotionParameter m_click_value = new MotionParameter();
    protected static PaintFlagsDrawFilter m_default_filter;


    public MyNormalButton(Context context, int c_bg_drawable_id , int c_icon_drawable,int c_touch_radius) {
        super(context);
        if (c_bg_drawable_id != 0)
        {
            m_bg_bitmap = BitmapFactory.decodeResource(context.getResources(),c_bg_drawable_id);
        }
        if (c_icon_drawable != 0)
        {
            m_icon_bitmap = BitmapFactory.decodeResource(context.getResources(),c_icon_drawable);
        }
        m_touch_radius_inside = c_touch_radius;
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        this.setOnTouchListener(this);
        m_paint.setColor(Color.WHITE);
        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
    }
    public MyNormalButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.CustomView
                , 0, 0);
        int c_bg_resource = array.getResourceId(R.styleable.CustomView_bg_drawable, 0);
        if (c_bg_resource != 0)
        {
            m_bg_bitmap = BitmapFactory.decodeResource(context.getResources(),c_bg_resource);
        }

        int c_icon_resource = array.getResourceId(R.styleable.CustomView_icon_drawable, 0);
        if (c_icon_resource != 0)
        {
            m_icon_bitmap = BitmapFactory.decodeResource(context.getResources(),c_icon_resource);
        }

        m_touch_radius_inside = array.getFloat(R.styleable.CustomView_touch_circle_radius,40f);
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        this.setOnTouchListener(this);
        m_paint.setColor(Color.WHITE);
        if (m_default_filter == null)
        {
            m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        }
    }

    public void setIconDrawable(int c_resource)
    {
        if (c_resource != 0)
        {
            m_icon_bitmap = BitmapFactory.decodeResource(getContext().getResources(),c_resource);
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
        if (m_bg_bitmap != null)
        {
            m_paint.setAlpha((int)(getBgAlphaEffect()*255));
            canvas.drawBitmap(m_bg_bitmap,0,0,m_paint);
        }
        if (m_icon_bitmap != null)
        {
            m_paint.setAlpha((int)(getIconAlphaEffect()*255));
            canvas.drawBitmap(m_icon_bitmap,(this.getWidth()-m_icon_bitmap.getWidth())/2f,(this.getHeight()-m_icon_bitmap.getHeight())/2f,m_paint);
        }
        if (m_click_value.getCurrentValue() >0.01f && m_click_value.getCurrentValue() < 1.99f)
        {
            m_paint.setAlpha((int) (Math.min(255, (1f-Math.abs(1f - m_click_value.getCurrentValue())) * 128)));
            canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, m_click_value.getCurrentValue()*m_touch_radius_inside, m_paint);
        }
        if (c_motion)
        {
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
