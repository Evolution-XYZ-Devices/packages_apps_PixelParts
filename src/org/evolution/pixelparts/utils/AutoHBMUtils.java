package org.evolution.pixelparts.utils;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import org.evolution.pixelparts.autohbm.AutoHBM;
import org.evolution.pixelparts.services.AutoHBMService;

public class AutoHBMUtils {

    private static boolean mAutoHBMEnabled = false;

    private static void startAutoHBM(Context context) {
        context.startServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
        mAutoHBMEnabled = true;
    }

    private static void stopAutoHBM(Context context) {
        mAutoHBMEnabled = false;
        context.stopServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
    }

    public static void enableAutoHBM(Context context) {
        if (AutoHBM.isAutoHBMEnabled(context) && !mAutoHBMEnabled) {
            startAutoHBM(context);
        } else if (!AutoHBM.isAutoHBMEnabled(context) && mAutoHBMEnabled) {
            stopAutoHBM(context);
        }
    }
}
