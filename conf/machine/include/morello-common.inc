require ${COREBASE}/meta/conf/machine/include/arm/armv8-2a/tune-neoversen1.inc
require conf/machine/include/morello-1.5.inc

MACHINEOVERRIDES =. "morello:"

LLVM_SHARED_SOURCE ?= "${TMPDIR}/work-shared/${MACHINE}/llvm-morello-source"

MORELLO_ARCH = "c64"

MUSL_INSTALL_DIR=""
MUSL_INSTALL_DIR:append:gnu="/${ARCH_TRIPLE}"

C64_ARCH_TRIPLE = "aarch64-linux-musl_purecap"
A64_ARCH_TRIPLE = "aarch64-linux-gnu"

A64_LIB_TRIPLE = "aarch64-unknown-linux-gnu"
C64_LIB_TRIPLE = "aarch64-unknown-linux-musl_purecap"

GLOBAL_ARCH_TRIPLE:a64 = "${A64_ARCH_TRIPLE}"
GLOBAL_LIB_TRIPLE:a64  = "${A64_LIB_TRIPLE}"
GLOBAL_ARCH_FLAGS:a64  = "-march=armv8"

GLOBAL_ARCH_TRIPLE:c64 = "${C64_ARCH_TRIPLE}"
GLOBAL_LIB_TRIPLE:c64  = "${C64_LIB_TRIPLE}"
GLOBAL_ARCH_FLAGS:c64  = "-march=morello+c64 -mabi=purecap"


TMPDIR_BSP   = "tmp-bsp"
TMPDIR_LINUX = "tmp-linux"

APP_DIR ?= "pure-cap-apps"