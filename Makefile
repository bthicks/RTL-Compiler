PARSER=RTLparser.py

all: $(RTL) $(PLAINFILE) $(RTL)
    javac src/*.java

$(PLAINFILE): $(FILE)
    IFS='.'
    read -ra ADDR <<< $(FILE)
    for i in "${ADDR[@]}"
        echo "$i"
    done

$(RTL):
    python3 $(PARSER) $(FILE)

test:
	python3 $(PARSER) $(FILE)

clean:
	rm $(RTL)

