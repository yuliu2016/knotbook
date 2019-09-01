test = """

# A test

a = 5
a += 17 + 1

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
STR = "STR"

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
BIT_XOR = "BIT_XOR"
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
    "*": TIMES_ARGS,  # times and def(*args)
    "/": DIV,
    "%": MODULUS,
    "|": BIT_OR,
    "&": BIT_AND,
    "~": BIT_NOT,
    "^": BIT_XOR,
    "<": OPEN_ANGLE_LT,  # typing and comparison
    ">": CLOSE_ANGLE_MT,  # typing and comparison
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
BIT_XOR_ASSIGN = "BIT_XOR_ASSIGN"

double_ops = {
    "//": FDIV,
    "**": EXP_KWARGS,  # exponents and def(**kwargs)/ {**k, **v} etc
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
    "^=": BIT_XOR_ASSIGN
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

#
# Constants
#

UNDERSCORE_CHAR = "_"
SINGLE_COMMENT_CHAR = "#"
STR_CHAR = "\""
NEWLINE_CHAR_1 = "\n"
NEWLINE_CHAR_2 = "\r"


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
        return code[i:i + n]

    # pop the space when we know that we can to simplify opcode
    def pop_space(n):
        last_token = tokens[-n]
        if last_token == NEWLINE or last_token == SPACE:
            tokens.pop(len(tokens) - n)

    # checks if it is a symbol name
    def is_symbol(test_ch: str):
        return test_ch.isalnum() or test_ch == UNDERSCORE_CHAR

    def is_newline(test_ch: str):
        return test_ch == NEWLINE_CHAR_1 or test_ch == NEWLINE_CHAR_2

    i = 0
    size = len(code)
    tokens = []
    last_is_op = False

    while i < size:
        ch = code[i]
        in_comment = ch == "#"
        is_op = False
        if in_comment or ch.isspace():

            # comments and spaces
            j = i + 1
            newline = is_newline(ch)

            while j < size:
                peek_ch = code[j]
                if not (in_comment
                        or peek_ch.isspace()
                        or peek_ch == SINGLE_COMMENT_CHAR):
                    break
                if is_newline(peek_ch):
                    in_comment = False
                    newline = True
                if peek_ch == SINGLE_COMMENT_CHAR:
                    in_comment = True
                j += 1
            i = j

            if newline:
                tokens.append(NEWLINE)
            else:
                tokens.append(SPACE)

        elif ch.isnumeric():

            # check for numbers

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

        elif canPeek(3) and peek(3) in triple_ops.keys():

            # two-char operators
            is_op = True
            tokens.append((OP, triple_ops[peek(3)]))
            i += 3

        elif canPeek(2) and peek(2) in double_ops.keys():

            # two-char operators
            is_op = True
            tokens.append((OP, double_ops[peek(2)]))
            i += 2

        elif ch in single_ops.keys():
            # one-char operators
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

    # Discard spacing at the end of the sequence
    pop_space(n=1)

    # Discard spacing at the beginning of the sequence
    pop_space(n=len(tokens))
    return tokens


def print_tokens(tokens):
    for token in tokens:
        if type(token) is tuple:
            print("{:>8}:  {}".format(*token))
        else:
            print("{:>8}".format(token))


if __name__ == '__main__':
    print_tokens(tokenize(test))
