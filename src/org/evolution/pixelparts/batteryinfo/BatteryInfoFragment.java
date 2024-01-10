/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.batteryinfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.UsageProgressBarPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.FileUtils;
import org.evolution.pixelparts.utils.TileUtils;

public class BatteryInfoFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private Handler mHandler;
    private Runnable mUpdateRunnable;
    private SharedPreferences mSharedPrefs;

    // Battery info
    private UsageProgressBarPreference mCapacityStatusPreference;

    private Preference mTechnologyPreference;
    private Preference mUSBTypePreference;
    private Preference mTemperaturePreference;
    private Preference mCapacityLevelPreference;
    private Preference mCurrentPreference;
    private Preference mVoltagePreference;
    private Preference mWattagePreference;
    private Preference mHealthPreference;
    private Preference mManufacturingDatePreference;
    private Preference mFirstUsageDatePreference;
    private Preference mCycleCountPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.battery_info);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Context context = getContext();

        setHasOptionsMenu(true);

        mHandler = new Handler();
        mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePreferenceSummaries();
                mHandler.postDelayed(this,
                        mSharedPrefs.getBoolean(Constants.KEY_BATTERY_INFO_REFRESH, false) ? 1000 : 5000);
            }
        };

        mCapacityStatusPreference = findPreference(Constants.KEY_CAPACITY_STATUS);

        mTechnologyPreference = findPreference(Constants.KEY_TECHNOLOGY);
        mUSBTypePreference = findPreference(Constants.KEY_USB_TYPE);
        mTemperaturePreference = findPreference(Constants.KEY_TEMPERATURE);
        mCapacityLevelPreference = findPreference(Constants.KEY_CAPACITY_LEVEL);
        mCurrentPreference = findPreference(Constants.KEY_CURRENT);
        mVoltagePreference = findPreference(Constants.KEY_VOLTAGE);
        mWattagePreference = findPreference(Constants.KEY_WATTAGE);
        mHealthPreference = findPreference(Constants.KEY_HEALTH);
        mManufacturingDatePreference = findPreference(Constants.KEY_MANUFACTURING_DATE);
        mFirstUsageDatePreference = findPreference(Constants.KEY_FIRST_USAGE_DATE);
        mCycleCountPreference = findPreference(Constants.KEY_CYCLE_COUNT);

        updatePreferenceSummaries();
        mHandler.postDelayed(mUpdateRunnable,
                mSharedPrefs.getBoolean(Constants.KEY_BATTERY_INFO_REFRESH, false) ? 1000 : 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferenceSummaries();
        mHandler.postDelayed(mUpdateRunnable,
                mSharedPrefs.getBoolean(Constants.KEY_BATTERY_INFO_REFRESH, false) ? 1000 : 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateRunnable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.battery_info_menu, menu);
        menu.findItem(R.id.temperature_unit).setChecked(mSharedPrefs.getBoolean(Constants.KEY_TEMPERATURE_UNIT, false));
        menu.findItem(R.id.battery_info_refresh).setChecked(mSharedPrefs.getBoolean(Constants.KEY_BATTERY_INFO_REFRESH, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isChecked = !item.isChecked();
        item.setChecked(isChecked);
        if (item.getItemId() == R.id.temperature_unit) {
            mSharedPrefs.edit().putBoolean(Constants.KEY_TEMPERATURE_UNIT, isChecked).apply();
            return true;
        } else if (item.getItemId() == R.id.battery_info_refresh) {
            mSharedPrefs.edit().putBoolean(Constants.KEY_BATTERY_INFO_REFRESH, isChecked).apply();
            return true;
        } else if (item.getItemId() == R.id.add_tile) {
            TileUtils.requestAddTileService(
                    getContext(),
                    BatteryInfoTileService.class,
                    R.string.battery_info_title,
                    R.drawable.ic_battery_info_tile
            );
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updatePreferenceSummaries() {
        // Capacity & status preference
        if (FileUtils.isFileReadable(Constants.NODE_CAPACITY) && FileUtils.isFileReadable(Constants.NODE_STATUS)) {
            String capacityFileValue = FileUtils.getFileValue(Constants.NODE_CAPACITY, null);
            String statusFileValue = FileUtils.getFileValue(Constants.NODE_STATUS, null);
            int capacityFileIntValue = Integer.parseInt(capacityFileValue);
            int statusStringResourceId = getStatusStringResourceId(statusFileValue);
            mCapacityStatusPreference.setUsageSummary(capacityFileValue);
            mCapacityStatusPreference.setTotalSummary("%");
            mCapacityStatusPreference.setPercent(capacityFileIntValue, 100);
            mCapacityStatusPreference.setBottomSummary(getString(statusStringResourceId));
        } else {
            mCapacityStatusPreference.setUsageSummary("0");
            mCapacityStatusPreference.setTotalSummary("%");
            mCapacityStatusPreference.setBottomSummary(getString(R.string.kernel_node_access_error));
        }

        // Technology preference
        if (FileUtils.isFileReadable(Constants.NODE_TECHNOLOGY)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_TECHNOLOGY, null);
            mTechnologyPreference.setSummary(fileValue);
        } else {
            mTechnologyPreference.setSummary(getString(R.string.kernel_node_access_error));
            mTechnologyPreference.setEnabled(false);
        }

        // USB type preference
        if (FileUtils.isFileReadable(Constants.NODE_USB_TYPE)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_USB_TYPE, null);
            int usbTypeStringResourceId = getUSBTypeStringResourceId(fileValue);
            mUSBTypePreference.setSummary(getString(usbTypeStringResourceId));
        } else {
            mUSBTypePreference.setSummary(getString(R.string.kernel_node_access_error));
            mUSBTypePreference.setEnabled(false);
        }

        // Temperature preference
        if (FileUtils.isFileReadable(Constants.NODE_TEMPERATURE)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_TEMPERATURE, null);
            int temperature = Integer.parseInt(fileValue);
            float temperatureCelsius = temperature / 10.0f;
            float temperatureFahrenheit = temperatureCelsius * 1.8f + 32;
            if (mSharedPrefs.getBoolean(Constants.KEY_TEMPERATURE_UNIT, false)) {
                float roundedTemperatureFahrenheit = Math.round(temperatureFahrenheit * 10) / 10.0f;
                mTemperaturePreference.setSummary(roundedTemperatureFahrenheit + "°F");
            } else {
               float roundedTemperatureCelsius = Math.round(temperatureCelsius * 10) / 10.0f;
               mTemperaturePreference.setSummary(roundedTemperatureCelsius + "°C");
            }
        } else {
            mTemperaturePreference.setSummary(getString(R.string.kernel_node_access_error));
            mTemperaturePreference.setEnabled(false);
        }

        // Capacity level preference
        if (FileUtils.isFileReadable(Constants.NODE_CAPACITY_LEVEL)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_CAPACITY_LEVEL, null);
            int capacityLevelStringResourceId = geCapacityLevelStringResourceId(fileValue);
            mCapacityLevelPreference.setSummary(getString(capacityLevelStringResourceId));
        } else {
            mCapacityLevelPreference.setSummary(getString(R.string.kernel_node_access_error));
            mCapacityLevelPreference.setEnabled(false);
        }

        // Current preference
        if (FileUtils.isFileReadable(Constants.NODE_CURRENT)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_CURRENT, null);
            int chargingCurrent = Integer.parseInt(fileValue);
            int absoluteChargingCurrent = Math.abs(chargingCurrent);
            String formattedChargingCurrent = (absoluteChargingCurrent / 1000) + "mA";
            mCurrentPreference.setSummary(formattedChargingCurrent);
        } else {
            mCurrentPreference.setSummary(getString(R.string.kernel_node_access_error));
            mCurrentPreference.setEnabled(false);
        }

        // Voltage preference
        if (FileUtils.isFileReadable(Constants.NODE_VOLTAGE)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_VOLTAGE, null);
            float chargingVoltage = Float.parseFloat(fileValue);
            String formattedChargingVoltage = String.format("%.1f", (chargingVoltage / 1000000)) + "V";
            mVoltagePreference.setSummary(formattedChargingVoltage);
        } else {
            mVoltagePreference.setSummary(getString(R.string.kernel_node_access_error));
            mVoltagePreference.setEnabled(false);
        }

        // Wattage preference
        if (FileUtils.isFileReadable(Constants.NODE_VOLTAGE) && FileUtils.isFileReadable(Constants.NODE_CURRENT)) {
            String voltageFileValue = FileUtils.getFileValue(Constants.NODE_VOLTAGE, null);
            String currentFileValue = FileUtils.getFileValue(Constants.NODE_CURRENT, null);
            float chargingCurrent = Integer.parseInt(currentFileValue) / 1000.0f;
            float chargingVoltage = Float.parseFloat(voltageFileValue) / 1000000.0f;
            float wattage = (chargingVoltage * chargingCurrent) / 1000.0f;
            float absoluteWattage = Math.abs(wattage);
            String formattedWattage = String.format("%.1f", absoluteWattage) + "W";
            mWattagePreference.setSummary(formattedWattage);
        } else {
            mWattagePreference.setSummary(getString(R.string.kernel_node_access_error));
            mWattagePreference.setEnabled(false);
        }

        // Health preference
        if (FileUtils.isFileReadable(Constants.NODE_HEALTH)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_HEALTH, null);
            int healthStringResourceId = getHealthStringResourceId(fileValue);
            mHealthPreference.setSummary(getString(healthStringResourceId));
        } else {
            mHealthPreference.setSummary(getString(R.string.kernel_node_access_error));
            mHealthPreference.setEnabled(false);
        }

        // Manufacturing date preference
        if (FileUtils.isFileReadable(Constants.NODE_MANUFACTURING_DATE)) {
            long timestamp = Long.parseLong(FileUtils.getFileValue(Constants.NODE_MANUFACTURING_DATE, null)) * 1000L;
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            mManufacturingDatePreference.setSummary(sdf.format(date));
        } else {
            mManufacturingDatePreference.setSummary(getString(R.string.kernel_node_access_error));
            mManufacturingDatePreference.setEnabled(false);
        }

        // First usage date preference
        if (FileUtils.isFileReadable(Constants.NODE_FIRST_USAGE_DATE)) {
            Date date = new Date(Long.parseLong(FileUtils.getFileValue(Constants.NODE_FIRST_USAGE_DATE, null)) * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            mFirstUsageDatePreference.setSummary(sdf.format(date));
        } else {
            mFirstUsageDatePreference.setSummary(getString(R.string.kernel_node_access_error));
            mFirstUsageDatePreference.setEnabled(false);
        }

        // Cycle count preference
        if (FileUtils.isFileReadable(Constants.NODE_CYCLE_COUNT)) {
            String fileValue = FileUtils.getFileValue(Constants.NODE_CYCLE_COUNT, null);
            mCycleCountPreference.setSummary(fileValue);
        } else {
            mCycleCountPreference.setSummary(getString(R.string.kernel_node_access_error));
            mCycleCountPreference.setEnabled(false);
        }
    }

    // Status preference strings
    private int getStatusStringResourceId(String status) {
        switch (status) {
            case "Unknown":
                return R.string.status_unknown;
            case "Charging":
                return R.string.status_charging;
            case "Discharging":
                return R.string.status_discharging;
            case "Not charging":
                return R.string.status_not_charging;
            case "Full":
                return R.string.status_full;
            default:
                return R.string.kernel_node_returned_value_unknown;
        }
    }

    // USB type preference strings
    private int getUSBTypeStringResourceId(String usbType) {
        if (usbType.contains("[Unknown]")) {
            return R.string.usb_type_unknown_or_not_connected;
        } else if (usbType.contains("[SDP]")) {
            return R.string.usb_type_standard_downstream_port;
        } else if (usbType.contains("[CDP]")) {
            return R.string.usb_type_charging_downstream_port;
        } else if (usbType.contains("[DCP]")) {
            return R.string.usb_type_dedicated_charging_port;
        } else {
            return R.string.kernel_node_returned_value_unknown;
        }
    }

    // Capacity level preference strings
    private int geCapacityLevelStringResourceId(String capacitylevel) {
        switch (capacitylevel) {
            case "Unknown":
                return R.string.capacity_level_unknown;
            case "Critical":
                return R.string.capacity_level_critical;
            case "Low":
                return R.string.capacity_level_low;
            case "Normal":
                return R.string.capacity_level_normal;
            case "High":
                return R.string.capacity_level_high;
            case "Full":
                return R.string.capacity_level_full;
            default:
                return R.string.kernel_node_returned_value_unknown;
        }
    }

    // Health preference strings
    private int getHealthStringResourceId(String health) {
        switch (health) {
            case "Unknown":
                return R.string.health_unknown;
            case "Good":
                return R.string.health_good;
            case "Overheat":
                return R.string.health_overheat;
            case "Dead":
                return R.string.health_dead;
            case "Over voltage":
                return R.string.health_over_voltage;
            case "Unspecified failure":
                return R.string.health_unspecified_failure;
            case "Cold":
                return R.string.health_cold;
            case "Watchdog timer expire":
                return R.string.health_watchdog_timer_expire;
            case "Safety timer expire":
                return R.string.health_safety_timer_expire;
            case "Over current":
                return R.string.health_over_current;
            case "Calibration required":
                return R.string.health_calibration_required;
            case "Warm":
                return R.string.health_warm;
            case "Cool":
                return R.string.health_cool;
            case "Hot":
                return R.string.health_hot;
            default:
                return R.string.kernel_node_returned_value_unknown;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
