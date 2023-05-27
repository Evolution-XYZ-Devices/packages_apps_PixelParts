# PixelParts app
PRODUCT_PACKAGES += \
    PixelParts

# PixelParts sepolicy
BOARD_SEPOLICY_DIRS += packages/apps/PixelParts/sepolicy

# PixelParts init rc
PRODUCT_COPY_FILES += \
    packages/apps/PixelParts/init/init.pixelparts.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/init.pixelparts.rc
