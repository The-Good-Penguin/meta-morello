PROVIDES += "virtual/${OUTPUTS_NAME}-c64 virtual/crypt-c64"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${D}${nonarch_base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1
}