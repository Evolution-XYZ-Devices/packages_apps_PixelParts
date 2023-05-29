![example](https://raw.githubusercontent.com/Evolution-X-Devices/packages_apps_PixelParts/tiramisu/PixelParts.png)

## About the app

I created PixelParts as an alternative to Google's HbmSVManager, providing users with both direct control and more fine-grained customization options for High Brightness Mode (HBM). Unlike the stock functionality, where HBM activation is limited to automatic brightness being enabled, and lux thresholds being predefined, PixelParts allows users to directly control the lux threshold, enable time, and disable time. This allows users to precisely adjust HBM settings according to their preferences, independent of ambient lighting conditions. While the app includes other features, this was its primary purpose from the outset.
## Current features

| Category | Feature | Description | QS Tile | Required kernel changes |
| --- | --- | --- | --- | --- |
| **Camera** | `Torch strength` | Adjust the brightness of the PixelParts flashlight QS-Tile | PixelParts Flashlight QS | N/A |
| **CPU** | `Power efficient workqueue` | Save power by rescheduling work to a core that is already awake. | Yes | [Commit 1/1](https://github.com/Evolution-X-Devices/kernel_google_gs101/commit/3a9c9c32cf09ba99024e3803f395249ecc19c87b) |
| **Display** | `High brightness mode (HBM)` | Enable peak luminance. | Yes | N/A |
|  | `Automatic HBM` | Enable peak luminance based on sunlight | Yes | N/A |
| **Ui-Bench** | `Jitter` | Calculate rendering jitter. | N/A | N/A |
| **USB** | `USB 2.0 fast charge` | Enable CDP mode for faster charging on USB 2.0 ports. | Yes | [Commit 1/1](https://github.com/Evolution-X-Devices/kernel_google_gs101/commit/a594c64a588e307bc8156d75ee62ea64afae5c94) |


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

This line includes the [device.mk](https://github.com/Evolution-X-Devices/packages_apps_PixelParts/blob/tiramisu/device.mk) file from the PixelParts repository, which will add the PixelParts application, its initialization script (init.rc), and the necessary security policies (sepolicies) to your AOSP build during compilation.

## Testing changes

- When testing new changes, it is much faster to compile the application itself rather than a full AOSP build. Please note that some changes may require you to chmod 0666 sysfs nodes and set selinux to permissive. When compiling a full AOSP build, this is not needed assuming the init cmds and sepolicies have been correctly set up.

```
m PixelParts
```
-  This also assumes you are already running an AOSP build including PixelParts as a priv-app in /system_ext.

## Screenshots
![example](https://raw.githubusercontent.com/Evolution-X-Devices/packages_apps_PixelParts/tiramisu/example.png)

## Credits

#### Base preference fragment, boot receiver and CustomSeekbar preference

[Neobuddy89](https://github.com/neobuddy89)

#### Original AutoHBMService

[Hikari-no-Tenshi](https://github.com/Hikari-no-Tenshi)

[Maxwen](https://github.com/maxwen)

#### ShakeUtils

[AmeChanRain](https://github.com/AmeChanRain)

#### First launch warning dialog

[Ramisky](https://github.com/Ramisky)

#### Leonids particle system
[Plattysoft](https://github.com/plattysoft)

#### Randomized color particle effect

[LorDClockaN](https://github.com/LorDClockaN)
