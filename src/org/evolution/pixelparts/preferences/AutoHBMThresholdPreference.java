/*
 * Copyright (C) 2016 The OmniROM Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.evolution.pixelparts.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import org.evolution.pixelparts.misc.Constants;

public class AutoHBMThresholdPreference extends CustomSeekBarPreference {

    private static int mMinVal = 2000;
    private static int mMaxVal = 60000;
    private static int mDefVal = 20000;

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
        mValue = Integer.parseInt(sharedPrefs.getString(Constants.KEY_AUTO_HBM_THRESHOLD, "20000"));

        setPersistent(false);
    }

    @Override
    protected void changeValue(int newValue) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putString(Constants.KEY_AUTO_HBM_THRESHOLD, String.valueOf(newValue)).commit();
    }
}
