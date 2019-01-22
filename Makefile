PARSER=RTLparser.py
RTL=rtl.json

all: $(RTL)
	javac src/*.java

$(RTL):
	@python3 $(PARSER) $(FILE)


test:
	python3 $(PARSER) $(FILE)

clean:
	rm $(RTL)

