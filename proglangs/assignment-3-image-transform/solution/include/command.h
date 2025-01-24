#ifndef COMMAND_H
#define COMMAND_H

#include "image.h"
#include <errno.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>


void handle_transformation(char* command, struct image* img);
void do_none(struct image* img);
void do_cw90(struct image* img);
void do_ccw90(struct image* img);
void do_fliph(struct image* img);
void do_flipv(struct image* img);
#endif
