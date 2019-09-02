from TokenizerTest import *

if __name__ == '__main__':
    with open("tests/funcdef.txt", "w") as f:
        print(format_tokens_for_tests(tokenize(test_case_funcdef)), file=f, end="")