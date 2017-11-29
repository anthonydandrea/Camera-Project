#include "server.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include "camera.h"
#include "server_common.h"
#include <time.h>


pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;

int fd;


struct global_data{
    char task;
    const int count;
    camera * cam;
    frame * frame;
    long size;
};



void * task_a(void * ctx)
{
    struct global_data *d = ctx;
    int i;
    typedef char byte;
    byte * b;
    int fd;



    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        printf("beforewhile\n");
        while(d->task != 'a') pthread_cond_wait(&cnd, &mtx);
    // ----------------------------------
        printf("task_a: %d\n",i);
        // gets image from fake camera and stores into global data
        d->frame = camera_get_frame(d->cam);
        b = get_frame_bytes(d->frame);
        d->size = (long) get_frame_bytes(d->frame);
        do_serve(fd, b, d->size);

         frame_free(d->frame);
        //sleep for fake camera
        struct timespec ts;
        int milliseconds = 250;
        ts.tv_sec = milliseconds / 1000;
        ts.tv_nsec = (milliseconds % 1000) * 1000000;
        nanosleep(&ts, NULL);
     // ----------------------------------
        d->task = 'b';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    printf("Done\n");
    // close(fd);
    // capture_close_stream(d->ms);
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


int main(int argc, char* argv[])
{
    //struct global_data data = {0,10};
    typedef char byte;
    const char * c = "c";
    camera * cam;
    //media_frame * image;
    byte * b;
    cam = camera_open();
    struct global_data data = {0,247, cam};

    printf("started task_a\n");

    printf("a before loop\n");

     fd = getConnection(9990);
     sleep(3);
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
    camera_close(cam);
    closeConnection();
    printf("Here\n");
   return 0;
}
