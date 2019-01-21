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

        if instruction[0] in ["note", "insn", "jump_insn", "call_insn",
                              "code_label", "barrier"]:
            new_instruction = {
                "type": instruction[0],
                "uid": int(instruction[1]),
                "prev": int(instruction[2]),
                "next": int(instruction[3]),
            }

            if len(instruction) >= 5 and instruction[4].isdigit():
                new_instruction["block"] = int(instruction[4])

        if instruction[0] == "note":
            if instruction[-1] != "NOTE_INSN_DELETED":
                new_instruction["note_type"] = instruction[-1]

                result.append(new_instruction)
        elif instruction[0] == "barrier":
            result.append(new_instruction)
        elif instruction[0] == "insn":
            new_instruction["expr"] = {
                "type": instruction[5][0], "rest": instruction[5][1:]
            }

            match = re.match(r"reg(/\w)*:(?P<type>[A-Z]I)",
                             instruction[5][1][0])
            if match:
                new_instruction["target"] = {
                    "value": "r{number}:{type}".format(
                        number=instruction[5][1][1],
                        type=match.group('type')),
                    "offset": 0
                }

                if instruction[5][-1][0] == "const_int":
                    new_instruction["sources"] = [{
                        "value": instruction[5][2][1],
                        "offset": 0
                    }]

            if re.match(r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}",
                        instruction[5][1][0]):

                match = re.match(r"(?P<type>\w*):[A-Z]{2}",
                                 instruction[5][1][1][0])
                if match:
                    new_instruction['expr']['mem'] = {
                        'type': match.group('type'),
                        'rest': instruction[5][1][1][1:]
                    }

                    match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                     instruction[5][1][1][1][0])
                    if match:
                        string = "r{number}:{type}".format(
                            number=instruction[5][1][1][1][1],
                            type=match.group('type'))

                        key = 'sources' if 'target' in new_instruction else 'target'

                        if instruction[5][1][1][2][0] == 'const_int':
                            new_instruction[key] = {
                                "value": string,
                                "offset": int(instruction[5][1][1][2][1])
                            }
                        else:
                            new_instruction[key] = {
                                "value": string,
                                "offset": 0
                            }

                        if key == 'sources':
                            new_instruction[key] = [new_instruction[key]]
                else:
                    new_instruction['expr']['mem'] = instruction[5][1][1:]

            if len(instruction[5]) >= 3:
                if re.match(
                        r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}",
                        instruction[5][2][0]):

                    match = re.match(r"(?P<type>\w*):[A-Z]{2}",
                                     instruction[5][2][1][0])
                    if match:
                        new_instruction['expr']['mem'] = {
                            'type': match.group('type'),
                            'rest': instruction[5][2][1][1:]
                        }

                        match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                         instruction[5][2][1][1][0])
                        if match:
                            string = "r{number}:{type}".format(
                                number=instruction[5][2][1][1][1],
                                type=match.group('type'))

                            key = 'sources' if 'target' in new_instruction else 'target'

                            if instruction[5][2][1][2][0] == 'const_int':
                                new_instruction[key] = {
                                    "value": string,
                                    "offset": int(
                                        instruction[5][2][1][2][1])
                                }
                            else:
                                new_instruction[key] = {
                                    "value": string,
                                    "offset": 0
                                }

                            if key == 'sources':
                                new_instruction[key] = [new_instruction[key]]
                    else:
                        new_instruction['expr']['mem'] = instruction[5][2][1:]
                elif re.match(r"([a-z]|[A-Z])+:[A-Z]{2}",
                              instruction[5][2][0]):

                    match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                     instruction[5][2][1][0])
                    if match:
                        string = "r{number}:{type}".format(
                            number=instruction[5][2][1][1],
                            type=match.group('type'))

                        key = 'sources' if 'target' in new_instruction else 'target'

                        if instruction[5][2][2][0] == 'const_int':
                            new_instruction[key] = {
                                "value": string,
                                "offset": int(
                                    instruction[5][2][2][1])
                            }

                            if key == 'sources':
                                new_instruction[key] = [new_instruction[key]]
                        elif re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                      instruction[5][2][2][0]):

                            string2 = "r{number}:{type}".format(
                                number=instruction[5][2][1][1],
                                type=match.group('type'))

                            new_instruction[key] = [
                                {
                                    "value": string,
                                    "offset": 0
                                },
                                {
                                    "value": string2,
                                    "offset": 0
                                },
                            ]

            match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                             instruction[5][-1][0])
            if match:
                string = "r{number}:{type}".format(
                    number=instruction[5][-1][1],
                    type=match.group('type'))

                if "sources" in new_instruction:
                    new_instruction["sources"] += [{
                        "value": string,
                        "offset": 0
                    }]
                else:
                    new_instruction["sources"] = [{
                        "value": string,
                        "offset": 0
                    }]

            if instruction[-1] != ['nil']:
                new_instruction["expr_list"] = instruction[-1]

            result.append(new_instruction)
        elif instruction[0] == "call_insn":
            new_instruction["rest"] = instruction[5:]

            result.append(new_instruction)
        elif instruction[0] == "jump_insn":
            new_instruction.update({
                "expr": {
                    "type": instruction[5][0],
                    "target": instruction[5][1][0],
                    "rest": instruction[5][2:]
                },
                "label_ref": instruction[-1]
            })
            result.append(new_instruction)
        elif instruction[0] == "code_label":
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

    file_name = sys.argv[1][:sys.argv[1].index('.')]

    with open("{infile}.json".format(infile=file_name), 'w') as outfile:
        json.dump(process(parse(sys.argv[1])), outfile, indent=4)


if __name__ == '__main__':
    main()
