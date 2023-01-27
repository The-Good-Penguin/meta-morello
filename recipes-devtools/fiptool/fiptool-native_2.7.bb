inherit native

DESCRIPTION = "fiptool - Trusted Firmware tool for packaging"
LICENSE = "BSD-3-Clause"

DEPENDS += "openssl-native"
PROVIDES = "virtual/fiptool-native"

SRC_URI    = "gitsm://git.morello-project.org/morello/trusted-firmware-a;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH  = "morello/master"
SRCREV     = "3ce2815936774fe924ec7538151b71085c2f18d9"

LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

EXTRA_OEMAKE = "HOSTCC='${BUILD_CC}' OPENSSL_DIR=${STAGING_DIR_NATIVE}${prefix_native}"

S = "${WORKDIR}/git"

do_compile() {
    sed -i '/^LDLIBS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile
    oe_runmake fiptool

    sed -i '/^LIB/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/cert_create/Makefile
    oe_runmake certtool
}

do_install() {
    install -D -p -m 0755 tools/fiptool/fiptool ${D}${bindir}/fiptool
    install -D -p -m 0755 tools/cert_create/cert_create ${D}${bindir}/cert_create
}