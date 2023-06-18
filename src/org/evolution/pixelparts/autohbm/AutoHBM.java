/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.autohbm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.AutoHBMUtils;

public class AutoHBM extends PreferenceFragment implements OnMainSwitchChangeListener {
    private static final String TAG = AutoHBM.class.getSimpleName();

    private MainSwitchPreference mAutoHBMSwitch;

    private static final String[] AUTO_HBM_PREFERENCES = new String[] {
            Constants.KEY_AUTO_HBM_THRESHOLD,
            Constants.KEY_AUTO_HBM_ENABLE_TIME,
            Constants.KEY_AUTO_HBM_DISABLE_TIME
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.autohbm);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Context context = getContext();

        mAutoHBMSwitch = (MainSwitchPreference) findPreference(Constants.KEY_AUTO_HBM);
        mAutoHBMSwitch.setChecked(sharedPrefs.getBoolean(Constants.KEY_AUTO_HBM, false));
        mAutoHBMSwitch.addOnSwitchChangeListener(this);
        toggleAutoHBMPreferencesVisibility(mAutoHBMSwitch.isChecked());
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putBoolean(Constants.KEY_AUTO_HBM, isChecked).apply();
        AutoHBMUtils.enableAutoHBM(getContext());
        toggleAutoHBMPreferencesVisibility(isChecked);
    }

    private void toggleAutoHBMPreferencesVisibility(boolean show) {
        for (String prefKey : AUTO_HBM_PREFERENCES) {
            Preference pref = findPreference(prefKey);
            if (pref != null) {
                pref.setVisible(show);
            }
        }
    }

    public static boolean isAutoHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.KEY_AUTO_HBM, false);
    }
}
