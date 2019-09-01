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
        self.last_is_op = False

    def canPeek(self, n: int):
        # Decides if the string is long enough to peek
        return self.i + n <= self.size

    def peek(self, n: int):
        # Peek the code
        return self.code[self.i:self.i + n]

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

    def tokenize(self):
        self.tokens.clear()
        self.i = 0
        self.last_is_op = False
        self.size = len(self.code)
        while self.i < self.size:
            ch = self.code[self.i]
            in_comment = ch == "#"
            is_op = False
            if in_comment or ch.isspace():

                # comments and spaces
                j = self.i + 1
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

            elif ch.isnumeric():

                # check for numbers

                j = self.i + 1
                while j < self.size and self.code[j].isnumeric():
                    j += 1
                self.add_token((INT, int(self.code[self.i:j])))
                self.i = j
                pass

            elif is_symbol(ch):

                # check for symbols
                j = self.i + 1
                while j < self.size and is_symbol(self.code[j]):
                    j += 1
                self.add_token((SYMBOL, self.code[self.i:j]))
                self.i = j

            elif self.canPeek(3) and self.peek(3) in triple_ops.keys():

                # two-char operators
                is_op = True
                self.add_token((OP, triple_ops[self.peek(3)]))
                self.i += 3

            elif self.canPeek(2) and self.peek(2) in double_ops.keys():

                # two-char operators
                is_op = True
                self.add_token((OP, double_ops[self.peek(2)]))
                self.i += 2

            elif ch in single_ops.keys():
                # one-char operators
                is_op = True
                self.add_token((OP, single_ops[ch]))
                self.i += 1
            else:
                raise Exception()

            # check whether spaces can be popped off
            if self.last_is_op:
                self.pop_space(n=1)
            if is_op:
                self.pop_space(n=2)
                self.last_is_op = True
            else:
                self.last_is_op = False

            # Discard spacing at the end of the sequence
        self.pop_space(n=1)
        self.pop_newline(n=1)

        # Discard spacing at the beginning of the sequence
        self.pop_space(n=len(self.tokens))
        self.pop_newline(n=len(self.tokens))


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


if __name__ == '__main__':

    test = """

    # A test

    a = 5
    a += 17 + 1

    """

    print_tokens(tokenize(test))
