PARSER=RTLparser.py
BUILD=build
JAR=lib/json-20180813.jar
SOURCE=sources.txt
DRIVER=Driver

# C stuff
CC=gcc
TARGET=a.out
FLAGS=-o $(TARGET)

LIB_O=cpe431util.o
LIB_A=libutil.a


all: java $(TARGET)

$(TARGET):
	$(CC) -c -o $(LIB_O) addwithprint/cpe431util.c
	ar rcs $(LIB_A) $(LIB_O)
	$(CC) $(FILE).s -L. -lutil $(FLAGS)

java: $(SOURCE)	python
	javac -cp $(JAR) @$(SOURCE) -d $(BUILD)
	java -cp $(BUILD):$(JAR) $(DRIVER) $(FILE).json

$(SOURCE):
	@find -name "*.java" > $(SOURCE)

python:
	python3 $(PARSER) $(FILE).c.*.expand

test:
	python3 $(PARSER) $(FILE).c.*.expand

clean:
	rm -rf $(BUILD)/* $(SOURCE) $(TARGET) $(LIB_A) $(LIB_O)

