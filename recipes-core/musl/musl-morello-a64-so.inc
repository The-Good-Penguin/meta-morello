PROVIDES += "virtual/${OUTPUTS_NAME}-a64 virtual/crypt-a64"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}
    ln -rs ${TARGET_INSTALL_DIR}${libdir}/libc.so ${D}${nonarch_base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1
}