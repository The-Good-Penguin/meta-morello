require musl-morello-common.inc

EXTRA_CONFIGUREOPTS = "--disable-morello"

ARCH_TRIPLE = "${A64_ARCH_TRIPLE}"
LIB_TRIPLE  = "${A64_LIB_TRIPLE}"
ARCH_FLAGS  = "-march=armv8"