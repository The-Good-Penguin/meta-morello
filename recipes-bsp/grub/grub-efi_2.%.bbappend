

COMPATIBLE_MACHINE = "morello"
OUTPUTS_NAME       = "grub-efi"

PROVIDES          += "virtual/${OUTPUTS_NAME}"

GRUB_BUILDIN = " boot chain configfile ext2 fat gzio help linux loadenv \
    lsefi normal ntfs ntfscomp part_gpt part_msdos progress read search \
    search_fs_file search_fs_uuid search_label terminal terminfo \
    "

do_deploy() {
    install -d ${DEPLOYDIR}/${OUTPUTS_NAME}
    install -m 644 ${B}/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} "${DEPLOYDIR}/${OUTPUTS_NAME}/"
}