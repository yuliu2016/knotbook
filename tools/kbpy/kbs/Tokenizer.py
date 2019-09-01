test = """

# A test

whaaat = 1 + 1

"""

SYMBOL = "SYMBOL"
OP = "OP"
NEWLINE = "NEWLINE"
SPACE = "SPACE"
INT = "INT"
FLOAT = "FLOAT"

DOT = "DOT"
ASSIGN = "ASSIGN"
PLUS = "PLUS"
MINUS = "MINUS"
TIMES = "TIMES"
DIV = "DIV"
MODULUS = "MODULUS"
BIT_OR = "BIT_OR"
BIT_AND = "BIT_AND"
BIT_NOT = "BIT_NOT"
LESS_THAN = "LESS_THAN"
MORE_THAN = "MORE_THAN"

single_ops = {
    ".": DOT,
    "=": ASSIGN,
    "+": PLUS,
    "-": MINUS,
    "*": TIMES,
    "/": DIV,
    "%": MODULUS,
    "|": BIT_OR,
    "&": BIT_AND,
    "~": BIT_NOT,
    "<": LESS_THAN,
    ">": MORE_THAN,
}

FDIV = "FDIV"
EXP = "EXP"
SHL = "SHL"
SHR = "SHR"
EQUAL = "EQUAL"
NOT_EQUAL = "NOT_EQUAL"
LESS_EQUAL = "LESS_EQUAL"
MORE_EQUAL = "MORE_EQUAL"
RANGE = "RANGE"

double_ops = {
    "//": FDIV,
    "**": EXP,
    "<<": SHL,
    ">>": SHR,
    "==": EQUAL,
    "!=": NOT_EQUAL,
    "<=": LESS_EQUAL,
    ">=": MORE_EQUAL,
    "..": RANGE
}


def tokenize(code: str):
    def canPeek(n: int):
        return i + n <= size

    def peek(n: int):
        return code[i:i + n - 1]

    # pop the space when we know that we can to simplify opcode
    def pop_space(n):
        last_token = tokens[-n]
        if last_token == NEWLINE or last_token == SPACE:
            tokens.pop(len(tokens) - n)

    def is_symbol(test_ch: str):
        return test_ch.isalnum() or test_ch == "_"

    i = 0
    size = len(code)
    tokens = []
    last_is_op = False

    while i < size:
        ch = code[i]
        in_comment = ch == "#"
        is_op = False
        if in_comment or ch.isspace():  # comments and spaces
            j = i + 1
            newline = False
            while j < size:
                peek_ch = code[j]
                if not (in_comment or peek_ch.isspace() or peek_ch == "#"):
                    break
                if peek_ch == "\n" or peek_ch == "\r":
                    in_comment = False
                    newline = True
                if peek_ch == "#":
                    in_comment = True
                j += 1
            i = j
            if len(tokens) == 0:
                continue # safe to abandon the space at the top
            if newline:
                tokens.append(NEWLINE)
            else:
                tokens.append(SPACE)
        elif ch.isnumeric():
            j = i + 1
            while j < size and code[j].isnumeric():
                j += 1
            tokens.append((INT, int(code[i:j])))
            i = j
            pass
        elif is_symbol(ch):  # symbols
            j = i + 1
            while j < size and is_symbol(code[j]):
                j += 1
            tokens.append((SYMBOL, code[i:j]))
            i = j
        elif canPeek(2) and peek(2) in double_ops.keys():  # two-char operators
            is_op = True
            tokens.append((OP, double_ops[peek(2)]))
            i += 1
        elif ch in single_ops.keys():  # one-char operators
            is_op = True
            tokens.append((OP, single_ops[ch]))
            i += 1
        else:
            raise Exception()
        if last_is_op:
            pop_space(n=1)
        if is_op:
            pop_space(n=2)
            last_is_op = True
        else:
            last_is_op = False
    pop_space(n=1)
    return tokens


def print_tokens(tokens):
    for token in tokens:
        if type(token) is tuple:
            print("{:>8}:  {}".format(*token))
        else:
            print("{:>8}".format(token))


if __name__ == '__main__':
    print_tokens(tokenize(test))
