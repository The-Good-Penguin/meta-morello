inherit cmake llvm-morello-native
require musl-morello-${MORELLO_RELEASE_VERSION}.inc
COMPATIBLE_MACHINE = "morello"
DESCRIPTION        = "CHERI enabled musl libc"
OUTPUTS_NAME       = "libc"
LICENSE            = "MIT"
SECTION            = "libs"

RPROVIDES:${PN}   += "ldd rtld(GNU_HASH)"

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SUMMARY = "CHERI enabled musl libc"
SRC_URI = " \
    git://git.morello-project.org/morello/musl-libc;protocol=https;tag=${SRCTAG};branch=${SRCBRANCH} \
    file://files/compiler_rt.cmake \
"

PV           = "git${SRCPV}"
S            = "${WORKDIR}/git"
B            = "${WORKDIR}/build"

B_COMPILERRT   = "${WORKDIR}/build_compiler_rt"
S_CRT          = "${LLVM_SHARED_SOURCE}/compiler-rt/lib/crt"
S_COMPILER_RT  = "${LLVM_SHARED_SOURCE}/compiler-rt"

MUSL_LDSO_ARCH = "morello"

OVERRIDES = "${MORELLO_ARCH}"

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
INHIBIT_DEFAULT_DEPS  = "1"
INSANE_SKIP:${PN}-dev = "staticdev"
INSANE_SKIP:${PN}     = "libdir ${ARCH_TRIPLE}${libdir}"

INSANE_SKIP:${PN}    += "/${ARCH_TRIPLE}${prefix}/lib"

FILES:${PN}  += "/"
FILES:${PN}  += "/${ARCH_TRIPLE}"
FILES:${PN}  += "${libdir}/lib*${SOLIBSDEV}"
FILES:${PN}  += "${bindir}"
FILES:${PN}  += "${nonarch_base_libdir}/ld-musl-${MUSL_LDSO_ARCH}.so.1 ${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path"

FILES:${PN}-staticdev = "/${ARCH_TRIPLE}${libdir}/libc.a"
FILES:${PN}-dev      += "/${ARCH_TRIPLE}${libdir}/libcrypt.a /${ARCH_TRIPLE}${libdir}/libdl.a /${ARCH_TRIPLE}${libdir}/libm.a \
                         /${ARCH_TRIPLE}${libdir}/libpthread.a /${ARCH_TRIPLE}${libdir}/libresolv.a \
                         /${ARCH_TRIPLE}${libdir}/librt.a /${ARCH_TRIPLE}${libdir}/libutil.a /${ARCH_TRIPLE}${libdir}/libxnet.a \
                        "

LEAD_SONAME             = "libc.so"

do_build_libs[nostamp]  = "1"
do_build_libs[depends] += "llvm-morello-native:do_symlink"

do_configure() {
    local config="${CONFIGUREOPTS}"
    config="${config} --target=${ARCH_TRIPLE} ${EXTRA_CONFIGUREOPTS} --disable-shared"
	${S}/configure ${config}
}

do_compile() {
    oe_runmake
}

do_install() {
    local destdir="${D}/${ARCH_TRIPLE}"
    oe_runmake install DESTDIR="${destdir}"
	install -d ${destdir}${bindir} ${D}${sysconfdir}
    echo "${base_libdir}" > ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
    echo "${libdir}" >> ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
}

do_build_libs() {

    local resourcedir=$(${CC} -print-resource-dir)
    local destdir="${resourcedir}/lib/${LIB_TRIPLE}"
    local builddir="${B_COMPILERRT}/${ARCH_TRIPLE}"
    local sysroot="${D}/${ARCH_TRIPLE}/usr"
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
    cp ${builddir}/lib/linux/libclang_rt.builtins-aarch64.a ${destdir}/libclang_rt.builtins.a
}
addtask do_build_libs after do_install before do_build

do_build_so() {

    local destdir="${D}/${ARCH_TRIPLE}"

    local config="${CONFIGUREOPTS}"
    config="${config} --target=${ARCH_TRIPLE} ${EXTRA_CONFIGUREOPTS}"

	${S}/configure ${CONFIGUREOPTS}
	oe_runmake
	oe_runmake install DESTDIR="${destdir}"
}
addtask do_build_so after do_build_libs before do_build