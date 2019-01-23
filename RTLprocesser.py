###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import re


class RTLprocesser:
    # noinspection SpellCheckingInspection
    @staticmethod
    def process(instructions):
        """Strip unnecessary bits of the instructions.

        Args:
            instructions (List[str]): The parsed list of instructions.

        Returns:
            List(str): The processed list of instructions.
        """
        functions = {
            "note": RTLprocesser._process_barrier,
            "barrier": RTLprocesser._process_barrier,
            "insn": RTLprocesser._process_insn,
            "call_insn": RTLprocesser._process_call_insn,
            "jump_insn": RTLprocesser._process_jump_insn,
            "code_label": RTLprocesser._process_code_label,
        }

        result = []
        for instruction in instructions:
            new_insn = functions.get(instruction[0])(instruction)

            if new_insn is not None:
                result.append(new_insn)

        return result

    @staticmethod
    def _preprocess(instruction):
        """Preprocess the instruction to include common fields.

        Args:
            instruction (List[str]): An RTL instruction.

        Returns:
            dict: The preprocessed instruction
        """
        # ["note", "insn", "jump_insn", "call_insn", "code_label", "barrier"]
        new_ins = {
            "type": instruction[0],
            "uid": int(instruction[1]),
            "prev": int(instruction[2]),
            "next": int(instruction[3]),
        }

        if len(instruction) >= 5 and instruction[4].isdigit():
            new_ins["block"] = int(instruction[4])

        return new_ins

    @staticmethod
    def _process_note(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

        if instruction[-1] != "NOTE_INSN_DELETED":
            new_instruction["note_type"] = instruction[-1]

            return new_instruction
        return None

    @staticmethod
    def _process_barrier(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

        new_instruction["block"] = -1

        return new_instruction

    @staticmethod
    def _process_insn(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

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
        elif instruction[5][1][0] == "reg:CC":
            new_instruction["target"] = {
                "value": "cc:CC",
                "offset": 0,
            }
        elif re.match(r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}",
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

                if type(instruction[5][2][1][0]) is list:
                    new_instruction['unparsed'] = instruction[5][2][1]
                else:
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
                                new_instruction[key] = [
                                    new_instruction[key]]
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

        return new_instruction

    @staticmethod
    def _process_call_insn(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

        new_instruction["rest"] = instruction[5:]

        return new_instruction

    @staticmethod
    def _process_jump_insn(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

        new_instruction.update({
            "target": instruction[5][1][0],
            "sources": [{"value": instruction[-1], "offset": 0}],
            "rest": instruction[5][2:],
        })

        return new_instruction

    @staticmethod
    def _process_code_label(instruction):
        new_instruction = RTLprocesser._preprocess(instruction)

        return new_instruction

    @staticmethod
    def _process_other(instruction):
        return instruction
