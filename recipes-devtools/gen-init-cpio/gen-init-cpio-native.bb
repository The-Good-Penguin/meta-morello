inherit llvm-morello-native native

SUMMARY      = "Gen init cpio"
DESCRIPTION  = "Gen init cpio"
HOMEPAGE     = "http://llvm.org"
LICENSE      = "GPL-2.0-only"

OUTPUTS_NAME = "gen-init-cpio"
PROVIDES     = "${OUTPUTS_NAME}-native"

LIC_FILES_CHKSUM  = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = " \
    git://git.morello-project.org/morello/kernel/linux;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    "

SRCBRANCH     = "morello/master"
SRCTAG        = "morello-release-1.5.0"

S             = "${WORKDIR}/git/usr"

FILES:${PN}   = "${bindir}/${OUTPUTS_NAME}"

do_configure[noexec] = "1"

do_compile(){
    mkdir -p ${B}/${OUTPUTS_NAME}
    oe_runmake gen_init_cpio
}

do_install(){
    install -d ${D}${bindir}
    install -m 0744 ${S}/gen_init_cpio ${D}${bindir}/gen_init_cpio
}