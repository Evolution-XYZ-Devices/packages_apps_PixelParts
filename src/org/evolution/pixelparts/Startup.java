/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *               2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.evolution.pixelparts.saturation.Saturation;
import org.evolution.pixelparts.services.PixelTorchTileService;
import org.evolution.pixelparts.utils.AutoHBMUtils;
import org.evolution.pixelparts.utils.ComponentUtils;
import org.evolution.pixelparts.utils.TorchUtils;

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

        // PixelTorchTileService
        ComponentUtils.setComponentEnabled(
                context,
                PixelTorchTileService.class,
                TorchUtils.hasTorch(context)
        );
    }
}
