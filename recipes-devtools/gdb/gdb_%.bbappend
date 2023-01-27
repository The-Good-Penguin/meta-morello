COMPATIBLE_MACHINE = "morello-linux"

SRC_URI   = "git://git.morello-project.org/morello/binutils-gdb.git;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "users/ARM/morello-binutils-gdb-master"
SRCREV    = "f6e8c7228463a552d175211f4fcea93cca889686"

S  = "${WORKDIR}/git"