/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixel.PixelParts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import java.util.Arrays;
import java.util.Random;

import com.plattysoft.leonids.ParticleSystem;

public class PixelParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = PixelParts.class.getSimpleName();

    private static final String KEY_POWER_EFFICIENT_WORKQUEUE = "power_efficient_workqueue";
    private static final String NODE_POWER_EFFICIENT_WORKQUEUE = "/sys/module/workqueue/parameters/power_efficient";

    private static final String KEY_FSYNC = "fsync";
    private static final String KEY_FSYNC_INFO = "fsync_info";
    private static final String NODE_FSYNC = "/sys/module/sync/parameters/fsync_enabled";

    private static final String KEY_USB2_SWITCH = "usb2_fast_charge";
    private static final String NODE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";

    private static final String KEY_MIC_GAIN = "mic_gain";
    private static final String NODE_MIC_GAIN = "/sys/kernel/sound_control/mic_gain";
    private static final String MIC_GAIN_DEFAULT = "0";

    private static final String KEY_SPEAKER_GAIN = "speaker_gain";
    private static final String NODE_SPEAKER_GAIN = "/sys/kernel/sound_control/speaker_gain";
    private static final String SPEAKER_GAIN_DEFAULT = "0";

    private Preference mFSyncInfo;

    private SwitchPreference mPowerEfficientWorkqueueModeSwitch;
    private SwitchPreference mFSyncModeSwitch;
    private SwitchPreference mUSB2FastChargeModeSwitch;

    private CustomSeekBarPreference mMicGainPreference;
    private CustomSeekBarPreference mSpeakerGainPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Power efficient workqueue switch
        mPowerEfficientWorkqueueModeSwitch = (SwitchPreference) findPreference(KEY_POWER_EFFICIENT_WORKQUEUE);
        if (Utils.fileWritable(NODE_POWER_EFFICIENT_WORKQUEUE)) {
            mPowerEfficientWorkqueueModeSwitch.setEnabled(true);
            mPowerEfficientWorkqueueModeSwitch.setChecked(sharedPrefs.getBoolean(KEY_POWER_EFFICIENT_WORKQUEUE,
                Utils.getFileValueAsBoolean(NODE_POWER_EFFICIENT_WORKQUEUE, false)));
            mPowerEfficientWorkqueueModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mPowerEfficientWorkqueueModeSwitch.setEnabled(false);
        }

        // Fsync switch
        mFSyncModeSwitch = (SwitchPreference) findPreference(KEY_FSYNC);
        if (Utils.fileWritable(NODE_FSYNC)) {
            mFSyncModeSwitch.setEnabled(true);
            mFSyncModeSwitch.setChecked(sharedPrefs.getBoolean(KEY_FSYNC,
                Utils.getFileValueAsBoolean(NODE_FSYNC, false)));
            mFSyncModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mFSyncModeSwitch.setEnabled(false);
        }

        // USB 2.0 force fast charge
        mUSB2FastChargeModeSwitch = (SwitchPreference) findPreference(KEY_USB2_SWITCH);
        if (Utils.fileWritable(NODE_FAST_CHARGE)) {
            mUSB2FastChargeModeSwitch.setEnabled(true);
            mUSB2FastChargeModeSwitch.setChecked(sharedPrefs.getBoolean(KEY_USB2_SWITCH,
                Utils.getFileValueAsBoolean(NODE_FAST_CHARGE, false)));
            mUSB2FastChargeModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mUSB2FastChargeModeSwitch.setEnabled(false);
        }

        // Mic gain preference
        mMicGainPreference =  (CustomSeekBarPreference) findPreference(KEY_MIC_GAIN);
        if (Utils.fileWritable(NODE_MIC_GAIN)) {
            mMicGainPreference.setValue(sharedPrefs.getInt(KEY_MIC_GAIN,
                Integer.parseInt(Utils.getFileValue(NODE_MIC_GAIN, MIC_GAIN_DEFAULT))));
            mMicGainPreference.setOnPreferenceChangeListener(this);
        } else {
            mMicGainPreference.setEnabled(false);
        }

        // Speaker gain preference
        mSpeakerGainPreference =  (CustomSeekBarPreference) findPreference(KEY_SPEAKER_GAIN);
        if (Utils.fileWritable(NODE_SPEAKER_GAIN)) {
            mSpeakerGainPreference.setValue(sharedPrefs.getInt(KEY_SPEAKER_GAIN,
                Integer.parseInt(Utils.getFileValue(NODE_SPEAKER_GAIN, SPEAKER_GAIN_DEFAULT))));
            mSpeakerGainPreference.setOnPreferenceChangeListener(this);
        } else {
            mSpeakerGainPreference.setEnabled(false);
        }

        // EasterEgg (FSYNC_INFO)
        mFSyncInfo = (Preference)findPreference(KEY_FSYNC_INFO);
        mFSyncInfo.setOnPreferenceClickListener(preference -> {

            Random rand =new Random();
             int firstRandom = rand.nextInt(91-0);
             int secondRandom = rand.nextInt(181-90)+90;
             int thirdRandom = rand.nextInt(181-0);

            Drawable evo = getResources().getDrawable(R.drawable.evo,null);
             int randomColor;
             randomColor = Color.rgb(
             Color.red(rand.nextInt(0xFFFFFF)),
             Color.green(rand.nextInt(0xFFFFFF)),
             Color.blue(rand.nextInt(0xFFFFFF)));
             evo.setTint(randomColor);

            ParticleSystem ps = new ParticleSystem(getActivity(),50,evo,2000);
             ps.setScaleRange(0.7f,1.3f);
             ps.setSpeedRange(0.1f,0.25f);
             ps.setAcceleration(0.0001f,thirdRandom);
             ps.setRotationSpeedRange(firstRandom,secondRandom);
             ps.setFadeOut(300);
             ps.oneShot(this.getView(),50);
        return true;
        });
    }

    private void registerPreferenceListener(String key) {
        Preference p = findPreference(key);
        p.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPowerEfficientWorkqueueModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_POWER_EFFICIENT_WORKQUEUE, enabled).commit();
            Utils.writeValue(NODE_POWER_EFFICIENT_WORKQUEUE, enabled ? "1" : "0");
            return true;
        } else if (preference == mFSyncModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_FSYNC, enabled).commit();
    	    Utils.writeValue(NODE_FSYNC, enabled ? "1" : "0");
            return true;
        } else if (preference == mUSB2FastChargeModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(KEY_USB2_SWITCH, enabled).commit();
    	    Utils.writeValue(NODE_FAST_CHARGE, enabled ? "1" : "0");
            return true;
        } else if (preference == mMicGainPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_MIC_GAIN, value).commit();
            Utils.writeValue(NODE_MIC_GAIN, String.valueOf(value));
            return true;
        } else if (preference == mSpeakerGainPreference) {
            int value = Integer.parseInt(newValue.toString());
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putInt(KEY_SPEAKER_GAIN, value).commit();
            Utils.writeValue(NODE_SPEAKER_GAIN, String.valueOf(value));
            return true;
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
    }

    // Power efficient workqueue switch
    public static void restorePowerEfficientWorkqueueSetting(Context context) {
        if (Utils.fileWritable(NODE_POWER_EFFICIENT_WORKQUEUE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(KEY_POWER_EFFICIENT_WORKQUEUE,
                Utils.getFileValueAsBoolean(NODE_POWER_EFFICIENT_WORKQUEUE, false));
            Utils.writeValue(NODE_POWER_EFFICIENT_WORKQUEUE, value ? "0" : "1");
        }
    }

    // Fsync switch
    public static void restoreFSyncSetting(Context context) {
        if (Utils.fileWritable(NODE_FSYNC)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(KEY_FSYNC,
                Utils.getFileValueAsBoolean(NODE_FSYNC, false));
            Utils.writeValue(NODE_FSYNC, value ? "1" : "0");
        }
    }

    // USB 2.0 force fast charge
    public static void restoreFastChargeSetting(Context context) {
        if (Utils.fileWritable(NODE_FAST_CHARGE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(KEY_USB2_SWITCH,
                Utils.getFileValueAsBoolean(NODE_FAST_CHARGE, false));
            Utils.writeValue(NODE_FAST_CHARGE, value ? "1" : "0");
        }
    }

    // Mic gain preference
    public static void restoreMicGainSetting(Context context) {
        if (Utils.fileWritable(NODE_MIC_GAIN)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_MIC_GAIN,
                Integer.parseInt(Utils.getFileValue(NODE_MIC_GAIN, MIC_GAIN_DEFAULT)));
            Utils.writeValue(NODE_MIC_GAIN, String.valueOf(value));
        }
    }

    // Speaker gain preference
    public static void restoreSpeakerGainSetting(Context context) {
        if (Utils.fileWritable(NODE_SPEAKER_GAIN)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(KEY_SPEAKER_GAIN,
                Integer.parseInt(Utils.getFileValue(NODE_SPEAKER_GAIN, SPEAKER_GAIN_DEFAULT)));
            Utils.writeValue(NODE_SPEAKER_GAIN, String.valueOf(value));
        }
    }
}
