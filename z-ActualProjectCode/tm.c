#include "server.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include "fakecapture.h"
#include "server_common.h"


pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;

struct global_data{
    char task;
    const int count;
    media_stream * ms;
    media_frame * image;
    long size;
};



void * task_a(void * ctx)
{
    struct global_data *d = ctx;
    int i;
    typedef char byte;
    byte * b;
    int fd;
    
    printf("started task_a\n");
    
    printf("a before loop\n");
    fd = create_server_socket(9993);
    if(fd < 0){
        perror("create_server_socket");
        return 1;
    }

    
    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        printf("beforewhile\n");
        while(d->task != 'a') pthread_cond_wait(&cnd, &mtx);
    // ----------------------------------
        printf("task_a: %d\n",i);
        // gets image from fake camera and stores into global data
        d->image = capture_get_frame(d->ms);
        b = (byte*) d->image->data;
        d->size = (long) capture_frame_size(d->image);
        do_serve(fd, b, d->size);
     // ----------------------------------
        d->task = 'b';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    printf("Done\n");
    printf("simple_tcp_server: closing socket: %d\n", fd);
    close(fd);
    capture_close_stream(d->ms);
    return NULL;
}


void * task_b(void * ctx)
{
    struct global_data *d = ctx;
    int i;

    printf("started task_b\n");
    for(i=0; i < d->count; ++i){
	pthread_mutex_lock(&mtx);
	while(d->task != 'b') pthread_cond_wait(&cnd, &mtx);
     // ----------------------------------
        printf("b\n");
     // ----------------------------------
	d->task = 'a';
	pthread_cond_signal(&cnd);
	pthread_mutex_unlock(&mtx);
    }
    return NULL;
}


int main()
{
    //struct global_data data = {0,10};
    typedef char byte;
    const char * c = "c";
    media_stream * ms;
    //media_frame * image;
    byte * b;
    ms = capture_open_stream(c, c);
    struct global_data data = {0,10, ms};
    
    pthread_t imageThread;

    pthread_t thread_b;

    // Create the first thread
    if(pthread_create(&imageThread, NULL, task_a, &data)){
        printf("Failed to create thread_a\n");
        exit(1);
    }
    //start the first task
     data.task = 'a';
    
    // Create the second thread
    
    if(pthread_create(&thread_b, NULL, task_b, &data)){
        printf("Failed to create thread_b\n");
        exit(2);
    }
    
    sleep(1);
    pthread_mutex_lock(&mtx);
    
    // allow the first to start
   // printf("setting task to a\n");
   // data.task = 'a';
    
    // finish
    pthread_cond_signal(&cnd);
    pthread_mutex_unlock(&mtx);
    pthread_join(imageThread, NULL);
    pthread_join(thread_b, NULL);
    printf("Here\n");
   return 0; 
}
