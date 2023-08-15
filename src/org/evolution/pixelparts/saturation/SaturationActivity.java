/*
 * Copyright (C) 2023-2024 The Evolution X Project
 *               2018 Havoc-OS
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.saturation;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class SaturationActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG = "Saturation";

    private ViewPager mPreviewViewPager;
    private LinearLayout mSliderDotsPanel;
    private ImageView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saturation);

        setupImageSlider();

        getFragmentManager().beginTransaction().replace(R.id.saturation,
                new SaturationFragment(), TAG).commit();
    }

    private void setupImageSlider() {
        mPreviewViewPager = findViewById(R.id.preview);
        mSliderDotsPanel = findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        mPreviewViewPager.setAdapter(viewPagerAdapter);

        int dotCount = viewPagerAdapter.getCount();
        mDots = new ImageView[dotCount];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dotCount; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dot));
            mSliderDotsPanel.addView(dot, params);
            mDots[i] = dot;
        }

        if (dotCount > 0) {
            mDots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));
        }

        mPreviewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (ImageView dot : mDots) {
                    dot.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.inactive_dot));
                }
                if (position >= 0 && position < dotCount) {
                    mDots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
