/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class TorchUtils {

    /**
     * Checks if the device has torch hardware.
     *
     * @param context The context used to access the package manager.
     * @return True if the device has torch hardware, false otherwise.
     */
    public static boolean hasTorch(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
