PROVIDES += "virtual/${OUTPUTS_NAME}-c64 virtual/crypt-c64"

do_install:append() {
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${TARGET_INSTALL_DIR}${bindir}/ldd
}