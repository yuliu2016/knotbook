from Tokenizer import *
from enum import Enum


class I(Enum):
    # Do nothing code
    NO_OP = 1

    """
    STACK OPERATIONS
    [2-7] Range; [2-6] Full, [7] Empty
    """

    # Removes the top-of-stack (TOS) item.
    POP_TOP = 2

    # Swaps the two top-most stack items
    ROT_TWO = 3

    # Lifts second and third stack item one position up,
    # moves top down to position three.
    ROT_THREE = 4

    # Duplicates the reference on top of the stack
    DUP_TOP = 5

    # Duplicates the two references on top of the stack,
    # leaving them in the same order.
    DUP_TOP_TWO = 6

    """
    UNARY OPERATIONS
    [8-15] Range
    """

    # Implements TOS = +TOS
    UNARY_POSITIVE = 8

    # Implements TOS = -TOS
    UNARY_NEGATIVE = 9

    # Implements TOS = not TOS
    UNARY_NOT = 10

    # Implements TOS = ~TOS
    UNARY_BIT_NOT = 11

    """
    BINARY OPERATIONS
    [16-31] Range
    """

    BINARY_PLUS = 16

    BINARY_MINUS = 17

    BINARY_TIMES = 18

    BINARY_DIVIDE = 19

    BINARY_FLOOR_DIVIDE = 20

    BINARY_SUBSCRIPT = 21

    BINARY_SHIFT_LEFT = 22

    BINARY_SHIFT_RIGHT = 23

    BINARY_AND = 24

    BINARY_OR = 25

    BINARY_XOR = 26

    BINARY_MODULO = 27

    BINARY_POWER = 28

    """
    IN-PLACE AND FAST BINARY OPERATIONS
    [32-63] Range; Reserved
    """

    """
    MISCELLANEOUS
    [64-95] Range
    """

    PRINT_EXPR = 64

    BREAK_LOOP = 65

    CONTINUE_LOOP = 66

    RETURN_VALUE = 67

    IMPORT_STAR = 68

    POP_BLOCK = 69

    POP_EXCEPT = 70

    END_FINALLY = 71

    # Not in Python -- suspends a function
    SUSPEND_COROUTINE = 74

    # Not in Python -- resumes a coroutine
    RESUME_COROUTINE = 75

    RAISE_VARARGS = 76

    CALL_FUNCTION = 77

    CALL_FUNCTION_KW = 78

    CALL_FUNCTION_EX = 79

    LOAD_METHOD = 80

    CALL_METHOD = 81

    FORMAT_VALUE = 82

    """
    VARIABLES AND NAMES
    [96-127] Range
    """

    STORE_NAME = 96

    DELETE_NAME = 97

    UNPACK_SEQUENCE = 98

    IMPORT_NAME = 99

    LOAD_FAST = 100

    LOAD_CONST = 101

    STORE_FAST = 102

    DELETE_FAST = 103

    """
    JUMP INSTRUCTIONS
    [128-144] Range
    """

    JUMP_FORWARD = 128

    POP_JUMP_IF_TRUE = 129

    POP_JUMP_IF_FALSE = 130

    JUMP_IF_TRUE_OR_POP = 131

    JUMP_IF_FALSE_OR_POP = 132

    JUMP_ABSOLUTE = 133



test_opo = "1 + 1"

if __name__ == '__main__':
    print(format_printing(tokenize(test_opo)))
