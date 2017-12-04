#include "server.h"
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include "camera.h"
#include "server_common.h"
#include "motion_client.h"
#include <time.h>
#include <string.h>


pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;


struct global_data{
    // Global data shared between threads
    char task; // thread that has permission to run
    
    int count; // number of turns for fake camera
    
    camera * cam; // Pointer to camera where we get the images
    frame * frame; // Pointer to thre actual image
    int size; // size of the image
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
    int i; // loop variable for fake camera
    typedef char byte;
    byte * b; // bytes of image
    struct timespec ts;
    char sizeBuffer[6];
    char timeBuffer[20];
    char noMotionMsg[5] = "stil";
    char motionMsg[5] = "have";
    d->cam = camera_open(); // open the camera
    int delayBetweenFrames;
    
    //while(true){ // change this to for for fake camera
    for(i=0; i < d->count; ++i){ // change this to while for real camera

        pthread_mutex_lock(&mtx);
        while(d->task != 'a') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        if(d->userMode == 0){
            // enforcing idle mode
            delayBetweenFrames = 250;
        } else if (d->userMode == 2){
            // enforcing movie mode
            delayBetweenFrames = 125;
        } else {
            //Automatic mode, look at motion
            if(d->motion){
                // use movie mode
                delayBetweenFrames = 125;
            } else {
                // use idle mode
                delayBetweenFrames = 250;
            }
        }
        
        // get image from fake camera and stores into global data
        d->frame = camera_get_frame(d->cam);
        b = get_frame_bytes(d->frame);
        d->size = (int) get_frame_size(d->frame);
        d->timestamp = (unsigned long long) get_frame_timestamp(d->frame);
        
        
        // Create a buffer with the size of the image and send it
        snprintf(sizeBuffer, 6, "%d", d->size);
        do_serve(d->fd, sizeBuffer, 6, d->clientfd);
        
        if ((d->userMode == 1) && (d->motion == true)){
            // Only care about motion if we are in automatic mode
             do_serve(d->fd, motionMsg, 5, d->clientfd);
        } else {
            // Otherwise it has no effect
            do_serve(d->fd, noMotionMsg, 5, d->clientfd);
        }

        // Sends actual image
        do_serve(d->fd, b, d->size, d->clientfd);
        
        // Create a buffer with the time and send it
        snprintf(timeBuffer, 20, "%llu", d->timestamp);
        do_serve(d->fd, timeBuffer, 20, d->clientfd);
        
        // free frame
        frame_free(d->frame);
        d->frameCount += 1;
        
        //sleep for fake camera
        ts.tv_sec = delayBetweenFrames / 1000;
        ts.tv_nsec = (delayBetweenFrames % 1000) * 1000000;
        nanosleep(&ts, NULL);
        // ----------------------------------
        d->task = 'b';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    camera_close(d->cam);
    return NULL;
}


void * task_recieve(void * ctx)
{
    struct global_data *d = ctx;
    int i;
    char msg[4];
    

    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        while(d->task != 'b') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        // look to see if there's anything send back
        // update userMode accordingly
        get_javamsg(msg, d->clientfd);
        if (msg[0] ==  'I'){
            d->userMode = 0;
        } else if (msg[0] == 'A'){
            d->userMode = 1;
        } else {
            d->userMode = 2;
        }
        // ----------------------------------
        d->task = 'c';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    return NULL;
}


void * task_motion(void * ctx)
{
    struct global_data *d = ctx;
    int i = 0;
    
    for(i=0; i < d->count; ++i){
        pthread_mutex_lock(&mtx);
        while(d->task != 'c') pthread_cond_wait(&cnd, &mtx);
        // ----------------------------------
        // Look for motion
        // If in real camera, poll motion server here
        // get_motion();
        // interpret the data here
        if (d->frameCount > 86 && d->frameCount < 219){
            d->motion = true;
        } else {
            d->motion = false;
        }
        // ----------------------------------
        d->task = 'a';
        pthread_cond_signal(&cnd);
        pthread_mutex_unlock(&mtx);
    }
    return NULL;
}

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
    
    // create motion client connection
    //init("localhost", 9090);
    //motion_get();
    
    
    data.userMode = 1;
    data.motion = 0;
    sleep(3);
    
    pthread_t sendingThread;
    pthread_t recievingThread;
    pthread_t motionThread;
    
    // Create the thread sending data
    if(pthread_create(&sendingThread, NULL, task_send, &data)){
        printf("Failed to create sending thread\n");
        exit(1);
    }
    
    //start the first task
    data.task = 'a';
    
    // Create the thread revieving data
    if(pthread_create(&recievingThread, NULL, task_recieve, &data)){
        printf("Failed to create recieving thread\n");
        exit(2);
    }
    
    // create the thread detecting motion
    if(pthread_create(&motionThread, NULL, task_motion, &data)){
        printf("Failed to create motion thread\n");
        exit(2);
    }
    
    sleep(1);
    pthread_mutex_lock(&mtx);
    
    
    // finish
    pthread_cond_signal(&cnd);
    pthread_mutex_unlock(&mtx);
    pthread_join(sendingThread, NULL);
    pthread_join(recievingThread, NULL);
    pthread_join(motionThread, NULL);
    closeConnection(data.clientfd);
    return 0;
}
