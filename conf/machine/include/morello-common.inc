require ${COREBASE}/meta/conf/machine/include/arm/armv8-2a/tune-neoversen1.inc
require conf/machine/include/morello-1.5.inc

MACHINEOVERRIDES =. "morello:"

LLVM_SHARED_SOURCE ?= "${TMPDIR}/work-shared/${MACHINE}/llvm-morello-source"

MORELLO_BSP    ?= "${TMPDIR}/morello_bsp-oe-linux"
MORELLO_LINUX  ?= "${TMPDIR}/morello_distro-oe-linux"