inherit llvm-morello-native

COMPATIBLE_MACHINE = "morello"
SECTION            = "libs"

DEPENDS:append     = " virtual/musl-morello-libs-native"

MUSL_LDSO_ARCH = "aarch64"

FILES:${PN}  = "${MUSL_INSTALL_DIR}"
FILES:${PN} += "${MUSL_INSTALL_DIR}${datadir} ${MUSL_INSTALL_DIR}${sysconfdir}"
FILES:${PN} += "${MUSL_INSTALL_DIR}${includedir} ${MUSL_INSTALL_DIR}${includedir}/sys \
                 ${MUSL_INSTALL_DIR}${includedir}/arpa ${MUSL_INSTALL_DIR}${includedir}/bits \
                 ${MUSL_INSTALL_DIR}${includedir}/net ${MUSL_INSTALL_DIR}${includedir}/netinet \
                 ${MUSL_INSTALL_DIR}${includedir}/netpacket ${MUSL_INSTALL_DIR}${includedir}/arpa \
                 ${MUSL_INSTALL_DIR}${includedir}/scsi ${MUSL_INSTALL_DIR}${includedir}/sys"

SYSROOT_DIRS                     += "${MUSL_INSTALL_DIR}/musl"
SYSROOT_DIRS:morello-linux-glibc += "${MUSL_INSTALL_DIR}"

do_package[noexec]           = "1"
do_package_write_rpm[noexec] = "1"
do_packagedata[noexec]       = "1"

do_install() {

    echo "Installing into ${TARGET_INSTALL_DIR}"

    export CFLAGS=""

    install -d ${TARGET_INSTALL_DIR}${includedir} ${TARGET_INSTALL_DIR}${includedir}/sys ${TARGET_INSTALL_DIR}${includedir}/arpa \
               ${TARGET_INSTALL_DIR}${includedir}/bits ${TARGET_INSTALL_DIR}${includedir}/net ${TARGET_INSTALL_DIR}${includedir}/netinet \
               ${TARGET_INSTALL_DIR}${includedir}/netpacket ${TARGET_INSTALL_DIR}${includedir}/arpa ${TARGET_INSTALL_DIR}${includedir}/scsi \
               ${TARGET_INSTALL_DIR}${includedir}/sys

    local destdir="${TARGET_INSTALL_DIR}"

    local config="${CONFIGUREOPTS}"
    config="${config} --target=${ARCH_TRIPLE} ${EXTRA_CONFIGUREOPTS}"

	${S}/configure ${CONFIGUREOPTS}
	oe_runmake
	oe_runmake install DESTDIR="${destdir}"

    install -d ${destdir}${bindir} ${TARGET_INSTALL_DIR}${sysconfdir}

    echo "${TARGET_INSTALL_DIR}${base_libdir}" > ${TARGET_INSTALL_DIR}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
    echo "${TARGET_INSTALL_DIR}${libdir}" >> ${TARGET_INSTALL_DIR}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path

    install -d ${TARGET_INSTALL_DIR}/musl
    install ${TARGET_INSTALL_DIR}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path ${TARGET_INSTALL_DIR}/musl/ld-musl-${MUSL_LDSO_ARCH}.path
}