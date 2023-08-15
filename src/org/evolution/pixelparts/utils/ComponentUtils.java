/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class ComponentUtils {

    /**
     * Enables or disables a specified Android component dynamically at runtime.
     *
     * @param context       The context from which the component will be enabled or disabled.
     * @param componentClass The class of the component to be enabled or disabled.
     * @param enable        If true, the component will be enabled; if false, it will be disabled.
     */
    public static void toggleComponent(Context context, Class<?> componentClass, boolean enable) {
        ComponentName componentName = new ComponentName(context, componentClass);
        PackageManager packageManager = context.getPackageManager();
        int currentState = packageManager.getComponentEnabledSetting(componentName);
        int newState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        if (currentState != newState) {
            packageManager.setComponentEnabledSetting(
                    componentName,
                    newState,
                    PackageManager.DONT_KILL_APP
            );
        }
    }
}
