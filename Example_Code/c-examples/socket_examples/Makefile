
CFLAGS+=-O3 -g -Wall
LDLIBS+=-lpthread

all: server1 server2

server1: server1.o
server1.o: server1.c
server2: server2.o
server2.o: server2.c

clean:
	-rm server1.o server1 server2.o server2


.PHONY: all clean

