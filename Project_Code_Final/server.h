
#ifndef server_h

int bind_server_socket(int fd, int port);
int create_server_socket(int port);
int do_serve(int fd, char * msg, long filelen, int clientfd);
int getConnection1(int port);
int getConnection2(int fd);
int closeConnection();
void get_javamsg(char * msg, int fd);

#define server_h


#endif /* server_h */
