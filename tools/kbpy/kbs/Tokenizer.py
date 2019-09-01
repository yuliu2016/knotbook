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
        elif is_symbol(ch):  # symbols
            j = i + 1
            while j < size and is_symbol(code[j]):
                j += 1
            tokens.append(SYMBOL)
            tokens.append(code[i:j])
            i = j
        elif canPeek(2) and  peek(2) in double_ops.keys():  # two-char operators
            tokens.append(double_ops[peek(2)])
            i += 1
        elif ch in single_ops.keys():  # one-char operators
            tokens.append(single_ops[ch])
            i += 1
        else:
            raise Exception()
    if tokens[-1] == NEWLINE or tokens[-1] == SPACE:
        tokens.pop()
    return tokens


if __name__ == '__main__':
    print(tokenize(test))
