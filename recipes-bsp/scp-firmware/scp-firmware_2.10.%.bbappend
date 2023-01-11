inherit cmake nopackages

COMPATIBLE_MACHINE = "morello"
OUTPUTS_NAME       = "scp-firmware"
SECTION            = "firmware"

DEPENDS           += "virtual/board-firmware"

PROVIDES          += "virtual/${OUTPUTS_NAME}"

SRC_URI            = "gitsm://git.morello-project.org/morello/scp-firmware.git;protocol=https;branch=${SRCBRANCH}"
SRCREV             = "${AUTOREV}"
PV                 = "2.10.0+git${SRCPV}"

SRCBRANCH          = "morello/master"

SCP_PLATFORM  = "morello"
SCP_LOG_LEVEL = "INFO"

SENSOR        = "${RECIPE_SYSROOT}/board-firmware/LIB/sensor.a"
B             = "${WORKDIR}/build/morello"
S             = "${WORKDIR}/git"
FILES:${PN}   = "/${OUTPUTS_NAME}"
SYSROOT_DIRS += "/${OUTPUTS_NAME}"

FW_TARGETS = "scp mcp"
FW_INSTALL = "ramfw_soc romfw"

INHIBIT_DEFAULT_DEPS = "1"

unset do_configure[noexec]
unset do_compile[cleandirs]
do_deploy[noexec] = "1"

do_configure() {

    cd ${S}

    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do

            local target="${FW}_${TYPE}"

            local extra_cmake="\
             -DSCP_ENABLE_DEBUGGER='0' \
             -DSCP_FIRMWARE_SOURCE_DIR:PATH='${SCP_PLATFORM}/${target}' \
             -DSCP_TOOLCHAIN:STRING='GNU' \
             -DDISABLE_CPPCHECK='1' \
             -DCMAKE_BUILD_TYPE=Release \
             "

            if [ "${target}" = "scp_ramfw_soc" ]; then
                extra_cmake="${extra_cmake} -DSCP_MORELLO_SENSOR_LIB_PATH='${SENSOR}'"
            fi

            local builddir="${B}/${target}"
            cmake -S ${S} -B ${builddir} ${extra_cmake}
        done
    done
}

do_compile() {
    cd ${S}
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            local target="${FW}_${TYPE}"
            cmake --build "${B}/${target}"
        done
    done
}

do_install() {
    install -d ${D}/firmware
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            local target="${FW}_${TYPE}"
            cp -rf "${B}/${target}/bin/"*.bin "${D}/firmware/${target}.bin"
        done
    done
}