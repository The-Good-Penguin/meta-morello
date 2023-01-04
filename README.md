meta-morello
==============

Meta-morello provides the layers required to build the firmware that lives on the SD card and a Morello enabled
Linux kernel for the Morello System Development Platform. The layers are using what was done at [1]
in ARM's proprietary build system and this was used as the starting point.

Use https://github.com/The-Good-Penguin/morello-manifest to get all required dependencies or use the provided kas scripts.

Booting the hardware
--------------------

For information on how to boot the hardware and how the hardware is booting see [2] and [3].

Machines
--------

The machines have been split into:  
- morello-bsp for the SD card  
- morello-linux-musl for the Linux image with musl being the only libc
- morello-linux-glibc for the Linux image with an arbitrary Yocto image as the rootfs

Building images
--------------------

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-bsp bitbake board-firmware-image  

then:

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-linux-musl bitbake morello-linux-image  

or (mutually exclusive)

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-linux-glibc bitbake morello-linux-image  

Alternatively one can use kas:

kas build ./kas/morello-bsp.yml  
kas build ./kas/morello-linux-musl.yml  

or (a single command is needed for glibc)

kas build ./kas/morello-linux-glibc.yml  

Images
------

The outputs can be found under build/temp/deploy/images:  
- board-firmware-sd-image.img goes on the SD card  via DD  
- morello-linux-image-...img goes on the USB via DD  
- you do need to build the board-firmware-image to be able to build the morello-linux-image

Linux and musl-libc
-------------------

These packages are not versioned deliberately. For all we know the next iterations might have
a completely different API. So every release tag will infer a new recipe. The linux kernel and musl-libc are locked in
sync so that the release tags from upstream always match.

Known limitations
-----------------

- the current state of this layer is meant to be just a starting point and foundation for further development, the main aim was to have working Linux images ASAP for the community, do not expect elegant Yocto solutions yet
- Yocto packaging is disabled for the Morello apps due to not fully implemented toolchain (llvm-morello is an overrride bootstrap of the env variables), one of the packaging classes will drop an error as it expects a target_prefix-objcopy being available for example (in theory only applies to linux-morello-musl machine, have not tested if one can package Morello apps onto standard Yocto images)
- it follows that the final image is assembled manually
- the rtl_nic driver is missing

To do list
----------

- create clang-morello toolchain, follow the guide here http://www.openembedded.org/wiki/Adding_a_secondary_toolchain and move llvm-morello to its own layer
* can try append meta-clang instead
* can try to use precomipled external toolchain like here https://github.com/MentorEmbedded/meta-sourcery/ instead
* thus there are 3 paths to explore forward for the toolchain, Yocto expects target_prefixed toolchain binaries
- be considerate of incoming purecap GNU toolchain (related to the above toolchain problem)
- sort out the packaging for llvm-morello-compiled packages (related to the above toolchain problem)
- add a multiconfig conf to build all outputs in one go and have an actual hard dependency between the firmware and the linux image

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
