inherit llvm-morello-native native
require musl-morello-${MORELLO_ARCH}.inc

DESCRIPTION = " That is right, we have a native libc sysroot that is needed as an intermediate step \
to provide crt and compiler-rt that will be used by the cross compiler, this recipe exist to avoid having \
a native recipe dependant on a target recipe... which is actually the case, but there you go. Note that the \
libc sysroot is NOT actually native, it is just a trick."

PROVIDES = "virtual/musl-morello-native"
DEPENDS  = ""

BUILD_CC          = "${LLVM_PATH}/clang"
BUILD_CXX         = "${LLVM_PATH}/clang++"
BUILD_CPP         = "${LLVM_PATH}/clang"
BUILD_CCLD        = "${LLVM_PATH}/clang"
BUILD_RANLIB      = "${LLVM_PATH}/llvm-ranlib"
BUILD_AR          = "${LLVM_PATH}/llvm-ar"
BUILD_AS          = "${LLVM_PATH}/llvm-as"
BUILD_NM          = "${LLVM_PATH}/llvm-nm"
BUILD_OBJDUMP     = "${LLVM_PATH}/llvm-objdump"
BUILD_OBJCOPY     = "${LLVM_PATH}/llvm-objcopy"
BUILD_STRIP       = "${LLVM_PATH}/llvm-strip"
BUILD_STRINGS     = "${LLVM_PATH}/llvm-strings"
BUILD_READELF     = "${LLVM_PATH}/llvm-readelf"
BUILD_LD          = "${LLVM_PATH}/ld.lld"
BUILD_LTO         = "-fuse-ld=lld"
BUILD_HOSTCC      = "${LLVM_PATH}/clang"

FILES:${PN} += "${libdir}/${PN}/${ARCH_TRIPLE}"
INSTALL_DIR  = "${D}/${ARCH_TRIPLE}"

do_install() {
    export CFLAGS=""
    make install DESTDIR="${INSTALL_DIR}"
    install -d ${D}${libdir}/${PN}/${ARCH_TRIPLE}
    cp -rf ${INSTALL_DIR}${prefix}/* ${D}${libdir}/${PN}/${ARCH_TRIPLE}
}