#!/bin/busybox sh

mount() {
    /bin/busybox mount "$@"
}

umount() {
    /bin/busybox umount "$@"
}

grep() {
    /bin/busybox grep "$@"
}

cp() {
    /bin/busybox cp "$@"
}

mkdir() {
    /bin/busybox mkdir "$@"
}

switch_root () {
    /bin/busybox switch_root "$@"
}

sed () {
    /bin/busybox sed "$@"
}

echo "Running init script"

mount -t proc proc /proc
grep -qE $'\t'"devtmpfs$" /proc/filesystems && mount -t devtmpfs dev /dev
mount -t sysfs sysfs /sys

echo "Installing busybox..."

/bin/busybox --install -s

! grep -qE $'\t'"devtmpfs$" /proc/filesystems && mdev -s

ROOT="/newroot"

PARTUUID=$(cat proc/cmdline | sed 's/ /\n/g' | sed -n 's/^root=PARTUUID=*//p')

DEVID=$(blkid | sed -n "s/UUID=\"$PARTUUID\" TYPE=\"ext4\"*//p" | sed 's/://')

echo "Mounting ${DEVID} rootfs with PARTUUID: ${PARTUUID}"

mount ${DEVID} ${ROOT}

mkdir ${ROOT}/usr/pure-cap-apps
cp -rf pure-cap-apps ${ROOT}/usr

ulimit -c unlimited

cd ${ROOT}
exec switch_root . /sbin/init </dev/ttyAMA0 >dev/ttyAMA0 2>&1

echo "/bin/sh as PID 1!"
echo "init.sh"
exec setsid cttyhack sh
echo setsid ctty hack failed so \"exec /bin/sh\" fallback will be used
exec /bin/sh