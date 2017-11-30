#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <string.h>

int fd;

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
static int do_serve(int fd)
{
    int clientfd;
    const char* msg = "Hello, socket!\n"
                      "I am a text\n"
                      "BYE.\n";
    size_t len = strlen(msg);

    printf("simple_tcp_server: attempting accept on fd %d\n",fd);
    if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
#ifdef INFO
    printf("simple_tcp_server: writing msg (len=%lu) to clientfd (%d)\n",len,clientfd);
#endif

#ifdef WRITE_LOOP
    size_t written = 0;
    do {
        int res = write(clientfd, msg, len);
        if (res < 0) {
            perror("write to clientfd");
            goto error;
        }
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
    return clientfd;
}
/****Grab message from java client **/

#define BUFSZ 100
static void get_javamsg(int fd)
{
    char msg[BUFSZ];

        int res = read(fd, msg, BUFSZ-1);
        if(res < 0) {
            perror("ERROR reading from server");
        } else {
            msg[res]='\0'; /* ensure msg is null terminated */
            printf("client: response: %s\n",msg);
        }

}

int main()
{
    fd = create_server_socket(5020);

    if(fd < 0){
        perror("create_server_socket");
        return 1;
    }
    for(int x = 0 ; x < 20 ; x++){
      int clientfd =  do_serve(fd);
        get_javamsg(clientfd);
    printf("simple_tcp_server: closing socket: %d\n", fd);
  }
    close(fd);

    return 0;
}
