PROVIDES:append:c64 = "virtual/musl-c64 virtual/libc virtual/crypt virtual/${OUTPUTS_NAME}-c64 virtual/crypt-c64"
PROVIDES:append:a64 = "virtual/${OUTPUTS_NAME}-c64-static virtual/crypt-c64-static"

do_install:append:c64() {
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${TARGET_INSTALL_DIR}${bindir}/ldd
}