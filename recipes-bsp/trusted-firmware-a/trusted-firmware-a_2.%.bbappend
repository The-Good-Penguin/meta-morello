inherit llvm-morello-native

COMPATIBLE_MACHINE = "morello"
SUMMARY            = "TF-A to be compiled with LLVM Morello"
OUTPUTS_NAME       = "trusted-firmware-a"
SECTION            = "firmware"

PROVIDES          += "virtual/${OUTPUTS_NAME}"

SRC_URI     = "gitsm://git.morello-project.org/morello/trusted-firmware-a;protocol=https;name=tfa;branch=${SRCBRANCH}"
SRCREV_tfa  = "3ce2815936774fe924ec7538151b71085c2f18d9"
PV          = "2.7+git${SRCPV}"

SRCBRANCH        = "morello/master"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

TFA_MBEDTLS              = "1"
TFA_MBEDTLS_DIR          = "mbedtls"
TFA_MBEDTLS_BRANCH       = "mbedtls-2.28"
SRC_URI_MBEDTLS          = "git://github.com/ARMmbed/mbedtls;protocol=https;destsuffix=git/mbedtls;name=mbedtls;branch=${TFA_MBEDTLS_BRANCH}"
SRCREV_mbedtls           = "8b3f26a5ac38d4fdccbc5c5366229f3e01dafcc0"
LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

TFA_PLATFORM     = "morello"
TFA_UBOOT        = "0"
TFA_BUILD_TARGET = "bl1 bl2 bl31 dtbs"
TFA_DEBUG        = "0"

TFA_INSTALL_TARGET = "bl1 bl2 bl31 dtbs morello-soc morello_fw_config morello_tb_fw_config morello_nt_fw_config "

ARM_TF_ARCH = "aarch64"

EXTRA_OEMAKE += "\
                CREATE_KEYS=1 \
                GENERATE_COT=1 \
                ROT_KEY=plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem \
                TRUSTED_BOARD_BOOT=1 \
                ARM_ROTPK_LOCATION=devel_rsa \
                TARGET_PLATFORM=soc \
                ENABLE_MORELLO_CAP=1 \
                ARCH='${ARM_TF_ARCH}' \
            "

do_install:append() {
    install -m 0644 ${S}/plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem "${D}/firmware/"arm_rotprivk_rsa.pem
}