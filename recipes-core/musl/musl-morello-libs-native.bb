inherit cmake llvm-morello-native native
require musl-morello-${MORELLO_ARCH}.inc

DESCRIPTION    = "Libraries that go into the clang resource folder, \
                  search path for that folder is relative to clang directory itself."

DEPENDS        = "musl-morello-native"
PROVIDES       = "virtual/musl-morello-libs-native"

B_COMPILERRT   = "${WORKDIR}/build_compiler_rt"
S_CRT          = "${LLVM_SHARED_SOURCE}/compiler-rt/lib/crt"
S_COMPILER_RT  = "${LLVM_SHARED_SOURCE}/compiler-rt"

do_install[depends] += "llvm-morello-native:do_symlink"

FILES:${PN}       += "$(${CC} -print-resource-dir)/lib/${LIB_TRIPLE}"
INSANE_SKIP:${PN} += "$(${CC} -print-resource-dir)/lib/${LIB_TRIPLE}"

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


do_install() {

    export CFLAGS=""

    local resourcedir=$(${CC} -print-resource-dir)
    local destdir="${resourcedir}/lib/${LIB_TRIPLE}"
    local builddir="${B_COMPILERRT}/${ARCH_TRIPLE}"
    local sysroot="${STAGING_LIBDIR_NATIVE}/musl-morello-native/${ARCH_TRIPLE}"
    local target="${LIB_TRIPLE}"

    install -d ${destdir}
    mkdir -p ${builddir}

    local ccflags="--target=${target} ${ARCH_FLAGS} -nostdinc -isystem ${sysroot}/include"
    ${CC} ${ccflags} -c ${S_CRT}/crtbegin.c -o ${destdir}/clang_rt.crtbegin.o
    ${CC} ${ccflags} -c ${S_CRT}/crtend.c -o ${destdir}/clang_rt.crtend.o

    cp -f ${WORKDIR}/files/compiler_rt.cmake ${builddir}/compiler_rt.cmake

    local config="${COMPILERRT_CMAKE}"
    config="${config} -DCOMPILER_RT_DEFAULT_TARGET_TRIPLE=${LIB_TRIPLE} \
                      -DCMAKE_INSTALL_RPATH=\$ORIGIN/../lib \
                      -DCMAKE_TOOLCHAIN_FILE='${builddir}/compiler_rt.cmake' \
    "
    cmake -S ${S_COMPILER_RT} -B ${builddir} ${config} -DCMAKE_C_FLAGS="-nostdinc -isystem ${sysroot}/include" -DCMAKE_C_COMPILER_TARGET="${LIB_TRIPLE} ${ARCH_FLAGS}"

    cd ${builddir}
    make ${PARALLEL_MAKE} clang_rt.builtins-aarch64

    install ${builddir}/lib/linux/libclang_rt.builtins-aarch64.a ${destdir}/libclang_rt.builtins.a

    install -d ${D}${libdir}/llvm-morello-native/lib/clang/${LLVM_VERSION}/lib/${LIB_TRIPLE}
    local install_dir=${D}${libdir}/llvm-morello-native/lib/clang/${LLVM_VERSION}/lib/${LIB_TRIPLE}

    install ${destdir}/clang_rt.crtbegin.o ${install_dir}/clang_rt.crtbegin.o
    install ${destdir}/clang_rt.crtend.o ${install_dir}/clang_rt.crtend.o
    install ${destdir}/libclang_rt.builtins.a ${install_dir}/libclang_rt.builtins.a
}