/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixel.PixelParts.kcal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;

import org.evolution.pixel.PixelParts.CustomSeekBarPreference;
import org.evolution.pixel.PixelParts.R;
import org.evolution.pixel.PixelParts.Utils;

public class Kcal extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = Kcal.class.getSimpleName();

    private static final String KEY_RED = "red";
    private static final String NODE_RED = "/sys/module/msm_drm/parameters/kcal_red";
    private static final String RED_DEFAULT = "256";

    private static final String KEY_GREEN = "green";
    private static final String NODE_GREEN = "/sys/module/msm_drm/parameters/kcal_green";
    private static final String GREEN_DEFAULT = "256";

    private static final String KEY_BLUE = "blue";
    private static final String NODE_BLUE = "/sys/module/msm_drm/parameters/kcal_blue";
    private static final String BLUE_DEFAULT = "256";

    private static final String KEY_SATURATION = "saturation";
    private static final String NODE_SATURATION = "/sys/module/msm_drm/parameters/kcal_sat";
    private static final String SATURATION_DEFAULT = "255";

    private static final String KEY_CONTRAST = "contrast";
    private static final String NODE_CONTRAST = "/sys/module/msm_drm/parameters/kcal_cont";
    private static final String CONTRAST_DEFAULT = "255";

    private static final String KEY_HUE = "hue";
    private static final String NODE_HUE = "/sys/module/msm_drm/parameters/kcal_hue";
    private static final String HUE_DEFAULT = "0";

    private static final String KEY_VALUE = "value";
    private static final String NODE_VALUE = "/sys/module/msm_drm/parameters/kcal_val";
    private static final String VALUE_DEFAULT = "255";

    private CustomSeekBarPreference mRedPreference;
    private CustomSeekBarPreference mGreenPreference;
    private CustomSeekBarPreference mBluePreference;
    private CustomSeekBarPreference mSaturationPreference;
    private CustomSeekBarPreference mContrastPreference;
    private CustomSeekBarPreference mHuePreference;
    private CustomSeekBarPreference mValuePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kcal);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Red preference
        mRedPreference =  (CustomSeekBarPreference) findPreference(KEY_RED);
        if (Utils.fileWritable(NODE_RED)) {
            mRedPreference.setValue(sharedPrefs.getInt(KEY_RED,
                Integer.parseInt(Utils.getFileValue(NODE_RED, RED_DEFAULT))));
            mRedPreference.setOnPreferenceChangeListener(this);
        } else {
            mRedPreference.setEnabled(false);
        }

        // Green preference
        mGreenPreference =  (CustomSeekBarPreference) findPreference(KEY_GREEN);
        if (Utils.fileWritable(NODE_GREEN)) {
            mGreenPreference.setValue(sharedPrefs.getInt(KEY_GREEN,
                Integer.parseInt(Utils.getFileValue(NODE_GREEN, GREEN_DEFAULT))));
            mGreenPreference.setOnPreferenceChangeListener(this);
        } else {
            mGreenPreference.setEnabled(false);
        }

        // Blue preference
        mBluePreference =  (CustomSeekBarPreference) findPreference(KEY_BLUE);
        if (Utils.fileWritable(NODE_BLUE)) {
            mBluePreference.setValue(sharedPrefs.getInt(KEY_BLUE,
                Integer.parseInt(Utils.getFileValue(NODE_BLUE, BLUE_DEFAULT))));
            mBluePreference.setOnPreferenceChangeListener(this);
        } else {
            mBluePreference.setEnabled(false);
        }

        // Saturation preference
        mSaturationPreference =  (CustomSeekBarPreference) findPreference(KEY_SATURATION);
        if (Utils.fileWritable(NODE_SATURATION)) {
            mSaturationPreference.setValue(sharedPrefs.getInt(KEY_SATURATION,
                Integer.parseInt(Utils.getFileValue(NODE_SATURATION, SATURATION_DEFAULT))));
            mSaturationPreference.setOnPreferenceChangeListener(this);
        } else {
            mSaturationPreference.setEnabled(false);
        }

        // Contrast preference
        mContrastPreference =  (CustomSeekBarPreference) findPreference(KEY_CONTRAST);
        if (Utils.fileWritable(NODE_CONTRAST)) {
            mContrastPreference.setValue(sharedPrefs.getInt(KEY_CONTRAST,
                Integer.parseInt(Utils.getFileValue(NODE_CONTRAST, CONTRAST_DEFAULT))));
            mContrastPreference.setOnPreferenceChangeListener(this);
        } else {
            mContrastPreference.setEnabled(false);
        }

        // Hue preference
        mHuePreference =  (CustomSeekBarPreference) findPreference(KEY_HUE);
        if (Utils.fileWritable(NODE_HUE)) {
            mHuePreference.setValue(sharedPrefs.getInt(KEY_HUE,
                Integer.parseInt(Utils.getFileValue(NODE_HUE, HUE_DEFAULT))));
            mHuePreference.setOnPreferenceChangeListener(this);
        } else {
            mHuePreference.setEnabled(false);
        }

        // Value preference
        mValuePreference =  (CustomSeekBarPreference) findPreference(KEY_VALUE);
        if (Utils.fileWritable(NODE_VALUE)) {
            mValuePreference.setValue(sharedPrefs.getInt(KEY_VALUE,
                Integer.parseInt(Utils.getFileValue(NODE_VALUE, VALUE_DEFAULT))));
            mValuePreference.setOnPreferenceChangeListener(this);
        } else {
            mValuePreference.setEnabled(false);
        }
    }

    private void registerPreferenceListener(String key) {
        Preference p = findPreference(key);
        p.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRedPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_RED, value).commit();
            Utils.writeValue(NODE_RED, String.valueOf(value));
            return true;
        } else if (preference == mGreenPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_GREEN, value).commit();
            Utils.writeValue(NODE_GREEN, String.valueOf(value));
            return true;
        } else if (preference == mBluePreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_BLUE, value).commit();
            Utils.writeValue(NODE_BLUE, String.valueOf(value));
            return true;
        } else if (preference == mSaturationPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_SATURATION, value).commit();
            Utils.writeValue(NODE_SATURATION, String.valueOf(value));
            return true;
        } else if (preference == mContrastPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_CONTRAST, value).commit();
            Utils.writeValue(NODE_CONTRAST, String.valueOf(value));
            return true;
        } else if (preference == mHuePreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_HUE, value).commit();
            Utils.writeValue(NODE_HUE, String.valueOf(value));
            return true;
        } else if (preference == mValuePreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_VALUE, value).commit();
            Utils.writeValue(NODE_VALUE, String.valueOf(value));
            return true;
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
    }

    // Red preference
    public static void restoreRedSetting(Context context) {
        if (Utils.fileWritable(NODE_RED)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_RED,
                Integer.parseInt(Utils.getFileValue(NODE_RED, RED_DEFAULT)));
            Utils.writeValue(NODE_RED, String.valueOf(value));
        }
    }

    // Green preference
    public static void restoreGreenSetting(Context context) {
        if (Utils.fileWritable(NODE_GREEN)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_GREEN,
                Integer.parseInt(Utils.getFileValue(NODE_GREEN, GREEN_DEFAULT)));
            Utils.writeValue(NODE_GREEN, String.valueOf(value));
        }
    }

    // Blue preference
    public static void restoreBlueSetting(Context context) {
        if (Utils.fileWritable(NODE_BLUE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_BLUE,
                Integer.parseInt(Utils.getFileValue(NODE_BLUE, BLUE_DEFAULT)));
            Utils.writeValue(NODE_BLUE, String.valueOf(value));
        }
    }

    // Saturation preference
    public static void restoreSaturationSetting(Context context) {
        if (Utils.fileWritable(NODE_SATURATION)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_SATURATION,
                Integer.parseInt(Utils.getFileValue(NODE_SATURATION, SATURATION_DEFAULT)));
            Utils.writeValue(NODE_SATURATION, String.valueOf(value));
        }
    }

    // Contrast preference
    public static void restoreContrastSetting(Context context) {
        if (Utils.fileWritable(NODE_CONTRAST)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_CONTRAST,
                Integer.parseInt(Utils.getFileValue(NODE_CONTRAST, CONTRAST_DEFAULT)));
            Utils.writeValue(NODE_CONTRAST, String.valueOf(value));
        }
    }

    // Hue preference
    public static void restoreHueSetting(Context context) {
        if (Utils.fileWritable(NODE_HUE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_HUE,
                Integer.parseInt(Utils.getFileValue(NODE_HUE, HUE_DEFAULT)));
            Utils.writeValue(NODE_HUE, String.valueOf(value));
        }
    }

    // Value preference
    public static void restoreValueSetting(Context context) {
        if (Utils.fileWritable(NODE_VALUE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_VALUE,
                Integer.parseInt(Utils.getFileValue(NODE_VALUE, VALUE_DEFAULT)));
            Utils.writeValue(NODE_VALUE, String.valueOf(value));
        }
    }
}
