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
OPEN_ROUND = "OPEN_ROUND"
CLOSE_ROUND = "CLOSE_ROUND"
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
    "<": OPEN_ANGLE_LT,  # typing and more-than comparison
    ">": CLOSE_ANGLE_MT,  # typing and more-than comparison
    "(": OPEN_ROUND,
    ")": CLOSE_ROUND,
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


def is_symbol(test_ch: str):
    # checks if it is a symbol name
    return test_ch.isalnum() or test_ch == UNDERSCORE_CHAR


def is_newline(test_ch: str):
    # checks against newline characters
    return test_ch == NEWLINE_CHAR_1 or test_ch == NEWLINE_CHAR_2


class _Tokenizer:

    def __init__(self, code: str):
        self.code = code

        # the caret index
        self.i = 0

        # the total size of the code
        self.size = len(code)

        # the list of generated tokens
        self.tokens = []

        # used to remove spacing when the last token is an operation
        self.last_token_is_operator = False

    def canPeek(self, n: int):
        # Decides if the string is long enough to peek
        return self.i + n <= self.size

    def peek(self, n: int):
        # Peek the code
        return self.code[self.i:self.i + n]

    def peekOrNone(self, n: int):
        # peek the code or return none
        if self.canPeek(n):
            return self.peek(n)
        return None

    def pop_space(self, n: int):
        """
         pop the space when we know that opcode is simplified
         """
        if self.tokens[-n] == SPACE:
            self.tokens.pop(len(self.tokens) - n)

    def pop_newline(self, n: int):
        """
         pop the newline when we know that opcode is simplified
         """
        if self.tokens[-n] == NEWLINE:
            self.tokens.pop(len(self.tokens) - n)

    def add_token(self, tok):
        self.tokens.append(tok)

    def reset_state(self):
        self.tokens.clear()
        self.i = 0
        self.last_token_is_operator = False
        self.size = len(self.code)

    def discard_spaces(self):

        # Discard spacing at the end of the sequence
        self.pop_space(n=1)
        self.pop_newline(n=1)

        # Discard spacing at the beginning of the sequence
        self.pop_space(n=len(self.tokens))
        self.pop_newline(n=len(self.tokens))

    def discard_operator_spaces(self, is_operator: bool):
        # check whether spaces can be popped off
        if self.last_token_is_operator:
            self.pop_space(n=1)
        if is_operator:
            self.pop_space(n=2)
            self.last_token_is_operator = True
        else:
            self.last_token_is_operator = False

    def append_spaces(self, ch):
        # comments and spaces

        j = self.i + 1
        in_comment = ch == SINGLE_COMMENT_CHAR

        # make sure that newline is added if it's the first char
        newline = is_newline(ch)

        while j < self.size:
            peek_ch = self.code[j]
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
        self.i = j

        if newline:
            self.add_token(NEWLINE)
        else:
            self.add_token(SPACE)

    def append_numeric_literal(self):

        # check for numbers

        j = self.i + 1
        while j < self.size and self.code[j].isnumeric():
            j += 1
        self.add_token((INT, int(self.code[self.i:j])))
        self.i = j

    def append_symbol(self):

        # check for symbols
        j = self.i + 1
        while j < self.size and is_symbol(self.code[j]):
            j += 1
        self.add_token((SYMBOL, self.code[self.i:j]))
        self.i = j

    def tokenize(self):
        self.reset_state()
        while self.i < self.size:
            ch = self.code[self.i]
            is_operator = False
            if ch == SINGLE_COMMENT_CHAR or ch.isspace():
                self.append_spaces(ch)
            elif ch.isnumeric():
                self.append_numeric_literal()
            elif is_symbol(ch):
                self.append_symbol()
            elif self.canPeek(3) and self.peek(3) in triple_ops.keys():

                # two-char operators
                is_operator = True
                self.add_token((OP, triple_ops[self.peek(3)]))
                self.i += 3

            elif self.canPeek(2) and self.peek(2) in double_ops.keys():

                # two-char operators
                is_operator = True
                self.add_token((OP, double_ops[self.peek(2)]))
                self.i += 2

            elif ch in single_ops.keys():

                # one-char operators
                is_operator = True
                self.add_token((OP, single_ops[ch]))
                self.i += 1

            else:
                raise Exception(f"{ch} is not recognized")

            self.discard_operator_spaces(is_operator)

        self.discard_spaces()


def tokenize(code: str):
    """
    Tokenizes a piece of code
    No regular expressions; just char-by-char
    """

    tk = _Tokenizer(code)
    tk.tokenize()

    return tk.tokens


def print_tokens(tokens):
    for token in tokens:
        if type(token) is tuple:
            print("{:>8}:  {}".format(*token))
        else:
            print("{:>8}".format(token))


test = """

# A test
# Two tests

dependencies {
    implementation()
}
"""

if __name__ == '__main__':
    print_tokens(tokenize(test))
