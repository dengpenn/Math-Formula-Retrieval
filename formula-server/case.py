import os
import pickle
import re
from xml.dom.minidom import parseString
from xml.dom import Node

fuzzy_formula = [f for f in os.listdir("./") if f.startswith("FUZZYINDEX")]
formula = [f for f in os.listdir("./") if f.startswith("INDEXFORMULA")]

print(fuzzy_formula)
print(formula)

fuzzy_dict = []
index_dict = []

for file in fuzzy_formula:
    f = open(file, "rb")
    fuzzy_index = pickle.load(f)
    fuzzy_dict.append(fuzzy_index)

for file in formula:
    f = open(file, "rb")
    fuzzy_index = pickle.load(f)
    index_dict.append(fuzzy_index)


def xml_to_fuzzy_terms(root):
    """docstring for xml2terms"""
    try:
        root = parseString(root).documentElement
    except:
        print(root)
        return None
    stack = [root, ]
    terms = set()
    result = []
    while stack:
        if stack[-1].firstChild and \
                stack[-1].firstChild.nodeType != Node.TEXT_NODE:
            term_raw = stack[-1].toxml()
            term_gen = re.sub('>[^<]+?<', '><', term_raw)
            if term_gen not in result:
                terms.add(term_gen)
                result.append((term_gen, len(stack)))

            # print term_raw, term_gen, len(stack)
            # term_raw = term_compress(term_raw)
            # term_gen = term_compress(term_gen)
            # print(term_raw, term_gen, len(stack))
        if stack[-1].firstChild and \
                stack[-1].firstChild.nodeType != Node.TEXT_NODE:
            stack.append(stack[-1].firstChild)
        elif stack[-1].nextSibling:
            stack[-1] = stack[-1].nextSibling
        else:
            stack.pop()
            while stack and not stack[-1].nextSibling:
                stack.pop()
            if stack:
                stack[-1] = stack[-1].nextSibling
    return result


def xml_to_terms(root):
    """docstring for xml2terms"""
    try:
        root = parseString(root).documentElement
    except:
        print(root)
        return None
    stack = [root, ]
    terms = set()
    result = []
    while stack:
        if stack[-1].firstChild and \
                stack[-1].firstChild.nodeType != Node.TEXT_NODE:
            term_raw = stack[-1].toxml()
            if term_raw not in terms:
                terms.add(term_raw)
                result.append((term_raw, len(stack)))
        if stack[-1].firstChild and \
                stack[-1].firstChild.nodeType != Node.TEXT_NODE:
            stack.append(stack[-1].firstChild)
        elif stack[-1].nextSibling:
            stack[-1] = stack[-1].nextSibling
        else:
            stack.pop()
            while stack and not stack[-1].nextSibling:
                stack.pop()
            if stack:
                stack[-1] = stack[-1].nextSibling
    return result


index_term = ""


# FUZZY
# TREE STRUCTURE LEVEL ONLY MATCH ONCE
# (max_single, score)
PATTEN = re.compile("\w+=\".*?\"")
def parse_query(math_formula_string, fuzzy):
    math_formula_string = re.sub(PATTEN, "", math_formula_string)
    math_formula_string = re.sub("<<", "&lt;<", math_formula_string)
    math_formula_string = re.sub(">><", ">&gt;<", math_formula_string)
    math_formula_string = re.sub("&<", "&amp;<", math_formula_string)
    parse_terms = xml_to_fuzzy_terms(math_formula_string)
    print(parse_terms)
    result_docouments = {}
    for term in parse_terms:
        sub_structure = term[0]
        level = term[1]
        for stored_dict in fuzzy_dict:
            if sub_structure in stored_dict:
                matched_documetns = stored_dict[sub_structure]
                total_matched_documents = len(matched_documetns)
                for single_document in matched_documetns:
                    current_level = single_document[1]
                    if single_document[0] in result_docouments:
                        current_score = result_docouments[single_document[0]]
                        current_score += 1 / ((level + current_level) * total_matched_documents)
                        result_docouments[single_document[0]] = current_score
                    else:
                        score = 1 / ((level + current_level) * total_matched_documents)
                        result_docouments[single_document[0]] = score
    return result_docouments


def calculate_score():
    pass


if __name__ == '__main__':
    f = open("try.xml", "r", encoding="utf-8")
    temp = []
    for line in f:
        temp.append(line.strip())
    query = "".join(temp)
    results = parse_query(query, False)
    # sorted_result = dict(sorted(results.iteritems(), key=results.itemgetter(1), reverse=True)[:5])
    import json

    sorted_keys = sorted(results, key=results.get, reverse=True)[:100]
    for key in sorted_keys:
        print(results[key])
        print(key)
    # print(json.dumps())

# NOT FUZZY
