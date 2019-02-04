PARSER=RTLparser.py

all: $(RTL)
    javac src/*.java

$(RTL):
    python3 $(PARSER) $(FILE)

    IFS='.'




test:
	python3 $(PARSER) $(FILE)

clean:
	rm $(RTL)

