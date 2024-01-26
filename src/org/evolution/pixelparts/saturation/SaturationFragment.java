/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.saturation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.settingslib.widget.LayoutPreference;

import java.io.IOException;
import java.util.Arrays;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.CustomSeekBarPreference;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.TileUtils;

public class SaturationFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    private View mViewArrowPrevious;
    private View mViewArrowNext;
    private ViewPager mViewPager;

    private ImageView[] mDotIndicators;
    private View[] mViewPagerImages;

    private CustomSeekBarPreference mSaturationPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.saturation, rootKey);
        setHasOptionsMenu(true);

        LayoutPreference preview = findPreference(Constants.KEY_SATURATION_PREVIEW);
        addViewPager(preview);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mSaturationPreference = (CustomSeekBarPreference) findPreference(Constants.KEY_SATURATION);
        mSaturationPreference.setOnPreferenceChangeListener(this);
        int seekBarValue = sharedPrefs.getInt(Constants.KEY_SATURATION, 100);
        updateSaturation(seekBarValue);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.saturation_menu, menu);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSaturationPreference) {
            int seekBarValue = (Integer) newValue;
            updateSaturation(seekBarValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_tile) {
            TileUtils.requestAddTileService(
                    getContext(),
                    SaturationTileService.class,
                    R.string.saturation_title,
                    R.drawable.ic_saturation_tile
            );
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void updateSaturation(int seekBarValue) {
        float saturation;
        if (seekBarValue == 100) {
            saturation = 1.001f;
        } else {
            saturation = seekBarValue / 100.0f;
        }

        try {
            Runtime.getRuntime().exec("service call SurfaceFlinger 1022 f " + saturation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restoreSaturationSetting(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int seekBarValue = sharedPrefs.getInt(Constants.KEY_SATURATION, 100);
        updateSaturation(seekBarValue);
    }

    void addViewPager(LayoutPreference preview) {
        mViewPager = preview.findViewById(R.id.viewpager);

        int[] drawables = new int[]{
                R.drawable.image_preview1,
                R.drawable.image_preview2,
                R.drawable.image_preview3
        };

        mViewPagerImages = new View[drawables.length];

        for (int idx = 0; idx < drawables.length; idx++) {
            mViewPagerImages[idx] = getLayoutInflater().inflate(R.layout.image_layout, null);
            ImageView imageView = mViewPagerImages[idx].findViewById(R.id.imageView);
            imageView.setImageResource(drawables[idx]);
        }

        mViewPager.setAdapter(new ImagePreviewPagerAdapter(mViewPagerImages));

        mViewArrowPrevious = preview.findViewById(R.id.arrow_previous);
        mViewArrowPrevious.setOnClickListener(v -> mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true));

        mViewArrowNext = preview.findViewById(R.id.arrow_next);
        mViewArrowNext.setOnClickListener(v -> mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true));

        mViewPager.addOnPageChangeListener(createPageListener());

        final ViewGroup viewGroup = preview.findViewById(R.id.viewGroup);
        mDotIndicators = new ImageView[mViewPagerImages.length];
        for (int i = 0; i < mViewPagerImages.length; i++) {
            final ImageView imageView = new ImageView(getContext());
            final ViewGroup.MarginLayoutParams lp =
                    new ViewGroup.MarginLayoutParams(12, 12);
            lp.setMargins(6, 0, 6, 0);
            imageView.setLayoutParams(lp);
            mDotIndicators[i] = imageView;

            viewGroup.addView(mDotIndicators[i]);
        }

        updateIndicator(mViewPager.getCurrentItem());
    }

    private ViewPager.OnPageChangeListener createPageListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0) {
                    for (View mViewPagerImage : mViewPagerImages) {
                        mViewPagerImage.setVisibility(View.VISIBLE);
                    }
                } else {
                    mViewPagerImages[position].setContentDescription(
                            getContext().getString(R.string.image_preview_content_description));
                    updateIndicator(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }

    private void updateIndicator(int position) {
        for (int i = 0; i < mViewPagerImages.length; i++) {
            if (position == i) {
                mDotIndicators[i].setBackgroundResource(
                        R.drawable.ic_image_preview_page_indicator_focused);

                mViewPagerImages[i].setVisibility(View.VISIBLE);
            } else {
                mDotIndicators[i].setBackgroundResource(
                        R.drawable.ic_image_preview_page_indicator_unfocused);

                mViewPagerImages[i].setVisibility(View.INVISIBLE);
            }
        }

        if (position == 0) {
            mViewArrowPrevious.setVisibility(View.INVISIBLE);
            mViewArrowNext.setVisibility(View.VISIBLE);
        } else if (position == (mViewPagerImages.length - 1)) {
            mViewArrowPrevious.setVisibility(View.VISIBLE);
            mViewArrowNext.setVisibility(View.INVISIBLE);
        } else {
            mViewArrowPrevious.setVisibility(View.VISIBLE);
            mViewArrowNext.setVisibility(View.VISIBLE);
        }
    }

    static class ImagePreviewPagerAdapter extends PagerAdapter {
        private final View[] mPageViewList;

        ImagePreviewPagerAdapter(View[] pageViewList) {
            mPageViewList = pageViewList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mPageViewList[position] != null) {
                container.removeView(mPageViewList[position]);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPageViewList[position]);
            return mPageViewList[position];
        }

        @Override
        public int getCount() {
            return mPageViewList.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }
    }
}
