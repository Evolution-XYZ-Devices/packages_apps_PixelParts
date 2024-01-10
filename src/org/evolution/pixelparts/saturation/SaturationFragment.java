/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.saturation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import java.io.IOException;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.CustomSeekBarPreference;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.TileUtils;

public class SaturationFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    // Saturation preference
    private CustomSeekBarPreference mSaturationPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.saturation);
        setHasOptionsMenu(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Saturation preference
        mSaturationPreference =  (CustomSeekBarPreference) findPreference(Constants.KEY_SATURATION);
        mSaturationPreference.setOnPreferenceChangeListener(this);
        int seekBarValue = sharedPrefs.getInt(Constants.KEY_SATURATION, 100);
        updateSaturation(seekBarValue);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.saturation_menu, menu);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Saturation preference
        if (preference == mSaturationPreference) {
            int seekBarValue = (Integer) newValue;
            updateSaturation(seekBarValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_tile) {
            TileUtils.requestAddTileService(
                    getContext(),
                    SaturationTileService.class,
                    R.string.saturation_title,
                    R.drawable.ic_saturation_tile
            );
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void updateSaturation(int seekBarValue) {
        float saturation;
        if (seekBarValue == 100) {
            saturation = 1.001f;
        } else {
            saturation = seekBarValue / 100.0f;
        }

        try {
            Runtime.getRuntime().exec("service call SurfaceFlinger 1022 f " + saturation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Saturation preference
    public static void restoreSaturationSetting(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int seekBarValue = sharedPrefs.getInt(Constants.KEY_SATURATION, 100);
        updateSaturation(seekBarValue);
    }
}
