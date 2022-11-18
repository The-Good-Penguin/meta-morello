inherit llvm-morello-native
require recipes-kernel/linux/linux-yocto.inc
COMPATIBLE_MACHINE = "morello"
DESCRIPTION        = "CHERI enabled Linux kernel"
OUTPUTS_NAME       = "morello-linux"
SECTION            = "kernel"

FILESEXTRAPATHS:prepend := "${THISDIR}:${THISDIR}/${PN}:"
PROVIDES                +="virtual/${OUTPUTS_NAME}"

SUMMARY = "Morello Linux kernel"
SRC_URI = " \
    git://git.morello-project.org/morello/kernel/linux;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    file://defconfig \
    "

PV  = "${LINUX_VERSION}+git${SRCPV}"
S   = "${WORKDIR}/git"

BUILD_CC  = "${LLVM_PATH}/clang"
BUILD_CPP = "${LLVM_PATH}/clang"
BUILD_CXX = "${LLVM_PATH}/clang++"

KERNEL_CC  = "${LLVM_PATH}/clang"
KERNEL_LD  = "${LLVM_PATH}/ld.lld"

do_kernel_configcheck[noexec] = "1"

KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_IMAGETYPE       = "Image"