OUTPUTS_NAME       ?= "pure-cap-app-default-name"

FILESEXTRAPATHS:prepend := "${THISDIR}:${THISDIR}/${PN}:"

DEPENDS  += "virtual/musl-morello"

MUSL_HOME = "${STAGING_DIR_TARGET}${MUSL_INSTALL_DIR}"

INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

S = "${WORKDIR}/src"

EXTRA_OEMAKE = "\
               LLVM_PATH='${LLVM_PATH}' \
               NAME='${OUTPUTS_NAME}' \
               OUT='${S}' \
               SYSROOT_LIB='${MUSL_HOME}${libdir}' \
               SYSROOT_INC='${MUSL_HOME}${includedir}' \
               SYSROOT='${MUSL_HOME}' \
               "

do_compile() {
    cd ${S}
    echo "Resource dir ${resourcedir}"
    echo "Musl home ${MUSL_HOME}"
    oe_runmake ${EXTRA_OEMAKE}
}

do_install() {
    install -d ${D}/${APP_DIR} ${D}${bindir}
    install ${S}/${OUTPUTS_NAME} ${D}/${APP_DIR}/${OUTPUTS_NAME}
    install ${S}/${OUTPUTS_NAME} ${D}${bindir}
}