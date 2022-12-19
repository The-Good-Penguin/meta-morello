inherit python3native llvm-morello-native

COMPATIBLE_MACHINE = "morello"
SUMMARY            = "EDK2 to be compiled with LLVM Morello"
OUTPUTS_NAME       = "uefi"
SECTION            = "firmware"

PROVIDES          += "virtual/${OUTPUTS_NAME}"
DEPENDS           += "acpica-native python3-native"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "\
    ${EDK2_SRC_URI};name=edk2;destsuffix=edk2 \
    ${EDK2_PLATFORMS_SRC_URI};name=edk2-platforms;destsuffix=edk2/edk2-platforms \
    ${EDK2_NON_OSI_SRC_URI};name=edk2-non-osi;destsuffix=edk2-non-osi \
    file://files/0001-Basetools-remove-brotli-build-errors.patch \
    "

EDK2_SRC_URI           = "gitsm://git.morello-project.org/morello/edk2;branch=morello/master;protocol=https"
EDK2_PLATFORMS_SRC_URI = "gitsm://git.morello-project.org/morello/edk2-platforms;branch=morello/master;protocol=https"
EDK2_NON_OSI_SRC_URI   = "git://github.com/tianocore/edk2-non-osi;branch=master;protocol=https"
SRCREV_edk2-non-osi    = "0320db977fb27e63424b0953a3020bb81c89e8f0"
SRCREV_edk2            = "${AUTOREV}"
SRCREV_edk2-platforms  = "${AUTOREV}"
PV                     = "git${SRCPV}"

EDK2_BUILD_RELEASE = "0"
EDK2_PLATFORM      = "morellosoc"
EDK2_PLATFORM_DSC  = "edk2-platforms/Platform/ARM/Morello/MorelloPlatformSoc.dsc"
EDK2_BIN_NAME      = "BL33_AP_UEFI.fd"
EDK2_ARCH          = "AARCH64"
EDK2_BUILD_FLAGS  += "-D ENABLE_MORELLO_CAP=1"
EDK_COMPILER       = "CLANG35"

export CLANG35_AARCH64_PREFIX = "${TARGET_PREFIX}"
export CLANG35_BIN            = "${LLVM_PATH}/"
export CLANG35_AARCH64_PREFIX = "${LLVM_PATH}/llvm-"
export PACKAGES_PATH          = "${S}:${S}/edk2-platforms:${WORKDIR}/edk2-non-osi"
export CC_PATH                = "${LLVM_PATH}/clang"
export LLVM_PATH_35           = "${LLVM_PATH}"

do_deploy[noexec] = "1"

do_install() {
    install -d ${D}/firmware
    install ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/${EDK2_BIN_NAME} ${D}/firmware/uefi.bin
}