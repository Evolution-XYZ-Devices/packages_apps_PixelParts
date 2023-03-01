/*
 * Copyright (C) 2016 The OmniROM Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.pixel.PixelParts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.preference.PreferenceManager;

import org.evolution.pixel.PixelParts.R;

public class AutoHBMThresholdPreference extends CustomSeekBarPreference {

    public AutoHBMThresholdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = 1000;
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        mMinValue = mMinVal;
        mMaxValue = mMaxVal;
        mDefaultValueExists = true;
        mDefaultValue = mDefVal;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mValue = Integer.parseInt(sharedPrefs.getString(PixelParts.KEY_HBM_AUTO_HBM_THRESHOLD, "20000"));

        setPersistent(false);
    }

    @Override
    protected void changeValue(int newValue) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putString(PixelParts.KEY_AUTO_HBM_THRESHOLD, String.valueOf(newValue)).commit();
    }
}
