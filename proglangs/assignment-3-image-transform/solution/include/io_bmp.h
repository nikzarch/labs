
#ifndef IO_BMP
#define IO_BMP

#include "bmp.h"
#include "image.h"
#include <stdio.h>
#include <stdlib.h>


enum read_status  {
  READ_OK = 0,
  READ_INVALID_SIGNATURE,
  READ_INVALID_BITS,
  READ_INVALID_HEADER,
  READ_INVALID_INPUT_FILE,
  READ_ALLOCATION_FAILED
  };
enum  write_status  {
  WRITE_OK = 0,
  WRITE_ERROR,
  WRITE_INVALID_INPUT_FILE
};
enum read_status from_bmp( FILE* in, struct image* img );
enum write_status to_bmp( FILE* out, struct image const* img );
#endif
