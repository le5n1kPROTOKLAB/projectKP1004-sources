package com.spd.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ShadowBuilder {
    private static Canvas m_shadow_canvas = new Canvas();
    private static Paint m_shadow_paint = new Paint();
    protected static PaintFlagsDrawFilter m_default_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    static {
        ColorMatrix c_saturation_matrix = new ColorMatrix();
        c_saturation_matrix.setSaturation(0);
        ColorMatrix c_lum_matrix = new ColorMatrix();
        c_lum_matrix.setScale(0,0,0,1f);
        ColorMatrix c_color_matrix = new ColorMatrix();
        c_color_matrix.postConcat(c_saturation_matrix);
        c_color_matrix.postConcat(c_lum_matrix);
        ColorMatrixColorFilter c_filter = new ColorMatrixColorFilter(c_color_matrix);
        m_shadow_paint.setAntiAlias(true);
        m_shadow_paint.setColorFilter(c_filter);
        m_shadow_canvas.setDrawFilter(m_default_filter);
    }
    public static Bitmap buildBitmapShadow(Context context , Bitmap c_bmp , int c_radius)
    {
        Bitmap c_shadow_bmp = Bitmap.createBitmap(c_bmp.getWidth()+c_radius*2,c_bmp.getHeight()+c_radius*2, Bitmap.Config.ARGB_8888);
        m_shadow_canvas.setBitmap(c_shadow_bmp);
        m_shadow_canvas.drawBitmap(c_bmp,c_radius,c_radius,m_shadow_paint);
        c_shadow_bmp = rsBlur(context,c_shadow_bmp,c_radius);
        return c_shadow_bmp;
    }
    public static Bitmap buildIconBitmapShadow(Context context , Bitmap c_bmp , int c_radius)
    {
        Bitmap c_shadow_bmp = Bitmap.createBitmap(c_bmp.getWidth(),c_bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c_shadow_canvas = new Canvas();
        Paint c_shadow_paint = new Paint();
        ColorMatrix c_saturation_matrix = new ColorMatrix();
        c_saturation_matrix.setSaturation(0);
        ColorMatrix c_lum_matrix = new ColorMatrix();
        c_lum_matrix.setScale(0,0,0,1f);
        ColorMatrix c_color_matrix = new ColorMatrix();
        c_color_matrix.postConcat(c_saturation_matrix);
        c_color_matrix.postConcat(c_lum_matrix);
        ColorMatrixColorFilter c_filter = new ColorMatrixColorFilter(c_color_matrix);
        c_shadow_paint.setAntiAlias(true);
        c_shadow_paint.setColorFilter(c_filter);
        c_shadow_canvas.setDrawFilter(m_default_filter);
        c_shadow_canvas.setBitmap(c_shadow_bmp);
        c_shadow_canvas.drawBitmap(c_bmp,0,0,c_shadow_paint);
        c_shadow_bmp = rsBlur(context,c_shadow_bmp,c_radius);
        return c_shadow_bmp;
    }
    public static Bitmap buildBitmapShadow(Context context , Bitmap c_bmp , Bitmap c_bmp_sub)
    {
        int c_radius = 8;
        int c_bmp_width = c_bmp.getWidth()>c_bmp_sub.getWidth()?c_bmp.getWidth():c_bmp_sub.getWidth();
        int c_bmp_height = c_bmp.getHeight()>c_bmp_sub.getHeight()?c_bmp.getHeight():c_bmp_sub.getHeight();

        Bitmap c_shadow_bmp = Bitmap.createBitmap(c_bmp_width+c_radius*2,c_bmp_height+c_radius*2, Bitmap.Config.ARGB_8888);
        m_shadow_canvas.setBitmap(c_shadow_bmp);
        float c_draw_x = c_bmp_width/2f-c_bmp.getWidth()/2f;
        float c_draw_y = c_bmp_height/2f-c_bmp.getHeight()/2f;
        m_shadow_canvas.drawBitmap(c_bmp,c_draw_x+c_radius,c_draw_y+c_radius,m_shadow_paint);
        c_draw_x = c_bmp_width/2f-c_bmp_sub.getWidth()/2f;
        c_draw_y = c_bmp_height/2f-c_bmp_sub.getHeight()/2f;
        m_shadow_canvas.drawBitmap(c_bmp_sub,c_draw_x+c_radius,c_draw_y+c_radius,m_shadow_paint);

        c_shadow_bmp = rsBlur(context,c_shadow_bmp,8);
        return c_shadow_bmp;
    }
    private static Bitmap rsBlur(Context context,Bitmap source,int c_radius){

        Bitmap inputBmp = source;
        //(1)
        RenderScript renderScript =  RenderScript.create(context);

        // Allocate memory for Renderscript to work with
        //(2)
        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(c_radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        //(8)
        renderScript.destroy();

        return inputBmp;
    }
}
