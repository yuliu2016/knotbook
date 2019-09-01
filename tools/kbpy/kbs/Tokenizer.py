test = """

# A test

whaaat = 1 + 1

"""

#
# OPcode types
#

SYMBOL = "SYMBOL"
OP = "OP"
NEWLINE = "NEWLINE"
SPACE = "SPACE"
INT = "INT"
FLOAT = "FLOAT"

#
# Single-char operations
#

DOT = "DOT"
COMMA = "COMMA"
ASSIGN = "ASSIGN"
PLUS = "PLUS"
MINUS = "MINUS"
TIMES_ARGS = "TIMES_ARGS"
DIV = "DIV"
MODULUS = "MODULUS"
BIT_OR = "BIT_OR"
BIT_AND = "BIT_AND"
BIT_NOT = "BIT_NOT"
OPEN_ANGLE_LT = "OPEN_ANGLE_LT"
CLOSE_ANGLE_MT = "CLOSE_ANGLE_MT"
OPEN_SQUARE = "OPEN_SQUARE"
CLOSE_SQUARE = "CLOSE_SQUARE"
OPEN_CURLY = "OPEN_CURLY"
CLOSE_CURLY = "CLOSE_CURLY"

single_ops = {
    ".": DOT,
    ",": COMMA,
    "=": ASSIGN,
    "+": PLUS,
    "-": MINUS,
    "*": TIMES_ARGS, # times and def(*args)
    "/": DIV,
    "%": MODULUS,
    "|": BIT_OR,
    "&": BIT_AND,
    "~": BIT_NOT,
    "<": OPEN_ANGLE_LT, # typing and comparison
    ">": CLOSE_ANGLE_MT, # typing and comparison
    "[": OPEN_SQUARE,
    "]": CLOSE_SQUARE,
    "{": OPEN_CURLY,
    "}": CLOSE_CURLY
}

#
# Double-char operations ("//=" and "**=" operations are ignored)
#

FDIV = "FDIV"
EXP_KWARGS = "EXP_KWARGS"
SHL = "SHL"
SHR = "SHR"
EQUAL = "EQUAL"
NOT_EQUAL = "NOT_EQUAL"
LESS_EQUAL = "LESS_EQUAL"
MORE_EQUAL = "MORE_EQUAL"
RANGE = "RANGE"
PLUS_ASSIGN = "PLUS_ASSIGN"
MINUS_ASSIGN = "MINUS_ASSIGN"
TIMES_ASSIGN = "TIMES_ASSGIN"
DIV_ASSIGN = "DIV_ASSIGN"
MODULUS_ASSIGN = "MODULUS_ASSIGN"
BIT_OR_ASSIGN = "BIT_OR_ASSIGN"
BIT_AND_ASSIGN = "BIT_AND_ASSIGN"
BIT_NOT_ASSIGN = "BIT_NOT_ASSIGN"

double_ops = {
    "//": FDIV,
    "**": EXP_KWARGS, # exponents and def(**kwargs)/ {**k, **v} etc
    "<<": SHL,
    ">>": SHR,
    "==": EQUAL,
    "!=": NOT_EQUAL,
    "<=": LESS_EQUAL,
    ">=": MORE_EQUAL,
    "..": RANGE,
    "+=": PLUS_ASSIGN,
    "-=": MINUS_ASSIGN,
    "*=": TIMES_ASSIGN,
    "/=": DIV_ASSIGN,
    "%=": MODULUS_ASSIGN,
    "|=": BIT_OR_ASSIGN,
    "&=": BIT_AND_ASSIGN,
    "~=": BIT_NOT_ASSIGN
}

#
# Triple-char operations
#

FDIV_ASSIGN = "FDIV_ASSIGN"
EXP_ASSIGN = "EXP_ASSIGN"

triple_ops = {
    "//=": FDIV_ASSIGN,
    "**=": EXP_ASSIGN
}


def tokenize(code: str):
    """
    Tokenizes a piece of code
    No regular expressions; just char-by-char
    :param code: the code to parse
    :return:
    """

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
                continue  # safe to abandon the space at the top
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
        elif is_symbol(ch):
            # check for symbols
            j = i + 1
            while j < size and is_symbol(code[j]):
                j += 1
            tokens.append((SYMBOL, code[i:j]))
            i = j
        elif canPeek(3) and peek(3) in triple_ops.keys():  # two-char operators
            is_op = True
            tokens.append((OP, triple_ops[peek(3)]))
            i += 3
        elif canPeek(2) and peek(2) in double_ops.keys():  # two-char operators
            is_op = True
            tokens.append((OP, double_ops[peek(2)]))
            i += 2
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
