PARSER=RTLparser.py
BUILD=build
JAR=lib/json-20180813.jar
SOURCE=sources.txt
DRIVER=Driver

# C stuff
CC=gcc
FLAGS=-o $(FILE).o 
TARGET=a.out


all: $(DRIVER)
	java -cp $(BUILD):$(JAR) $(DRIVER) $(FILE).json
	$(CC) $(FILE).s $(FLAGS) 

$(DRIVER): $(SOURCE)
	python3 $(PARSER) $(FILE).c.*.expand
	javac -cp $(JAR) @$(SOURCE) -d $(BUILD)

$(SOURCE):
	find -name "*.java" > $(SOURCE)

test:
	python3 $(PARSER) $(FILE)

clean:
	rm -rf $(BUILD)/* $(SOURCE)

