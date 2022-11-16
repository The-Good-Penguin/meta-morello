require ${COREBASE}/meta/conf/machine/include/arm/armv8-2a/tune-neoversen1.inc

MACHINEOVERRIDES =. "morello:"

PREFERRED_VERSION_scp-firmware       ?= "2.10%"
PREFERRED_VERSION_trusted-firmware-a ?= "2.4%"

EXTRA_IMAGEDEPENDS_morello_sd_image  += "virtual/board-firmware-image"
IMAGE_FSTYPES_morello_sd_image        = "wic"
WKS_FILE_morello_sd_image             = "morello.wks"