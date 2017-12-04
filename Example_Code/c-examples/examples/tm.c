
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
};

void * task_a(void * ctx)
{
    struct global_data *d = ctx;
    int i;

    printf("started task_a\n");
    
    printf("a before loop\n");
    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        printf("here\n");
        while(d->task != 'a') pthread_cond_wait(&cnd, &mtx);
    // ----------------------------------
        printf("task_a: %d\n",i);
        image = capture_get_frame(ms);
        //b = (byte*) image->data;
     // ----------------------------------
        d->task = 'b';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    
    capture_close_stream(ms);
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
    struct global_data data = {0,10};
    typedef char byte;
    const char * c = "c";
    media_stream * ms;
    media_frame * image;
    byte * b;
    ms = capture_open_stream(c, c);
    
    pthread_t imageThread;

    pthread_t thread_b;

    // Create the first thread
    if(pthread_create(&imageThread, NULL, task_a, &data)){
	printf("Failed to create thread_a\n");
	exit(1);
    }
    
    // Create the second thread
    if(pthread_create(&thread_b, NULL, task_b, &data)){
	printf("Failed to create thread_b\n");
	exit(2);
    }
    
    sleep(1);
    pthread_mutex_lock(&mtx);
    
    // allow the first to start
    printf("setting task to a\n");
    data.task = 'a';
    
    // finish
    pthread_cond_signal(&cnd);
    pthread_mutex_unlock(&mtx);
    pthread_join(imageThread, NULL);
    pthread_join(thread_b, NULL);
    //printf("Here\n");
   return 0; 
}
