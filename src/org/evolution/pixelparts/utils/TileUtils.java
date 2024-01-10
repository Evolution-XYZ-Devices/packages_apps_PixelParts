/*
 * Copyright (C) 2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.utils;

import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.widget.Toast;

import org.evolution.pixelparts.R;

public class TileUtils {

    public static void requestAddTileService(Context context, Class<?> tileServiceClass, int labelResId, int iconResId) {
        ComponentName componentName = new ComponentName(context, tileServiceClass);
        String label = context.getString(labelResId);
        Icon icon = Icon.createWithResource(context, iconResId);

        StatusBarManager sbm = (StatusBarManager) context.getSystemService(Context.STATUS_BAR_SERVICE);

        if (sbm != null) {
            sbm.requestAddTileService(
                    componentName,
                    label,
                    icon,
                    context.getMainExecutor(),
                    result -> handleResult(context, result)
            );
        }
    }

    private static void handleResult(Context context, Integer result) {
        if (result == null)
            return;
        switch (result) {
            case StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED:
                Toast.makeText(context, R.string.tile_added, Toast.LENGTH_SHORT).show();
                break;
            case StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED:
                Toast.makeText(context, R.string.tile_not_added, Toast.LENGTH_SHORT).show();
                break;
            case StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED:
                Toast.makeText(context, R.string.tile_already_added, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
