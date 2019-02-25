CXX = g++
CXXFLAGS = -g -Wall -O1
CXXSRCS = driver.cpp regtest1.c regtest2.c
BIN = regtest

all:
	$(CXX) $(CXXFLAGS) -c $(CXXSRCS)
	$(CXX) $(CXXFLAGS) -o $(BIN) *.o

clean:
	rm -f $(BIN) *.o
