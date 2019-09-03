from typing import *
from enum import Enum
from io import StringIO


# TODO
#  fix space tokenizer
#  add multi-line strings
#  check closing of docstrings
#  add floats and exponents
#  add bin and hex
#  add long ints
#  add underscore_delimiter
#  add escape chars
#  ordered set for operators?
#  check CRLF '\r\n' in a new line


#
# Token types
#

class TokenType(Enum):
    NEWLINE = 0
    SPACE = 1
    KEYWORD = 2
    SYMBOL = 3
    OPERATOR = 4
    INT = 5
    LONG = 6
    COMPLEX = 7
    FLOAT = 8
    STRING = 9
    DOCSTR = 10

    def __repr__(self):
        # do this so that tests run properly using repr
        return self.name


#
# Token tuple
#

class Token(NamedTuple):
    line: int
    start_index: int
    token_type: TokenType
    value: Any

    def __repr__(self):
        return f"Token({self.line!r}, {self.start_index!r}," \
               f"{self.token_type!r}, {self.value!r})"


class Operator(Enum):
    """
    Includes all operators of the language. This includes delimiters
    such as brackets
    """

    #
    #
    # Single-char operators
    #
    #

    DOT = "."
    COMMA = ","
    ASSIGN = "="
    COLON = ":"
    DECORATOR = "@"

    # second most common - brackets

    OPEN_ROUND = "("
    CLOSE_ROUND = ")"

    OPEN_CURLY = "{"
    CLOSE_CURLY = "}"

    OPEN_SQUARE = "["
    CLOSE_SQUARE = "]"

    OPEN_ANGLE_LT = "<"
    CLOSE_ANGLE_MT = ">"

    # arithmetic

    PLUS = "+"
    MINUS = "-"
    TIMES_ARGS = "*"
    DIV = "/"
    MODULUS = "%"

    # bitwise operators

    BIT_OR = "|"
    BIT_AND = "&"
    BIT_NOT = "~"
    BIT_XOR = "^"

    #
    #
    # Double-char operators
    #
    #

    EQUAL = "=="
    NOT_EQUAL = "!="
    LESS_EQUAL = "<="
    MORE_EQUAL = ">="

    ARROW = "->"

    FDIV = "//"
    EXP_KWARGS = "**"

    PLUS_ASSIGN = "+="
    MINUS_ASSIGN = "-="
    TIMES_ASSIGN = "*="
    DIV_ASSIGN = "/="
    MODULUS_ASSIGN = "%="

    BIT_OR_ASSIGN = "|="
    BIT_AND_ASSIGN = "&="
    BIT_XOR_ASSIGN = "^="

    SHIFT_LEFT = "<<"
    SHIFT_RIGHT = ">>"

    #
    #
    # Triple-char operators
    #
    #

    FDIV_ASSIGN = "//="
    EXP_ASSIGN = "**="

    def __repr__(self):
        # do this so that tests run properly using repr
        return self.name


single_operators_set = {
    Operator.DOT,
    Operator.COMMA,
    Operator.ASSIGN,
    Operator.COLON,
    Operator.DECORATOR,

    Operator.OPEN_ROUND,
    Operator.CLOSE_ROUND,
    Operator.OPEN_CURLY,
    Operator.CLOSE_CURLY,
    Operator.OPEN_SQUARE,
    Operator.CLOSE_SQUARE,
    Operator.OPEN_ANGLE_LT,  # typing and more-than comparison
    Operator.CLOSE_ANGLE_MT,  # typing and more-than comparison

    Operator.PLUS,
    Operator.MINUS,
    Operator.TIMES_ARGS,  # times and def(*args)
    Operator.DIV,
    Operator.MODULUS,

    Operator.BIT_OR,
    Operator.BIT_AND,
    Operator.BIT_NOT,
    Operator.BIT_XOR
}

single_operators = {op.value: op for op in single_operators_set}

#
# Double-char operators
#

double_operators_set = {
    Operator.EQUAL,
    Operator.NOT_EQUAL,
    Operator.LESS_EQUAL,
    Operator.MORE_EQUAL,

    Operator.ARROW,

    Operator.FDIV,
    Operator.EXP_KWARGS,  # exponents and def(**kwargs)/ {**k, **v} etc

    Operator.PLUS_ASSIGN,
    Operator.MINUS_ASSIGN,
    Operator.TIMES_ASSIGN,
    Operator.DIV_ASSIGN,
    Operator.MODULUS_ASSIGN,

    Operator.BIT_OR_ASSIGN,
    Operator.BIT_AND_ASSIGN,
    Operator.BIT_XOR_ASSIGN,

    Operator.SHIFT_LEFT,
    Operator.SHIFT_RIGHT
}

double_operators = {op.value: op for op in double_operators_set}

#
# Triple-char operators
#

triple_operators_set = {
    Operator.FDIV_ASSIGN,
    Operator.EXP_ASSIGN
}

triple_operators = {op.value: op for op in triple_operators_set}

#
# Keywords (subset of symbols, easier to check later)
#
keywords = {  # set notation

    # Functional keywords
    "return",
    "def",
    "suspend",

    # Condition kewords
    "if",
    "else",
    "when",

    # Booleans and operations
    "and",
    "or",
    "not",
    "is",
    "in",
    "True",
    "False",

    # None
    "None",
    "pass",

    # Context keywords
    "with",
    "as",
    "from",
    "import",

    # Loop keywords
    "while",
    "for",
    "continue",
    "break",

    # Exception handling
    "try",
    "except",
    "finally",
    "raise"
}


class Lexing:

    @staticmethod
    def is_underscore(ch: str):
        return ch == "_"

    @staticmethod
    def is_identifier_symbol(ch: str):
        # checks if it is a symbol name
        return ch.isalnum() or Lexing.is_underscore(ch)

    @staticmethod
    def is_newline(ch: str):
        # checks against newline characters
        return ch == "\n" or ch == "\r"

    @staticmethod
    def is_crlf(ch: str):
        # this needs to be treated as one single newline
        return  ch == "\r\n"

    @staticmethod
    def is_open_multi_comment(ch: str):
        return ch == "/*"

    @staticmethod
    def is_open_docstr(ch: str):
        return ch == "/**"

#
# Other Constants
#

OPEN_DOCSTRING = "/**"
CLOSE_MULTILINE_COMMENT = "*/"

SINGLE_COMMENT_CHAR = "#"

UNDERSCORE_CHAR = "_"

STR_CHAR = "\""


class _Visitor:
    """
    Defines a visitor to a piece of code
    Allows peeking and moving indices
    """

    def __init__(self, code: str, initial_index=0, allow_backtrack=False):
        self.code = code
        self.allow_backtrack = allow_backtrack

        # the caret index
        self.i = initial_index

        self.size = len(code)

        # used to lookup up to the next three characters
        # p2 and p3 are optional because it could be reading into EOF
        self.p1: str = "\0"
        self.p2: Optional[str] = None
        self.p3: Optional[str] = None

    def reset_state(self):
        # resets the visitor
        self.tokens.clear()
        self.i = 0
        self.size = len(self.code)
        self.p1 = "\0"
        self.p2 = None
        self.p3 = None

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

    def peek_all(self):
        # peek into the future...
        self.p1 = self.code[self.i]
        self.p2 = self.peek_or_none(2)
        self.p3 = self.peek_or_none(3)


class _Tokenizer(_Visitor):

    def __init__(self, code: str):

        # delegate to superclass
        super().__init__(code)

        # the list of generated tokens
        self.tokens: List[Token] = []

        # used to remove spacing when the last token is an operation
        self.token_is_operator = False
        self.last_token_is_operator = False

        # used to lookup newline characters from the last i value
        # -1 because the first token might be from 0
        self.last_token_index = -1

        # Assume starting on the first line
        # line_number is not a property of _Visitor because it is not
        # aware of newline characters
        self.line_number = 1

    def raise_syntax_error(self):
        raise SyntaxError(f"{self.p1} is not a recognized syntax")

    def reset_state(self):
        super().reset_state()
        self.token_is_operator = False
        self.last_token_is_operator = False
        self.last_token_index = -1
        self.line_number = 1

    def visitor_from_next_index(self):
        return _Visitor(self.code, self.i + 1, False)

    def pop_space(self, n: int):
        """
         pop the space when the opcode can be simplified
         """

        # fixes trying to pop the space when there is no tokens
        # (such as an unclosed multi-line comment or string)
        if len(self.tokens) == 0:
            return

        if self.tokens[-n].token_type == TokenType.SPACE:
            self.tokens.pop(len(self.tokens) - n)

    def pop_newline(self, n: int):
        """
         pop the newline when the opcode can be simplified
         """

        # fixes trying to pop the space when there is no tokens
        # (such as an unclosed multi-line comment or string)
        if len(self.tokens) == 0:
            return

        if self.tokens[-n].token_type == TokenType.NEWLINE:
            self.tokens.pop(len(self.tokens) - n)

    def discard_code_spaces(self):
        # Discard spacing at the end of the sequence
        self.pop_space(n=1)
        self.pop_newline(n=1)

        # Discard spacing at the beginning of the sequence
        self.pop_space(n=len(self.tokens))
        self.pop_newline(n=len(self.tokens))

    def discard_operator_spaces(self):
        # check whether spaces can be popped off around the operator

        if self.last_token_is_operator:
            # pop the space after the last operator
            self.pop_space(n=1)

        if self.token_is_operator:
            # pop the space before the current operator
            self.pop_space(n=2)
            self.last_token_is_operator = True
        else:
            self.last_token_is_operator = False

    def add_token(self, tk_type: TokenType, tk_value):

        # Adds a token with the proper line numbering

        # i must be increased from the last call to this function
        # otherwise breaks contract
        assert self.i != self.last_token_index

        # iterate through the section of the string that
        # has been added to the token to search for newlines
        for ch in self.code[self.last_token_index:self.i]:
            if Lexing.is_newline(ch):
                self.line_number += 1

        self.last_token_index = self.i
        self.tokens.append(Token(self.line_number, self.i, tk_type, tk_value))

    def tokenize_docstr(self):

        # tokenize a docstr and add it as a token
        # it does not allow nested multi-line comments

        in_docstr = Lexing.is_open_docstr(self.p3)

        if not in_docstr:
            return False

        # by above condition this will not break index
        j = self.i + 3

        while j < self.size - 1:
            peek2 = self.code[j: j + 2]

            if not (in_docstr
                    or peek2 == CLOSE_MULTILINE_COMMENT):
                break

            if peek2 == CLOSE_MULTILINE_COMMENT:
                in_docstr = False
                j += 1
            j += 1

        self.add_token(TokenType.DOCSTR, self.code[self.i + 3: j - 2])
        self.i = j

        return True

    def tokenize_spaces(self):

        # tokenize all spacing as either NEWLINE or SPACE
        # and ignore all the comments

        in_comment = self.p1 == SINGLE_COMMENT_CHAR

        # Use an int instead of boolean because it needs a
        # counter to keep track of nested multi-line comments

        in_multi_comment = int(Lexing.is_open_multi_comment(self.p2))

        if not (in_comment
                or in_multi_comment > 0
                or self.p1.isspace()):
            return False  # not actually a space to be parsed

        # ensures condition: not (in_comment and in_multi_line_comment)

        # Create a visitor
        vis = self.visitor_from_next_index()

        if in_multi_comment > 0:
            # because the parsing needs to start an extra char later
            vis.i += 1

        # make sure that newline is added if it's the first char
        newline = Lexing.is_newline(self.p1)

        while vis.i < vis.size:
            vis.peek_all()

            # check that either a docstring starts, or the space ends
            if (not (in_comment
                     or in_multi_comment > 0
                     or vis.p1.isspace()
                     or vis.p1 == SINGLE_COMMENT_CHAR
                     or Lexing.is_open_multi_comment(vis.p2))
                    # Fix: docstring with spaces before it will trigger multi-line
                    # comments instead of a docstr
                    or (Lexing.is_open_docstr(vis.p3) and in_multi_comment == 0)):
                break

            if Lexing.is_newline(vis.p1):
                in_comment = False
                newline = True

            if vis.p1 == SINGLE_COMMENT_CHAR:
                in_comment = True

            if Lexing.is_open_multi_comment(vis.p2):
                in_multi_comment += 1
                # since peek2 is not None, this does not break indexing
                vis.i += 1

            elif vis.p2 == CLOSE_MULTILINE_COMMENT:
                in_multi_comment -= 1
                # since peek2 is not None, this does not break indexing
                vis.i += 1

            vis.i += 1

        # This is the case when in_multi_line_comment is not 0
        # when the while loop has iterated through the entire piece of code
        if in_multi_comment > 0:
            raise SyntaxError("Multi-line comments not closed off")

        # NEWLINE and SPACE are the only symbols that have a value of None
        if newline:
            # This is relevant because of the short-hand syntax
            # (using ':' and '\n' as delimiters instead of curly brackets)
            self.add_token(TokenType.NEWLINE, None)
        else:
            self.add_token(TokenType.SPACE, None)

        # this line must be after add_token for lineno to be correct
        self.i = vis.i

        return True

    def tokenize_number(self):

        if not self.p1.isnumeric():
            return False

        # check for numbers

        j = self.i + 1
        while j < self.size and self.code[j].isnumeric():
            j += 1
        self.add_token(TokenType.INT, int(self.code[self.i:j]))

        # this line must be after add_token for lineno to be correct
        self.i = j

        return True

    def tokenize_symbol(self):

        if not Lexing.is_identifier_symbol(self.p1):
            return False

        # check for symbols
        j = self.i + 1
        while j < self.size and Lexing.is_identifier_symbol(self.code[j]):
            j += 1

        symbol_val = self.code[self.i:j]

        # since keywords have the same rules as symbols, just add them
        # here.

        if symbol_val in keywords:
            self.add_token(TokenType.KEYWORD, symbol_val)
        else:
            self.add_token(TokenType.SYMBOL, symbol_val)

        # this line must be after add_token for lineno to be correct
        self.i = j

        return True

    def tokenize_string(self):

        if self.p1 != STR_CHAR:
            return False

        # check for symbols

        # string requires an extra character, so must be accounted
        # for in the index
        j = self.i + 1
        while j < self.size - 1 and self.code[j] != STR_CHAR:
            j += 1
        self.add_token(TokenType.STRING, self.code[self.i + 1:j])

        # this line must be after add_token for lineno to be correct
        self.i = j + 1

        return True

    def tokenize_triple_operator(self):

        # three-char operators

        if self.p3 is not None and self.p3 in triple_operators.keys():
            self.token_is_operator = True
            self.add_token(TokenType.OPERATOR, triple_operators[self.p3])
            self.i += 3
            return True

        return False

    def tokenize_double_operator(self):

        # two-char operators

        if self.p2 is not None and self.p2 in double_operators.keys():
            self.token_is_operator = True
            self.add_token(TokenType.OPERATOR, double_operators[self.p2])
            self.i += 2
            return True

        return False

    def tokenize_single_operator(self):

        # one-char operators

        if self.p1 in single_operators.keys():
            self.token_is_operator = True
            self.add_token(TokenType.OPERATOR, single_operators[self.p1])
            self.i += 1
            return True

        return False

    def tokenize(self):
        # Runs the tokenization loop

        self.reset_state()

        while self.i < self.size:
            # find p1, p2, and p3
            self.peek_all()
            # reset operator state
            self.token_is_operator = False
            if not (self.tokenize_docstr()
                    or self.tokenize_spaces()
                    or self.tokenize_number()
                    or self.tokenize_string()
                    or self.tokenize_symbol()
                    or self.tokenize_triple_operator()
                    or self.tokenize_double_operator()
                    or self.tokenize_single_operator()):
                self.raise_syntax_error()
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


class TColor:
    BACK_BLACK = "\033[40m"
    BACK_WHITE = "\033[47m"
    WHITE = "\033[37m"
    BOLD = "\033[1m"
    GREEN = "\033[32m"
    BLUE = "\033[34m"
    BRIGHT_BLUE = "\033[34;1m"
    MAGENTA = "\033[35m"
    END = "\033[0m"


def wrap(c: str, s: str):
    # wraps a terminal style to be displayed
    return f"{c}{s}{TColor.END}"


def format_token_for_print(tokens: List[Token]):
    """
    Prints out a list of tokens formatted
    """
    io = StringIO()
    last_line = 0

    for token in tokens:
        line, _, token_type, token_value = token

        if line != last_line:
            wrapped_line = wrap(TColor.BOLD, "L{:03d} ".format(line))
            print(wrapped_line, end="", file=io)
        else:
            print("     ", end="", file=io)
        last_line = line

        type_padded = "{:>9}".format(token_type.name)

        if token_type == TokenType.NEWLINE or token_type == TokenType.SPACE:
            # does not print the value because it's None
            print(wrap(TColor.WHITE, type_padded), file=io)
            continue

        if token_type == TokenType.STRING or token_type == TokenType.DOCSTR:
            # print repr for escape chars in strings
            value_formatted = wrap(TColor.GREEN, repr(token_value))
        elif token_type == TokenType.OPERATOR:
            # token_value is an Operator enum so .value is needed
            value_formatted = f" {token_value.value}"
        else:
            # to line up with repr calls
            value_formatted = f" {token_value}"

        if token_type == TokenType.KEYWORD:
            value_formatted = wrap(TColor.BRIGHT_BLUE, value_formatted)
        elif token_type == TokenType.SYMBOL:
            value_formatted = wrap(TColor.MAGENTA, value_formatted)
        elif token_type == TokenType.INT:
            value_formatted = wrap(TColor.BLUE, value_formatted)

        print("{}  {}".format(type_padded, value_formatted), file=io)

    return io.getvalue()


def format_tokens_for_tests(tokens: List[Token]):
    io = StringIO()
    for token in tokens:
        print(repr(token), file=io)
    return io.getvalue()


test_funcdef = """

# A test

number = Union[int, float]

sqrt = def(x: number) -> number {
    return x ** (1 / 2)
}

# test-test

if __name__ == "__main__" {
    print(sqrt(9))
}
"""

test_multi_line_comment_nested = """
/*/* */
Hi
*/
print("Hello World")
"""

test_docstr = """
/** Hello */
a = 3
"""

test_recursive_fibonacci = """
/**
 * Calculates a fibonacci number
 */
fib = def(n: int) {
    if n < 2: return 1
    return fib(n - 1) + fib(n - 2)
}

print(fib(10))

"""

if __name__ == '__main__':
    print(format_token_for_print(tokenize(test_recursive_fibonacci)))
