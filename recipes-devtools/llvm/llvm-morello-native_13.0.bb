inherit cmake pkgconfig python3native native

DESCRIPTION  = "The Morello LLVM Compiler Infrastructure native"

PROVIDES     = "virtual/${OUTPUTS_NAME}-native"
PN           = "${OUTPUTS_NAME}-native"

require llvm-morello.inc

FILES:${PN} += "${libdir}/${PN}"

# TODO no real need for this
do_install:append() {
  install -d ${D}${libdir}/${PN}
  cp -rvf ${LLVM_INSTALL_DIR}/* ${D}${libdir}/${PN} 
}