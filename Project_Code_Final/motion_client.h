#ifndef motion_client_h
#define motion_client_h

void init(const char* server_name, int port);
int poll_motion(int period);
int install_handlers();
void cleanup(int);
void socket_init();
void socket_close();
void motion_get();


#endif /* motion_client_h */
