#include <stdint.h>

#ifndef IMAGE_H
#define IMAGE_H

struct pixel { uint8_t b, g, r; };

struct image {
  uint64_t width, height;
  struct pixel* data;
};

#endif
