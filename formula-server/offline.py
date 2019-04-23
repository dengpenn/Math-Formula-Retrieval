import os
from lib import xml2terms
from lib import index2pickle
from lib import check_variables
import re
import time

root_dir = "/Users/dengpan/Desktop/Information_Retrival/NTCIR-12_MathIR_Wikipedia_Corpus/MathTagArticles"


def find_math(file, document_name):
    start = False
    math_formula = []
    total_math_formula = []
    count = 0
    for line in file:
        line = line.strip()
        if line.startswith("<semantics>"):
            start = True
        elif line.startswith("<annotation-xml"):
            start = False
            math_formula_string = "".join(math_formula)
            math_formula_string = re.sub(PATTEN, "", math_formula_string)
            math_formula_string = re.sub("<<", "&lt;<", math_formula_string)
            math_formula_string = re.sub(">><", ">&gt;<", math_formula_string)
            math_formula_string = re.sub("&<", "&amp;<", math_formula_string)
            math_formula_string = math_formula_string.replace(" ", "")
            xml2terms(math_formula_string, document_name, count)
            count += 1
            math_formula = []
            # print("Done")
        elif start:
            math_formula.append(line)
    # print(total_math_formula)


PATTEN = re.compile("\w+=\".*?\"")
query = "<mrow><mrow><mi>a</mi><mo>+</mo><mi>b</mi></mrow><mo>=</mo><mi>c</mi></mrow>"
if __name__ == '__main__':
    root_dir = "/Users/dengpan/Desktop/Information_Retrival/NTCIR-12_MathIR_Wikipedia_Corpus/MathTagArticles"

    count = 0
    directories = sorted([os.path.join(root_dir, d) for d in os.listdir(root_dir) if d.startswith("wpmath")])
    # print(directories)
    start_time = time.time()
    # d = directories[13]
    # f = os.path.join(d, "Mason–Stothers_theorem.html")
    # file = open(f, "r", encoding="utf-8")
    # find_math(file, "avcd")
    # for d in directories[12:]:
    for d in directories:
        print(d)
        count += 1
        files = os.listdir(d)
        for f in files:
            f = os.path.join(d, f)
            document_name = f.split("MathTagArticles/")[-1]
            file = open(f, "r", encoding="utf-8")
            find_math(file, document_name)
        print("Done")
        print(time.time() - start_time)
        start_time = time.time()
        if count % 1 == 0:
            check_variables(query)
        if count % 4 == 0:
            index2pickle(count)
