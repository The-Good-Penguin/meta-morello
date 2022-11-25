inherit nopackages

COMPATIBLE_MACHINE = "morello"

EXTRA_OECONF+="\
                --disable-efiemu \
                --disable-werror \
                --enable-dependency-tracking \
                --disable-grub-mkfont \
                --disable-grub-themes \
                --disable-grub-mount \
                "