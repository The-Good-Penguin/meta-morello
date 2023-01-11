inherit cmake pkgconfig python3native native
require llvm-morello.inc

DESCRIPTION  = "The Morello LLVM Compiler Infrastructure native"

PROVIDES     = "virtual/${OUTPUTS_NAME}-native"
PN           = "${OUTPUTS_NAME}-native"

FILES:${PN} += "${prefix}"

do_install:append() {
  install -d ${D}${prefix}
  cp -rvf ${LLVM_INSTALL_DIR}/* ${D}${prefix}
}