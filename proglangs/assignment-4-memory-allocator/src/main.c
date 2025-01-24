#include "mem.h"

#include <assert.h>
#include <stdio.h>

void test_allocation() {
    heap_init(1024);
    void* ptr = _malloc(256);
    assert(ptr != NULL);
    printf("block allocated succesfully\n");
    _free(ptr);
    printf("block freed\n");
    heap_term();
}

void test_free_one_block() {
    heap_init(1024);
    void* ptr1 = _malloc(256);
    void* ptr2 = _malloc(256);
    printf("Two blocks %p bytes and  %p bytes allocated\n",ptr1,ptr2);
    assert(ptr1 != NULL);
    assert(ptr2 != NULL);
    _free(ptr1);
    printf("One block %p freed\n", ptr1);
    heap_term();
}

void test_free_two_blocks() {
    heap_init(1024);
    void* ptr1 = _malloc(256);
    void* ptr2 = _malloc(256);
    assert(ptr1 != NULL);
    assert(ptr2 != NULL);
    printf("Two blocks %p bytes and  %p bytes allocated\n",ptr1,ptr2);
    _free(ptr1);
    printf("One block %p freed\n", ptr1);
    _free(ptr2);
    printf("Second block %p freed\n", ptr2);
    heap_term();
}

void test_allocate_three_blocks_after_one_being_freed() {
    heap_init(1024);
    void* ptr1 = _malloc(512);
    void* ptr2 = _malloc(512);
    assert(ptr1 != NULL);
    assert(ptr2 != NULL);
    printf("Two blocks %p bytes and  %p bytes allocated\n",ptr1,ptr2);
    _free(ptr1);
    printf("One block %p freed\n", ptr1);
    void* ptr3 = _malloc(256);
    assert(ptr3 != NULL);
    printf("Third block %p allocated succesfully\n", ptr3);
    _free(ptr2);
    _free(ptr3);
    heap_term();
}


int main() {
    test_allocation();
    test_free_one_block();
    test_free_two_blocks();
    test_allocate_three_blocks_after_one_being_freed();
    printf("All tests passed\n");
    return 0;
}
