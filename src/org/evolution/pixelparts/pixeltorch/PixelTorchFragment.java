/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.pixeltorch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.TileUtils;

public class PixelTorchFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private Preference mButtonServicePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pixel_torch);
        setHasOptionsMenu(true);

        mButtonServicePreference = findPreference(Constants.KEY_PIXEL_TORCH_BUTTON_SERVICE);
        mButtonServicePreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return true;
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pixel_torch_menu, menu);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_tile) {
            TileUtils.requestAddTileService(
                    getContext(),
                    PixelTorchTileService.class,
                    R.string.pixel_torch_title,
                    R.drawable.ic_pixel_torch_tile
            );
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static boolean hasTorch(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
