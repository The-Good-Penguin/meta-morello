

SUMMARY      = "LLVM Morello"
HOMEPAGE     = "http://llvm.org"
LICENSE      = "Apache-2.0-with-LLVM-exception"
OUTPUTS_NAME = "llvm-morello"

DEPENDS      = "zlib-native ninja-native ${COMPILER}-native"

SRC_URI = "git://git.morello-project.org/morello/llvm-project;protocol=https;branch=${SRCBRANCH}"
SRCREV  = "cac045cbb70a9c8161f2f32fa5ef056242eb28f3"

SRCBRANCH        = "morello/release-1.5"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=8a15a0759ef07f2682d2ba4b893c9afe"

S             = "${WORKDIR}/git/llvm"
S_git         = "${WORKDIR}/git"

FILES:${PN}          = "/${OUTPUTS_NAME}"
SYSROOT_DIRS_NATIVE += "/${OUTPUTS_NAME}"

COMPILER     = "llvm-13.0.1"

LLVM_TARGETS_TO_BUILD = "X86;AArch64"
LLVM_PROJECTS         = "libcxx;libcxxabi;compiler-rt;libunwind;llvm;clang;lld;lldb"
LLVM_BUILD_DIR        = "${WORKDIR}/${OUTPUTS_NAME}-build"
LLVM_INSTALL_DIR      = "${D}/${OUTPUTS_NAME}"

NATIVE_LLVM_PATH  = "${STAGING_LIBDIR_NATIVE}/${COMPILER}/bin"
LIB_PATH          = "${STAGING_LIBDIR_NATIVE}/${COMPILER}/lib"
LIB_PATH_RT       = "${STAGING_LIBDIR_NATIVE}/${COMPILER}/lib/clang/13.0.1/lib/linux"

EXTRA_OECMAKE += "-Wno-dev -G Ninja \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${LLVM_INSTALL_DIR} \
    -DCMAKE_C_COMPILER='${NATIVE_LLVM_PATH}/clang' \
    -DCMAKE_CXX_COMPILER='${NATIVE_LLVM_PATH}/clang++' \
    -DCMAKE_ASM_COMPILER='${NATIVE_LLVM_PATH}/clang' \
    -DCMAKE_AR='${NATIVE_LLVM_PATH}/llvm-ar' \
    -DCMAKE_RANLIB='${NATIVE_LLVM_PATH}/llvm-ranlib' \
    -DCMAKE_NM='${NATIVE_LLVM_PATH}/llvm-nm' \
    -DCMAKE_OBJDUMP='${NATIVE_LLVM_PATH}/llvm-objdump' \
    -DCMAKE_OBJCOPY='${NATIVE_LLVM_PATH}/llvm-objcopy' \
    -DCMAKE_LINKER='${NATIVE_LLVM_PATH}/ld.lld' \
    -DCMAKE_C_FLAGS_RELEASE='${CFLAGS}' \
    -DCLANG_DEFAULT_LINKER='lld' \
    -DCMAKE_EXE_LINKER_FLAGS='-fuse-ld=lld' \
    -DCMAKE_SHARED_LINKER_FLAGS='-fuse-ld=lld' \
    -DCMAKE_SKIP_BUILD_RPATH=OFF \
    -DCMAKE_INSTALL_RPATH='$ORIGIN/../lib:${LIB_PATH}' \
    -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON \
    -DCMAKE_EXPORT_COMPILE_COMMANDS=ON \
    -DLLVM_CCACHE_BUILD=FALSE \
    -DLLVM_ENABLE_ASSERTIONS=OFF \
    -DLLVM_ENABLE_EH=ON \
    -DLLVM_ENABLE_RTTI=ON \
    -DLLVM_ENABLE_LLD=ON \
    -DLLVM_ENABLE_LIBCXX=ON \
    -DBUILD_SHARED_LIBS=ON \
    -DCOMPILER_RT_BUILD_BUILTINS=ON \
    -DCOMPILER_RT_BUILD_XRAY=OFF \
    -DCOMPILER_RT_BUILD_LIBFUZZER=OFF \
    -DCOMPILER_RT_BUILD_PROFILE=OFF \
    -DLLVM_TARGETS_TO_BUILD='${LLVM_TARGETS_TO_BUILD}' \
    -DLLVM_ENABLE_PROJECTS='${LLVM_PROJECTS}' \
    -DLIBCXX_CXX_ABI='libcxxabi' \
    -DLIBCXX_CXX_ABI_INCLUDE_PATHS='${WORKDIR}/git/libcxxabi/include' \
    -DLIBCXXABI_USE_LLVM_UNWINDER=ON \
    -DLIBCXXABI_USE_COMPILER_RT=ON \
    -DLIBUNWIND_ENABLE_THREADS=ON \
    -DCLANG_DEFAULT_RTLIB='compiler-rt' \
    -DCLANG_DEFAULT_CXX_STDLIB='libc++' \
    -DCLANG_DEFAULT_OBJCOPY='llvm-objcopy'\
    '-DLLVM_LIT_ARGS=--max-time 3600 --timeout 300 -s -vv' \
    -DLLVM_USE_SPLIT_DWARF=TRUE \
    -DLLVM_ENABLE_OCAMLDOC=FALSE \
    -DLLVM_ENABLE_BINDINGS=FALSE \
    -DLLVM_INCLUDE_EXAMPLES=FALSE \
    -DLLVM_INCLUDE_DOCS=FALSE \
    -DLLVM_INCLUDE_BENCHMARKS=FALSE \
    -DCLANG_ENABLE_STATIC_ANALYZER=FALSE \
    -DCLANG_ENABLE_ARCMT=FALSE \
    -DLLVM_ENABLE_Z3_SOLVER=FALSE \
    -DLLVM_TOOL_LLVM_MCA_BUILD=FALSE \
    -DLLVM_TOOL_LLVM_EXEGESIS_BUILD=FALSE \
    -DLLVM_TOOL_LLVM_RC_BUILD=FALSE \
    -DLLVM_OPTIMIZED_TABLEGEN=FALSE \
    -DLLVM_ENABLE_ZLIB=FORCE_ON \
    -DLLVM_ENABLE_LIBXML2=FALSE \
"

INSANE_SKIP_${PN}           = "already-stripped"
INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_DEFAULT_DEPS        = "1"

do_unpack[depends] += "${COMPILER}-native:do_populate_sysroot"

do_configure() {
    mkdir -p ${LLVM_BUILD_DIR}
    cd ${LLVM_BUILD_DIR}
    cmake -S ${S} -B ${LLVM_BUILD_DIR} ${EXTRA_OECMAKE}
}

do_compile() {
    export CC="${NATIVE_LLVM_PATH}/clang"
    export NM="${NATIVE_LLVM_PATH}/llvm-nm"
    export CXX="${NATIVE_LLVM_PATH}/clang++"
    export LD="${NATIVE_LLVM_PATH}/ld.lld"
    cd ${LLVM_BUILD_DIR}
    cmake --build . --target all -- -j20
}

do_install() {
  cd ${LLVM_BUILD_DIR}
  cmake --build . --target install
  install -d ${LLVM_SHARED_SOURCE}
}

python do_symlink() {
    s = d.getVar("S_git")
    if s[-1] == '/':
        # drop trailing slash, so that os.symlink(src, s) doesn't use s as directory name and fail
        s=s[:-1]
    src = d.getVar("LLVM_SHARED_SOURCE")
    if s != src:
        bb.utils.mkdirhier(src)
        bb.utils.remove(src, recurse=True)
        os.symlink(s, src)
    else:
        import shutil
        shutil.move(s, src)
        os.symlink(src, s)
}
addtask do_symlink after do_install before do_build