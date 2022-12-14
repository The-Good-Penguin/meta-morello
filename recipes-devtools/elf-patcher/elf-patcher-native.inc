inherit llvm-morello-native native

SUMMARY      = "Elf-patcher"
DESCRIPTION  = "Tool for paching up elf e_flag with EF_AARCH64_CHERI_PURECAP"
HOMEPAGE     = "http://llvm.org"
LICENSE      = "BSD-3-Clause"
OUTPUTS_NAME = "elf-patcher"

PROVIDES     = "virtual/${OUTPUTS_NAME}-native"

SRC_URI = " \
    git://git.morello-project.org/morello/morello-aarch64;protocol=https;branch=${SRCBRANCH} \
    "
DEPENDS:remove  = " virtual/elf-patcher-native virtual/musl-morello-libs-native"

SRCREV        = "${AUTOREV}"
PV            = "${PN}+git${SRCPV}"

S             = "${WORKDIR}/git"
S-elf-patcher = "${WORKDIR}/git/morello/tools"

FILES:${PN}   = "${bindir}/${OUTPUTS_NAME}"

do_configure[noexec] = "1"

do_compile(){
    mkdir -p ${B}/${OUTPUTS_NAME}
    ${CC} -o ${B}/${OUTPUTS_NAME}/${OUTPUTS_NAME} ${S-elf-patcher}/morello_elf.c
}

do_install(){
    install -d ${D}${bindir}
    install -m 0744 ${S}/${OUTPUTS_NAME}/${OUTPUTS_NAME} ${D}${bindir}/${OUTPUTS_NAME}
}