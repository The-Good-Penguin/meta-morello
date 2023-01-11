inherit llvm-morello-native

COMPATIBLE_MACHINE = "morello-linux"
SUMMARY            = "CHERI enabled busybox"
LICENSE            = "GPL-2.0-only"
SECTION            = "base"

FILESEXTRAPATHS:prepend := "${THISDIR}:${THISDIR}/${PN}:"

DEPENDS  += "virtual/kernel virtual/musl-morello"
PROVIDES += "virtual/morello-busybox"

RPROVIDES:${PN} += "busybox-morello"

SRC_URI = " \
    git://git.morello-project.org/morello/morello-busybox;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    file://files/0001-Remove-elf-patch-append-toolchain-with-relative-path.patch \
    "

PV  = "git${SRCPV}"
S   = "${WORKDIR}/git"

do_compile[depends] = "virtual/kernel:do_install"

EXTRA_OEMAKE = "\
                MUSL_HOME='${STAGING_DIR_TARGET}${MUSL_INSTALL_DIR}${prefix}' \
                KHEADERS='${STAGING_KERNEL_BUILDDIR}/kernel-headers/include/' \
            "

FILES:${PN}  += "/busybox/busybox"
SYSROOT_DIRS += "/busybox"

INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_configure() {
    export CFLAGS=""
    oe_runmake ${EXTRA_OEMAKE} LLVM_PATH=${LLVM_PATH} morello_busybox_defconfig
}

do_compile() {
    export CFLAGS=""
    local resourcedir=$(${CC} -print-resource-dir)
    EXTRA_OEMAKE="${EXTRA_OEMAKE} CLANG_RESOURCE_DIR='${resourcedir}'"
    oe_runmake ${EXTRA_OEMAKE} LLVM_PATH=${LLVM_PATH}
}

do_install() {
    install -d ${D}/busybox
    install -m 0755  ${S}/busybox ${D}/busybox/busybox
}