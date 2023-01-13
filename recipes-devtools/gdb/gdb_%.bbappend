COMPATIBLE_MACHINE = "morello-linux"

SRC_URI   = "git://git.morello-project.org/morello/binutils-gdb.git;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "users/ARM/morello-binutils-gdb-master"
SRCREV    = "${AUTOREV}"

S  = "${WORKDIR}/git"