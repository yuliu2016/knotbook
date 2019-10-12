from mathlib import *
import unittest


class FuncUtilsTest(unittest.TestCase):

    def test_eq(self):
        self.assertTrue(eq(1.0, 1.0))

    def test_eq_inf(self):
        self.assertTrue(eq(math.inf, math.inf))

    def test_eq_diff(self):
        self.assertTrue(eq(1.0, 1.000000000000001))

    def test_eq_with_epsilon(self):
        self.assertTrue(eq(1.0, 1.0001, 1E-3))


class TranslationTest(unittest.TestCase):

    def test_mag(self):
        a = translation(3, 4)
        self.assertEqual(abs(a), 5.0)


if __name__ == '__main__':
    unittest.main(verbosity=3)