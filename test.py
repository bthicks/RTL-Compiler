def parse_helper(gen, token_list):
    word = next(gen)

    if word[0] == "(":
        parse_helper(gen, [])
    elif word[-1] == ")":
        pass
    else:
        token_list.append(word)


def parse(file_name):
    with open(file_name, 'r') as infile:
        word_generator = (word for line in infile for word in line.split())
        result = parse_helper(word_generator, [])

    print(result)


if __name__ == '__main__':
    # parse("helloworld/hello_world.c.234r.expand")
    print("(hello ())".split(" |)"))
