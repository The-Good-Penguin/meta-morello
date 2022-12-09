inherit llvm-morello-native

SUMMARY      = "Libarchcap"
DESCRIPTION  = "Libarchcap"
HOMEPAGE     = "http://llvm.org"
LICENSE      = "ARM-Limited"
OUTPUTS_NAME = "libarchcap"

PROVIDES     = "virtual/${OUTPUTS_NAME}"

SRC_URI = " \
    git://git.morello-project.org/morello/android/platform/external/libarchcap;protocol=https;branch=${SRCBRANCH} \
    "


LIC_FILES_CHKSUM  = "file://LICENSE.txt;md5=dc35e02a115ff61453fb21eb8ec7da7f"
SRCBRANCH         = "morello/mainline"
SRCREV            = "${AUTOREV}"
PV                = "${PN}+git${SRCPV}"

S             = "${WORKDIR}/git"
FILES:${PN}   = "${includedir}/${OUTPUTS_NAME}"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install(){
    install -d ${D}${includedir}/${OUTPUTS_NAME}
    install -m 0744 ${S}/include/* ${D}${includedir}/${OUTPUTS_NAME}
}