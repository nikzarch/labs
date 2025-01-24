%define SYS_EXIT 60
%define SYS_READ 0
%define SYS_WRITE 1
%define STDIN 0
%define STDOUT 1
%define newline 0xA
%define space 0x20
%define tab 0x9

section .text
 
 
; Принимает код возврата и завершает текущий процесс
exit:
    mov rax, SYS_EXIT
    syscall 

; Принимает указатель на нуль-терминированную строку, возвращает её длину
string_length:
    xor rax, rax
    .loop:
        cmp byte [rdi+rax], 0
        je .end
        inc rax
        jmp .loop
    .end:
        ret


; Принимает указатель на нуль-терминированную строку, выводит её в stdout
print_string:
    push rdi ; caller-saved register saving
    call string_length
    pop rdi ; restoring
    mov rsi, rdi  ; string address
    mov rdx, rax ; string length in bytes
    mov rax, SYS_WRITE ; 'write' syscall number
    mov rdi, STDOUT ; stdout descriptor
    syscall
    ret

; Переводит строку (выводит символ с кодом 0xA)
print_newline:
    mov rdi, newline ; tco lets go

; Принимает код символа и выводит его в stdout
print_char:
    mov rax,rdi
    push ax ; pushing symbol code on stack
    mov rsi,rsp ;  pointer on symbol code -> rsi
    mov rax, SYS_WRITE  ; "sys_write"
    mov rdi, STDOUT  ; stdout descriptor
    mov rdx, 1 ; string length = 1
    syscall
    pop ax
    ret

; Выводит знаковое 8-байтовое число в десятичном формате 
print_int:
    test rdi,rdi
    jns print_uint
    push rdi ; saving caller-saved register
    mov rdi, "-"
    call print_char
    pop rdi
    neg rdi ; tco lets go

; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
print_uint:
    mov rax,rdi
    xor r9,r9 ; counter
    mov r10,10 
    push rbp
    mov rbp,rsp
    sub rsp, 32 ; allocating space for 8 byte number + stack aligning
    
    .loop:
        dec r9
        xor rdx,rdx ;
        div r10  ;div by 10
        add rdx, '0'
        mov byte [rbp+r9], dl
        cmp rax,0
        je .end
        jmp .loop
    .end:
        mov rdi, rbp
        add rdi,r9
        call print_string
        mov rsp, rbp
        pop rbp
        ret



; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
string_equals:
    xor rax, rax
    .loop:
        mov r9b, byte [rdi+rax] 
        cmp r9b, byte [rsi+rax] ; comparing
        jne .not_equals ; if not equals => set 0 into rax
        inc rax
        cmp byte r9b, 0 ; if it didn't jump to .not_equals, then check if it ended
        je .equals ; if end, they equals
        jmp .loop
    .equals:
        mov rax,1
        jmp .end
    .not_equals:
        mov rax,0
        jmp .end
    .end:
        ret

; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
    mov rax, SYS_READ; "sys_read"
    sub rsp, 8
    mov rsi,rsp ; read in stack
    mov rdi, STDIN ; stdin descriptor
    mov rdx,1 ; string length = 1
    syscall

    cmp rax, 0
    je .end

    mov al, byte [rsp]

    .end:
        add rsp, 8
        ret

; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор

read_word:
    push r12
    push r13
    push r14
    mov r12,rdi
    mov r13,rsi
    xor r14,r14 ; counter
    .skip:
        call read_char
        cmp al, space ; space
        je .skip
        cmp al, tab ; tab
        je .skip
        cmp al, newline ; newline
        je .skip
        cmp al, 0
        je .end
        jmp .continue
    
    .loop:
        call read_char
        jmp .check
        
        .continue:
            mov byte [r12+r14],al ; using 1 byte
            inc r14
            cmp r13,r14
            jl .set_zero ; if buffer length < string length  => return 0 
        jmp .loop
    .check:
        cmp al, space
        je .end
        cmp al, tab
        je .end
        cmp al, newline
        je .end
        cmp al, 0
        je .end
        jmp .continue
    .set_zero:
        xor rax,rax
        jmp .pop_and_exit
    .end:
        mov byte [r12+r14], 0
        mov rax,r12
        mov rdx,r14
        .pop_and_exit:
            pop r14
            pop r13
            pop r12
         ret
 

; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
parse_uint:
    xor rax, rax
    xor r9,r9 ; counter
    xor r10,r10 ; i
    mov r11, 10 
    .loop:
        mov r10b, byte [rdi+r9]
        cmp r10b, '0'
        jl .end
        cmp r10b,'9'
        jg .end
        sub r10b, '0' ; decoding
        mul r11 ; rax * 10
        add rax, r10
        inc r9
        jmp .loop
    .end:
        mov rdx, r9
        ret




; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
parse_int:
    cmp byte [rdi], '-' ; check if negative
    jne .parse_positive

    inc rdi ; skip sign
    push 0 ; stack aligning
    call parse_uint
    pop r9
    inc rdx ; number length + 1 (sign)
    neg rax ; making number negative
    ret
    .parse_positive:
        push 0 ; stack aligning
        call parse_uint
        pop r9
        ret

; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
string_copy:
    xor rax, rax
    push rdi ; saving caller-saved registers
    push rsi
    push rdx
    call string_length
    pop rdx
    pop rsi
    pop rdi ; restoring caller-saved registers

    cmp rdx,rax ; buffer length - string length
    jl .is_bigger ; if less, return 0
    push rax ; save string length to return
    xor rax,rax ; counter = 0
    .loop:
        mov r9b, byte [rdi+rax]
        mov byte [rsi+rax],r9b
        inc rax
        cmp r9b,0
        je .end
        jmp .loop
    .is_bigger:
        xor rax,rax ; 0
        ret
    .end:
        pop rax
        ret
