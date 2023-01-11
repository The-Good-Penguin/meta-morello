inherit deploy nopackages

COMPATIBLE_MACHINE = "morello-linux-glibc"
SUMMARY            = "Initramfs for busybox"
DESCRIPTION        = "Initramfs for busybox, bypassing the Yocto way"
LICENSE            = "MIT"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
OUTPUTS_NAME       = "morello-initramfs"

DEPENDS           += "virtual/morello-busybox gen-init-cpio-native"
PROVIDES           = "${OUTPUTS_NAME}"

BB_DONT_CACHE        = "1"
INHIBIT_DEFAULT_DEPS = "1"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI = "file://files/init.sh \
           file://files/initramfs.list.template \
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

  install -d ${D}/${OUTPUTS_NAME}

  rm -f ${D}/${OUTPUTS_NAME}/initramfs

  {
    env -C ${WORKDIR} ${STAGING_BINDIR_NATIVE}/gen_init_cpio "${WORKDIR}/files/initramfs.list"
  } > ${D}/${OUTPUTS_NAME}/initramfs

}

do_deploy() {
  install -d ${DEPLOYDIR}/${OUTPUTS_NAME}
  install ${D}/${OUTPUTS_NAME}/initramfs ${DEPLOYDIR}/${OUTPUTS_NAME}/initramfs
}
addtask deploy after do_install