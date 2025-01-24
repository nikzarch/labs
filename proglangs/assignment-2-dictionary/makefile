ASM = nasm
LD = ld

LDFLAGS = -o
ASMFLAGS = -felf64

SRCS = lib.asm main.asm dict.asm
OBJECTS = $(SRCS:.asm=.o)

all: main

%.o: %.asm
	$(ASM) $(ASMFLAGS) -o $@ $<
main: $(OBJECTS)
	$(LD) $(OBJECTS) $(LDFLAGS) main

test: main
	python3 test.py

clean:
	rm -f *.o
	rm -f main
.phony: all clean