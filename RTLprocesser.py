###########################################
# Authors:    Brian Hicks & Eitan Simler  #
# Assignment: Milestone 1                 #
# Class:      CPE 431                     #
###########################################
import re
import sys


def dict_get(key, a_dict):
    for k in a_dict.keys():
        if k in key:
            return a_dict[k]
    return None


class RTLprocesser:
    @staticmethod
    def _process_mem(instruction, new_insn):
        if "plus" in instruction[1][0]:
            RTLprocesser._process_plus(instruction[1], new_insn, mem=True)
        else:
            print("MEM", instruction)

    @staticmethod
    def _process_plus(instruction, new_insn, mem=False):
        print("PLUS", new_insn["uid"], mem, instruction)
        if mem:
            RTLprocesser._get_register(instruction, new_insn, mem)
        else:
            RTLprocesser._get_register(instruction[1], new_insn, mem)
            RTLprocesser._get_register(instruction[2], new_insn, mem)

    @staticmethod
    def _process_set(instruction, new_insn):
        """An RTL instruction of form: (set lval x).

        Represents the action of storing the value of x into the place
        represented by lval. lval must be an expression representing a place
        that can be stored in: reg (or subreg, strict_low_part or zero_extract),
        mem, pc, parallel, or cc0.

        If lval is (cc0), it has no machine mode, and x may be either a compare
        expression or a value that may have any mode.

        If lval is a parallel, it is used to represent the case of a function
        returning a structure in multiple registers. Each element of the
        parallel is an expr_list whose first operand is a reg and whose second
        operand is a const_int representing the offset (in bytes) into the
        structure at which the data in that register corresponds. The first
        element may be null to indicate that the structure is also passed partly
        in memory.

        If lval is (pc), we have a jump instruction, and the possibilities for
        x are very limited. It may be a label_ref expression
        (unconditional jump). It may be an if_then_else (conditional jump),
        in which case either the second or the third operand must be (pc)
        (for the case which does not jump) and the other of the two must be a
        label_ref (for the case which does jump). x may also be a mem or
        (plus:SI (pc) y), where y may be a reg or a mem; these unusual patterns
        are used to represent jumps through branch tables.
        """
        print("SET", new_insn["uid"], instruction, file = sys.stderr)

        functions = {
            "reg": RTLprocesser._get_register,
            "const_int": RTLprocesser._get_register,
            "plus": RTLprocesser._process_plus,
            "mem": RTLprocesser._process_mem,
        }

        result = dict_get(instruction[0][0], functions)
        if result is not None:
            result(instruction[0], new_insn)
        else:
            print(instruction[0][0])

        result = dict_get(instruction[1][0], functions)
        if result is not None:
            result(instruction[1], new_insn)
        else:
            print(instruction[1][0])

    @staticmethod
    def _process_return(*args):
        """An RTL instruction representing a return.

        As the sole expression in a pattern, represents a return from the
        current function, on machines where this can be done with one
        instruction, such as VAXen. On machines where a multi-instruction
        “epilogue” must be executed in order to return from the function,
        returning is done by jumping to a label which precedes the epilogue,
        and the return expression code is never used.

        Inside an if_then_else expression, represents the value to be placed in
        pc to return to the caller.

        Note that an insn pattern of (return) is logically equivalent to
        (set (pc) (return)), but the latter form is never used.
        """
        pass

    @staticmethod
    def _process_call(instruction, new_insn):
        pass

    @staticmethod
    def _process_use(instruction, new_insn):
        pass

    @staticmethod
    def _process_clobber(instruction, new_insn):
        pass

    @staticmethod
    def _process_simple_return(*args):
        """An RTL instruction representing a return from a function.

        Like (return), but truly represents only a function return, while
        (return) may represent an insn that also performs other functions of the
        function epilogue. Like (return), this may also occur in conditional
        jumps.
        """
        pass

    @staticmethod
    def _process_asm_input(instruction, new_insn):
        pass

    @staticmethod
    def _process_asm_output(instruction, new_insn):
        pass

    @staticmethod
    def _process_addr_vec(instruction, new_insn):
        pass

    @staticmethod
    def _process_addr_diff_vec(instruction, new_insn):
        pass

    @staticmethod
    def _process_parallel(instruction, new_insn):
        new_insn["target"] = {
            "value": RTLprocesser._get_register(instruction[0][1], new_insn),
            "offset": 0,
        }

    @staticmethod
    def _process_trap_if(instruction, new_insn):
        pass

    @staticmethod
    def _process_unspec(instruction, new_insn):
        pass

    @staticmethod
    def _process_unspec_volatile(instruction, new_insn):
        pass

    @staticmethod
    def _process_cond_exec(instruction, new_insn):
        pass

    @staticmethod
    def _process_sequence(instruction, new_insn):
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
            "insn": RTLprocesser._process_insn2,
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
        if instruction[-1] != "NOTE_INSN_DELETED":
            RTLprocesser._preprocess(instruction, new_insn)
            new_insn["note_type"] = instruction[-1]

    @staticmethod
    def _process_barrier(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        new_insn["block"] = -1

    @staticmethod
    def _process_insn2(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

        codes = {
            "addr_diff_vec": RTLprocesser._process_addr_diff_vec,
            "addr_vec": RTLprocesser._process_addr_vec,
            "asm_input": RTLprocesser._process_asm_input,
            "asm_output": RTLprocesser._process_asm_output,
            "call": RTLprocesser._process_call,
            "clobber": RTLprocesser._process_clobber,
            "cond_exec": RTLprocesser._process_cond_exec,
            "parallel": RTLprocesser._process_parallel,
            "return": RTLprocesser._process_return,
            "sequence": RTLprocesser._process_sequence,
            "set": RTLprocesser._process_set,
            "simple_return": RTLprocesser._process_simple_return,
            "trap_if": RTLprocesser._process_trap_if,
            "unspec": RTLprocesser._process_unspec,
            "unspec_volatile": RTLprocesser._process_unspec_volatile,
            "use": RTLprocesser._process_use,
        }

        method = codes.get(instruction[5][0])

        if method is not None:
            method(instruction[5][1:], new_insn)
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
    def _set_register(new_insn, reg_type, value, offset):
        info = {
            'type': reg_type,
            'value': value,
            'offset': offset,
        }

        if 'target' not in new_insn:
            new_insn['target'] = info
        elif 'sources' not in new_insn:
            new_insn['sources'] = [info]
        elif 'sources' in new_insn:
            new_insn['sources'].append(info)

    @staticmethod
    def _get_register(instruction, new_insn, mem=False):
        print("GET_REGISTER", new_insn["uid"], instruction)
        if mem:
            RTLprocesser._set_register(new_insn, 'reg', int(instruction[1][1]), int(instruction[2][1]))
        elif re.match(r"^reg(/\w)*:[A-Z]I", instruction[0]):
            RTLprocesser._set_register(new_insn, 'reg', int(instruction[1]), 0)
            return instruction[1]
        elif instruction[0] == "reg:CC":
            new_insn['type'] = 'cmp_insn'
            return "cc"
        elif instruction[0] == "const_int":
            RTLprocesser._set_register(new_insn, 'const', int(instruction[1]), 0)
            return instruction[1]

        return ""

    @staticmethod
    def _process_code_label(instruction, new_insn):
        RTLprocesser._preprocess(instruction, new_insn)

    @staticmethod
    def _process_other(instruction, new_insn):
        pass
