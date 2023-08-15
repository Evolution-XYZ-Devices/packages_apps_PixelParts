/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.chargecontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.CustomSeekBarPreference;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.FileUtils;

public class ChargeControlFragment extends PreferenceFragment
        implements OnMainSwitchChangeListener, Preference.OnPreferenceChangeListener {

    // Charge control preference
    private MainSwitchPreference mChargeControlSwitch;

    // Stop/Start preferences
    private CustomSeekBarPreference mStopChargingPreference;
    private CustomSeekBarPreference mStartChargingPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.charge_control);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Charge control preference
        mChargeControlSwitch = findPreference(Constants.KEY_CHARGE_CONTROL);
        mChargeControlSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_CHARGE_CONTROL, false));
        mChargeControlSwitch.addOnSwitchChangeListener(this);

        // Stop preference
        mStopChargingPreference = findPreference(Constants.KEY_STOP_CHARGING);
        if (FileUtils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            mStopChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING))));
            mStopChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStopChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStopChargingPreference.setEnabled(false);
        }
        mStopChargingPreference.setVisible(mChargeControlSwitch.isChecked());

        // Start preference
        mStartChargingPreference = findPreference(Constants.KEY_START_CHARGING);
        if (FileUtils.isFileWritable(Constants.NODE_START_CHARGING)) {
            mStartChargingPreference.setValue(sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING))));
            mStartChargingPreference.setOnPreferenceChangeListener(this);
        } else {
            mStartChargingPreference.setSummary(getString(R.string.kernel_node_access_error));
            mStartChargingPreference.setEnabled(false);
        }
        mStartChargingPreference.setVisible(mChargeControlSwitch.isChecked());
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        prefChange.putBoolean(Constants.KEY_CHARGE_CONTROL, isChecked).apply();

        mStopChargingPreference.setVisible(isChecked);
        mStartChargingPreference.setVisible(isChecked);

        if (!isChecked) {
            // Stop preference
            int defaultStopCharging = 100;
            prefChange.putInt(Constants.KEY_STOP_CHARGING, defaultStopCharging).apply();
            FileUtils.writeValue(Constants.NODE_STOP_CHARGING, Integer.toString(defaultStopCharging));
            mStopChargingPreference.refresh(defaultStopCharging);

            // Start preference
            int defaultStartCharging = 0;
            prefChange.putInt(Constants.KEY_START_CHARGING, defaultStartCharging).apply();
            FileUtils.writeValue(Constants.NODE_START_CHARGING, Integer.toString(defaultStartCharging));
            mStartChargingPreference.refresh(defaultStartCharging);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Stop preference
        if (preference == mStopChargingPreference) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            int value = Integer.parseInt(newValue.toString());
            int stopLevel = Integer.parseInt(newValue.toString());
            int startLevel = sharedPrefs.getInt(Constants.KEY_START_CHARGING, 0);
            if (startLevel >= stopLevel) {
                startLevel = stopLevel - 1;
                sharedPrefs.edit().putInt(Constants.KEY_START_CHARGING, startLevel).apply();
                FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(startLevel));
                mStartChargingPreference.refresh(startLevel);
                Toast.makeText(getContext(), R.string.stop_below_start_error, Toast.LENGTH_SHORT).show();

            }
            sharedPrefs.edit().putInt(Constants.KEY_STOP_CHARGING, value).apply();
            FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
            return true;
        }
        // Start preference
        else if (preference == mStartChargingPreference) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            int value = Integer.parseInt(newValue.toString());
            int startLevel = Integer.parseInt(newValue.toString());
            int stopLevel = sharedPrefs.getInt(Constants.KEY_STOP_CHARGING, 100);
            if (stopLevel <= startLevel) {
                stopLevel = startLevel + 1;
                sharedPrefs.edit().putInt(Constants.KEY_STOP_CHARGING, stopLevel).apply();
                FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(stopLevel));
                mStopChargingPreference.refresh(stopLevel);
                Toast.makeText(getContext(), R.string.start_above_stop_error, Toast.LENGTH_SHORT).show();
            }
            sharedPrefs.edit().putInt(Constants.KEY_START_CHARGING, value).apply();
            FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
            return true;
        }

        return false;
    }

    // Stop preference
    public static void restoreStopChargingSetting(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean chargeControlEnabled = sharedPrefs.getBoolean(Constants.KEY_CHARGE_CONTROL, true);

        if (chargeControlEnabled && FileUtils.isFileWritable(Constants.NODE_STOP_CHARGING)) {
            int value = sharedPrefs.getInt(Constants.KEY_STOP_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_STOP_CHARGING, Constants.DEFAULT_STOP_CHARGING)));
            FileUtils.writeValue(Constants.NODE_STOP_CHARGING, String.valueOf(value));
        }
    }

    // Start preference
    public static void restoreStartChargingSetting(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean chargeControlEnabled = sharedPrefs.getBoolean(Constants.KEY_CHARGE_CONTROL, true);

        if (chargeControlEnabled && FileUtils.isFileWritable(Constants.NODE_START_CHARGING)) {
            int value = sharedPrefs.getInt(Constants.KEY_START_CHARGING,
                    Integer.parseInt(FileUtils.getFileValue(Constants.NODE_START_CHARGING, Constants.DEFAULT_START_CHARGING)));
            FileUtils.writeValue(Constants.NODE_START_CHARGING, String.valueOf(value));
        }
    }
}
