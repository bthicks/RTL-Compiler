PARSER=RTLparser.py
BUILD=build
JAR=lib/json-20180813.jar
SOURCE=sources.txt
DRIVER=Driver


all: $(DRIVER)
	java -cp $(BUILD):$(JAR) $(DRIVER) $(FILE2)

$(DRIVER): $(SOURCE)
	python3 $(PARSER) $(FILE)
	javac -cp $(JAR) @$(SOURCE) -d $(BUILD)

$(SOURCE):
	find -name "*.java" > $(SOURCE)

test:
	python3 $(PARSER) $(FILE)

clean:
	rm -rf $(BUILD)/* $(SOURCE)

