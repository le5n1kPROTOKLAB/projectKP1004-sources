package com.example.carinfoapp;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.carinfoapp.databinding.ActivityMainBinding;
import com.example.carinfoapp.fragment.DistributionBoxFragment;
import com.gyf.immersionbar.ImmersionBar;
import com.senptec.common.ui.util.DensityHelper;
import com.senptec.common.ui.util.ScreenUtil;

import java.util.HashMap;

import q.rorbin.verticaltablayout.widget.ITabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class MainActivity extends AppCompatActivity {

    private String[] names;
    public HashMap<Integer, ActivityMainBinding> bindingHashMap = new HashMap<>();
    private ActivityMainBinding mBinding;
    private int selectItem = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        names = new String[]{getString(R.string.distribution_box_information),};
        updateUI();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 记录上次选中的position
        selectItem = mBinding.viewPager.getCurrentItem();
        updateUI();
        mBinding.vtTable.setTabSelected(selectItem);
        mBinding.vtTable.getTabAt(selectItem);
    }

    private void updateUI() {
        int orientation = getResources().getConfiguration().orientation;
        DensityHelper.setCustomDensity(this);
        ImmersionBar.with(this).fullScreen(true).init();
        mBinding = bindingHashMap.get(orientation);
        if (mBinding == null) {
            mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this), null, false);
            bindingHashMap.put(orientation, mBinding);
            mBinding.viewPager.setAdapter(getAdapter());
            mBinding.viewPager.setCanSwipe(false);
            mBinding.vtTable.setupWithViewPager(mBinding.viewPager);
            for (int i = 0; i < mBinding.vtTable.getTabCount(); i++) {
                TabView tab = mBinding.vtTable.getTabAt(i);
                if (tab == null) continue;
                tab.setBackground(null);
                ITabView.TabTitle build = new ITabView.TabTitle.Builder()
                        .setTextColor(
                                Color.parseColor("#0381ff"),
                                getColor(R.color.text_gray)
                        )
                        .setContent(tab.getTitle().getContent())
                        .build();
                tab.setTitle(build);
                TextView textView = tab.getTitleView();
                TabView.LayoutParams layoutParams = (TabView.LayoutParams) textView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.leftMargin = getResources().getDimensionPixelOffset(R.dimen.tab_left);
                layoutParams.rightMargin = getResources().getDimensionPixelOffset(R.dimen.tab_left);
                textView.requestLayout();
                textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.text_size));
            }
        }
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        ScreenUtil.setWallPaper(rootView);
    }

    @NonNull
    private FragmentPagerAdapter getAdapter() {
        return new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new DistributionBoxFragment();
            }

            @Override
            public int getCount() {
                return names.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return names[position];
            }

            // 重写id让横竖屏的Fragment使用不同的itemId
            @Override
            public long getItemId(int position) {
                int orientation = getResources().getConfiguration().orientation;
                return orientation * 10L + position;
            }
        };
    }
}