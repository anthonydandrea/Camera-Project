#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <string.h>
#include <unistd.h>

static int bind_server_socket(int fd, int port);

/*
 * create a server socket bound to port
 * and listening.
 *
 * return positive file descriptor
 * or negative value on error
 */
int create_server_socket(int port)
{
    int fd = -1;

    if(port < 0 || port > 65535) {
       errno = EINVAL;
       return -1;
    }
    fd = socket(AF_INET,SOCK_STREAM,0);
    if(fd < 0) return fd;
    if(bind_server_socket(fd,port) < 0) return -1;

    if(listen(fd,10)) return -1;

    return fd;
}

static int bind_server_socket(int fd, int port){

    struct sockaddr_in addr;
    int val = 1;
    if(setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val))) {
        perror("setsockopt");
        return -1;
    }

    /* see man page ip(7) */
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

    if(bind(fd, (struct sockaddr*) &addr, sizeof(addr))) return -1;

#ifdef INFO
    printf("simple_tcp_server: bound fd %d to port %d\n",fd,port);
#endif

    return fd;
}

/*
 * Serve one client: send a message and close the socket
 */

 int clientfd;
static int do_serve(int num)
{

  //////////////////////////////////

    FILE *fileptr;
    char *msg;
    long filelen;

    if(num == 1)
      fileptr = fopen("media/film120.jpg", "rb");  // Open the file in binary mode
    if(num == 2)
      fileptr = fopen("media/film121.jpg", "rb");
      if(num == 3)
        fileptr = fopen("media/film122.jpg", "rb");
        if(num == 4)
          fileptr = fopen("media/film123.jpg", "rb");
          if(num == 5)
            fileptr = fopen("media/film124.jpg", "rb");
            if(num == 6)
              fileptr = fopen("media/film125.jpg", "rb");
              if(num == 7)
                fileptr = fopen("media/film126.jpg", "rb");
                if(num == 8)
                  fileptr = fopen("media/film127.jpg", "rb");
                  if(num == 9)
                    fileptr = fopen("media/film128.jpg", "rb");
                    if(num == 10)
                      fileptr = fopen("media/film129.jpg", "rb");
                      if(num == 11)
                        fileptr = fopen("media/film130.jpg", "rb");
                        if(num == 12)
                          fileptr = fopen("media/film131.jpg", "rb");
                          if(num == 13)
                            fileptr = fopen("media/film132.jpg", "rb");
                            if(num == 14)
                              fileptr = fopen("media/film133.jpg", "rb");
                              if(num == 15)
                                fileptr = fopen("media/film134.jpg", "rb");
                                if(num == 16)
                                  fileptr = fopen("media/film135.jpg", "rb");
                                  if(num == 17)
                                    fileptr = fopen("media/film136.jpg", "rb");
                                    if(num == 18)
                                      fileptr = fopen("media/film137.jpg", "rb");
                                      if(num == 19)
                                        fileptr = fopen("media/film138.jpg", "rb");
                                        if(num == 20)
                                          fileptr = fopen("media/film139.jpg", "rb");



    fseek(fileptr, 0, SEEK_END);          // Jump to the end of the file
    filelen = ftell(fileptr);             // Get the current byte offset in the file
    rewind(fileptr);                      // Jump back to the beginning of the file

    msg = (char *)malloc((filelen+1)*sizeof(char)); // Enough memory for file + \0
    fread(msg, filelen, 1, fileptr); // Read in the entire file
    fclose(fileptr); // Close the file

    printf("filelength: %lu\n",filelen);

    // FILE *f = fopen("CSent.txt", "w");
    // if (f == NULL)
    // {
    //   printf("Error opening file!\n");
    //   exit(1);
    // }
    //
    // for(int x = 0 ; x < filelen ; x++)
    //   //  printf("%d",msg[x]);
    //   fprintf(f, "%d", msg[x]);
    //
    // fclose(f);


  ////////////////////////////////////



    /*const char* msg = "Hello, socket!\n"
                      "I am a text\n"
                      "BYE.\n";
                      */
    size_t len = filelen;
    printf("len:%lu\n",len);
    //printf("simple_tcp_server: attempting accept on fd %d\n",fd);

#ifdef INFO
  //printf("simple_tcp_server: writing msg (len=%lu) to clientfd (%d)\n",len,clientfd);
#endif

#ifdef WRITE_LOOP
    size_t written = 0;
    do {
        int res = write(clientfd, msg, len);
        if (res < 0) {
            perror("write to clientfd");
            goto error;
        }
    free(msg);
#ifdef INFO
        printf("simple_tcp_server: write returned %d\n",res);
#endif
        written += res;
    } while (written < len);
#else
    {
        int res = write(clientfd, msg, len);
        if (res < 0) {
            perror("write to clientfd");
            goto error;
        }
    }
#endif

 error:
    printf("simple_tcp_server: closing clientfd (%d)\n",clientfd);

}

int makeConnection(int fd) {
    if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
    printf("Connection made on port %d\n",fd );
    return 1;

}

int closeConnection() {
    return close(clientfd);
}

int main()
{
    int fd = create_server_socket(9996);

    if(fd < 0){
        perror("create_server_socket");
        return 1;
    }

    if(!makeConnection(fd)) return -1;
    int x = 1;
    //for(int x = 1 ; x <= 20 ; x++) {
      do_serve(x);
        printf("on %d\n", x);
      //sleep(1);
    // }

    closeConnection();

    printf("simple_tcp_server: closing socket: %d\n", fd);
    close(fd);


    return 0;
}
