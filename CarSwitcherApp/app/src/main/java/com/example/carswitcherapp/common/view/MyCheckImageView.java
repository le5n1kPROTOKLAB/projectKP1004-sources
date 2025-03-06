package com.example.carswitcherapp.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.carswitcherapp.R;
import com.example.carswitcherapp.databinding.ViewMyCheckBinding;

public class MyCheckImageView extends FrameLayout {
    private ViewMyCheckBinding mBinding;
    // 最大级别
    private int maxLevel = 1;
    // 当前级别
    private int level;
    private String name;
    private Drawable iconDrawable;
    private OnLevelChangedListener listener;
    private long mLongPressTime = 3000;
    private Handler mHandler;
    private Runnable mLongPressRunnable;

    public MyCheckImageView(Context context) {
        this(context, null);
    }

    public MyCheckImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyCheckImageView);
        Drawable checkedImg = a.getDrawable(R.styleable.MyCheckImageView_checkedImg);
        name = a.getString(R.styleable.MyCheckImageView_text);
        maxLevel = a.getInteger(R.styleable.MyCheckImageView_maxLevel, 1);
        level = a.getInteger(R.styleable.MyCheckImageView_level, 0);
        a.recycle();

        mBinding = ViewMyCheckBinding.inflate(LayoutInflater.from(context), this, true);
        setName(name);
        setLevel();
        setBackground(checkedImg);
        mHandler = new Handler();
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                level++;
                if (level > maxLevel) {
                    level = 0;
                }
                setLevel();
            }
        };
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                level++;
//                if (level > maxLevel) {
//                    level = 0;
//                }
//                setLevel();
//            }
//        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.postDelayed(mLongPressRunnable, getLongPressTime());
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.removeCallbacks(mLongPressRunnable);
                        if (null != mActionUpCallback) {
                            mActionUpCallback.onActionUp();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private ActionUpCallback mActionUpCallback;

    public void setActionUpEvent(ActionUpCallback actionUpCallback) {
        this.mActionUpCallback = actionUpCallback;
    }

    public interface ActionUpCallback {
        void onActionUp();
    }

    public long getLongPressTime() {
        return mLongPressTime;
    }

    public void setLongPressTime(long longPressTime) {
        this.mLongPressTime = longPressTime;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        if (level == 0) {
            setBackgroundLevel(level);
        }
    }

    private Drawable[] drawables;

    public void setIconDrawable(Drawable drawable) {
        this.iconDrawable = drawable;
        mBinding.icon.setBackground(drawable);
    }

    /**
     * 设置级别
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setLevel(int level) {
        this.level = level;
        setLevel();
    }

    private void setLevel() {
        setBackgroundLevel(level);
        setIconLevel();
        if (listener != null) {
            listener.onLevelChanged(level);
        }
    }

    private void setBackgroundLevel(int level) {
        int drawableRes = maxLevel == 1 ? R.drawable.switch_bg : R.drawable.switch_bg_2;
        switch (level) {
            case 1:
                drawableRes = maxLevel > 1 ? R.drawable.switch_on1 : R.drawable.switch_on;
                break;
            case 2:
                drawableRes = R.drawable.switch_on2;
                break;
        }
        setBackground(getResources().getDrawable(drawableRes));
    }

    private void setIconLevel() {
        switch (level) {
            case 0:
                mBinding.icon.setSelected(false);
                mBinding.text.setSelected(false);
                break;
            case 1:
            case 2:
                mBinding.icon.setSelected(true);
                mBinding.text.setSelected(true);
                break;
        }
    }


    @Override
    public void setBackground(Drawable drawableRes) {
        mBinding.getRoot().setBackground(drawableRes);
    }

    /**
     * 设置监听
     */
    public void setOnLevelChangedListener(OnLevelChangedListener listener) {
        this.listener = listener;

    }

    public void setName(String name) {
        mBinding.text.setText(name);
    }

    /**
     * 状态改变的监听
     */
    public interface OnLevelChangedListener {
        void onLevelChanged(int newLevel);
    }
}