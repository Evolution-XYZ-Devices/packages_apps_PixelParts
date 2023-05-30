/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.misc;

public class Constants {

    // Stop/Start charging preferences
    public static final String KEY_STOP_CHARGING = "stop_charging";
    public static final String KEY_START_CHARGING = "start_charging";
    public static final String NODE_STOP_CHARGING = "/sys/devices/platform/google,charger/charge_stop_level";
    public static final String NODE_START_CHARGING = "/sys/devices/platform/google,charger/charge_start_level";
    public static final String DEFAULT_STOP_CHARGING = "100";
    public static final String DEFAULT_START_CHARGING = "0";

    // Power efficient workqueue switch
    public static final String KEY_POWER_EFFICIENT_WORKQUEUE = "power_efficient_workqueue";
    public static final String NODE_POWER_EFFICIENT_WORKQUEUE = "/sys/module/workqueue/parameters/power_efficient";

    // Flashlight
    public static final String KEY_TORCH_STRENGTH = "torch_strength";
    public static final String KEY_TORCH_STATE = "torch_state";

    // High brightness mode switches
    public static final String KEY_HBM = "hbm";
    public static final String KEY_AUTO_HBM = "auto_hbm";
    public static final String KEY_AUTO_HBM_THRESHOLD = "auto_hbm_threshold";
    public static final String KEY_HBM_ENABLE_TIME = "hbm_enable_time";
    public static final String KEY_HBM_DISABLE_TIME = "hbm_disable_time";
    public static final String NODE_HBM = "/sys/class/backlight/panel0-backlight/hbm_mode";

    // USB 2.0 fast charge switch
    public static final String KEY_USB2_FAST_CHARGE = "usb2_fast_charge";
    public static final String NODE_USB2_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
}
