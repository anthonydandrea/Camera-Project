#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <string.h>
#include "server.h"
#include "fakecapture.h"

int bind_server_socket(int fd, int port);

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

 int bind_server_socket(int fd, int port){

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

//int clientfd;
int getConnection1(int port) {
  int fd = create_server_socket(port);
  if(fd < 0){
      perror("create_server_socket");
      return 1;
  }

  //if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
  //printf("Connected successfully\n");
  return fd;
}

int getConnection2(int fd){
    int clientfd;
    if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
    printf("Connected successfully\n");
    return clientfd;
}

int closeConnection(clientfd) {

  printf("simple_tcp_server: closing socket");
  return close(clientfd);
}
/*
 * Serve one client: send a message and close the socket
 */
int do_serve(int fd, char *msg, long filelen, int clientfd)
{
   // int javaclient;
    size_t len = filelen;
    printf("len:%lu\n",len);
    printf("simple_tcp_server: attempting accept on fd %d\n",fd);

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
    //fake_motion_free();
    return 0;
    //return javaclient;
}

#define BUFSZ 5
void get_javamsg(char * msg, int fd)
{
    //char msg[BUFSZ];
    printf("Trying to read from Java\n");
    int res = read(fd, msg, BUFSZ-1);
    if(res < 0) {
        printf("ERROR HERE\n");
        perror("ERROR reading from server");
        return;
    } else {
        msg[res]='\0'; /* ensure msg is null terminated */
        printf("client: response: %s\n",msg);
        return;
    }
    
}


