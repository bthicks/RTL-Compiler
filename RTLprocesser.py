###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import re
import sys


class RTLprocesser:
    codes = {
        "addr_diff_vec": _process_addr_diff_vec,
        "addr_vec": _process_addr_vec,
        "asm_input": _process_asm_input,
        "asm_output": _process_asm_output,
        "call": _process_call,
        "clobber": _process_clobber,
        "cond_exec": _process_cond_exec,
        "parallel": _process_parallel,
        "return": _process_return,
        "sequence": _process_sequence,
        "set": _process_set,
        "simple_return": _process_simple_return,
        "trap_if": _process_trap_if,
        "unspec": _process_unspec,
        "unspec_volatile": _process_unspec_volatile,
        "use": _process_use,
    }

    @staticmethod
    def _process_set():
        pass

    @staticmethod
    def _process_return():
        pass

    @staticmethod
    def _process_call():
        pass

    @staticmethod
    def _process_use():
        pass

    @staticmethod
    def _process_clobber():
        pass

    @staticmethod
    def _process_simple_return():
        pass

    @staticmethod
    def _process_asm_input():
        pass

    @staticmethod
    def _process_asm_output():
        pass

    @staticmethod
    def _process_addr_vec():
        pass

    @staticmethod
    def _process_addr_diff_vec():
        pass

    @staticmethod
    def _process_trap_if():
        pass

    @staticmethod
    def _process_unspec():
        pass

    @staticmethod
    def _process_unspec_volatile():
        pass

    @staticmethod
    def _process_cond_exec():
        pass

    @staticmethod
    def _process_sequence():
        pass

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
            "note": RTLprocesser._process_note,
            "barrier": RTLprocesser._process_barrier,
            "insn": RTLprocesser._process_insn,
            "call_insn": RTLprocesser._process_call_insn,
            "call_insn/i": RTLprocesser._process_call_insn,
            "jump_insn": RTLprocesser._process_jump_insn,
            "code_label": RTLprocesser._process_code_label,
        }

        result = []
        for instruction in instructions:
            function = functions.get(instruction[0])

            new_insn = {}

            if function is None:
                RTLprocesser._process_other(instruction, new_insn)
            else:
                function(instruction, new_insn)

            if new_insn:
                result.append(new_insn)

        return result

    @staticmethod
    def _preprocess(instruction, new_insn):
        """Preprocess the instruction to include common fields.

        Args:
            instruction (List[str]): An RTL instruction.
        """
        # ["note", "insn", "jump_insn", "call_insn", "code_label", "barrier"]
        new_insn.update({
            "type": instruction[0],
            "uid": int(instruction[1]),
            "prev": int(instruction[2]),
            "next": int(instruction[3]),
        })

        if len(instruction) >= 5 and instruction[4].isdigit():
            new_insn["block"] = int(instruction[4])

    @staticmethod
    def _process_note(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        if instruction[-1] != "NOTE_INSN_DELETED":
            new_insn["note_type"] = instruction[-1]

    @staticmethod
    def _process_barrier(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        new_insn["block"] = -1

    @staticmethod
    def _process_insn2(instruction, new_insn):
        method = RTLprocesser.codes.get(instruction[0])

        if method is not None:
            method(instruction[1:], new_insn)
        else:
            print("UNRECOGNIZED INSTRUCTION:", instruction, file=sys.stderr)

    @staticmethod
    def _process_insn(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        new_insn["expr"] = {
            "type": instruction[5][0], "rest": instruction[5][1:]
        }

        match = re.match(r"reg(/\w)*:(?P<type>[A-Z]I)",
                         instruction[5][1][0])
        if match:
            new_insn["target"] = {
                "value": "r{number}:{type}".format(
                    number = instruction[5][1][1],
                    type = match.group('type')),
                "offset": 0
            }

            if instruction[5][-1][0] == "const_int":
                new_insn["sources"] = [{
                    "value": instruction[5][2][1],
                    "offset": 0
                }]
        elif instruction[5][1][0] == "reg:CC":
            new_insn["target"] = {
                "value": "cc:CC",
                "offset": 0,
            }
        elif re.match(r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}", instruction[5][1][0]):

            match = re.match(r"(?P<type>\w*):[A-Z]{2}",
                             instruction[5][1][1][0])
            if match:
                if match.group('type') != 'reg':
                    new_insn['expr']['mem'] = {
                        'type': match.group('type'),
                        'rest': instruction[5][1][1][1:]
                    }

                    match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                     instruction[5][1][1][1][0])
                    if match:
                        string = "r{number}:{type}".format(
                            number = instruction[5][1][1][1][1],
                            type = match.group('type'))

                        key = 'sources' if 'target' in new_insn else 'target'

                        if instruction[5][1][1][2][0] == 'const_int':
                            new_insn[key] = {
                                "value": string,
                                "offset": int(instruction[5][1][1][2][1])
                            }
                        else:
                            new_insn[key] = {
                                "value": string,
                                "offset": 0
                            }

                        if key == 'sources':
                            new_insn[key] = [new_insn[key]]
                else:
                    new_insn['target'] = {
                        "value": RTLprocesser._get_register(
                            instruction[5][1][1], new_insn),
                        "offset": 0
                    }
            elif RTLprocesser._get_register(instruction[5][1][1], new_insn):
                new_insn['target'] = {
                    "value": RTLprocesser._get_register(instruction[5][1][1],
                                                        new_insn),
                    "offset": 0
                }
            else:
                new_insn['expr']['mem'] = instruction[5][1][1:]

        if len(instruction[5]) >= 3:
            if instruction[5][2][0] == "unspec:SI":
                new_insn['sources'] = [
                    {
                        "value": "symbol_ref",
                        "offset": 0,
                    }
                ]
            elif re.match(
                    r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}",
                    instruction[5][2][0]):

                match = re.match(r"(?P<type>\w*):[A-Z]{2}",
                                 instruction[5][2][1][0])
                if match:
                    new_insn['expr']['mem'] = {
                        'type': match.group('type'),
                        'rest': instruction[5][2][1][1:]
                    }

                    match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                     instruction[5][2][1][1][0])
                    if match:
                        string = "r{number}:{type}".format(
                            number = instruction[5][2][1][1][1],
                            type = match.group('type'))

                        key = 'sources' if 'target' in new_insn else 'target'

                        if instruction[5][2][1][2][0] == 'const_int':
                            new_insn[key] = {
                                "value": string,
                                "offset": int(
                                    instruction[5][2][1][2][1])
                            }
                        else:
                            new_insn[key] = {
                                "value": string,
                                "offset": 0
                            }

                        if key == 'sources':
                            new_insn[key] = [new_insn[key]]
                elif re.match(r"mem(/([a-z]|[A-Z]))*:[A-Z]{2}",
                              instruction[5][2][0]):
                    new_insn['sources'] = [
                        {
                            "value": RTLprocesser._get_register(
                                instruction[5][2][1], new_insn),
                            "offset": 0,
                        }
                    ]
                else:
                    new_insn['expr']['mem'] = instruction[5][2][1:]
            elif re.match(r"([a-z]|[A-Z])+:[A-Z]{2}",
                          instruction[5][2][0]):

                if type(instruction[5][2][1][0]) is list:
                    new_insn['unparsed'] = instruction[5][2][1]
                else:
                    match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                     instruction[5][2][1][0])
                    if match:
                        string = "r{number}:{type}".format(
                            number = instruction[5][2][1][1],
                            type = match.group('type'))

                        key = 'sources' if 'target' in new_insn else 'target'

                        if instruction[5][2][2][0] == 'const_int':
                            new_insn[key] = [
                                {
                                    "value": string,
                                    "offset": 0
                                },
                                {
                                    "value": instruction[5][2][2][1],
                                    "offset": 0
                                }
                            ]
                        elif re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                                      instruction[5][2][2][0]):

                            string2 = "r{number}:{type}".format(
                                number = instruction[5][2][2][1],
                                type = match.group('type'))

                            new_insn[key] = [
                                {
                                    "value": string,
                                    "offset": 0
                                },
                                {
                                    "value": string2,
                                    "offset": 0
                                },
                            ]
            elif "zero_extend" in instruction[5][2][0]:
                new_insn['sources'] = [
                    {
                        "value": "zero_extend([" + RTLprocesser._get_register(
                            instruction[5][2][1][1], new_insn) + "])",
                        "offset": 0
                    }
                ]

        match = re.match(r"^reg(/\w)*:(?P<type>[A-Z]I)",
                         instruction[5][-1][0])
        if match:
            string = "r{number}:{type}".format(
                number = instruction[5][-1][1],
                type = match.group('type'))

            if "sources" in new_insn:
                new_insn["sources"] += [{
                    "value": string,
                    "offset": 0
                }]
            else:
                new_insn["sources"] = [{
                    "value": string,
                    "offset": 0
                }]
        elif "subreg" in instruction[5][-1][0]:
            new_insn["sources"] = [
                {
                    "value": RTLprocesser._get_register(
                        instruction[5][-1][1], new_insn) + "#" +
                             instruction[5][-1][-1],
                    "offset": 0
                }
            ]

        if instruction[-1] != ['nil']:
            new_insn["expr_list"] = instruction[-1]

    @staticmethod
    def _process_parallel(instruction, new_insn):
        new_insn["target"] = {
            "value": RTLprocesser._get_register(instruction[0][1], new_insn),
            "offset": 0,
        }

    @staticmethod
    def _process_call_insn(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        if instruction[5][0] == "parallel":
            RTLprocesser._process_parallel(instruction[5][1], new_insn)

        new_insn["sources"] = [
            {
                "value": "symbol_ref",
                "offset": 0
            }
        ]

    @staticmethod
    def _process_jump_insn(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        new_insn.update({
            "target": {
                "value": instruction[5][1][0],
                "offset": 0
            },
            "sources": [{"value": instruction[-1], "offset": 0}],
            "label_ref": int(instruction[-1]),
        })

        if instruction[5][2][0] == "if_then_else":
            new_insn['sources'][0][
                'value'] = RTLprocesser._process_if_then_else(instruction[5][2],
                                                              new_insn)

    @staticmethod
    def _process_if_then_else(instruction, new_insn):
        operators = {
            "ge": ">=",
            "gt": ">",
            "le": "<=",
            "lt": "<",
            "eq": "==",
            "ne": "!="
        }

        # Get condition
        result = ["{", "(",
                  RTLprocesser._get_register(instruction[1][1], new_insn),
                  operators.get(instruction[1][0], ""),
                  RTLprocesser._get_register(instruction[1][2], new_insn), ")?"]

        # Get Then and Else
        if instruction[2][0] == "label_ref":
            result.append("L{num}:".format(num = instruction[2][1]))
            result.append(instruction[3][0])
        else:
            result.append(instruction[3][0])
            result.append("L{num}:".format(num = instruction[2][1]))

        result.append("}")

        return ''.join(result)

    @staticmethod
    def _get_register(instruction, new_insn):
        new_insn["target"] = {
            "value": RTLprocesser._get_register(instruction[0][1], new_insn),
            "offset": 0,
        }
        if re.match(r"^reg(/\w)*:[A-Z]I", instruction[0]):
            return instruction[1]
        elif instruction[0] == "reg:CC":
            new_insn['type'] = 'cmp_insn'
            return "cc"
        elif instruction[0] == "const_int":
            return instruction[1]

        return ""

    @staticmethod
    def _process_code_label(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

    @staticmethod
    def _process_other(instruction, new_insn):
        pass
