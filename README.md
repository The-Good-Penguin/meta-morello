meta-morello
==============

Meta-morello provides the layers required to build the firmware that lives on the SD card and a Morello enabled
Linux kernel for the Morello System Development Platform. The layers are using what was done at [1]
in ARM's proprietary build system and this was used as the starting point.

Use https://github.com/The-Good-Penguin/morello-manifest to get all required dependencies.

Booting the hardware
--------------------

For information on how to boot the hardware and how the hardware is booting see [2] and [3].

Machines
--------

The machines have been split into:  
- morello-bsp for the SD card  
- morello-linux for the Linux image  

The morello-linux environment will be based purely on a capability aware
toolchain. The morello-bsp is still using plain gcc for some of its outputs.
The capabilities are enabled/disabled in the rootfs apps via MORELLO_ARCH flag found in
the morello-linux.conf file.

SD card image builld
--------------------

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-bsp bitbake board-firmware-image  

Linux build
-----------
Work in progress, we can build the kernel and libc:

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-linux bitbake virtual/kernel  
$ MACHINE=morello-linux bitbake virtual/libc  

Images
------

The outputs can be found under build/deploy/images:  
- board-firmware-sd-image.img goes on the SD card  

Linux and musl-libc
-------------------

These packages are not versioned deliberately. For all we know the next iterations might have
a completely different API. So every release tag will infer a new recipe. The linux kernel and musl-libc are locked in
sync so that the release tags from upstream always match.

Adding new recipes
------------------

Follow the coding style found in other layers, the aim here is to keep them consistent where possible
and very easy to read. Follow the order found in the "headers" of each recipe and in general.

Mailing list
------------

https://op-lists.linaro.org/mailman3/lists/linux-morello-distros.op-lists.linaro.org/

References
----------

[1] https://git.morello-project.org/morello \
[2] https://developer.arm.com/documentation/den0132/0100/Setting-up-the-Morello-Hardware-Development-Platform \
[3] https://developer.arm.com/documentation/102278/0001/?lang=en
