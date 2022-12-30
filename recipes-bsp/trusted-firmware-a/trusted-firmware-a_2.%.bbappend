inherit deploy nopackages llvm-morello-native

COMPATIBLE_MACHINE = "morello"
SUMMARY            = "TF-A to be compiled with LLVM Morello"
OUTPUTS_NAME       = "trusted-firmware-a"
SECTION            = "firmware"

PROVIDES          += "virtual/${OUTPUTS_NAME}"

SRC_URI = "gitsm://git.morello-project.org/morello/trusted-firmware-a;protocol=https;branch=${SRCBRANCH}"
SRCREV  = "${AUTOREV}"
PV      = "2.7+git${SRCPV}"

SRCBRANCH        = "morello/master"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

TFA_MBEDTLS              = "1"
TFA_MBEDTLS_DIR          = "mbedtls"
TFA_MBEDTLS_BRANCH       = "mbedtls-2.28"
SRC_URI_MBEDTLS          = "git://github.com/ARMmbed/mbedtls;protocol=https;destsuffix=git/mbedtls;branch=${TFA_MBEDTLS_BRANCH}"
SRCREV_mbedtls           = "8b3f26a5ac38d4fdccbc5c5366229f3e01dafcc0"
LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

B             = "${WORKDIR}/build"
S             = "${WORKDIR}/git"

TFA_PLATFORM     = "morello"
TFA_UBOOT        = "0"
TFA_BUILD_TARGET = "bl1 bl2 bl31 dtbs"
TFA_DEBUG        = "0"

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

unset do_compile[cleandirs]

do_compile:prepend() {
    make -C ${S}/tools/fiptool
    make -C ${S}/tools/cert_create
}

do_install() {

    install -d -m 755 ${D}/firmware
    for atfbin in ${TFA_INSTALL_TARGET}; do
        processed="0"
        if [ -f ${BUILD_DIR}/$atfbin.bin ]; then
            echo "Install $atfbin.bin"
            install -m 0644 ${BUILD_DIR}/$atfbin.bin ${D}/firmware/tf-$atfbin.bin
            processed="1"
        fi
        if [ "$processed" = "0" ]; then
            if  ["$atfbin" != "dtbs" ]; then
                bberror "Unsupported TFA_INSTALL_TARGET target $atfbin"
                exit 1
            fi
        fi
    done

    install -m 0644 "${BUILD_DIR}/fdts/morello-soc.dtb" "${D}/firmware/morello-soc.dtb"
    install -m 0644 "${BUILD_DIR}/fdts/morello_fw_config.dtb" "${D}/firmware/morello_fw_config.dtb"
    install -m 0644 "${BUILD_DIR}/fdts/morello_tb_fw_config.dtb" "${D}/firmware/morello_tb_fw_config.dtb"
    install -m 0644 "${BUILD_DIR}/fdts/morello_nt_fw_config.dtb" "${D}/firmware/morello_nt_fw_config.dtb"
    install -m 0644 ${S}/plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem "${D}/firmware/"arm_rotprivk_rsa.pem
    install -m 0744 ${S}/tools/fiptool/fiptool "${D}/firmware/"fiptool
    install -m 0744 ${S}/tools/cert_create/cert_create "${D}/firmware/"cert_create
}

do_deploy() {
    install -d  ${DEPLOYDIR}/${OUTPUTS_NAME}
    cp -rf ${D}/firmware/morello-soc.dtb ${DEPLOYDIR}/${OUTPUTS_NAME}/morello-soc.dtb
}
addtask deploy after do_install before do_build