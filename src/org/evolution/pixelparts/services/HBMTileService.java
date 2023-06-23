/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;

import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.R;
import org.evolution.pixelparts.utils.FileUtils;

public class HBMTileService extends TileService {

    private BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPrefs.edit().putBoolean(Constants.KEY_HBM, false).commit();
                updateTile(false);
            }
        }
    };

    private void updateTile(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        String subtitle = enabled ? getString(R.string.tile_on) : getString(R.string.tile_off);
        tile.setSubtitle(subtitle);
        tile.updateTile();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenStateReceiver);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateTile(sharedPrefs.getBoolean(Constants.KEY_HBM, false));
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean enabled = !(sharedPrefs.getBoolean(Constants.KEY_HBM, false));
        FileUtils.writeValue(Constants.NODE_HBM, enabled ? "1" : "0");
        sharedPrefs.edit().putBoolean(Constants.KEY_HBM, enabled).commit();
        updateTile(enabled);
    }
}
