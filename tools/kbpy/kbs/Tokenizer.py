from typing import *
from enum import Enum
from io import StringIO


# TODO
#  add docstrings
#  add multi-line strings
#  add line number persistence
#  add floats and exponents
#  add bin and hex

#
# Token types
#

class TokenType(Enum):
    NEWLINE = 0
    SPACE = 1
    KEYWORD = 2
    SYMBOL = 3
    OP = 4
    INT = 5
    STR = 6
    FLOAT = 7
    DOCSTR = 8


#
# Single-char operations
#

# most common

DOT = "DOT"
COMMA = "COMMA"
ASSIGN = "ASSIGN"
COLON = "COLON"

# second most common - brackets

OPEN_ROUND = "OPEN_ROUND"
CLOSE_ROUND = "CLOSE_ROUND"

OPEN_CURLY = "OPEN_CURLY"
CLOSE_CURLY = "CLOSE_CURLY"

OPEN_SQUARE = "OPEN_SQUARE"
CLOSE_SQUARE = "CLOSE_SQUARE"

OPEN_ANGLE_LT = "OPEN_ANGLE_LT"
CLOSE_ANGLE_MT = "CLOSE_ANGLE_MT"

# math

PLUS = "PLUS"
MINUS = "MINUS"
TIMES_ARGS = "TIMES_ARGS"
DIV = "DIV"
MODULUS = "MODULUS"

# bitwise ops

BIT_OR = "BIT_OR"
BIT_AND = "BIT_AND"
BIT_NOT = "BIT_NOT"
BIT_XOR = "BIT_XOR"

single_ops = {
    ".": DOT,
    ",": COMMA,
    "=": ASSIGN,
    ":": COLON,

    "(": OPEN_ROUND,
    ")": CLOSE_ROUND,
    "{": OPEN_CURLY,
    "}": CLOSE_CURLY,
    "[": OPEN_SQUARE,
    "]": CLOSE_SQUARE,
    "<": OPEN_ANGLE_LT,  # typing and more-than comparison
    ">": CLOSE_ANGLE_MT,  # typing and more-than comparison

    "+": PLUS,
    "-": MINUS,
    "*": TIMES_ARGS,  # times and def(*args)
    "/": DIV,
    "%": MODULUS,

    "|": BIT_OR,
    "&": BIT_AND,
    "~": BIT_NOT,
    "^": BIT_XOR
}

#
# Double-char operations ("//=" and "**=" operations are ignored)
#

EQUAL = "EQUAL"
NOT_EQUAL = "NOT_EQUAL"
LESS_EQUAL = "LESS_EQUAL"
MORE_EQUAL = "MORE_EQUAL"

ARROW = "ARROW"
RANGE = "RANGE"

FDIV = "FDIV"
EXP_KWARGS = "EXP_KWARGS"

PLUS_ASSIGN = "PLUS_ASSIGN"
MINUS_ASSIGN = "MINUS_ASSIGN"
TIMES_ASSIGN = "TIMES_ASSGIN"
DIV_ASSIGN = "DIV_ASSIGN"
MODULUS_ASSIGN = "MODULUS_ASSIGN"
BIT_OR_ASSIGN = "BIT_OR_ASSIGN"
BIT_AND_ASSIGN = "BIT_AND_ASSIGN"
BIT_XOR_ASSIGN = "BIT_XOR_ASSIGN"

SHIFT_LEFT = "SHIFT_LEFT"
SHIFT_RIGHT = "SHIFT_RIGHT"

double_ops = {
    "==": EQUAL,
    "!=": NOT_EQUAL,
    "<=": LESS_EQUAL,
    ">=": MORE_EQUAL,

    "->": ARROW,

    "..": RANGE,

    "//": FDIV,
    "**": EXP_KWARGS,  # exponents and def(**kwargs)/ {**k, **v} etc

    "+=": PLUS_ASSIGN,
    "-=": MINUS_ASSIGN,
    "*=": TIMES_ASSIGN,
    "/=": DIV_ASSIGN,
    "%=": MODULUS_ASSIGN,
    "|=": BIT_OR_ASSIGN,
    "&=": BIT_AND_ASSIGN,
    "^=": BIT_XOR_ASSIGN,

    "<<": SHIFT_LEFT,
    ">>": SHIFT_RIGHT
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
# Keywords (subset of symbols, easier to check later)
#
keywords = [
    "return",
    "if",
    "else",
    "when",
    "def",
    "and",
    "or",
    "not",
    "True",
    "False",
    "None",
    "suspend",
    "with",
    "as",
    "from",
    "import",
    "pass",
    "continue",
    "break",
    "try",
    "except",
    "finally",
    "while",
    "in",
    "is",
    "for",
    "interface",  # ?
    "class"  # ?
]

#
# Constants
#

OPEN_MULTILINE_COMMENT = "/*"
OPEN_DOCSTRING = "/**"
CLOSE_MULTILINE_COMMENT = "*/"

SINGLE_COMMENT_CHAR = "#"

UNDERSCORE_CHAR = "_"

STR_CHAR = "\""

NEWLINE_CHAR_1 = "\n"
NEWLINE_CHAR_2 = "\r"


class _Tokenizer:

    @staticmethod
    def is_symbol(test_ch: str):
        # checks if it is a symbol name
        return test_ch.isalnum() or test_ch == UNDERSCORE_CHAR

    @staticmethod
    def is_newline(test_ch: str):
        # checks against newline characters
        return test_ch == NEWLINE_CHAR_1 or test_ch == NEWLINE_CHAR_2

    def __init__(self, code: str):
        self.code = code

        # the caret index
        self.i: int = 0

        # the total size of the code
        self.size: int = len(code)

        # the list of generated tokens
        self.tokens: List[Tuple[TokenType, Optional[str]]] = []

        # used to remove spacing when the last token is an operation
        self.token_is_operator = False
        self.last_token_is_operator = False

        # used to lookup up to the next three characters
        self.p1: str = "\0"
        self.p2: Optional[str] = None
        self.p3: Optional[str] = None

    def canPeek(self, n: int):
        # Decides if the string is long enough to peek
        return self.i + n <= self.size

    def peek(self, n: int):
        # Peek the code
        return self.code[self.i:self.i + n]

    def peek_or_none(self, n: int):
        # peek the code or return none
        if self.canPeek(n):
            return self.peek(n)
        return None

    def pop_space(self, n: int):
        """
         pop the space when we know that opcode is simplified
         """
        if self.tokens[-n][0] == TokenType.SPACE:
            self.tokens.pop(len(self.tokens) - n)

    def pop_newline(self, n: int):
        """
         pop the newline when we know that opcode is simplified
         """
        if self.tokens[-n][0] == TokenType.NEWLINE:
            self.tokens.pop(len(self.tokens) - n)

    def add_token(self, tok):
        self.tokens.append(tok)

    def reset_state(self):
        self.tokens.clear()
        self.i = 0
        self.token_is_operator = False
        self.last_token_is_operator = False
        self.size = len(self.code)

    def discard_code_spaces(self):

        # Discard spacing at the end of the sequence
        self.pop_space(n=1)
        self.pop_newline(n=1)

        # Discard spacing at the beginning of the sequence
        self.pop_space(n=len(self.tokens))
        self.pop_newline(n=len(self.tokens))

    def discard_operator_spaces(self):
        # check whether spaces can be popped off
        if self.last_token_is_operator:
            self.pop_space(n=1)
        if self.token_is_operator:
            self.pop_space(n=2)
            self.last_token_is_operator = True
        else:
            self.last_token_is_operator = False

    def peek_all(self):
        self.p1 = self.code[self.i]
        self.p2 = self.peek_or_none(2)
        self.p3 = self.peek_or_none(3)

    def try_append_spaces(self):

        in_comment = self.p1 == SINGLE_COMMENT_CHAR
        in_multi_line_comment = self.p2 == OPEN_MULTILINE_COMMENT

        if not (in_comment
                or in_multi_line_comment
                or self.p1.isspace()):
            return False # not actually a space to be parsed

        assert not (in_comment and in_multi_line_comment)

        if in_multi_line_comment:
            # start searching after /* to prevent /*/ parsed valid
            j = self.i + 2
        else:
            j = self.i + 1

        # make sure that newline is added if it's the first char
        newline = self.is_newline(self.p1)

        while j < self.size:
            peek1 = self.code[j]
            if j < self.size - 1:
                peek2 = self.code[j: j + 2]
            else:
                peek2 = None

            # check if peek1 is # becuase it could mean the start of
            # a single-line comment
            if not (in_comment
                    or in_multi_line_comment
                    or peek1.isspace()
                    or peek1 == SINGLE_COMMENT_CHAR
                    or peek2 == OPEN_MULTILINE_COMMENT):
                break

            if self.is_newline(peek1):
                in_comment = False
                newline = True
            if peek1 == SINGLE_COMMENT_CHAR:
                in_comment = True
            if peek2 == OPEN_MULTILINE_COMMENT:
                in_multi_line_comment = True
                # since peek2 is not None, this does not break indexing
                j += 1
            elif peek2 == CLOSE_MULTILINE_COMMENT:
                in_multi_line_comment = False
                # since peek2 is not None, this does not break indexing
                j += 1
            j += 1

        self.i = j

        if newline:
            self.add_token((TokenType.NEWLINE, None))
        else:
            self.add_token((TokenType.SPACE, None))

        return True

    def append_numeric_literal(self):

        # check for numbers

        j = self.i + 1
        while j < self.size and self.code[j].isnumeric():
            j += 1
        self.add_token((TokenType.INT, int(self.code[self.i:j])))
        self.i = j

    def append_symbol_or_keywords(self):

        # check for symbols
        j = self.i + 1
        while j < self.size and self.is_symbol(self.code[j]):
            j += 1

        symbol_val = self.code[self.i:j]

        # since keywords have the same rules as symbols, just add them
        # here.

        if symbol_val in keywords:
            self.add_token((TokenType.KEYWORD, symbol_val))
        else:
            self.add_token((TokenType.SYMBOL, symbol_val))
        self.i = j

    def append_string(self):
        # check for symbols

        # string requires an extra character, so must be accounted
        # for in the index
        j = self.i + 1
        while j < self.size - 1 and self.code[j] != STR_CHAR:
            j += 1
        self.add_token((TokenType.STR, self.code[self.i + 1:j]))
        self.i = j + 1

    def try_append_triple_ops(self):

        # three-char operators

        if self.p3 is not None and self.p3 in triple_ops.keys():
            self.token_is_operator = True
            self.add_token((TokenType.OP, triple_ops[self.p3]))
            self.i += 3
            return True

        return False

    def try_append_double_ops(self):

        # two-char operators

        if self.p2 is not None and self.p2 in double_ops.keys():
            self.token_is_operator = True
            self.add_token((TokenType.OP, double_ops[self.p2]))
            self.i += 2
            return True

        return False

    def try_append_single_ops(self):

        # one-char operators

        if self.p1 in single_ops.keys():
            self.token_is_operator = True
            self.add_token((TokenType.OP, single_ops[self.p1]))
            self.i += 1
            return True

        return False

    def tokenize(self):

        self.reset_state()

        while self.i < self.size:
            self.peek_all()
            self.token_is_operator = False
            if self.try_append_spaces():
                pass
            elif self.p1.isnumeric():
                self.append_numeric_literal()
            elif self.p1 == STR_CHAR:
                self.append_string()
            elif self.is_symbol(self.p1):
                self.append_symbol_or_keywords()
            elif self.try_append_triple_ops():
                pass
            elif self.try_append_double_ops():
                pass
            elif self.try_append_single_ops():
                pass
            else:
                raise Exception(f"{self.p1} is not a recognized character")

            self.discard_operator_spaces()
        self.discard_code_spaces()


def tokenize(code: str):
    """
    Tokenizes a piece of code
    No regular expressions; just char-by-char
    """

    tk = _Tokenizer(code)
    tk.tokenize()

    return tk.tokens


def print_tokens(tokens: List[Tuple[TokenType, Optional[str]]]):
    """
    Prints out a list of tokens formatted
    """
    with StringIO() as io:
        for token in tokens:
            tk_type = token[0]
            if tk_type == TokenType.STR:
                # print repr for escape chars in strings
                tk_value = repr(token[1])
            else:
                tk_value = token[1]

            tk_name = tk_type.name

            if tk_value is None:
                print("{:>8}".format(tk_name), file=io)
            else:
                print("{:>8} | {}".format(tk_name, tk_value), file=io)
        print(io.getvalue())


test_funcdef = """

# A test

number = Union[int, float]

square = def(x: number) -> number {
    return x * x
}

# test-test

if __name__ == "__main__" {
    print(square(5))
}
"""

test_multiline = """
/*
Hi
*/
print("Hello World")
/*
adasd
*/
1 + 1
"""

if __name__ == '__main__':
    print_tokens(tokenize(test_multiline))
