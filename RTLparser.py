###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import json
import re
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
         List(str): The parsed list of instructions.

    TODO:
        - Write tests
    """
    with open(file_name, 'r') as infile:
        for line in infile:
            if ";; Full RTL generated for this function:" in line:
                break

        word_generator = (letter for line in infile for letter in line)

        for _ in range(2):  # Remove trailing comments
            next(word_generator, None)

        return parse_helper(word_generator, [])


def process(instructions):
    """Strip unnecessary bits of the instructions.

    Args:
        instructions (List[str]): The parsed list of instructions.

    Returns:
        List(str): The processed list of instructions.
    """
    result = []
    for instruction in instructions:
        new_instruction = {}

        if instruction[0] in ["note", "insn", "jump_insn", "call_insn", "code_label", "barrier"]:
            new_instruction = {
                "type": instruction[0],
                "uid": instruction[1],
                "prev": instruction[2],
                "next": instruction[3],
            }

        if instruction[0] == "note":
            if instruction[-1] != "NOTE_INSN_DELETED":
                new_instruction.update({
                    "block": instruction[4],
                    "note_type": instruction[-1]
                })

                result.append(new_instruction)
        elif instruction[0] == "barrier":
            result.append(new_instruction)
        elif instruction[0] == "insn":
            new_instruction.update({
                "block": instruction[4],
                "expr": {"type": instruction[5][0], "rest": instruction[5][1:]}
            })

            match = re.search(r"^reg:(?P<type>[A-Z]I)$", instruction[5][1][0])
            if match:
                new_instruction["expr"]["target"] = "r{number}:{type}".format(
                    number=instruction[5][1][1],
                    type=match.group('type'))

                if instruction[5][-1][0] == "const_int":
                    new_instruction["expr"]["source"] = [instruction[5][2][1]]

            match = re.search(r"^reg:(?P<type>[A-Z]I)$", instruction[5][-1][0])
            if match:
                string = "r{number}:{type}".format(
                    number=instruction[5][-1][1],
                    type=match.group('type'))

                if "source" in new_instruction:
                    new_instruction["expr"]["source"] += [string]
                else:
                    new_instruction["expr"]["source"] = [string]

            if instruction[-1] != ['nil']:
                new_instruction["expr_list"] = instruction[-1]

            result.append(new_instruction)
        elif instruction[0] == "call_insn":
            new_instruction.update({
                "block": instruction[4],
                "rest": instruction[5:]
            })
            result.append(new_instruction)
        elif instruction[0] == "jump_insn":
            new_instruction.update({
                "block": instruction[4],
                "expr": {"type": instruction[5][0], "rest": instruction[5][1:]},
                "label_ref": instruction[-1]
            })
            result.append(new_instruction)
        elif instruction[0] == "code_label":
            new_instruction["block"] = instruction[4]
            result.append(new_instruction)
        else:
            result.append(instruction)

    return result


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

    i = sys.argv[1].index('.')  # Index of first period

    with open("{infile}.json".format(infile=sys.argv[1][:i]), 'w') as outfile:
        json.dump(process(parse(sys.argv[1])), outfile, indent=4)


if __name__ == '__main__':
    main()
