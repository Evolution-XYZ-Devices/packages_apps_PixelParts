/*
 * Copyright (C) 2023-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts;

public class Constants {

    // Battery info
    public static final String KEY_BATTERY_INFO_REFRESH = "battery_info_refresh";
    public static final String KEY_TECHNOLOGY = "technology";
    public static final String KEY_STATUS = "status";
    public static final String KEY_USB_TYPE = "usb_type";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_TEMPERATURE_UNIT = "temperature_unit";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_CAPACITY_LEVEL = "capacity_level";
    public static final String KEY_CURRENT = "current";
    public static final String KEY_VOLTAGE = "voltage";
    public static final String KEY_WATTAGE = "wattage";
    public static final String KEY_HEALTH = "health";
    public static final String KEY_MANUFACTURING_DATE = "manufacturing_date";
    public static final String KEY_FIRST_USAGE_DATE = "first_usage_date";
    public static final String KEY_CYCLE_COUNT = "cycle_count";
    public static final String NODE_TECHNOLOGY = "/sys/class/power_supply/battery/technology";
    public static final String NODE_STATUS = "/sys/class/power_supply/battery/status";
    public static final String NODE_USB_TYPE = "/sys/class/power_supply/usb/usb_type";
    public static final String NODE_TEMPERATURE = "/sys/class/power_supply/battery/temp";
    public static final String NODE_CAPACITY = "/sys/class/power_supply/battery/capacity";
    public static final String NODE_CAPACITY_LEVEL = "/sys/class/power_supply/battery/capacity_level";
    public static final String NODE_CURRENT = "/sys/class/power_supply/battery/current_now";
    public static final String NODE_VOLTAGE = "/sys/class/power_supply/battery/voltage_now";
    public static final String NODE_HEALTH = "/sys/class/power_supply/battery/health";
    public static final String NODE_MANUFACTURING_DATE = "/sys/class/power_supply/battery/manufacturing_date";
    public static final String NODE_FIRST_USAGE_DATE = "/sys/class/power_supply/battery/first_usage_date";
    public static final String NODE_CYCLE_COUNT = "/sys/class/power_supply/battery/cycle_count";

    // Charge control
    public static final String KEY_CHARGE_CONTROL = "charge_control";
    public static final String KEY_STOP_CHARGING = "stop_charging";
    public static final String KEY_START_CHARGING = "start_charging";
    public static final String NODE_STOP_CHARGING = "/sys/devices/platform/google,charger/charge_stop_level";
    public static final String NODE_START_CHARGING = "/sys/devices/platform/google,charger/charge_start_level";
    public static final String DEFAULT_STOP_CHARGING = "100";
    public static final String DEFAULT_START_CHARGING = "0";

    // Pixel torch
    public static final String KEY_PIXEL_TORCH_STRENGTH = "pixel_torch_strength";

    // AutoHbm
    public static final String KEY_AUTO_HBM = "auto_hbm";
    public static final String KEY_AUTO_HBM_THRESHOLD = "auto_hbm_threshold";
    public static final String KEY_AUTO_HBM_ENABLE_TIME = "auto_hbm_enable_time";
    public static final String KEY_AUTO_HBM_DISABLE_TIME = "auto_hbm_disable_time";
    public static final String KEY_CURRENT_LUX_LEVEL = "current_lux_level";
    public static final String NODE_HBM = "/sys/class/backlight/panel0-backlight/hbm_mode";

    // Saturation
    public static final String KEY_SATURATION = "saturation";

    // Fast charge
    public static final String KEY_FAST_CHARGE = "fast_charge";
    public static final String NODE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
}
