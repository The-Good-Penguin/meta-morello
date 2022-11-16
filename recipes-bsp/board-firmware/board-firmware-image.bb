inherit deploy nopackages

COMPATIBLE_MACHINE = "morello"
SUMMARY            = "SDK Card Firmware Image "
DESCRIPTION        = "Image containing all the firmwares and motherboard configuration files"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
OUTPUTS_NAME       = "board-firmware-sd-image"
SECTION            = "firmware"

BB_DONT_CACHE      = "1"

DEPENDS           += "virtual/board-firmware virtual/scp-firmware virtual/trusted-firmware-a virtual/uefi mtools-native"
PROVIDES           = "virtual/board-firmware-image"

MCP_BLOB_ID   = "54464222-a4cf-4bf8-b1b6-cee7dade539e"

FIP_OPTIONS   = "\
                --tb-fw '${FIRMWARE_PATH}/tf-bl2.bin' \
                --soc-fw '${FIRMWARE_PATH}/tf-bl31.bin' \
                --nt-fw '${FIRMWARE_PATH}/uefi.bin' \
                --fw-config '${FIRMWARE_PATH}/morello_fw_config.dtb' \
                --tb-fw-config '${FIRMWARE_PATH}/morello_tb_fw_config.dtb' \
                --nt-fw-config '${FIRMWARE_PATH}/morello_nt_fw_config.dtb' \
                --trusted-key-cert '${FIRMWARE_PATH}/tfa_certs/trusted_key.crt' \
                --soc-fw-key-cert '${FIRMWARE_PATH}/tfa_certs/bl31_key.crt' \
                --nt-fw-key-cert '${FIRMWARE_PATH}/tfa_certs/bl33_key.crt' \
                --soc-fw-cert '${FIRMWARE_PATH}/tfa_certs/bl31.crt' \
                --nt-fw-cert '${FIRMWARE_PATH}/tfa_certs/bl33.crt' \
                --tb-fw-cert '${FIRMWARE_PATH}/tfa_certs/bl2.crt' \
                "

FIRMWARE_PATH         = "${RECIPE_SYSROOT}/firmware"
SYSROOT_SOFTWARE_PATH = "${RECIPE_SYSROOT}/board-firmware/SOFTWARE"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"
do_install[depends] += "virtual/board-firmware:do_populate_sysroot"
do_install[depends] += "virtual/scp-firmware:do_populate_sysroot"
do_install[depends] += "virtual/uefi:do_populate_sysroot"
do_install[depends] += "virtual/trusted-firmware-a:do_populate_sysroot"

do_install:prepend() {

    ${FIRMWARE_PATH}/fiptool create \
         --scp-fw "${FIRMWARE_PATH}/scp_ramfw_soc.bin" \
        "${FIRMWARE_PATH}/scp_fw.bin"

    ${FIRMWARE_PATH}/fiptool create \
        --blob uuid="${MCP_BLOB_ID}",file="${FIRMWARE_PATH}/mcp_ramfw_soc.bin" \
        "${FIRMWARE_PATH}/mcp_fw.bin"

    mkdir -p "${FIRMWARE_PATH}/tfa_certs"
    ${FIRMWARE_PATH}/cert_create ${FIP_OPTIONS} -n --tfw-nvctr 0 --ntfw-nvctr 0 \
        --rot-key ${FIRMWARE_PATH}/arm_rotprivk_rsa.pem

    ${FIRMWARE_PATH}/fiptool update ${FIP_OPTIONS} "${FIRMWARE_PATH}/fip.bin"
}

do_install() {

    cp -rf ${FIRMWARE_PATH}/fip.bin ${SYSROOT_SOFTWARE_PATH}/fip.bin
    cp -rf ${FIRMWARE_PATH}/scp_fw.bin ${SYSROOT_SOFTWARE_PATH}/scp_fw.bin
    cp -rf ${FIRMWARE_PATH}/mcp_fw.bin ${SYSROOT_SOFTWARE_PATH}/mcp_fw.bin

    install -d ${D}/board-firmware/SOFTWARE
    install -m 644 ${SYSROOT_SOFTWARE_PATH}/fip.bin ${D}/board-firmware/SOFTWARE/fip.bin
    install -m 644 ${SYSROOT_SOFTWARE_PATH}/scp_fw.bin ${D}/board-firmware/SOFTWARE/scp_fw.bin
    install -m 644 ${SYSROOT_SOFTWARE_PATH}/mcp_fw.bin ${D}/board-firmware/SOFTWARE/mcp_fw.bin

    dd if=/dev/zero of=${OUTPUTS_NAME}.img bs=512K count=100
    mformat -i ${OUTPUTS_NAME}.img ::
    mcopy -i ${OUTPUTS_NAME}.img -s -Q -p -m ${RECIPE_SYSROOT}/board-firmware/* ::

    install -d ${D}/firmware/board-firmware-image
    install -m 644 ${OUTPUTS_NAME}.img ${D}/firmware/board-firmware-image/${OUTPUTS_NAME}.img
}

do_deploy() {
    cp -rf ${D}/firmware/board-firmware-image/${OUTPUTS_NAME}.img ${DEPLOYDIR}/${OUTPUTS_NAME}.img
}
addtask deploy after do_install