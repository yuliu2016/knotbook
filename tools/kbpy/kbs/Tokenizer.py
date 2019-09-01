test = """

# A test

whaaat = 1 + 1

"""


def is_symbol(ch: str):
    return ch.isalnum() or ch == "_"


SYMBOL = "SYMBOL"
NEWLINE = "NEWLINE"
SPACE = "SPACE"
INT = "INT"
FLOAT = "FLOAT"

PLUS = "PLUS"
EQUALS = "EQUALS"

single_ops = {
    "+": PLUS,
    "=": EQUALS
}

double_ops = {

}

def tokenize(code: str):
    i = 0
    size = len(code)
    tokens = []

    def canPeek(n: int):
        return i + n <= size

    def peek(n: int):
        return code[i:i + n - 1]

    while i < size:
        ch = code[i]
        in_comment = ch == "#"
        if in_comment or ch.isspace():  # comments spaces
            j = i + 1
            newline = False
            while j < size:
                ch_j = code[j]
                if not (in_comment or ch_j.isspace() or ch_j == "#"):
                    break
                if ch_j == "\n" or ch_j == "\r":
                    in_comment = False
                    newline = True
                if ch_j == "#":
                    in_comment = True
                j += 1
            i = j
            if len(tokens) == 0:
                continue
            if newline:
                tokens.append(NEWLINE)
            else:
                tokens.append(SPACE)
        elif ch.isnumeric():
            j = i + 1
            while j < size and code[j].isnumeric():
                j += 1
            tokens.append(INT)
            tokens.append(int(code[i:j]))
            i = j
            pass
        elif is_symbol(ch): # symbols
            j = i + 1
            while j < size and is_symbol(code[j]):
                j += 1
            tokens.append(SYMBOL)
            tokens.append(code[i:j])
            i = j
        elif ch in single_ops.keys():
            tokens.append(single_ops[ch])
            i += 1
            continue
        elif canPeek(2):
            ch2 = peek(2)
            if ch in double_ops.keys():
                tokens.append(double_ops[ch2])
            continue
        else:
            raise Exception()
    if tokens[-1] == NEWLINE or tokens[-1] == SPACE:
        tokens.pop()
    return tokens


if __name__ == '__main__':
    print(tokenize(test))
