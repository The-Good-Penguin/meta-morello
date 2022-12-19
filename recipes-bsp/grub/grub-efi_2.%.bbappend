inherit deploy nopackages

COMPATIBLE_MACHINE = "morello"
OUTPUTS_NAME       = "grub-efi"

PROVIDES          += "virtual/${OUTPUTS_NAME}"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

GRUB_BUILDIN = " boot chain configfile ext2 fat gzio help linux loadenv \
    lsefi normal ntfs ntfscomp part_gpt part_msdos progress read search \
    search_fs_file search_fs_uuid search_label terminal terminfo \
    "

SRC_URI += "file://files/grub-config.cfg"

do_deploy() {
    install -d ${DEPLOYDIR}/${OUTPUTS_NAME}
    install -m 644 ${B}/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} "${DEPLOYDIR}/${OUTPUTS_NAME}/"
    install -m 644 ${WORKDIR}/files/grub-config.cfg "${DEPLOYDIR}/${OUTPUTS_NAME}/grub-config.cfg"
}
addtask deploy after do_install before do_build