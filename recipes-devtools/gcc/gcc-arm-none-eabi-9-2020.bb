inherit nopackages

DESCRIPTION  = "GCC used purely for compiling the firmware binaries"
HOMEPAGE     = "https://developer.arm.com/"
LICENSE      = "Apache-2.0-with-LLVM-exception"
OUTPUTS_NAME = "gcc-9"

PROVIDES     = "virtual/gcc-none-eabi"

SUMMARY = "GCC 9"
SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu-rm/9-2020q2/gcc-arm-none-eabi-9-2020-q2-update-x86_64-linux.tar.bz2"

SRC_URI[md5sum]  = "041e3a5c735d5f956668254b1ffd35d1"
LIC_FILES_CHKSUM = "file://include/llvm/Support/LICENSE.TXT;md5=986c03435151a0086b8aaac964939cdd"

S             = "${WORKDIR}/gcc-arm-none-eabi-9-2020-q2-update-x86_64-linux"
FILES:${PN}   = "/${OUTPUTS_NAME}"
SYSROOT_DIRS += "/${OUTPUTS_NAME}"

do_install() {
    install -d ${D}/${OUTPUTS_NAME}
    cp -rf ${S}/* ${D}/${OUTPUTS_NAME}
}

INSANE_SKIP_${PN}           = "already-stripped libdir staticdev file-rdeps arch dev-so rpaths useless-rpaths"
INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_DEFAULT_DEPS        = "1"
EXCLUDE_FROM_SHLIBS         = "1"