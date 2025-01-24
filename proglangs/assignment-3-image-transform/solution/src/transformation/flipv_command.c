#include "command.h"

void do_flipv(struct image* img) {
    for (uint32_t i = 0; i < img->height / 2; i++) {
        for (uint32_t j = 0; j < img->width; j++) {

            uint32_t i1 = i * img->width + j;
            uint32_t i2 = (img->height - 1 - i) * img->width + j;
            
            struct pixel temp = img->data[i1];
            img->data[i1] = img->data[i2];
            img->data[i2] = temp;
        }
    }
}
