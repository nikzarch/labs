%include "lib.inc"
%include "dict.inc"
%include "words.inc"
section .bss
    dict: resb 256
section .text
global _start


_start:
    mov rdi, dict
    mov rsi, 255
    call read_word
    test rax,rax
    jnz .step1
    call print_read_error
    jmp .end
    .step1:
        mov rdi,rax
        mov rsi, first_word
        call find_word
        test rax, rax
        jnz .step2
        call print_read_error
        jmp .end
    .step2:
        mov rdi,rax
        call print_string
    .end:
        call exit
