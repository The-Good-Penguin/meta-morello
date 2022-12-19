meta-morello
==============

Meta-morello provides the layer required to build the firmware that lives on the SD card and a Morello enabled
Linux kernel for the Morello System Development Platform.  

-Use the provided kas scripts to get all of the dependencies.  
-Read on how to get and use kas here [1]  

Booting the hardware
--------------------

For information on how to boot the hardware and how the hardware is booting see [2] and [3].

Machines
--------

The machines have been split into:  
- morello-bsp for the SD card  
- morello-linux-musl for the Linux image with musl being the only libc
- morello-linux-glibc for the Linux image with an arbitrary Yocto image as the rootfs, there are two system loaders:
  musl for capability aware applications, glibc for all of the rest

Building images
--------------------

kas build ./kas/morello-linux-glibc.yml  

or  

kas build ./kas/morello-linux-musl.yml  

Images
------

The outputs can be found under build/temp/deploy/images:  
- board-firmware-sd-image.img goes on the SD card  via DD  
- morello-linux-image-...img goes on the USB via DD  

Linux and musl-libc
-------------------
The linux kernel and musl-libc are locked in sync so that the release tags from upstream always match.


Known limitations
-----------------

- the current state of this layer is meant to be just a starting point and foundation for further development, the main aim was to have working Linux images ASAP for the community, do not expect elegant Yocto solutions yet
- the rtl_nic driver is missing from the musl image

Adding new recipes
------------------

Follow the coding style found in other layers, the aim here is to keep them consistent where possible
and very easy to read. Follow the order found in the "headers" of each recipe and in general.

.bb recipes that come from Morello gitlab and are Morello "edits"" of upstream inherit the name Morello in the recipe: package-name-morello  
.bbappends do not need to do this even if they come from Morello gitlab as they would only change SRC_URI  

Whether to append or start a new recipe is up to the designer, whatever is the easiest.  


Mailing list
------------

https://op-lists.linaro.org/mailman3/lists/linux-morello-distros.op-lists.linaro.org/

References
----------

[1] https://kas.readthedocs.io/en/latest/ \
[2] https://developer.arm.com/documentation/den0132/0100/Setting-up-the-Morello-Hardware-Development-Platform \
[3] https://developer.arm.com/documentation/102278/0001/?lang=en 
