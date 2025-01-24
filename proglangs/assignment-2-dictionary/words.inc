%include "colon.inc"
%macro makestr 1
  db %1,0
%endmacro

%define mystring "a"
%rep 254
  %strcat mystring mystring "b"
%endrep
section .data
colon "big", big_one
makestr mystring
colon "third", third_word
    db "world!", 0
colon "second", second_word
    db ",", 0
colon "first", first_word
    db "Hello", 0
