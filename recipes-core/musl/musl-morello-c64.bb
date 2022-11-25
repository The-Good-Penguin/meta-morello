require musl-morello-common.inc

EXTRA_CONFIGUREOPTS = "--enable-morello"

PROVIDES:append:c64 = "virtual/libc virtual/${OUTPUTS_NAME}-c64 virtual/crypt-c64"
PROVIDES:append:a64 = "virtual/${OUTPUTS_NAME}-c64-static virtual/crypt-c64-static"

ARCH_TRIPLE = "${C64_ARCH_TRIPLE}"
LIB_TRIPLE  = "${C64_LIB_TRIPLE}"
ARCH_FLAGS  = "-march=morello+c64 -mabi=purecap"

do_build_so:append:c64() {
    oe_runmake install DESTDIR="${D}"
    ln -rs ${D}${libdir}/libc.so ${D}${bindir}/ldd
}