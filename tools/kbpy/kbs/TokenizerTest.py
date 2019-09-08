import unittest
from Tokenizer import *

test_case_funcdef = """
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

class TokenizerTest(unittest.TestCase):

    def test_funcdef(self):
        with open("tests/funcdef.txt") as f:
            expected = f.read()
        actual = format_tokens_for_tests(tokenize(test_case_funcdef))
        self.assertEqual(expected, actual)
