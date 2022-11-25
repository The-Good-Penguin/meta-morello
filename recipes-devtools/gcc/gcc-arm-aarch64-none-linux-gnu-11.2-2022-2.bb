inherit nopackages

#todo use these to compile all the firmware, we do not need all the stuff that comes with using the meta gcc

DESCRIPTION  = "GCC used purely for compiling the firmware binaries"
HOMEPAGE     = "https://developer.arm.com/"
LICENSE      = "Apache-2.0-with-LLVM-exception"
OUTPUTS_NAME = "gcc-11.2"

PROVIDES     = "virtual/gcc-aarch64-none-linux-gnu"

SUMMARY = "GCC 11.2"
SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/11.2-2022.02/binrel/gcc-arm-11.2-2022.02-x86_64-aarch64-none-linux-gnu.tar.xz"

SRC_URI[md5sum]  = "041e3a5c735d5f956668254b1ffd35d1"
LIC_FILES_CHKSUM = "file://include/llvm/Support/LICENSE.TXT;md5=986c03435151a0086b8aaac964939cdd"

S             = "${WORKDIR}/gcc-arm-11.2-2022.02-x86_64-aarch64-none-linux-gnu"
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