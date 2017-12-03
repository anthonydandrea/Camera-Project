#include "server.h"
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include "camera.h"
#include "server_common.h"
#include <time.h>
#include <string.h>


pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;

//int fd;


struct global_data{
    // Global data shared between threads
    char task; // thread that has permission to run
    
    // *****************
    int count; // number of turns
    //WE WANT TO GET RID OF THIS AND RUN WHILE THERES A CONNECTION
    //******************
    
    camera * cam; // Pointer to camera where we get the images
    frame * frame; // Pointer to thre actual image
    unsigned long long size; // size of the image
    unsigned long long timestamp;
    bool motion; // Whether or not there's motion
    int userMode; // 0 = IDLE, 1 = AUTO, 2 = MOVIE
    int fd;
    int clientfd;
    int frameCount;
    
};



void * task_send(void * ctx)
{
    struct global_data *d = ctx; // Pointer to data
    // *****************
    int i; // loop variable want to get rid of this
    //*******************
    typedef char byte;
    byte * b; // bytes of image
    struct timespec ts;
    char buffer[24];
    d->cam = camera_open(); // open the camera
    //******************
    //while(true){
    for(i=0; i < d->count; ++i){ // change this to while connection, etc
    //****************
        pthread_mutex_lock(&mtx);
        printf("beforewhile\n");
        while(d->task != 'a') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        if(d->userMode == 0){
            // enforcing idle mode
            //delayBetweenFrames = 4; // 4ms wait for 15 fps
            ts.tv_sec = 0;
            ts.tv_nsec = 50000000;
        } else if (d->userMode == 2){
            // enforcing movie mode
            //delayBetweenFrames = 12; //12ms wait for 5 fps
            ts.tv_nsec = 200000000;
        } else {
            //Automatic mode, look at motion
            if(d->motion){
                // use movie mode
                //delayBetweenFrames = 4;
                ts.tv_nsec = 50000000;
            } else {
                // use idle mode
                //delayBetweenFrames = 12;
                ts.tv_nsec = 200000000;
            }
        }

        
        // gets image from fake camera and stores into global data
        d->frame = camera_get_frame(d->cam);
        b = get_frame_bytes(d->frame);
        d->size = (unsigned long) get_frame_size(d->frame);
        d->timestamp = (unsigned long long) get_frame_timestamp(d->frame);
        
        printf("size of image = %llu\n", d->size);
        printf("timestamp = %llu\n", d->timestamp);

        
        // Create a buffer with the size of the image and send it
        snprintf(buffer, 24, "%llu", d->size);
        do_serve(d->fd, buffer, 24, d->clientfd);
        
        // Create a buffer with the time and send it
        snprintf(buffer, 24, "%llu", d->timestamp);
        do_serve(d->fd, buffer, 24, d->clientfd);


        // Sends actual image
        printf("Sending image\n");
        do_serve(d->fd, b, d->size, d->clientfd);
        frame_free(d->frame);
        d->frameCount += 1;
        
        //sleep for fake camera
       // delayBetweenFrames = 60;
        //ts.tv_sec = delayBetweenFrames / 1000;
        //ts.tv_nsec = (delayBetweenFrames % 1000) * 1000000;
        nanosleep(&ts, NULL);
        // ----------------------------------
        d->task = 'b';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    camera_close(d->cam);
    printf("Done\n");
    return NULL;
}


void * task_recieve(void * ctx)
{
    struct global_data *d = ctx;
    int i;
    char msg[4];
    
    printf("started task_b\n");
    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        while(d->task != 'b') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        // look to see if there's anything send back
        // update userMode accordingly
        printf("b\n");
        //get_javamsg(msg, d->clientfd);
        if (msg[0] ==  'I'){
            printf("MESSAGE = IDL\n");
            d->userMode = 0;
        } else if (msg[0] == 'A'){
            printf("MESSAGE = AUT\n");
            d->userMode = 1;
        } else {
            printf("MESSAGE = MOV\n");
            d->userMode = 2;
        }
        // ----------------------------------
        d->task = 'a';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    return NULL;
}

/*
void * task_motion(void * ctx)
{
    struct global_data *d = ctx;
    int i = 0;
    
    printf("started task_b\n");
    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        while(d->task != 'c') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        // Look for motion
        if (frameCount > 86 && frameCount < 219){
            d->motion = true;
        } else {
            d->motion = false;
        }
 
        printf("motion\n");
        // ----------------------------------
        d->task = 'a';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    return NULL;
}
*/
int main(int argc, char* argv[])
{
    // Main program run on cameras
    // Opens connection and starts threads to send
    struct global_data data = {0,247};
    int port;
    
    printf("Enter the port number: "); //Gets user input for the numbers
    scanf(" %d", &port);
    printf("You entered %d\n", port);
    
    // create connection
    data.task = 0;
    data.count = 247;
    data.fd = getConnection1(port);
    data.clientfd = getConnection2(data.fd);
    data.userMode = 0;
    sleep(3);
    
    pthread_t sendingThread;
    pthread_t recievingThread;
    //pthread_t motionThread;
    
    // Create the first thread
    if(pthread_create(&sendingThread, NULL, task_send, &data)){
        printf("Failed to create thread_a\n");
        exit(1);
    }
    
    //start the first task
    data.task = 'a';
    
    // Create the second thread
    if(pthread_create(&recievingThread, NULL, task_recieve, &data)){
        printf("Failed to create thread_b\n");
        exit(2);
    }
    
    /*
    if(pthread_create(&motionThread, NULL, task_motion, &data)){
        printf("Failed to create thread_b\n");
        exit(2);
    }
    */
    sleep(1);
    pthread_mutex_lock(&mtx);
    
    
    // finish
    pthread_cond_signal(&cnd);
    pthread_mutex_unlock(&mtx);
    pthread_join(sendingThread, NULL);
    pthread_join(recievingThread, NULL);
    closeConnection(data.clientfd);
    printf("Here\n");
    return 0;
}
