/*
 * Copyright (C) 2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.pixeltorch;

import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import org.evolution.pixelparts.Constants;

public class PixelTorchHelper {

    private final CameraManager mCameraManager;

    public PixelTorchHelper(CameraManager cameraManager) {
        this.mCameraManager = cameraManager;
    }

    public void toggleTorch(SharedPreferences sharedPrefs) {
        boolean cycleModes = sharedPrefs.getBoolean(Constants.KEY_PIXEL_TORCH_CYCLE_MODES, false);

        try {
            String outCameraId = mCameraManager.getCameraIdList()[0];

            int currentState = getCurrentState(sharedPrefs);

            if (cycleModes) {
                currentState = (currentState + 1) % 4;
            } else {
                currentState = (currentState == 0) ? 1 : 0;
            }

            switch (currentState) {
                case 0:
                    turnOffTorch(outCameraId);
                    break;
                case 1:
                case 2:
                case 3:
                    turnOnTorch(currentState, sharedPrefs, outCameraId);
                    break;
            }

            setCurrentState(sharedPrefs, currentState);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffTorch(String outCameraId) {
        try {
            mCameraManager.setTorchMode(outCameraId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnOnTorch(int currentState, SharedPreferences sharedPrefs, String outCameraId) {
        try {
            String strengthKey = getStrengthKey(currentState);
            int torchStrength = sharedPrefs.getInt(strengthKey, getDefaultStrength(currentState));

            if (torchStrength != 0) {
                mCameraManager.turnOnTorchWithStrengthLevel(outCameraId, torchStrength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurrentState(SharedPreferences sharedPrefs) {
        return sharedPrefs.getInt(Constants.KEY_PIXEL_TORCH_STATE, 0);
    }

    public void setCurrentState(SharedPreferences sharedPrefs, int state) {
        sharedPrefs.edit().putInt(Constants.KEY_PIXEL_TORCH_STATE, state).apply();
    }

    private String getStrengthKey(int currentState) {
        switch (currentState) {
            case 1:
                return Constants.KEY_PIXEL_TORCH_STRENGTH_1;
            case 2:
                return Constants.KEY_PIXEL_TORCH_STRENGTH_2;
            case 3:
                return Constants.KEY_PIXEL_TORCH_STRENGTH_3;
            default:
                return Constants.KEY_PIXEL_TORCH_STRENGTH_1;
        }
    }

    private int getDefaultStrength(int currentState) {
        switch (currentState) {
            case 1:
                return 45;
            case 2:
                return 25;
            case 3:
                return 10;
            default:
                return 45;
        }
    }
}
