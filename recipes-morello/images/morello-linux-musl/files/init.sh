#!/bin/busybox sh

# Copyright (c) 2021 Arm Limited. All rights reserved.
#
# SPDX-License-Identifier: BSD-3-Clause

mount() {
    /bin/busybox mount "$@"
}

grep() {
    /bin/busybox grep "$@"
}

echo "Running init script"

mount -t proc proc /proc
grep -qE $'\t'"devtmpfs$" /proc/filesystems && mount -t devtmpfs dev /dev
mount -t sysfs sysfs /sys

echo "Installing busybox"

/bin/busybox --install -s

! grep -qE $'\t'"devtmpfs$" /proc/filesystems && mdev -s

ulimit -c unlimited

echo "/bin/sh as PID 1!"
echo "init.sh"
exec setsid cttyhack sh
echo setsid ctty hack failed so \"exec /bin/sh\" fallback will be used
exec /bin/sh
