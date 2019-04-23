import os
import pickle

formula = sorted([f for f in os.listdir("./") if f.startswith("FUZZYINDEXFORMULA")])
index_dict = []
for file in formula:
    f = open(file, "rb")
    fuzzy_index = pickle.load(f)
    index_dict.append(fuzzy_index)
total_info = {}

for d in index_dict:
    for key in d:
        if key not in total_info:
            total_info[key] = d[key]
        else:
            total_info[key].extend(d[key])
    print("Done")
total_formula = open("FUZZYINDEXFORMULA", "wb")
pickle.dump(total_info, total_formula)
total_formula.close()

# import re
# root = """
# <math alttext="T_{c}=\\left(\\frac{n}{\\zeta(3/2)}\\right)^{2/3}\\frac{2\\pi\\hbar^{2}}{mk_{B}}" display="block" xmlns="http://www.w3.org/1998/Math/MathML">  <mrow>    <msub>      <mi>T</mi>      <mi>c</mi>    </msub>    <mo>=</mo>    <mrow>      <msup>        <mrow>          <mo>(</mo>          <mfrac>            <mi>n</mi>            <mrow>              <mi>ζ</mi>                            <mrow>                <mo stretchy="false">(</mo>                <mrow>                  <mn>3</mn>                  <mo>/</mo>                  <mn>2</mn>                </mrow>                <mo stretchy="false">)</mo>              </mrow>            </mrow>          </mfrac>          <mo>)</mo>        </mrow>        <mrow>          <mn>2</mn>          <mo>/</mo>          <mn>3</mn>        </mrow>      </msup>            <mfrac>        <mrow>          <mn>2</mn>                    <mi>π</mi>                    <msup>            <mi mathvariant="normal">ℏ</mi>            <mn>2</mn>          </msup>        </mrow>        <mrow>          <mi>m</mi>                    <msub>            <mi>k</mi>            <mi>B</mi>          </msub>        </mrow>      </mfrac>    </mrow>  </mrow></math>
# """
# print(re.sub("<math.*1998/Math/MathML\">", "", root).replace("</math>", "").replace(" ", ""))