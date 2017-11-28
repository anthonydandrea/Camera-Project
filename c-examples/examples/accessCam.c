#include <stdio.h>
#include "fakecapture.h"
#include "server_common.h"

int main(){
    // accesses the fake camera
    typedef char byte;
    
    printf("Starting camera access\n");
    const char * c = "c";
    media_stream * ms;
    media_frame * image;
    media_frame * image2;
    byte * b;
    byte* b2;
    int numTurns = 254;
    int i = 0;
    ms = capture_open_stream(c, c); //open a media stream
    
    // Do stuff here
    //image1 = capture_get_frame(ms);
    //image2 = capture_get_frame(ms);
    for(int i = 0; i < numTurns; ++i){
        image = capture_get_frame(ms);
        b = (byte*) image->data;
    //b1 = (byte*) image1->data;
    //b2 = (byte*) image2->data;
    }
    
    capture_close_stream(ms); // close media stream
    
    return 0;
}
