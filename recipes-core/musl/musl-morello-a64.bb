require musl-morello-common.inc

EXTRA_CONFIGUREOPTS = "--disable-morello"

PROVIDES:append:a64 = "virtual/libc virtual/${OUTPUTS_NAME}-a64 virtual/crypt-a64"
PROVIDES:append:c64 = "virtual/${OUTPUTS_NAME}-a64-static virtual/crypt-a64-static"

ARCH_TRIPLE = "${A64_ARCH_TRIPLE}"
LIB_TRIPLE  = "${A64_LIB_TRIPLE}"
ARCH_FLAGS  = "-march=armv8"

do_build_so:append:a64() {
    oe_runmake install DESTDIR="${D}"
    ln -rs ${D}${libdir}/libc.so ${D}${bindir}/ldd
}