import os
from subprocess import Popen, PIPE, STDOUT

root_dir = "/Users/dengpan/Desktop/Information_Retrival/NTCIR-12_MathIR_Wikipedia_Corpus/MathTagArticles"
options = ['--preload=amsmath", "--preload=amsfonts", "--preload=amssymb']
commands = ['latexmlmath']
commands.extend(options)
commands.append('--')
commands.append('-')

def find_math(file, document_name):
    start = False
    math_formula = []
    total_math_formula = []
    count = 0
    for line in file:
        line = line.strip()
        if line.startswith("<annotation encoding=\"application/x-tex\">"):
            start = True
        elif line == "</annotation>" and start:
            start = False
            count += 1
            if r"\rang" in "".join(math_formula):
                print("".join(math_formula))
                print(file)
            # print commands
            # popen = Popen(commands, stdin=PIPE, stdout=PIPE, stderr=PIPE)
            # res = popen.communicate("".join(math_formula).encode("utf8"))[0]
            # if "error" in res.decode("utf-8"):
            #     print(res)
            math_formula = []
        elif start:
            math_formula.append(line)


# directories = sorted([d for d in os.listdir("./") if d.startswith("wpmath")])
directories = sorted([os.path.join(root_dir, d) for d in os.listdir(root_dir) if d.startswith("wpmath")])

for d in directories:
    print(d)
    files = os.listdir(d)
    for f in files:
        f = os.path.join(d, f)
        file = open(f, "r", encoding="utf-8")
        find_math(file, "TEST")
