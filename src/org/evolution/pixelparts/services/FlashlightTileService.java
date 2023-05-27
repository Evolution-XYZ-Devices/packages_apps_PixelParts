/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.services;

import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;

import org.evolution.pixelparts.misc.Constants;

public class FlashlightTileService extends TileService
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CameraManager cameraManager;
    private SharedPreferences sharedPrefs;
    private boolean isTorchOn;

    @Override
    public void onStartListening() {
        super.onStartListening();
        cameraManager = getSystemService(CameraManager.class);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isTorchOn = sharedPrefs.getBoolean(Constants.KEY_TORCH_STATE, false);
        updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        toggleTorch();
    }

    private void toggleTorch() {
        if (isTorchOn) {
            turnOffTorch();
        } else {
            turnOnTorch();
        }
    }

    private void turnOnTorch() {
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {
                String cameraId = cameraIds[0];
                String torchStrengthString = sharedPrefs.getString(Constants.KEY_TORCH_STRENGTH, "45");
                int torchStrength = Integer.parseInt(torchStrengthString);
                if (torchStrength > 0) {
                    cameraManager.turnOnTorchWithStrengthLevel(cameraId, torchStrength);
                    isTorchOn = true;
                    sharedPrefs.edit().putBoolean(Constants.KEY_TORCH_STATE, true).apply();
                    updateTile();
                    sharedPrefs.registerOnSharedPreferenceChangeListener(this);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffTorch() {
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {
                String cameraId = cameraIds[0];
                cameraManager.setTorchMode(cameraId, false);
                isTorchOn = false;
                sharedPrefs.edit().putBoolean(Constants.KEY_TORCH_STATE, false).apply();
                updateTile();
                sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateTile() {
        final Tile tile = getQsTile();
        tile.setState(isTorchOn ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.KEY_TORCH_STRENGTH)) {
            turnOnTorch();
        }
    }
}
