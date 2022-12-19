inherit native nopackages

SUMMARY      = "LLVM 13.01"
DESCRIPTION  = "LLVM used purely for compiling llvm-morello"
HOMEPAGE     = "http://llvm.org"
LICENSE      = "Apache-2.0-with-LLVM-exception"
OUTPUTS_NAME = "llvm-13.0.1"

PROVIDES     = "virtual/${OUTPUTS_NAME}-native ${OUTPUTS_NAME}-native"

SRC_URI = "https://github.com/llvm/llvm-project/releases/download/llvmorg-13.0.1/clang+llvm-13.0.1-x86_64-linux-gnu-ubuntu-18.04.tar.xz"

SRC_URI[md5sum]  = "041e3a5c735d5f956668254b1ffd35d1"
LIC_FILES_CHKSUM = "file://include/llvm/Support/LICENSE.TXT;md5=986c03435151a0086b8aaac964939cdd"

S             = "${WORKDIR}/clang+llvm-13.0.1-x86_64-linux-gnu-ubuntu-18.04"
FILES:${PN}   = "/${OUTPUTS_NAME}"

SYSROOT_DIRS_NATIVE += "/${OUTPUTS_NAME}"

do_install() {
    install -d ${D}${libdir}/${OUTPUTS_NAME}
    cp -rf ${S}/* ${D}${libdir}/${OUTPUTS_NAME}
}

INSANE_SKIP_${PN}           = "already-stripped libdir staticdev file-rdeps arch dev-so rpaths useless-rpaths"
INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_DEFAULT_DEPS        = "1"
EXCLUDE_FROM_SHLIBS         = "1"