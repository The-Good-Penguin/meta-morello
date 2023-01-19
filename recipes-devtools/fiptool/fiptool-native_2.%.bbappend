SRC_URI    = "gitsm://git.morello-project.org/morello/trusted-firmware-a;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH  = "morello/master"
SRCREV     = "3ce2815936774fe924ec7538151b71085c2f18d9"

LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

EXTRA_OEMAKE = "HOSTCC='${BUILD_CC}' OPENSSL_DIR=${STAGING_DIR_NATIVE}${prefix_native}"

PROVIDES = "virtual/fiptool-native"

S = "${WORKDIR}/git"

do_compile:append() {
    sed -i '/^LIB/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/cert_create/Makefile
    oe_runmake certtool
}

do_install:append() {
    install -D -p -m 0755 tools/cert_create/cert_create ${D}${bindir}/cert_create
}