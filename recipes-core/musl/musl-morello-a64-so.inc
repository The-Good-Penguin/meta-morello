PROVIDES += "virtual/${OUTPUTS_NAME}-a64 virtual/crypt-a64"

do_install:append() {
    install -d ${D}${libdir}
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${TARGET_INSTALL_DIR}${bindir}/ldd
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${D}${libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1
}