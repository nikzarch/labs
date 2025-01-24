#include  <stdint.h>

#ifndef BMP_H
#define BMP_H
#define BMP_SIGNATURE 0x4D42
#define BMP_HEADER_SIZE 40
#define BMP_PLANES 1
#define BMP_X_PELS_PER_METER 2835
#define BMP_Y_PELS_PER_METER 2835
#define BMP_HEADER_RESERVED 0
#define BMP_BIT_COUNT 24
#define BMP_CLR_USED 0
#define BMP_CLR_IMPORTANT 0



struct __attribute__((packed)) bmp_header
{
        uint16_t bfType;
        uint32_t  bfileSize;
        uint32_t bfReserved;
        uint32_t bOffBits;
        uint32_t biSize;
        uint32_t biWidth;
        uint32_t  biHeight;
        uint16_t  biPlanes;
        uint16_t biBitCount;
        uint32_t biCompression;
        uint32_t biSizeImage;
        uint32_t biXPelsPerMeter;
        uint32_t biYPelsPerMeter;
        uint32_t biClrUsed;
        uint32_t  biClrImportant;
};

#endif
