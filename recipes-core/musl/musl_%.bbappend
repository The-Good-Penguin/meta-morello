require musl-morello-${MORELLO_ARCH}.inc musl-morello-${MORELLO_ARCH}-so.inc musl-morello-target.inc

MACHINE_INC ?= ""
MACHINE_INC:morello-linux-glibc = "override-glibc.inc"
MACHINE_INC:morello-linux-musl  = "override-musl.inc"

require ${MACHINE_INC}

DEPENDS:remove = "virtual/${TARGET_PREFIX}binutils \
                  virtual/${TARGET_PREFIX}gcc \
                  libgcc-initial \
                  linux-libc-headers \
                  bsd-headers \
                  libssp-nonshared \
                 "

RDEPENDS:${PN}-dev:remove  = "linux-libc-headers-dev bsd-headers-dev libssp-nonshared-staticdev"