PROVIDES += "virtual/${OUTPUTS_NAME}-a64 virtual/crypt-a64"

do_install:append() {
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${TARGET_INSTALL_DIR}${bindir}/ldd
}