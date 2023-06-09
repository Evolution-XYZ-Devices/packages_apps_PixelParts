/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.evolution.pixelparts.misc.Constants;
import org.evolution.pixelparts.R;

public class PixelTorchTileService extends TileService {

    private SharedPreferences sharedPrefs;
    private CameraManager cameraManager;
    private CameraManager.TorchCallback torchCallback;

    @Override
    public void onStartListening() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        registerTorchCallback();
        updateTile(Tile.STATE_INACTIVE);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        cameraManager.unregisterTorchCallback(torchCallback);
    }

    @Override
    public void onClick() {
        toggleTorch();
    }

    private void toggleTorch() {
        try {
            String outCameraId = cameraManager.getCameraIdList()[0];

            if (getQsTile().getState() == Tile.STATE_ACTIVE) {
                turnOffTorch(outCameraId);
            } else {
                turnOnTorch(outCameraId);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffTorch(String outCameraId) {
        try {
            cameraManager.setTorchMode(outCameraId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTile(Tile.STATE_INACTIVE);
    }

    private void turnOnTorch(String outCameraId) {
        try {
            int torchStrength = sharedPrefs.getInt(Constants.KEY_TORCH_STRENGTH, 45);
            if (torchStrength != 0) {
                cameraManager.turnOnTorchWithStrengthLevel(outCameraId, torchStrength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTile(Tile.STATE_ACTIVE);
    }

    private void registerTorchCallback() {
        torchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                updateTile(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            }
        };
        cameraManager.registerTorchCallback(torchCallback, null);
    }

    private void updateTile(int state) {
        Tile qsTile = getQsTile();
        if (qsTile != null) {
            qsTile.setState(state);
            String subtitle = state == Tile.STATE_ACTIVE ?
                    getString(R.string.tile_on) :
                    getString(R.string.tile_off);
            qsTile.setSubtitle(subtitle);
            qsTile.updateTile();
        }
    }
}
