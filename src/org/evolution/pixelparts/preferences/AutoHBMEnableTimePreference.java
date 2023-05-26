/*
 * Copyright (C) 2016 The OmniROM Project
                 2023 The Evolution X Project
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

public class AutoHBMEnableTimePreference extends CustomSeekBarPreference {

    private static int mMinVal = 0;
    private static int mMaxVal = 10;
    private static int mDefVal = 0;

    public AutoHBMEnableTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = 1;
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        mMinValue = mMinVal;
        mMaxValue = mMaxVal;
        mDefaultValueExists = true;
        mDefaultValue = mDefVal;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mValue = Integer.parseInt(sharedPrefs.getString(Constants.KEY_HBM_ENABLE_TIME, "0"));

        setPersistent(false);
    }

    @Override
    protected void changeValue(int newValue) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putString(Constants.KEY_HBM_ENABLE_TIME, String.valueOf(newValue)).commit();
    }
}
