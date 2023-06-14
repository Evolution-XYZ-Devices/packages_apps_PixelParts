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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.TopIntroPreference;

import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.preferences.CustomSeekBarPreference;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.services.HBMService;
import org.evolution.pixelparts.utils.AutoHBMUtils;
import org.evolution.pixelparts.utils.Utils;

public class PixelParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = PixelParts.class.getSimpleName();

    // Device intro preference
    private TopIntroPreference mIntroPreference;

    // Power efficient workqueue switch
    private SwitchPreference mPowerEfficientWorkqueueModeSwitch;

    // Stop/Start charging preferences
    private CustomSeekBarPreference mStopChargingPreference;
    private CustomSeekBarPreference mStartChargingPreference;

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

        // Device intro preference
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;
        String deviceName = deviceManufacturer + " " + deviceModel;
        mIntroPreference = findPreference(Constants.KEY_DEVICE_INTRO);
        mIntroPreference.setTitle(deviceName);

        // Stop charging preference
        mStopChargingPreference =  (CustomSeekBarPreference) findPreference(Constants.KEY_STOP_CHARGING);
        if (Utils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            mStopChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(Utils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING))));
            mStopChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStopChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStopChargingPreference.setEnabled(false);
        }

        // Start charging preference
        mStartChargingPreference =  (CustomSeekBarPreference) findPreference(Constants.KEY_START_CHARGING);
        if (Utils.isFileWritable(Constants.NODE_START_CHARGING)) {
            mStartChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(Utils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING))));
            mStartChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStartChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStartChargingPreference.setEnabled(false);
        }

        // Power efficient workqueue switch
        mPowerEfficientWorkqueueModeSwitch = (SwitchPreference) findPreference(Constants.KEY_POWER_EFFICIENT_WORKQUEUE);
        if (Utils.isFileWritable(Constants.NODE_POWER_EFFICIENT_WORKQUEUE)) {
            mPowerEfficientWorkqueueModeSwitch.setEnabled(true);
            mPowerEfficientWorkqueueModeSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_POWER_EFFICIENT_WORKQUEUE, false));
            mPowerEfficientWorkqueueModeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mPowerEfficientWorkqueueModeSwitch.setSummary(getString(R.string.kernel_node_access_error));
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
            mHBMSwitch.setSummary(getString(R.string.kernel_node_access_error));
            mAutoHBMSwitch.setSummary(getString(R.string.kernel_node_access_error));
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
            mUSB2FastChargeSwitch.setSummary(getString(R.string.kernel_node_access_error));
            mUSB2FastChargeSwitch.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Stop charging preference
        if (preference == mStopChargingPreference) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            int value = Integer.parseInt(newValue.toString());
            int stopLevel = Integer.parseInt(newValue.toString());
            int startLevel = sharedPrefs.getInt(Constants.KEY_START_CHARGING, 0);
            if (startLevel >= stopLevel) {
                startLevel = stopLevel - 1;
                sharedPrefs.edit().putInt(Constants.KEY_START_CHARGING, startLevel).commit();
                Utils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(startLevel));
                mStartChargingPreference.refresh(startLevel);
                Toast.makeText(getContext(), R.string.stop_below_start_error, Toast.LENGTH_SHORT).show();
            }
            sharedPrefs.edit().putInt(Constants.KEY_STOP_CHARGING, value).commit();
            Utils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
            return true;
          // Start charging preference
        } else if (preference == mStartChargingPreference) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            int value = Integer.parseInt(newValue.toString());
            int startLevel = Integer.parseInt(newValue.toString());
            int stopLevel = sharedPrefs.getInt(Constants.KEY_STOP_CHARGING, 100);
            if (stopLevel <= startLevel) {
                stopLevel = startLevel + 1;
                sharedPrefs.edit().putInt(Constants.KEY_STOP_CHARGING, stopLevel).commit();
                Utils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(stopLevel));
                mStopChargingPreference.refresh(stopLevel);
                Toast.makeText(getContext(), R.string.start_above_stop_error, Toast.LENGTH_SHORT).show();
            }
            sharedPrefs.edit().putInt(Constants.KEY_START_CHARGING, value).commit();
            Utils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
            return true;
          // Power efficient workqueue switch
        } else if (preference == mPowerEfficientWorkqueueModeSwitch) {
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

    // Stop charging preference
    public static void restoreStopChargingSetting(Context context) {
        if (Utils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(Utils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING)));
            Utils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
        }
    }

    // Start charging preference
    public static void restoreStartChargingSetting(Context context) {
        if (Utils.isFileWritable(Constants.NODE_START_CHARGING)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(Utils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING)));
            Utils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
        }
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
