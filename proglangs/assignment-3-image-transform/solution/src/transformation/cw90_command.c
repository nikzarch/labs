#include "command.h"

void do_cw90(struct image* img) {

    size_t new_width = img->height;
    size_t new_height = img->width;

    struct pixel* new_pixels = malloc(sizeof(struct pixel) * new_width * new_height);
    if (!new_pixels) {
        fprintf(stderr, "Memory allocation failed during rotation\n");
        exit(ENOMEM);
    }

    for (size_t i = 0; i < img->height; i++) {
        for (size_t j = 0; j < img->width; j++) {
            size_t new_i = img->width - 1 - j;
            size_t new_j = i;
            new_pixels[new_i * new_width + new_j] = img->data[i * img->width + j];
        }
    }

    free(img->data); 
    img->data = new_pixels;
    img->width = new_width;
    img->height = new_height;
}
