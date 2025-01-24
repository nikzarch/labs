#include "io_bmp.h"

enum read_status from_bmp( FILE* in, struct image* img ){
    if (!in){
        fprintf(stderr, "Got invalid file");
        return READ_INVALID_INPUT_FILE;
    }
    struct bmp_header bmp_header;
    size_t read_bytes = fread(&bmp_header,sizeof(struct bmp_header),1,in);
    if (!read_bytes){
        fprintf(stderr, "An error occured during reading header\n");
        return READ_INVALID_HEADER;
    }
    if (bmp_header.bfType != BMP_SIGNATURE){
        fprintf(stderr, "Invalid bmp signature\n");
        return READ_INVALID_SIGNATURE;
    }
    size_t width = bmp_header.biWidth;
    size_t height = bmp_header.biHeight;
    int64_t padding = (int64_t) (4 - (width * 3 ) % 4) % 4; 
    struct pixel* pixels = malloc(sizeof(struct pixel)*width*height);
    if (!pixels) {
        fprintf(stderr, "Memory allocation during reading failed\n");
        return READ_ALLOCATION_FAILED;
    }
    fseek(in,bmp_header.bOffBits,SEEK_SET);
    for (size_t i = 0; i < height; i++){
        read_bytes = fread(&pixels[i*width],sizeof(struct pixel),width,in);
        if (read_bytes != width){
            fprintf(stderr, "An error occured while reading bytes\n");
            free(pixels);
            return READ_INVALID_BITS;
        }
        if (padding > 0) {
            fseek(in, padding, SEEK_CUR); // skip
        }


    }
    img->data = pixels;
    img->width = width;
    img->height = height;
    return READ_OK;
}
enum write_status to_bmp( FILE* out, struct image const* img ){

    if (!out){
        fprintf(stderr, "Got invalid file");
        return WRITE_INVALID_INPUT_FILE;
    }
    uint32_t width = img->width;
    uint32_t height = img->height;
    int64_t padding =  (int64_t) (4 - (width * 3 ) % 4) % 4; 
    struct bmp_header bmp_header = {
    .bfType = BMP_SIGNATURE,
    .bfileSize = sizeof(struct bmp_header) + (width * 3+padding)* height,
    .bfReserved = BMP_HEADER_RESERVED,
    .bOffBits = sizeof(struct bmp_header),
    .biSize = BMP_HEADER_SIZE,
    .biWidth = width,
    .biHeight = height,
    .biPlanes = BMP_PLANES,
    .biBitCount = BMP_BIT_COUNT,
    .biSizeImage = (width*3+padding)*height,
    .biXPelsPerMeter = BMP_X_PELS_PER_METER,
    .biYPelsPerMeter = BMP_Y_PELS_PER_METER,
    .biClrUsed = BMP_CLR_USED,
    .biClrImportant = BMP_CLR_IMPORTANT};
    fwrite(&bmp_header,sizeof(bmp_header),1,out);
   
    struct pixel* pixels = img->data;
    for (size_t i = 0; i < height; i++){
        size_t written_pixels = fwrite(&pixels[i*width],sizeof(struct pixel),width,out);
        if (written_pixels != width){
            fprintf(stderr, "An error occured while writing pixels\n");
            return WRITE_ERROR;
        }
        if (padding > 0){
            uint8_t zero_byte = 0;
            for (int64_t p = 0; p < padding; p++) {
                fwrite(&zero_byte, 1, 1, out);
            }
        }
    }
    return WRITE_OK;
}
