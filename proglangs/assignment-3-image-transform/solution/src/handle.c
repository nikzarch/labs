#include "command.h"
#include "io_bmp.h"
#include <string.h>

void handle_transformation(char* command, struct image* img){
    if (strcmp(command,"none") == 0){
        return;
    }
    else if (strcmp(command,"fliph") == 0){
        do_fliph(img);
    }else if (strcmp(command,"flipv") == 0){
        do_flipv(img);
    }else if (strcmp(command,"cw90") ==0){
        do_cw90(img);
    }else if (strcmp(command,"ccw90") == 0){
        do_ccw90(img);
    }
    
}
