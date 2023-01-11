/**
 * @author Pawel Zalewski Copyright (C) 2022  <The Good Penguin>
 * @email pzalewski@thegoodpenguin.co.uk
 * @create date 2022-07-11 16:13:49
 * @desc Simple Morello demonstration program aimed at Linux fs.
 *
 */

#include <stdio.h>
#include <stdbool.h>
#include <signal.h>
#include <string.h>
#include <stdlib.h>
#include <stdalign.h>
#include <stdint.h>

#include <getopt.h>
#include <memory.h>

#include <cheriintrin.h>

static void usage(void);
static void subPointersToPointersTest(void);
static void subPointersToPointersTest2(void);
static void outOfBoundAccess(void);
static void functionPointersTest(void);
static void atomicAccess(void);

static void sigactionSegfault(int signal, siginfo_t *si, void *args);
static void sigactionIllegal(int signal, siginfo_t *si, void *args);
static void sigactionAlingment(int signal, siginfo_t *si, void *args);

static void printPointer(void *pointer);

static void * cheri_perms_set(void * pointer, cheri_perms_t perm_new);

typedef void (*functionPointer)(void);

static const char shortOptions[] = "habcde";

static const struct option longOptions[] = {
    {"help", 0, NULL, 'h'},
    {"subPointersToPointersTest", 0, NULL, 'a'},
    {"subPointersToPointersTest2", 0, NULL, 'b'},
    {"outOfBoundAccess", 0, NULL, 'c'},
    {"functionPointersTest", 0, NULL, 'd'},
    {"atomicAccess", 0, NULL, 'e'},
    {NULL, 0, NULL, 0},
};

static void sigactionAlingment(int signal, siginfo_t *si, void *args)
{
    printf("Signal [%d] caught an alligment fault at address %p with code %d args: %p \n", signal, si->si_addr, si->si_code, args);
    exit(0);
}

static void sigactionSegfault(int signal, siginfo_t *si, void *args)
{
    printf("Signal [%d] caught a segfault at address %p with code %d args: %p \n", signal, si->si_addr, si->si_code, args);
    exit(0);
}

static void sigactionIllegal(int signal, siginfo_t *si, void *args)
{
    printf("Signal [%d] caught an illegal access at address %p with code %d args: %p \n", signal, si->si_addr, si->si_code, args);
    exit(0);
}

int main(int argc, char **argv)
{

    struct sigaction sa;

    memset(&sa, 0, sizeof(struct sigaction));
    sigemptyset(&sa.sa_mask);

    sa.sa_sigaction = sigactionSegfault;
    sa.sa_flags = SA_SIGINFO;
    sigaction(SIGSEGV, &sa, NULL);

    memset(&sa, 0, sizeof(struct sigaction));
    sa.sa_sigaction = sigactionIllegal;
    sa.sa_flags = SA_SIGINFO;
    sigaction(SIGILL, &sa, NULL);

    memset(&sa, 0, sizeof(struct sigaction));
    sa.sa_sigaction = sigactionAlingment;
    sa.sa_flags = SA_SIGINFO;
    sigaction(SIGBUS, &sa, NULL);

    size_t sizeInt = sizeof(int64_t);
    size_t sizePointer = sizeof(uint8_t *);

    printf("The size of int64 is %lu-byte/%lu-bit and pointers are %lu-byte/%lu-bit \r\n", sizeInt, sizeInt << 3, sizePointer, sizePointer << 3);

    int optionidx = 0;
    int opt;

    while ((opt = getopt_long(argc, argv, shortOptions, longOptions, &optionidx)) != EOF)
    {
        switch (opt)
        {
        case 'h':
            usage();
            return 0;

        case 'a':
            subPointersToPointersTest();
            break;

        case 'b':
            subPointersToPointersTest2();
            break;

        case 'c':
            outOfBoundAccess();
            break;

        case 'd':
            functionPointersTest();
            break;

        case 'e':
            atomicAccess();
            break;

        default:
            printf("Got: %c\r\n", opt);
            usage();
            exit(1);
        }
    }

    return 0;
}

/**
 * @brief Prints the pointer meta-data
 *
 * @param pointer teh pointer
 */
static void printPointer(void *pointer)
{
    size_t addr = cheri_address_get(pointer);
    size_t base = cheri_base_get(pointer);
    size_t offset = cheri_offset_get(pointer);
    size_t len = cheri_length_get(pointer);
    size_t perm = cheri_perms_get(pointer);
    bool tag = cheri_tag_get(pointer);

    printf("Capability size %lx align %lx addr %lX base %lX offset %lX length %lu permissions %lX tag %d \r\n", sizeof(pointer), alignof(pointer), addr, base, offset, len, perm, tag);

    cheri_perms_t permissions = cheri_perms_get(pointer);

    bool issealed = cheri_is_sealed(pointer);
    bool hasload  = (permissions & CHERI_PERM_LOAD        ) == CHERI_PERM_LOAD;
    bool hasstore = (permissions & CHERI_PERM_STORE       ) == CHERI_PERM_STORE;
    bool hasexec  = (permissions & CHERI_PERM_EXECUTE     ) == CHERI_PERM_EXECUTE;
    bool hassys   = (permissions & CHERI_PERM_SYSTEM_REGS ) == CHERI_PERM_SYSTEM_REGS;
    bool hasseal  = (permissions & CHERI_PERM_SEAL        ) == CHERI_PERM_SEAL;
    printf("Is sealed %d, load %d, store %d, execute %d, system %d, seal %d\r\n", issealed, hasload, hasstore, hasexec, hassys, hasseal);
}

/**
 * @brief This function tests how meta-data behaves in sub-sequent pointers.
 *
 */

static void subPointersToPointersTest(void)
{
    uint8_t array[21] = {42};

    uint8_t *pArray10 = &array[10];
    uint8_t *pArray15 = pArray10 + 5;

    printPointer(array);
    printPointer(pArray10);
    printPointer(pArray15);
}

/**
 * @brief This function tests changing bounds of pointers.
 *
 */
static void subPointersToPointersTest2(void)
{
    uint8_t array[21] = {42};

    uint8_t *pArray10 = &array[10];
    uint8_t *pArray10Bounded = cheri_bounds_set(pArray10, 4);

    printPointer(array);
    printPointer(pArray10);
    printPointer(pArray10Bounded);

    uint8_t *pArray10UnBounded = cheri_bounds_set(pArray10Bounded, 5);

    printPointer(pArray10UnBounded);

    pArray10Bounded--;

    printf("Some value is %d \r\n", *pArray10Bounded);
    printf("Some value is %d \r\n", *pArray10UnBounded);
}

/**
 * @brief This function checks if we can access OOB value.
 *
 */

int someValue = 0x8000;

static void outOfBoundAccess(void)
{

    int *pAddress = &someValue;
    printPointer(pAddress);

    pAddress++;
    printPointer(pAddress);

    printf("Some value is %d \r\n", *pAddress);
}

/**
 * @brief This function checks how function pointers behave.
 *
 */

static void functionPointersTest(void)
{

    functionPointer fp = &outOfBoundAccess;

    printPointer(fp);

    int *pData = (int *)fp;

    printPointer(pData);

    cheri_perms_t perm  = cheri_perms_get((void*)fp) | CHERI_PERM_STORE;
    functionPointer fpE = cheri_perms_set(fp, perm);

    printPointer(fpE);

    fpE();

    int *pAddress = &someValue;
    functionPointer fpAddr = (functionPointer)pAddress;

    printPointer(pAddress);
}

/**
 * @brief  This function checks if we can atomically apply capability to a data field.
 *
 */
static void atomicAccess(void)
{
    uint8_t array[21] = {42};

    uint8_t *pWriter = cheri_perms_set(&array[0], 0b010000000001000001);
    uint8_t *pReader = cheri_perms_set(&array[0], 0b100000000001000001);

    printPointer(pWriter);
    printPointer(pReader);

    pWriter[0] = 1;

    uint8_t value = pReader[0];

    pReader[0] = 1;

    printf("Value is %u array is %u \r\n", value, array[0]);
}

static void * cheri_perms_set(void * pointer, cheri_perms_t perm_new) {

    cheri_perms_t perm_old = cheri_perms_get(pointer);
    return cheri_perms_clear(pointer, perm_old  & ~perm_new);
}

/**
 * @brief Teh usage
 *
 */
static void usage(void)
{
    printf(
        "Usage: MorelloExample [OPTION]\n"
        "\n"
        "-h, --help              help\n"
        "-a                      subPointersToPointersTest\n"
        "-b                      subPointersToPointersTest2\n"
        "-c                      outOfBoundAccess        \n"
        "-d                      functionPointersTest    \n"
        "-e                      atomicAccess\n");
}