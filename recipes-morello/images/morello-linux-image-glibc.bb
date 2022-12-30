inherit deploy nopackages

COMPATIBLE_MACHINE = "morello-linux-glibc"
SUMMARY            = "Bootable Morello Linux Image"
DESCRIPTION        = "Image that goes on a bootable device, can be DD'ed onto a USB stick"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
OUTPUTS_NAME       = "morello-linux-image"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS             += "virtual/kernel morello-initramfs mtools-native e2fsprogs-native coreutils-native bc-native util-linux-native"
PROVIDES             = "${OUTPUTS_NAME}"

ESP_SIZE             = "100"
BOOT_SECTORS         = "204800"

LBA                  = "512"
PART_START_ALIGNMENT = "2048"

ESP_IMAGE            = "${OUTPUTS_NAME}-esp"

ROOTFS               = "${DEPLOY_DIR}/images/morello-linux-glibc/rootfs-morello-linux-glibc.ext4"

do_compile[noexec]        = "1"
do_configure[depends]    += "${MORELLO_ROOTFS_IMAGE}:do_image_complete morello-initramfs:do_deploy"
do_configure[mcdepends]  += "mc:${BB_CURRENT_MC}:morello-firmware:board-firmware-image:do_deploy"

def get_next_part_start (d):
    next_image_start = int(d.getVar('BOOT_SECTORS')) + int(d.getVar('PART_START_ALIGNMENT')) + int(d.getVar('PART_START_ALIGNMENT')) - 1
    next_image_start = next_image_start & ~(int(d.getVar('PART_START_ALIGNMENT')) -1)
    return next_image_start

mult() {
    local ret=$(echo "${1} * ${2}" | bc)
    echo ${ret}
}

add() {
    local ret=$(echo "${1} + ${2}" | bc)
    echo ${ret}
}

div() {
    local ret=$(echo "${1} / ${2}" | bc)
    echo ${ret}
}

get_size() {
    local link=$(readlink -f ${1})
    local size=$(stat --dereference --format="%s" ${link})
    local ret=${size% *}
    echo ${ret}
}

add_to_image() {
    mcopy -i ${1} -m -D overwrite ${2} ::${3}
}

get_uuid() {
    local part="$(blkid ${1})"
    local tmp=${part#*\"}
    local uuid=$(echo $tmp | head -c 36)
    echo ${uuid}
}

create_gpt() {

    local esp_type="C12A7328-F81F-11D2-BA4B-00A0C93EC93B"
    local linux_type="0FC63DAF-8483-4772-8E79-3D69D8477DE4"

    local part_start_esp=${PART_START_ALIGNMENT}
    local part_start_linux="${@get_next_part_start(d)}"

    local size=$(get_size ${ROOTFS})
    local tmp=$(add ${size} ${LBA})

    local rootfs_sectors=$(div ${tmp} ${LBA})
    local rootfs_uuid=$(get_uuid ${ROOTFS})

    echo "Sectors $rootfs_sectors ${BOOT_SECTORS}"

    {
        echo "label: gpt"
        echo "start=${part_start_esp}, size=${BOOT_SECTORS}, name=ESP, type=${esp_type}"
        echo "start=${part_start_linux}, size=${rootfs_sectors}, name=root, type=${linux_type}, uuid=${rootfs_uuid}"
    } | sfdisk -q "${1}"

    dd if="${2}" of="${1}" seek=$(mult ${part_start_esp} ${LBA}) bs=8M conv=notrunc,sparse oflag=seek_bytes status=progress
    dd if="${3}" of="${1}" seek=$(mult ${part_start_linux} ${LBA}) bs=8M conv=notrunc,sparse oflag=seek_bytes status=progress
}

do_configure() {
    local grub="${BSP_GRUB_DIR}/grub-config.cfg"
    local uuid=$(get_uuid ${ROOTFS})
    echo ${uuid}
    rm -f ${BSP_GRUB_DIR}/grub-config.cfg.processed
    sed -e "s@%UUID%@${uuid}@" \
    "${BSP_GRUB_DIR}/grub-config.cfg" > "${BSP_GRUB_DIR}/grub-config.cfg.processed"
}

do_install() {

    local part0="${BSP_GRUB_DIR}/grub-efi-bootaa64.efi"
    local part1="${BSP_GRUB_DIR}/grub-config.cfg.processed"
    local part2="${BSP_DTB_DIR}/morello-soc.dtb"
    local part3="${DEPLOY_DIR}/images/morello-linux-glibc/Image"
    local part4="${DEPLOY_DIR}/images/morello-linux-glibc/morello-initramfs/initramfs"

    rm -f ${ESP_IMAGE}.img

    # create the ESP
    dd if=/dev/zero of=${ESP_IMAGE}.img bs=1024K count=${ESP_SIZE}
    mformat -i ${ESP_IMAGE}.img -v ESP ::

    mmd -i ${ESP_IMAGE}.img ::/EFI
    mmd -i ${ESP_IMAGE}.img ::/EFI/BOOT

    add_to_image ${ESP_IMAGE}.img ${part0} /EFI/BOOT/BOOTAA64.EFI
    add_to_image ${ESP_IMAGE}.img ${part1} /EFI/BOOT/grub.cfg
    add_to_image ${ESP_IMAGE}.img ${part2} /morello.dtb
    add_to_image ${ESP_IMAGE}.img ${part3} /Image
    add_to_image ${ESP_IMAGE}.img ${part4} /initramfs

    local size=$(get_size ${ROOTFS})

    local rootfs_size=$(div ${size} 1048576)
    local total_size=$(add ${ESP_SIZE} ${rootfs_size})

    : > ${OUTPUTS_NAME}.img
    truncate --size="$(add ${total_size} 50)M" ${OUTPUTS_NAME}.img

    create_gpt ${OUTPUTS_NAME}.img ${ESP_IMAGE}.img ${ROOTFS} ${size}

    install ${OUTPUTS_NAME}.img ${D}/${OUTPUTS_NAME}.img
    install ${ESP_IMAGE}.img ${D}/${ESP_IMAGE}.img
}

do_deploy() {
    install -d ${DEPLOYDIR}/ESP
    install ${D}/${OUTPUTS_NAME}.img ${DEPLOYDIR}/${OUTPUTS_NAME}-${MORELLO_ARCH}-${TCLIBC}.img
    install ${D}/${ESP_IMAGE}.img ${DEPLOYDIR}/ESP/${ESP_IMAGE}.img

}
addtask deploy after do_install