%define NULL 0
%define NEXT_ELEMENT NULL
%macro colon 2
    %2: dq NEXT_ELEMENT
    db %1, NULL
    %define NEXT_ELEMENT %2
%endmacro