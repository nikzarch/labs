#define _DEFAULT_SOURCE

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mem_internals.h"
#include "mem.h"
#include "util.h"

void debug_block(struct block_header* b, const char* fmt, ... );
void debug(const char* fmt, ... );

extern inline block_size size_from_capacity( block_capacity cap );
extern inline block_capacity capacity_from_size( block_size sz );

static bool            block_is_big_enough( size_t query, struct block_header* block ) { return block->capacity.bytes >= query; }
static size_t          pages_count   ( size_t mem )                      { return mem / getpagesize() + ((mem % getpagesize()) > 0); }
static size_t          round_pages   ( size_t mem )                      { return getpagesize() * pages_count( mem ) ; }

static void block_init( void* restrict addr, block_size block_sz, void* restrict next ) {
  *((struct block_header*)addr) = (struct block_header) {
    .next = next,
    .capacity = capacity_from_size(block_sz),
    .is_free = true
  };
}

static size_t region_actual_size( size_t query ) { return size_max( round_pages( query ), REGION_MIN_SIZE ); }

extern inline bool region_is_invalid( const struct region* r );



static void* map_pages(void const* addr, size_t length, int additional_flags) {
  return mmap( (void*) addr, length, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | additional_flags , -1, 0 );
}
/*  --- Разделение блоков (если найденный свободный блок слишком большой )--- */
#define BLOCK_MIN_CAPACITY 24
static bool block_splittable( struct block_header* restrict block, size_t query) {
  return block-> is_free && query + offsetof( struct block_header, contents ) + BLOCK_MIN_CAPACITY <= block->capacity.bytes;
}

static bool split_if_too_big( struct block_header* block, size_t query ) {
  if (!block){
    return false;
  }
  if (!block_splittable(block,query)){
    return false;
  }
  
  struct block_header *new_block ;
  size_t new_size = block->capacity.bytes - query;
  new_block = (struct block_header *)((void *) block + offsetof(struct block_header, contents) + query);
  block_init(new_block, (block_size){.bytes = new_size}, block->next);
  block->next = new_block;
  block->capacity.bytes = query;
  return true;
}
static void* block_after( struct block_header const* block ) ;

static bool blocks_continuous (
                               struct block_header const* fst,
                               struct block_header const* snd ) {
  return (void*)snd == block_after(fst);
}
/*  аллоцировать регион памяти и инициализировать его блоком */
static struct region alloc_region  ( void const * addr, size_t query ) {
    query = size_max(query, BLOCK_MIN_CAPACITY);
    struct region r; //address size extends
    size_t size = region_actual_size(size_from_capacity((block_capacity){query}).bytes);

    void* address = map_pages(addr, size, MAP_FIXED_NOREPLACE);

    if (address == MAP_FAILED) {
        address = map_pages(addr, size, 0);
        if (address == MAP_FAILED) {
            return REGION_INVALID;
        }
        r.extends = 0;
    } else {
        r.extends = 1;
    }

    r.addr = address;
    r.size = size;

    block_init(r.addr, (block_size){size}, NULL);
    return r;

}



void* heap_init( size_t initial ) {
  const struct region region = alloc_region( HEAP_START, initial );
  if ( region_is_invalid(&region) ) return NULL;

  return region.addr;
}

/*  освободить всю память, выделенную под кучу */
void heap_term( ) {
  struct block_header *block = HEAP_START;

    while (block) {
        struct block_header *start = block;
        size_t size = size_from_capacity(block->capacity).bytes;
        while (blocks_continuous(block, block->next)) {
            block = block->next;
            size += size_from_capacity(block->capacity).bytes + offsetof(struct block_header, contents);
        }
        struct block_header *next_block = block->next;
        if(munmap(start, size) == -1){
          break;
        };
        block = next_block;
    }
     
}





/*  --- Слияние соседних свободных блоков --- */

static void* block_after( struct block_header const* block )              {
  return  (void*) (block->contents + block->capacity.bytes);
}


static bool mergeable(struct block_header const* restrict fst, struct block_header const* restrict snd) {
  return fst->is_free && snd->is_free && blocks_continuous( fst, snd ) ;
}

static bool try_merge_with_next( struct block_header* block ) {
  if (!block){
    return false;
  }
  struct block_header *next_block = block->next;
  if (next_block && mergeable(block,next_block)){
    struct block_header *next_next_block = next_block->next;
    block->next = next_next_block;
    block->capacity.bytes += size_from_capacity(next_block->capacity).bytes;
    block->is_free = true;
    return true;
  }
  return false;

}


/*  --- ... ecли размера кучи хватает --- */

struct block_search_result {
  enum {BSR_FOUND_GOOD_BLOCK, BSR_REACHED_END_NOT_FOUND, BSR_CORRUPTED} type;
  struct block_header* block;
};


static struct block_search_result find_good_or_last(struct block_header* restrict block, size_t sz) {
    struct block_search_result block_search_result = {.block = block};
    if (!block) {
        block_search_result.type = BSR_CORRUPTED;
        return block_search_result;
    }
    while (block->next) {
        if (block->is_free) {
            while (try_merge_with_next(block)); // merging till it's possible
            if (block_is_big_enough(sz,block)) {
                block_search_result.type = BSR_FOUND_GOOD_BLOCK;
                block_search_result.block = block;
                return block_search_result;
            }
        }
        block = block->next;
    }

    if (block->is_free) {
        while (try_merge_with_next(block));
        if (block_is_big_enough(sz,block)) {
            block_search_result.type = BSR_FOUND_GOOD_BLOCK;
            block_search_result.block = block;
            return block_search_result;
        }
    }
    block_search_result.type = BSR_REACHED_END_NOT_FOUND;
    block_search_result.block = block;
    return block_search_result;
}



/*  Попробовать выделить память в куче начиная с блока `block` не пытаясь расширить кучу
 Можно переиспользовать как только кучу расширили. */
static struct block_search_result try_memalloc_existing ( size_t query, struct block_header* block ) {
  struct block_search_result block_search_result = find_good_or_last(block, query);
    if (block_search_result.type == BSR_FOUND_GOOD_BLOCK) {
        split_if_too_big(block_search_result.block, query);
        block_search_result.block->is_free = false;
        
    }
    return block_search_result;
}



static struct block_header* grow_heap( struct block_header* restrict last, size_t query ) {
  if (!last){
    return NULL;
  }
  struct region r = alloc_region(block_after(last),query);
  if (region_is_invalid(&r)){
    return NULL;
  }
  struct block_header *new_block = r.addr;
  if (last->is_free && r.extends){
    last->capacity.bytes += r.size;
    return last;
  }else{
    last->next = new_block;
    return new_block;
  }
}

/*  Реализует основную логику malloc и возвращает заголовок выделенного блока */
static struct block_header* memalloc( size_t query, struct block_header* heap_start) {
  struct block_header* block;
  size_t size = size_max(query, BLOCK_MIN_CAPACITY);
  struct block_search_result block_search_result = try_memalloc_existing(size,heap_start);
  switch (block_search_result.type)
  {
  case BSR_REACHED_END_NOT_FOUND:
    block = grow_heap(block_search_result.block,size);
    if (!block){
      return NULL;
    }
    split_if_too_big(block,size);
    block->is_free = false;
    return block;
    break;
  case BSR_FOUND_GOOD_BLOCK:
    return block_search_result.block;
    break;
  default:
    return NULL;
    break;
  }
}

void* _malloc(size_t query) {
  struct block_header* const addr = memalloc( query, (struct block_header*) HEAP_START );
  if (addr) return addr->contents;
  else return NULL;
}

static struct block_header* block_get_header(void* contents) {
  return (struct block_header*) (((uint8_t*)contents)-offsetof(struct block_header, contents));
}

void _free( void* mem ) {
  if (!mem) return ;
  struct block_header* header = block_get_header( mem );
  header->is_free = true;
  try_merge_with_next(header);
}
