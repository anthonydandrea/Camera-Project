#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <unistd.h>

int main(){

    int fd  =  socket(AF_INET,SOCK_STREAM,0); 

    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_port =htons(9990);
    inet_aton("localhost",&server_address.sin_addr);

    int send = connect(fd,(struct sockaddr*) &server_address,sizeof(server_address));

    char s[10000];
    printf("made array\n");

    FILE* fp;
    fp = fdopen(send,"r+");
    fprintf(fp,"GET / HTTP/1.0\n\n");
    fflush(fp);

    printf("found while loop\n");
    while(fgets(s,sizeof(s),fp) !=0){
        fputs(s,stdout);
    }

    fclose(fp);

    printf("Close socket %d",send);
    printf("connected");

   close(fd);
    

    printf("Connected");
    return 0;
}
