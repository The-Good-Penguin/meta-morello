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

The morello-linux environment is currenlty purecap only and is built with capability aware
compiler. This will be changed so the usual Yocto images can also be built with purecap
being statically linked as this would arguably be more usefull and robust.

SD card image builld
--------------------

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-bsp bitbake board-firmware-image  

Linux build
-----------

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build  
$ MACHINE=morello-linux bitbake morello-linux-image  

Images
------

The outputs can be found under build/temp/deploy/images:  
- board-firmware-sd-image.img goes on the SD card  via DD  
- moirello-linux-image.img goes on the USB via DD  

Linux and musl-libc
-------------------

These packages are not versioned deliberately. For all we know the next iterations might have
a completely different API. So every release tag will infer a new recipe. The linux kernel and musl-libc are locked in
sync so that the release tags from upstream always match.

Known limitations
-----------------
- the current state of this layer is meant to be just a starting point and foundation for further development, the main aim was to have working Linux images ASAP for the community, do not expect elegant Yocto solutions yet
- Yocto packaging is disabled due to not fully implemented toolchain (llvm-morello is a overrride bootstrap of the env variables), one of the packaging classes will drop an error as it expects a target_prefix-objcopy being available for example
- it follows that the image is assembled manually
- the rtl_nic driver is missing

To do list
----------
- upon reflection it is worth keeping GNU as the system-wide toolchain and only have musl being statically linked as this way one can get a more functional and robust distro with only certain apps utilising the capabilities, this could be easily achieved by adding another machine conf and editing some of the recipes, in fact this is probably a more pragmatic use case than the current c64/a64 ovveride for musl-libc, as one can just add clang-offending libs to a blacklist in the llvm class/toolchain and use the best of both worlds. Pursuing purecap only rootfs is still the end goal and very useful from an academic point of view but requires a lot of community work that is not in there yet
- create clang-morello toolchain, follow the guide here http://www.openembedded.org/wiki/Adding_a_secondary_toolchain and move llvm-morello to its own layer
* can try append meta-clang instead
* can try to use precomipled external toolchain like here https://github.com/MentorEmbedded/meta-sourcery/ instead
* thus there are 3 paths to explore forward for the toolchain, Yocto expects target_prefixed toolchain binaries 
- be considerate of incoming purecap GNUtoolchain (related to the above toolchain problem)
- sort out the packaging for llvm-morello-compiled packages (related to the above toolchain problem)
- add KAS support

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
