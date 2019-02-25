CXX = g++
CXXFLAGS = -g -Wall -O1
CXXSRCS = driver.cpp
BIN = regtest

all:
	$(CXX) $(CXXFLAGS) -c $(CXXSRCS) $(FILE)
	$(CXX) $(CXXFLAGS) -o $(BIN) *.o

clean:
	rm -f $(BIN) *.o
