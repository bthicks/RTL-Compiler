###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import json
import sys


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

        if letter == "(":
            if buffer:  # Check if there is something in the buffer
                token_list.append(''.join(buffer))
                buffer = []
            token_list.append(parse_helper(gen, []))
        elif letter == ")":
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
         List(str): The result from parse_helper.

    TODO:
        - Write tests
    """
    with open(file_name, 'r') as infile:
        word_generator = (letter for line in infile for letter in line)
        return parse_helper(word_generator, [])


def check_args():
    """Show usage message if program called incorrectly."""
    if len(sys.argv) != 2:
        sys.exit(f"Usage: {sys.argv[0]} file_name")


def main():
    """Run the parser on the given RTL file.

    Return:
        JSON: The JSON version of the parsed file.
    """
    check_args()

    return json.dumps(parse(sys.argv[1]))


if __name__ == '__main__':
    print(main())
