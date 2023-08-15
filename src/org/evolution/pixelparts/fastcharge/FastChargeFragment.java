/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.fastcharge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.FileUtils;

public class FastChargeFragment extends PreferenceFragment
        implements OnMainSwitchChangeListener {

    private MainSwitchPreference mFastChargeSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fast_charge);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mFastChargeSwitch = findPreference(Constants.KEY_FAST_CHARGE);
        mFastChargeSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_FAST_CHARGE, false));
        mFastChargeSwitch.addOnSwitchChangeListener(this);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putBoolean(Constants.KEY_FAST_CHARGE, isChecked).apply();
        if (FileUtils.isFileWritable(Constants.NODE_FAST_CHARGE)) {
            FileUtils.writeValue(Constants.NODE_FAST_CHARGE, isChecked ? "1" : "0");
        }
    }

    public static void restoreFastChargeSetting(Context context) {
        if (FileUtils.isFileWritable(Constants.NODE_FAST_CHARGE)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sharedPrefs.getBoolean(Constants.KEY_FAST_CHARGE, false);
            FileUtils.writeValue(Constants.NODE_FAST_CHARGE, value ? "1" : "0");
        }
    }
}
