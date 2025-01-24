%include "dict.inc"

;rdi - null-terminated string pointer, rsi - dictionary first element pointer
;returns pointer on entry key
find_word:
    .loop: 
        test rsi, rsi   
        jz .not_found
        push rdi
        push rsi
        mov rax, rsi    
        add rsi, 8      
        call string_equals
        pop rsi
        pop rdi
        cmp al, 1
        je .found

        mov rsi, [rsi]   
        jmp .loop
    .found:
        mov rax, rsi 
        add rax, 8   
        ret

    .not_found:
        xor rax, rax
        ret
