/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.pixeltorch;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.R;

public class PixelTorchTileService extends TileService {

    private SharedPreferences mSharedPrefs;
    private CameraManager mCameraManager;
    private CameraManager.TorchCallback mTorchCallback;
    private PixelTorchHelper mPixelTorchHelper;

    @Override
    public void onStartListening() {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mPixelTorchHelper = new PixelTorchHelper(mCameraManager);

        mTorchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                if (!enabled) {
                    mPixelTorchHelper.setCurrentState(mSharedPrefs, 0);
                }
                updateTile(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            }

            @Override
            public void onTorchModeUnavailable(String cameraId) {
                super.onTorchModeUnavailable(cameraId);
                updateTile(Tile.STATE_UNAVAILABLE);
            }
        };

        mCameraManager.registerTorchCallback(mTorchCallback, null);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        mCameraManager.unregisterTorchCallback(mTorchCallback);
    }

    @Override
    public void onClick() {
        mPixelTorchHelper.toggleTorch(mSharedPrefs);
        int torchState = mPixelTorchHelper.getCurrentState(mSharedPrefs);

        int tileState;
        if (torchState == 0) {
            tileState = Tile.STATE_INACTIVE;
        } else {
            tileState = Tile.STATE_ACTIVE;
        }

        updateTile(tileState);
    }

    private void updateTile(int state) {
        Tile qsTile = getQsTile();
        if (qsTile == null) {
            return;
        }

        String subtitle;
        if (state == Tile.STATE_UNAVAILABLE) {
            subtitle = getString(R.string.tile_camera_in_use);
        } else if (mSharedPrefs.getBoolean(Constants.KEY_PIXEL_TORCH_CYCLE_MODES, false)) {
            subtitle = (mPixelTorchHelper.getCurrentState(mSharedPrefs) == 0) ?
                    getString(R.string.tile_off) :
                    String.format(getString(R.string.pixel_torch_state), mPixelTorchHelper.getCurrentState(mSharedPrefs));
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
