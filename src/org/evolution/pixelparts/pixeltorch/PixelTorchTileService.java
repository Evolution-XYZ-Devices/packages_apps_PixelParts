/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.pixeltorch;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.R;

public class PixelTorchTileService extends TileService {

    private SharedPreferences sharedPrefs;
    private CameraManager cameraManager;
    private CameraManager.TorchCallback torchCallback;
    private int currentState = 0;

    @Override
    public void onStartListening() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        registerTorchCallback();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        cameraManager.unregisterTorchCallback(torchCallback);
    }

    @Override
    public void onClick() {
        boolean cycleModes = sharedPrefs.getBoolean(Constants.KEY_PIXEL_TORCH_CYCLE_MODES, false);
        if (cycleModes) {
            currentState = (currentState + 1) % 4;
        } else {
            currentState = (currentState == 0) ? 1 : 0;
        }
        toggleTorch();
    }

    private void registerTorchCallback() {
        torchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                if (!enabled) {
                    currentState = 0;
                }
                updateTile(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            }

            @Override
            public void onTorchModeUnavailable(String cameraId) {
                super.onTorchModeUnavailable(cameraId);
                updateTile(Tile.STATE_UNAVAILABLE);
            }
        };
        cameraManager.registerTorchCallback(torchCallback, null);
    }

    private void toggleTorch() {
        try {
            String outCameraId = cameraManager.getCameraIdList()[0];

            switch (currentState) {
                case 0:
                    turnOffTorch(outCameraId);
                    break;
                case 1:
                    turnOnTorch(Constants.KEY_PIXEL_TORCH_STRENGTH_1, outCameraId);
                    break;
                case 2:
                    turnOnTorch(Constants.KEY_PIXEL_TORCH_STRENGTH_2, outCameraId);
                    break;
                case 3:
                    turnOnTorch(Constants.KEY_PIXEL_TORCH_STRENGTH_3, outCameraId);
                    break;
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

    private void turnOnTorch(String strengthKey, String outCameraId) {
        try {
            int torchStrength = sharedPrefs.getInt(strengthKey,
                    (strengthKey.equals(Constants.KEY_PIXEL_TORCH_STRENGTH_1)) ? 45 :
                            (strengthKey.equals(Constants.KEY_PIXEL_TORCH_STRENGTH_2)) ? 25 :
                                    (strengthKey.equals(Constants.KEY_PIXEL_TORCH_STRENGTH_3)) ? 10 : 45);

            if (torchStrength != 0) {
                cameraManager.turnOnTorchWithStrengthLevel(outCameraId, torchStrength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTile(Tile.STATE_ACTIVE);
    }

    private void updateTile(int state) {
        Tile qsTile = getQsTile();
        if (qsTile == null) {
            return;
            }

        String subtitle;
        if (state == Tile.STATE_UNAVAILABLE) {
            subtitle = getString(R.string.tile_camera_in_use);
        } else if (sharedPrefs.getBoolean(Constants.KEY_PIXEL_TORCH_CYCLE_MODES, false)) {
            subtitle = (currentState == 0) ?
                    getString(R.string.tile_off) :
                    String.format(getString(R.string.pixel_torch_state), currentState);
        } else {
            subtitle = (state == Tile.STATE_ACTIVE) ?
                    getString(R.string.tile_on) :
                    getString(R.string.tile_off);
        }
        qsTile.setSubtitle(subtitle);
        qsTile.setState(state);
        qsTile.updateTile();
    }
}
