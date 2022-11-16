Meta-morello
==============

Meta-morello provides the layers required to build the firmware that lives on the SD card and a Morello enabled
Linux kernel for the Morello System Development Platform. The layers are using what was done at [1]
in ARM's propitiatory build system and this was used as the starting point.

Booting the hardware
--------------------

For information on how to boot the hardware and how the hardware is booting see [2] and [3].

Running the SD card image builld
--------------------------------

$ TEMPLATECONF=meta-morello/conf . oe-init-build-env build
$ bitbake board-firmware-image

Images
------

The outputs can be found under build/deploy/images:
- board-firmware-sd-image.img goes on the SD card

Adding new recipes
------------------

Follow the coding style found in other layers, the aim here is to keep them consistent where possible
and very easy to read. Follow the order found in the "headers" of each recipe and in general.

References
----------

[1] https://git.morello-project.org/morello
[2] https://developer.arm.com/documentation/den0132/0100/Setting-up-the-Morello-Hardware-Development-Platform
[3] https://developer.arm.com/documentation/102278/0001/?lang=en