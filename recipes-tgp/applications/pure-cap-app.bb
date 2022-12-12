inherit pure-cap-application llvm-morello-native

COMPATIBLE_MACHINE = "morello-linux"
SUMMARY            = "Simple capability application"
DESCRIPTION        = "Applicatyion used in the series of blogs found @ https://www.thegoodpenguin.co.uk/blog/tag/morello/"
OUTPUTS_NAME       = "pure-cap-app"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PROVIDES           = "${OUTPUTS_NAME}"

do_configure[depends] += "llvm-morello-native:do_populate_sysroot virtual/libc:do_populate_sysroot"

FILES:${PN} += "/${APP_DIR} ${bindir}"
SYSROOT_DIRS +="/${APP_DIR}"

SRC_URI = " \
    file://files/pure-cap-app.c \
    file://files/Makefile \
    "

do_configure () {
    install -d ${S}
    install ${WORKDIR}/files/* ${S}
}