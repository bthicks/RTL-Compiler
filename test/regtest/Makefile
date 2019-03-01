CC = gcc
CFLAGS = -g -Wall -O1
CSRCS = driver.c regtest1.s regtest2.s
BIN = regtest

all:
	$(CC) $(CFLAGS) -c $(CSRCS)
	$(CC) $(CFLAGS) -o $(BIN) *.o

clean:
	rm -f $(BIN) *.o
