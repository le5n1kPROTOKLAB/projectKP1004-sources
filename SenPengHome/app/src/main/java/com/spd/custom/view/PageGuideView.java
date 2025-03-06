package com.spd.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PageGuideView extends View {
    public PageGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        m_paint.setColor(0xffffffff);
    }

    private Paint m_paint;
    private int m_postion;
    private int m_page_num = 1;
    private float m_point_step = 20f;
    private float m_point_radius = 4f;

    public void setPageGuide(int c_posiont, int c_page_num) {
        m_postion = c_posiont;
        m_page_num = c_page_num;
        if (c_page_num <= 1) {
            m_page_num = 0;
        }
        if (m_postion < 0) {
            m_postion = 0;
        } else if (m_postion >= m_page_num) {
            m_postion = m_page_num - 1;
        }
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(ShadowBuilder.m_default_filter);
        // drawCircle(canvas);
        // TODO 页面指示器
        drawArc(canvas);
    }

    private float arc_step = 28f;
    private float arc_width = 20f;
    private float arc_radius = 2f;

    private void drawArc(Canvas canvas) {
        float rectWidth = arc_width - arc_radius * 2;
        // 校准参考点
        float c_point_start_x = canvas.getWidth() / 2f - (m_page_num - 1) * arc_step / 2f - rectWidth / 2;
        for (int i = 0; i < m_page_num; i++) {
            float c_point_draw_x = c_point_start_x + i * arc_step;
            float c_point_draw_x_left = c_point_draw_x - arc_radius;
            float c_point_draw_x_right = c_point_start_x + i * arc_step + arc_radius;
            float c_point_draw_y = canvas.getHeight() / 2f;
            float c_point_draw_y_top = c_point_draw_y - arc_radius;
            float c_point_draw_y_bottom = c_point_draw_y + arc_radius;
            if (i == m_postion) {
                m_paint.setAlpha(200);
            } else {
                m_paint.setAlpha(100);
            }
            RectF rectF = new RectF(c_point_draw_x_left, c_point_draw_y_top, c_point_draw_x_right, c_point_draw_y_bottom);
            // 画左半圆
            canvas.drawArc(rectF, 90f, 180f, false, m_paint);
            // 画矩形
            canvas.drawRect(new RectF(c_point_draw_x, c_point_draw_y_top, c_point_draw_x + rectWidth, c_point_draw_y_bottom), m_paint);
            // 画右半圆
            rectF.left += rectWidth;
            rectF.right += rectWidth;
            canvas.drawArc(rectF, 270f, 180f, false, m_paint);
        }
    }

    private void drawCircle(Canvas canvas) {
        float c_point_start_x = canvas.getWidth() / 2f - (m_page_num - 1) * m_point_step / 2f;
        for (int i = 0; i < m_page_num; i++) {
            float c_point_draw_x = c_point_start_x + i * m_point_step;
            float c_point_draw_y = canvas.getHeight() / 2f;
            if (i == m_postion) {
                m_paint.setAlpha(200);
            } else {
                m_paint.setAlpha(100);
            }
            canvas.drawCircle(c_point_draw_x, c_point_draw_y, m_point_radius, m_paint);
        }
    }

    public interface PageGuideOrder {
        void onPageGuideChanged(int c_posiont, int c_page_num);
    }
}
