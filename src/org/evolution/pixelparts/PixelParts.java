/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.services.HBMService;
import org.evolution.pixelparts.utils.AutoHBMUtils;
import org.evolution.pixelparts.utils.Utils;

public class PixelParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = PixelParts.class.getSimpleName();

    // Power efficient workqueue switch
    private SwitchPreference mPowerEfficientWorkqueueModeSwitch;

    // High brightness mode switches
    private SwitchPreference mHBMSwitch;
    private SwitchPreference mAutoHBMSwitch;

    // USB 2.0 fast charge switch
    private SwitchPreference mUSB2FastChargeSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main);
        SharedPreferences prefs = getActivity().getSharedPreferences("main",
                Activity.MODE_PRIVATE);
        if (savedInstanceState == null && !prefs.getBoolean("first_warning_shown", false)) {
            showWarning();
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Context context = getContext();

        // Power efficient workqueue switch
        mPowerEfficientWorkqueueModeSwitch = (SwitchPreference) findPreference(Constants.KEY_POWER_EFFICIENT_WORKQUEUE);
        if (Utils.isFileWritable(Constants.NODE_POWER_EFFICIENT_WORKQUEUE)) {
            mPowerEfficientWorkqueueModeSwitch.setEnabled(true);
            mPowerEfficientWorkqueueModeSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_POWER_EFFICIENT_WORKQUEUE, false));
            mPowerEfficientWorkqueueModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mPowerEfficientWorkqueueModeSwitch.setEnabled(false);
        }

        // High brightness mode switches
        mHBMSwitch = (SwitchPreference) findPreference(Constants.KEY_HBM);
        mAutoHBMSwitch = (SwitchPreference) findPreference(Constants.KEY_AUTO_HBM);
        if (Utils.isFileWritable(Constants.NODE_HBM)) {
            mHBMSwitch.setEnabled(true);
            mHBMSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_HBM, false));
            mHBMSwitch.setOnPreferenceChangeListener(this);
            mAutoHBMSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(Constants.KEY_AUTO_HBM, false));
            mAutoHBMSwitch.setOnPreferenceChangeListener(this);
        } else {
            mHBMSwitch.setEnabled(false);
            mAutoHBMSwitch.setEnabled(false);
        }

        // USB 2.0 fast charge switch
        mUSB2FastChargeSwitch = (SwitchPreference) findPreference(Constants.KEY_USB2_FAST_CHARGE);
        if (Utils.isFileWritable(Constants.NODE_USB2_FAST_CHARGE)) {
            mUSB2FastChargeSwitch.setEnabled(true);
            mUSB2FastChargeSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_USB2_FAST_CHARGE, false));
            mUSB2FastChargeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mUSB2FastChargeSwitch.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Power efficient workqueue switch
        if (preference == mPowerEfficientWorkqueueModeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(Constants.KEY_POWER_EFFICIENT_WORKQUEUE, enabled).commit();
            Utils.writeValue(Constants.NODE_POWER_EFFICIENT_WORKQUEUE, enabled ? "1" : "0");
            return true;
        // High brightness mode switch
        } else if (preference == mHBMSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(Constants.KEY_HBM, enabled).commit();
            Utils.writeValue(Constants.NODE_HBM, enabled ? "1" : "0");
            Intent hbmServiceIntent = new Intent(this.getContext(), HBMService.class);
            if (enabled) {
                this.getContext().startService(hbmServiceIntent);
            } else {
                this.getContext().stopService(hbmServiceIntent);
            }
            return true;
        // Auto HBM switch
        } else if (preference == mAutoHBMSwitch) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(Constants.KEY_AUTO_HBM, enabled).commit();
            AutoHBMUtils.enableAutoHBM(getContext());
            return true;
        // USB 2.0 fast charge switch
        } else if (preference == mUSB2FastChargeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(Constants.KEY_USB2_FAST_CHARGE, enabled).commit();
            Utils.writeValue(Constants.NODE_USB2_FAST_CHARGE, enabled ? "1" : "0");
            return true;
        }

        return false;
    }

    public static boolean isAutoHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.KEY_AUTO_HBM, false);
    }

    // Power efficient workqueue switch
    public static void restorePowerEfficientWorkqueueSetting(Context context) {
        if (Utils.isFileWritable(Constants.NODE_POWER_EFFICIENT_WORKQUEUE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_POWER_EFFICIENT_WORKQUEUE, false);
            Utils.writeValue(Constants.NODE_POWER_EFFICIENT_WORKQUEUE, value ? "1" : "0");
        }
    }

    // High brightness mode switch
    public static void restoreHBMSetting(Context context) {
        if (Utils.isFileWritable(Constants.NODE_HBM)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_HBM, false);
            Utils.writeValue(Constants.NODE_HBM, value ? "1" : "0");
        }
    }

    // USB 2.0 fast charge switch
    public static void restoreUSB2FastChargeSetting(Context context) {
        if (Utils.isFileWritable(Constants.NODE_USB2_FAST_CHARGE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_USB2_FAST_CHARGE, false);
            Utils.writeValue(Constants.NODE_USB2_FAST_CHARGE, value ? "1" : "0");
        }
    }


    // First launch warning dialog
    public static class WarningDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pixel_parts_warning_title)
                    .setMessage(R.string.pixel_parts_warning_text)
                    .setNegativeButton(R.string.pixel_parts_dialog, (dialog, which) -> dialog.cancel())
                    .create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().getSharedPreferences("main", Activity.MODE_PRIVATE)
                    .edit()
                    .putBoolean("first_warning_shown", true)
                    .commit();
        }
    }

    private void showWarning() {
        WarningDialogFragment fragment = new WarningDialogFragment();
        fragment.show(getFragmentManager(), "warning_dialog");
    }
}
