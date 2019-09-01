test = """

# A test

whaaat = 1 + 1

"""


def is_symbol(ch: str):
    return ch.isalnum() or ch == "_"

SYMBOL = "SYMBOL"
NEWLINE = "NEWLINE"
SPACE = "SPACE"
PLUS = "PLUS"
EQUALS = "EQUALS"

single_ops = {
    "+": PLUS,
    "=": EQUALS
}

def tokenize(code: str):
    i = 0
    size = len(code)
    tokens = []

    while i < size:
        ch = code[i]
        if ch == "#": # comments
            j = i + 1
            while j < size and code[j] != "\n":
                j += 1
            i = j
            continue
        if ch.isspace(): # spaces
            j = i + 1
            newline = False
            while j < size and code[j].isspace():
                if code[j] == "\n" or code[j] == "\r":
                    newline = True
                j += 1
            i = j
            if newline:
                tokens.append(NEWLINE)
            else:
                tokens.append(SPACE)
            continue
        if is_symbol(ch): # symbols
            j = i + 1
            while j < size and is_symbol(code[j]):
                j += 1
            tokens.append(SYMBOL)
            tokens.append(code[i:j])
            i = j
            continue
        if ch in single_ops.keys():
            tokens.append(single_ops[ch])
            i += 1
    return tokens


if __name__ == '__main__':
    print(tokenize(test))
