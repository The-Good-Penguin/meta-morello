MORELLO_COMPILER = "llvm-morello-native"

LLVM_VERSION = "13.0.0"

LLVM_PATH    = "${STAGING_DIR_NATIVE}/usr/bin"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS                                        += "virtual/llvm-morello-native"
DEPENDS:append:morello-linux-glibc:class-target = " virtual/musl-morello-libs-native"
DEPENDS:append:morello-linux-musl:class-target  = " virtual/musl-morello-libs-native"

# rough hack to deal with llvm-morello not being a proper toolchain in its own meta yet
DEPENDS:remove = "libgcc"

do_configure[depends] += " ${@get_depends(d)}"

LIBCPLUSPLUS       = "-stdlib-libc++"

export CC          = "${LLVM_PATH}/clang"
export CXX         = "${LLVM_PATH}/clang++"
export CPP         = "${LLVM_PATH}/clang"
export CCLD        = "${LLVM_PATH}/clang"
export RANLIB      = "${LLVM_PATH}/llvm-ranlib"
export AR          = "${LLVM_PATH}/llvm-ar"
export AS          = "${LLVM_PATH}/llvm-as"
export NM          = "${LLVM_PATH}/llvm-nm"
export OBJDUMP     = "${LLVM_PATH}/llvm-objdump"
export OBJCOPY     = "${LLVM_PATH}/llvm-objcopy"
export STRIP       = "${LLVM_PATH}/llvm-strip"
export STRINGS     = "${LLVM_PATH}/llvm-strings"
export READELF     = "${LLVM_PATH}/llvm-readelf"
export LD          = "${LLVM_PATH}/ld.lld"
export LTO         = "-fuse-ld=lld"
export HOSTCC      = "${LLVM_PATH}/clang"
export LLVM_CONFIG = "${LLVM_PATH}/llvm-config"

def get_depends(d):
    if d.getVar('DEPENDENCIES'):
        return "llvm-morello-native:do_populate_sysroot"
    else:
        return "llvm-morello-native:do_populate_sysroot virtual/musl-morello:do_populate_sysroot"

DEPENDENCIES:kernel              = "1"
DEPENDENCIES:musl                = "1"
DEPENDENCIES:musl-morello-native = "1"
DEPENDENCIES                    ?= "0"