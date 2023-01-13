inherit cmake pkgconfig python3native native
require llvm-morello.inc
DESCRIPTION  = "The Morello LLVM Compiler Infrastructure native"

PROVIDES     = "virtual/${OUTPUTS_NAME}-native"
PN           = "${OUTPUTS_NAME}-native"

FILES:${PN} += "${prefix}"

LLVM_TARGETS_TO_BUILD = "X86;AArch64"
LLVM_INSTALL_DIR      = "${D}/${OUTPUTS_NAME}"

do_install:append() {
  install -d ${D}${prefix}
  cp -rvf ${LLVM_INSTALL_DIR}/* ${D}${prefix}
}