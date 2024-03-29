inherit llvm-morello-native
require recipes-kernel/linux/linux-yocto.inc
COMPATIBLE_MACHINE = "morello-linux"
SUMMARY            = "Morello Linux kernel fork"
DESCRIPTION        = "CHERI aware Linux kernel"
OUTPUTS_NAME       = "morello-linux"
SECTION            = "kernel"

FILESEXTRAPATHS:prepend := "${THISDIR}:${THISDIR}/${PN}:"

DEPENDS                 += "rsync-native"
PROVIDES                += "virtual/${OUTPUTS_NAME}"

SRC_URI = " \
    git://git.morello-project.org/morello/kernel/linux;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    "

PV  = "${LINUX_VERSION}+git${SRCPV}"
S   = "${WORKDIR}/git"

BUILD_CC  = "${LLVM_PATH}/clang"
BUILD_CPP = "${LLVM_PATH}/clang"
BUILD_CXX = "${LLVM_PATH}/clang++"
BUILD_LD  = "${LLVM_PATH}/ld.lld"

KERNEL_CC  = "${LLVM_PATH}/clang"
KERNEL_LD  = "${LLVM_PATH}/ld.lld"

ARCH = "arm64"

LINUX_KERNEL_TYPE     ?= "standard"

KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_IMAGETYPE       = "Image"

KBUILD_DEFCONFIG = "morello_transitional_pcuabi_defconfig"

do_kernel_configcheck[noexec] = "1"
do_kernel_configme[depends]  += "llvm-morello-native:do_populate_sysroot"

do_configure:prepend(){
    rm -f ${B}/.config
}

do_install:append() {
    oe_runmake headers_install ARCH=arm64 INSTALL_HDR_PATH=${STAGING_KERNEL_BUILDDIR}/kernel-headers
}