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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.TopIntroPreference;

import org.evolution.pixelparts.about.AboutActivity;
import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.preferences.CustomSeekBarPreference;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.services.HBMService;
import org.evolution.pixelparts.utils.FileUtils;
import org.evolution.pixelparts.utils.TorchUtils;

public class PixelParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = PixelParts.class.getSimpleName();

    // Device intro preference
    private TopIntroPreference mIntroPreference;

    // Stop/Start charging preferences
    private CustomSeekBarPreference mStopChargingPreference;
    private CustomSeekBarPreference mStartChargingPreference;

    // High brightness mode preferences/switches
    private Preference mAutoHBMPreference;
    private SwitchPreference mHBMSwitch;

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

        setHasOptionsMenu(true);

        // Device intro preference
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;
        String deviceName = deviceManufacturer + " " + deviceModel;
        mIntroPreference = findPreference(Constants.KEY_DEVICE_INTRO);
        mIntroPreference.setTitle(deviceName);

        // Stop charging preference
        mStopChargingPreference =  (CustomSeekBarPreference) findPreference(Constants.KEY_STOP_CHARGING);
        if (FileUtils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            mStopChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING))));
            mStopChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStopChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStopChargingPreference.setEnabled(false);
        }

        // Start charging preference
        mStartChargingPreference =  (CustomSeekBarPreference) findPreference(Constants.KEY_START_CHARGING);
        if (FileUtils.isFileWritable(Constants.NODE_START_CHARGING)) {
            mStartChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING))));
            mStartChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStartChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStartChargingPreference.setEnabled(false);
        }

        // High brightness mode preferences/switches
        mHBMSwitch = (SwitchPreference) findPreference(Constants.KEY_HBM);
        mAutoHBMPreference = (Preference) findPreference(Constants.KEY_AUTO_HBM_SETTINGS);
        if (FileUtils.isFileWritable(Constants.NODE_HBM)) {
            mHBMSwitch.setEnabled(true);
            mAutoHBMPreference.setEnabled(true);
            mHBMSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_HBM, false));
            mHBMSwitch.setOnPreferenceChangeListener(this);
        } else {
            mHBMSwitch.setSummary(getString(R.string.kernel_node_access_error));
            mAutoHBMPreference.setSummary(getString(R.string.kernel_node_access_error));
            mHBMSwitch.setEnabled(false);
            mAutoHBMPreference.setEnabled(false);
        }

        // LEDs (PixelTorch)
        PreferenceCategory categoryLEDS = findPreference("category_leds");
        if (!TorchUtils.hasTorch(getContext())) {
            getPreferenceScreen().removePreference(categoryLEDS);
        }

        // USB 2.0 fast charge switch
        mUSB2FastChargeSwitch = (SwitchPreference) findPreference(Constants.KEY_USB2_FAST_CHARGE);
        if (FileUtils.isFileWritable(Constants.NODE_USB2_FAST_CHARGE)) {
            mUSB2FastChargeSwitch.setEnabled(true);
            mUSB2FastChargeSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_USB2_FAST_CHARGE, false));
            mUSB2FastChargeSwitch.setOnPreferenceChangeListener(this);
        } else {
            mUSB2FastChargeSwitch.setSummary(getString(R.string.kernel_node_access_error));
            mUSB2FastChargeSwitch.setEnabled(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pixel_parts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pixel_parts_about) {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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
                FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(startLevel));
                mStartChargingPreference.refresh(startLevel);
                Toast.makeText(getContext(), R.string.stop_below_start_error, Toast.LENGTH_SHORT).show();
            }
            sharedPrefs.edit().putInt(Constants.KEY_STOP_CHARGING, value).commit();
            FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
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
                FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(stopLevel));
                mStopChargingPreference.refresh(stopLevel);
                Toast.makeText(getContext(), R.string.start_above_stop_error, Toast.LENGTH_SHORT).show();
            }
            sharedPrefs.edit().putInt(Constants.KEY_START_CHARGING, value).commit();
            FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
            return true;
          // High brightness mode switch
        } else if (preference == mHBMSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(Constants.KEY_HBM, enabled).commit();
            FileUtils.writeValue(Constants.NODE_HBM, enabled ? "1" : "0");
            Intent hbmServiceIntent = new Intent(this.getContext(), HBMService.class);
            if (enabled) {
                this.getContext().startService(hbmServiceIntent);
            } else {
                this.getContext().stopService(hbmServiceIntent);
            }
            return true;
          // USB 2.0 fast charge switch
        } else if (preference == mUSB2FastChargeSwitch) {
            boolean enabled = (Boolean) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPrefs.edit().putBoolean(Constants.KEY_USB2_FAST_CHARGE, enabled).commit();
            FileUtils.writeValue(Constants.NODE_USB2_FAST_CHARGE, enabled ? "1" : "0");
            return true;
        }

        return false;
    }

    // Stop charging preference
    public static void restoreStopChargingSetting(Context context) {
        if (FileUtils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING)));
            FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
        }
    }

    // Start charging preference
    public static void restoreStartChargingSetting(Context context) {
        if (FileUtils.isFileWritable(Constants.NODE_START_CHARGING)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING)));
            FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
        }
    }

    // High brightness mode switch
    public static void restoreHBMSetting(Context context) {
        if (FileUtils.isFileWritable(Constants.NODE_HBM)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_HBM, false);
            FileUtils.writeValue(Constants.NODE_HBM, value ? "1" : "0");
        }
    }

    // USB 2.0 fast charge switch
    public static void restoreUSB2FastChargeSetting(Context context) {
        if (FileUtils.isFileWritable(Constants.NODE_USB2_FAST_CHARGE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_USB2_FAST_CHARGE, false);
            FileUtils.writeValue(Constants.NODE_USB2_FAST_CHARGE, value ? "1" : "0");
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
