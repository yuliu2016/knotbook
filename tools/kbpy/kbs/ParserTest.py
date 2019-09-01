from .Parser import *
import unittest

test1 = """

# A test

1 + 1

"""

class SimpleTest(unittest.TestCase):
    def test_simple(self):
        self.assertTrue(True)