require musl-morello-common.inc

EXTRA_CONFIGUREOPTS = "--enable-morello"

ARCH_TRIPLE = "${C64_ARCH_TRIPLE}"
LIB_TRIPLE  = "${C64_LIB_TRIPLE}"
ARCH_FLAGS  = "-march=morello+c64 -mabi=purecap"