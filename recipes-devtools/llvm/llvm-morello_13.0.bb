
COMPATIBLE_MACHINE = "morello-linux"
DESCRIPTION        = "The Morello LLVM Compiler Infrastructure for A64"
SUMMARY            = "LLVM Morello"
HOMEPAGE           = "https://git.morello-project.org/morello/llvm-project-releases"
LICENSE            = "Apache-2.0-with-LLVM-exception"

RPROVIDES:${PN}    = "llvm-morello"
RDEPENDS:${PN}     = "zlib"
SRCBRANCH          = "morello/linux-aarch64-release-1.5"

SRC_URI = "https://git.morello-project.org/morello/llvm-project-releases/-/archive/${SRCBRANCH}/llvm-project-releases-morello-linux-aarch64-release-1.5.tar.gz"

SRC_URI[md5sum]  = "bc5ba415eccea9b2280586aa6f74519d"
LIC_FILES_CHKSUM = "file://include/llvm/Support/LICENSE.TXT;md5=986c03435151a0086b8aaac964939cdd"

S = "${WORKDIR}/llvm-project-releases-morello-linux-aarch64-release-1.5"

FILES:${PN} += "${bindir}"
FILES:${PN} += "${libdir} ${libdir}/clang"
FILES:${PN} += "${includedir}"

FILES:${PN}-staticdev += "${libdir}/*.a ${libdir}/clang/13.0.0/lib/linux/*.a \
                          ${libdir}/clang/13.0.0/lib/aarch64-unknown-linux-musl_purecap/*.a \
                          ${libdir}/clang/13.0.0/lib/aarch64-unknown-linux-gnu/*.a \
                          "
FILES:${PN}-dev += "\
  ${datadir} \
  ${libdir}/cmake \
"

FILES:libclang = "\
  ${libdir}/libclang.so.${PV} \
"

FILES:lldb = "\
  ${bindir}/lldb \
"

FILES:lldb-server = "\
  ${bindir}/lldb-server \
"

FILES:liblldb = "\
  ${libdir}/liblldbIntelFeatures.so* \
  ${libdir}/liblldb.so* \
"

INSANE_SKIP_${PN}           = "staticdev"
INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_install() {
    install -d ${D}${prefix}  ${D}${libdir} ${D}${bindir} ${D}${includedir} ${D}${datadir} ${D}${libexecdir}
    cp -rf ${S}/lib/* ${D}${libdir}
    cp -rf ${S}/bin/* ${D}${bindir}
    cp -rf ${S}/include/* ${D}${includedir}
    cp -rf ${S}/share/* ${D}${datadir}
    cp -rf ${S}/libexec/* ${D}${libexecdir}
}