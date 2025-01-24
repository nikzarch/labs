#include "main.h"


int main( int argc, char** argv ) {
    if (argc != 4){
        fprintf(stderr,"Illegal argument amount, must be 3 \n");
        return EINVAL; 
    }
    
    FILE* input_file = fopen(argv[1],"rb");
    
    if (!input_file){
        fprintf(stderr,"can't open bmp\n");
        return ENOENT;
    }

    struct image* image = malloc(sizeof(struct image));
    if (!image){
        fprintf(stderr, "Memory allocation for image failed\n");
        fclose(input_file);
        return ENOMEM;
    }
    enum read_status read_status = from_bmp(input_file,image);
    fclose(input_file);
    switch (read_status)
    {
    case READ_OK:
        handle_transformation(argv[3],image);
        FILE* output_file = fopen(argv[2], "w");
        if (!output_file){
            fprintf(stderr,"can't create/open output file\n");
            free(image->data);
            free(image);
            fclose(output_file);
            return EIO;
        }
        enum write_status write_status = to_bmp(output_file, image);
        fclose(output_file);
        free(image->data);
        free(image);
        switch(write_status)
         {  
            case WRITE_ERROR:
            case WRITE_INVALID_INPUT_FILE:      
                return EIO;
            default:
                break;
         }
        break;
    case READ_INVALID_BITS:
    case READ_ALLOCATION_FAILED:
        free(image);
        return ENOMEM;
    case READ_INVALID_SIGNATURE:
    case READ_INVALID_HEADER:
    case READ_INVALID_INPUT_FILE:
        free(image->data);
        free(image);
        return EIO;
    default:
        free(image->data);
        free(image);
    }

    
    return 0;
}




