NAME=$(OUTPUTS_NAME)
CC=$(LLVM_PATH)/clang

TARGET=aarch64-linux-musl_purecap
ARCH=-march=morello+c64 -mabi=purecap
CLANG_RESOURCE_DIR=$(shell ${CC} -print-resource-dir)
CLANG_LIB=$(CLANG_RESOURCE_DIR)/lib/aarch64-unknown-linux-musl_purecap
COMPILER_INC=$(CLANG_RESOURCE_DIR)/include
OUT=$(S)

CFLAGS=-Wno-gnu-alignof-expression \
        -Werror \
        -Wno-macro-redefined \
        -Werror=cheri \
        -Werror=cheri-inefficient \
        -Werror=cheri-pedantic \
        -Werror=cheri-pointer-conversion \
        -Werror=cheri-unimplemented-features

all:
	$(CC) -c -g -O0 -isystem $(SYSROOT_INC) -I$(COMPILER_INC) \
		$(ARCH) $(NAME).c -o $(OUT)/$(NAME).c.o \
		--target=$(TARGET)
	$(CC) --target=$(TARGET) $(ARCH) -fuse-ld=lld -rtlib=compiler-rt --sysroot $(SYSROOT) \
		$(OUT)/$(NAME).c.o \
		-o $(OUT)/$(NAME)

clean:
	rm -f $(OUT)/$(NAME).c.o
	rm -f $(OUT)/$(NAME)