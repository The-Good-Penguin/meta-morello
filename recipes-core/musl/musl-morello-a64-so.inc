PROVIDES:append:a64 = "virtual/libc virtual/crypt virtual/${OUTPUTS_NAME}-a64 virtual/crypt-a64 virtual/musl-a64"
PROVIDES:append:c64 = "virtual/${OUTPUTS_NAME}-a64-static virtual/crypt-a64-static"

do_install:append:a64() {
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${TARGET_INSTALL_DIR}${bindir}/ldd
}