/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.evolution.pixelparts.utils.AutoHBMUtils;
import org.evolution.pixelparts.saturation.Saturation;

public class Startup extends BroadcastReceiver {

    private static final String TAG = Startup.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
            // PixelParts
            PixelParts.restoreStopChargingSetting(context);
            PixelParts.restoreStartChargingSetting(context);
            PixelParts.restoreHBMSetting(context);
            PixelParts.restoreUSB2FastChargeSetting(context);
            AutoHBMUtils.enableAutoHBM(context);
            Saturation.restoreSaturationSetting(context);
    }
}
