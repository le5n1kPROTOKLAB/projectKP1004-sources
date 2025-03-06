package com.senptec.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.senptec.control.R;

public class CheckImageView extends AppCompatImageView {

    private Drawable checkedImg; //为选中图标
    private Drawable unCheckedImg; //已选中图标
    private boolean isChecked; //是否选中
    private OnCheckedChangeListener listener;

    public CheckImageView(Context context) {
        this(context,null);
    }

    public CheckImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckImageView);
        isChecked = a.getBoolean(R.styleable.CheckImageView_isChecked,false);
        checkedImg = a.getDrawable(R.styleable.CheckImageView_checkedImg);
        unCheckedImg = a.getDrawable(R.styleable.CheckImageView_uncheckedImg);

        setCheckedDrawable();

    }


    /**
     * 设置选中或者未选中状态
     * @param isChecked
     */
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
        setCheckedDrawable();
        if(listener != null){
            listener.onCheckedChanged(isChecked);
        }
    }

    /**
     * 设置背景图片
     */
    private void setCheckedDrawable(){
        if(isChecked){
            setImageDrawable(checkedImg);
        }else{
            setImageDrawable(unCheckedImg);
        }
    }

    /**
     * 返回按钮当前选中状态
     * @return
     */
    public boolean isChecked(){
        return isChecked;
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener = listener;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(!isChecked);
            }
        });
    }

    /**
     * 状态改变的监听
     */
    public interface OnCheckedChangeListener{
        void onCheckedChanged(boolean isChecked);
    }
}