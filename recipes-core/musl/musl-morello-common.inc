require musl-morello-${MORELLO_RELEASE_VERSION}.inc

DESCRIPTION        = "CHERI enabled musl libc"
OUTPUTS_NAME       = "libc"
LICENSE            = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SUMMARY = "CHERI enabled musl libc"
SRC_URI = " \
    git://git.morello-project.org/morello/musl-libc;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    file://files/compiler_rt.cmake \
"

SRCREV =""

PV           = "git${SRCPV}"
S            = "${WORKDIR}/git"
B            = "${WORKDIR}/build"

TARGET_INSTALL_DIR = "${D}${MUSL_INSTALL_DIR}"

INHIBIT_SYSROOT_STRIP       = "1"
INHIBIT_PACKAGE_STRIP       = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

CONFIGUREOPTS = " \
    --prefix=${prefix} \
    --exec-prefix=${exec_prefix} \
    --bindir=${bindir} \
    --libdir=${libdir} \
    --includedir=${includedir} \
    --syslibdir=${nonarch_base_libdir} \
    --target=${ARCH_TRIPLE} \
    --disable-libshim \
"

COMPILERRT_CMAKE = "-Wno-dev \
    -DCMAKE_TOOLCHAIN_FILE=compiler_rt.cmake \
    -DCMAKE_BUILD_TYPE=Release \
    -DLLVM_CONFIG_PATH='${LLVM_CONFIG}' \
    -DCMAKE_EXPORT_COMPILE_COMMANDS=ON \
    -DCMAKE_SKIP_BUILD_RPATH=OFF \
    -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON \
    -DLLVM_TARGETS_TO_BUILD='AArch64' \
    -DLLVM_ENABLE_ASSERTIONS=OFF \
    -DBUILD_SHARED_LIBS=ON \
    -DCOMPILER_RT_BUILD_BUILTINS=ON \
    -DCOMPILER_RT_BUILD_SANITIZERS=OFF \
    -DCOMPILER_RT_BUILD_XRAY=OFF \
    -DCOMPILER_RT_BUILD_LIBFUZZER=OFF \
    -DCOMPILER_RT_BUILD_PROFILE=OFF \
"
do_configure() {
    export CFLAGS=""
    local config="${CONFIGUREOPTS}"
    echo "Install dir ${TARGET_INSTALL_DIR} with ${TCLIBC} MACHINE_INC ${MACHINE_INC}"
    config="${config} --target=${ARCH_TRIPLE} ${EXTRA_CONFIGUREOPTS} --disable-shared"
	${S}/configure ${config}
}

do_compile() {
    export CFLAGS=""
    oe_runmake
}