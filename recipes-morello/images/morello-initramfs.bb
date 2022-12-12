inherit deploy nopackages

COMPATIBLE_MACHINE = "morello"
SUMMARY            = "Initramfs for busybox"
DESCRIPTION        = "Initramfs for busybox, bypassing the Yocto way"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
OUTPUTS_NAME       = "morello-initramfs"

DEPENDS           += "virtual/morello-busybox gen-init-cpio-native pure-cap-app"
PROVIDES           = "${OUTPUTS_NAME}"

BB_DONT_CACHE        = "1"
INHIBIT_DEFAULT_DEPS = "1"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "file://files/init.sh \
           file://files/initramfs.list.template \
           file://files/README.md \
           "
do_configure[noexec] = "1"
do_compile[noexec]   = "1"
do_deploy[depends]   = "virtual/morello-busybox:do_populate_sysroot"


do_install() {

  local sysroot_prefix="recipe-sysroot"

  sed -e "s@%FILES%@/files@" \
    "${WORKDIR}/files/initramfs.list.template" > "${WORKDIR}/files/initramfs.list.tmp1"

  sed -e "s@%BUSYBOX%@/${sysroot_prefix}/busybox@" \
    "${WORKDIR}/files/initramfs.list.tmp1" > "${WORKDIR}/files/initramfs.list.tmp2"

  sed -e "s@%PREFIX%@/${sysroot_prefix}${prefix}@" \
    "${WORKDIR}/files/initramfs.list.tmp2" > "${WORKDIR}/files/initramfs.list.tmp1"

  sed -e "s@%APP_DIR%@/${APP_DIR}@" \
    "${WORKDIR}/files/initramfs.list.tmp1" > "${WORKDIR}/files/initramfs.list.tmp2"

  sed -e "s@%MUSL%@/${sysroot_prefix}/musl@" \
    "${WORKDIR}/files/initramfs.list.tmp2" > "${WORKDIR}/files/initramfs.list"

  install -d ${D}/${OUTPUTS_NAME}

  rm -f ${D}/${OUTPUTS_NAME}/initramfs

  # I have tried absolute paths but I had to use env -C instead as it was moaning about files not being able to read
  {
    env -C ${WORKDIR} ${STAGING_BINDIR_NATIVE}/gen_init_cpio "${WORKDIR}/files/initramfs.list"
    env -C "${STAGING_DIR_TARGET}/" find . -not -path "./sysroot-providers*" -print0 | env -C "${STAGING_DIR_TARGET}/" cpio --null --owner +0:+0 --create --format=newc
  } > ${D}/${OUTPUTS_NAME}/initramfs
}

do_deploy() {
  install -d ${DEPLOYDIR}/${OUTPUTS_NAME}
  install ${D}/${OUTPUTS_NAME}/initramfs ${DEPLOYDIR}/${OUTPUTS_NAME}/initramfs
}
addtask deploy after do_install