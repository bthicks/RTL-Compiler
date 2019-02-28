###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import json
import re
import sys

from RTLprocesser import RTLprocesser


def parse_helper(gen, token_list):
    """A recursive method to help parse RTL.

    This method will convert all parentheses into lists and split every word
    by space into the appropriate list. A new letter will be generated until
    there are none left.

    Args:
        gen (Generator): Generates one letter at a time.
        token_list (List[str]): The list to which tokens should be added.

    Return:
        List(str): The complete list of elements in list form.

    TODO:
        - Write tests
        - Refactor `if buffer: token_list.append(...)`
    """
    buffer = []  # Python's version of a StringBuilder

    while True:
        try:
            letter = next(gen)
        except StopIteration:  # Better to ask for forgiveness than permission
            return token_list

        if letter in ["(", "["]:
            if buffer:  # Check if there is something in the buffer
                token_list.append(''.join(buffer))
                buffer = []
            token_list.append(parse_helper(gen, []))
        elif letter in [")", "]"]:
            if buffer:  # Check if there is something in the buffer
                token_list.append(''.join(buffer))
            return token_list
        elif letter.isspace():
            if buffer:  # Check if there is something in the buffer
                token_list.append(''.join(buffer))
                buffer = []
        else:
            buffer.append(letter)


def parse(file_name):
    """Open the file associated with the given file and parse the RTL.

    Args:
        file_name (str): The name of the file

    Returns:
         Dict(str): The parsed list of instructions.

    TODO:
        - Write tests
    """
    result = {}
    with open(file_name, 'r') as infile:
        while True:
            name = ""
            for line in infile:
                match = re.match(";; Function (?P<name>([A-Z]|[a-z]|[0-9])*)", line)
                if match:
                    name = match.group('name')
                elif ";; Full RTL generated for this function:" in line:
                    break
            else:
                return result

            for _ in range(1):
                infile.readline()

            word_generator = generate_instructions(infile)
            #
            # for _ in range(2):  # Remove trailing comments
            #     next(word_generator, None)

            result[name] = parse_helper(word_generator, [])


def generate_instructions(infile):
    for line in infile:
        if line not in ("\n", "\r\n"):
            for letter in line:
                yield letter
        else:
            break

    raise StopIteration


def check_args():
    """Show usage message if program called incorrectly."""
    if len(sys.argv) != 2:
        sys.exit("Usage: {fname} file_name".format(fname=sys.argv[0]))


def main():
    """Run the parser on the given RTL file and output to a file.

    TODO:
        - Allow for user to input their own outfile name
        - Take outfile name from given file if not specified
    """
    check_args()

    result = []
    parsed = parse(sys.argv[1])
    for name, insns in parsed.items():
        result.append(RTLprocesser.process(insns, name))

    file_name = sys.argv[1][:sys.argv[1].index('.')]
    with open("{infile}.json".format(infile=file_name), 'w') as outfile:
        json.dump(result, outfile, indent=4)

    # with open("rtl.json", 'w') as outfile:
    #     json.dump(RTLprocesser.process(parse(sys.argv[1])), outfile, indent=4)


if __name__ == '__main__':
    main()
