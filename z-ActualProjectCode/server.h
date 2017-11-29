
#ifndef server_h

int bind_server_socket(int fd, int port);
int create_server_socket(int port);
int do_serve(int fd, char * msg, long filelen);

#define server_h


#endif /* server_h */
