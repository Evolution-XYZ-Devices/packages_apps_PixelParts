![example](https://raw.githubusercontent.com/Evolution-X-Devices/packages_apps_PixelParts/udc/readme_resources/PixelParts.png)

## About the app

PixelParts began as a simple High Brightness Mode (HBM) tool. Since then, it has evolved to include a detailed battery information panel, a display saturation level adjuster, charging limiter and more. The app now offers a wide range of features, transitioning from its original function as an HBM tool to a hub for device tweaks and stats on customs, with a lot more planned going forward.

## Current features

| Category | Feature | Description | QS Tile | Required kernel changes |
| --- | --- | --- | --- | --- |
| **Battery** | `Battery information` | View various battery information | Yes |N/A |
| | `Charge control` | Start/stop charging at specific percentages | N/A | N/A | 
| | `Fast charge` | Enable CDP mode for faster charging on USB 2.0 ports. | Yes | [Commit 1/1](https://github.com/Evolution-X-Devices/kernel_google_gs101/commit/dcbfcd76bdc5d72e16f85fd8a48de6afa8804b61) |
| **Display** | `Automatic high brightness mode (HBM)` | Enable peak luminance based on sunlight | Yes | N/A |
|  | `Saturation` | Control the saturation level of the display | Yes | N/A |
| **Leds** | `Pixel torch` | Adjust the brightness of the PixelParts flashlight QS-Tile | PixelParts Flashlight QS | N/A |

## Including PixelParts

- Remove HbmSVManager:

[Commit 1/2 (vendor)](https://gitlab.com/EvoX/vendor_google_bluejay/-/commit/eb75035610983f92f2f7d2f245ba3aaea1664548)

[Commit 2/2 (device tree)](https://github.com/Evolution-X-Devices/device_google_bluejay/commit/6f905d723d22a9df8de3627958196f515b54add5)

- Clone this repository to packages/apps/PixelParts directory in your AOSP build tree:

```
croot && git clone https://github.com/Evolution-X-Devices/packages_apps_PixelParts packages/apps/PixelParts
```

- Include the app during compilation by adding the following to device-*.mk:

[Commit 1/1 (device tree)](https://github.com/Evolution-X-Devices/device_google_bluejay/commit/6822dabe27de84fb7d52e85cb34d9a71c14d1112)

```
# PixelParts
include packages/apps/PixelParts/device.mk
```

This line includes the [device.mk](https://github.com/Evolution-X-Devices/packages_apps_PixelParts/blob/udc/device.mk) file from the PixelParts repository, which will add the PixelParts application, its initialization script (init.rc), and the necessary security policies (sepolicies) to your AOSP build during compilation.

## Testing changes

- When testing new changes, it is much faster to compile the application standalone and update it manually rather than running a full AOSP build. Please note that some changes may require you to chmod 0666 sysfs nodes and set selinux to permissive. When compiling a full AOSP build, this is not needed assuming the init cmds and sepolicies have been properly configured.

Lunch your device and run the following cmd:

```
m PixelParts
```
- This also assumes you are already running an AOSP build including PixelParts as a priv-app in /system_ext.

## Credits

| Work                                                        | Author                                                                      |
| ----------------------------------------------------------- | --------------------------------------------------------------------------- |
| CustomSeekBar preference                                    | [Neobuddy89](https://forum.xda-developers.com/m/neobuddy89.3795148/)        |
| Original AutoHBMService                                     | [Hikari no Tenshi](https://forum.xda-developers.com/m/hikari-no-tenshi.4337348/) & [maxwen](https://forum.xda-developers.com/m/maxwen.4683552/) |
