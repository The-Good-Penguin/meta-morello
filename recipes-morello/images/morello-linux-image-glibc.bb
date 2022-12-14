inherit deploy nopackages

COMPATIBLE_MACHINE = "morello-linux-glibc"
SUMMARY            = "Bootable Morello Linux Image"
DESCRIPTION        = "Image that goes on a bootable device, can be DD'ed onto a USB stick"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
OUTPUTS_NAME       = "morello-linux-image"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS           += "virtual/kernel morello-initramfs mtools-native e2fsprogs-native coreutils-native bc-native util-linux-native"
PROVIDES           = "${OUTPUTS_NAME}"

IMAGE_SIZE           = "100"

# Yocto does not like function derived vars expanded in other functions, it does not like passing by argument from non-constant
# variables either...so I am just cheerfully hardcoding this as I have more important things to do in life
IMAGE_SECTORS        = "204800"

LBA                  = "512"
PART_START_ALIGNMENT = "2048"

ESP_IMAGE            = "${OUTPUTS_NAME}-esp"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"
do_install[depends] += "${MORELLO_ROOTFS_IMAGE}:do_image_complete"

def get_next_part_start (d):
    next_image_start = int(d.getVar('IMAGE_SECTORS')) + int(d.getVar('PART_START_ALIGNMENT')) + int(d.getVar('PART_START_ALIGNMENT')) - 1
    next_image_start = next_image_start & ~(int(d.getVar('PART_START_ALIGNMENT')) -1)
    return next_image_start

add_to_image() {
    mcopy -i ${1} -m -D overwrite ${2} ::${3}
}

mult() {
    local ret=$(echo "${1} * ${2}" | bc)
    echo ${ret}
}

create_gpt() {

    local esp_type="C12A7328-F81F-11D2-BA4B-00A0C93EC93B"
    local linux_type="0FC63DAF-8483-4772-8E79-3D69D8477DE4"

    local part_start_esp=${PART_START_ALIGNMENT}
    local part_start_linux="${@get_next_part_start(d)}"

    {
        echo "label: gpt"
        echo "start=${part_start_esp}, size=${IMAGE_SECTORS}, name=ESP, type=${esp_type}"
        echo "start=${part_start_linux}, size=${IMAGE_SECTORS}, name=root, type=${linux_type}"
    } | sfdisk -q "${1}"

    dd if="${2}" of="${1}" seek=$(mult ${part_start_esp} ${LBA}) bs=8M conv=notrunc,sparse oflag=seek_bytes status=progress
    dd if="${3}" of="${1}" seek=$(mult ${part_start_linux} ${LBA}) bs=8M conv=notrunc,sparse oflag=seek_bytes status=progress
}

do_install() {

    local part0="${BSP_GRUB_DIR}/grub-efi-bootaa64.efi"
    local part1="${BSP_GRUB_DIR}/grub-config.cfg"
    local part2="${BSP_DTB_DIR}/morello-soc.dtb"
    local part3="${DEPLOY_DIR}/images/morello-linux-glibc/Image"
    local part4="${DEPLOY_DIR}/images/morello-linux-glibc/morello-initramfs/initramfs"

    # create the ESP
    dd if=/dev/zero of=${ESP_IMAGE}.img bs=1024K count=${IMAGE_SIZE}
    mformat -i ${ESP_IMAGE}.img -v ESP ::

    mmd -i ${ESP_IMAGE}.img ::/EFI
    mmd -i ${ESP_IMAGE}.img ::/EFI/BOOT

    add_to_image ${ESP_IMAGE}.img ${part0} /EFI/BOOT/BOOTAA64.EFI
    add_to_image ${ESP_IMAGE}.img ${part1} /EFI/BOOT/grub.cfg
    add_to_image ${ESP_IMAGE}.img ${part2} /morello.dtb
    add_to_image ${ESP_IMAGE}.img ${part3} /Image
    add_to_image ${ESP_IMAGE}.img ${part4} /initramfs

    : > ${OUTPUTS_NAME}.img
    truncate --size="$(mult ${IMAGE_SIZE} 3)M" ${OUTPUTS_NAME}.img

    create_gpt ${OUTPUTS_NAME}.img ${ESP_IMAGE}.img ${DEPLOY_DIR}/images/morello-linux-glibc/rootfs-morello-linux-glibc.ext4
    install ${OUTPUTS_NAME}.img ${D}/${OUTPUTS_NAME}.img
}

do_deploy() {
    install ${D}/${OUTPUTS_NAME}.img ${DEPLOYDIR}/${OUTPUTS_NAME}-${MORELLO_ARCH}-${TCLIBC}.img
}
addtask deploy after do_install