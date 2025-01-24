#include "command.h"


void do_fliph(struct image* img){
    for (uint32_t i = 0; i < img->height; i++) {
        for (uint32_t j = 0; j < img->width / 2; j++) {

            uint32_t i1 = i * img->width + j;
            uint32_t i2 = i * img->width + (img->width - 1 - j);
            
            struct pixel temp = img->data[i1];
            img->data[i1] = img->data[i2];
            img->data[i2] = temp;
        }
    }
}
