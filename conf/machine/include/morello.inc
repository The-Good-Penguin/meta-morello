require ${COREBASE}/meta/conf/machine/include/arm/armv8-2a/tune-neoversen1.inc

MACHINEOVERRIDES =. "morello:"

PREFERRED_VERSION_scp-firmware       ?= "2.10%"
PREFERRED_VERSION_trusted-firmware-a ?= "2.7%"
PREFERRED_VERSION_board-firmware     ?= "1.4"

PREFERRED_PROVIDER_virtual/kernel    ?= "linux-morello-1.5"