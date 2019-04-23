from flask import Flask, request, jsonify
import os
import pickle
import re
from xml.dom.minidom import parseString
from xml.dom import Node
from flask_cors import CORS
from subprocess import Popen, PIPE, STDOUT

options = ['--preload=amsmath", "--preload=amsfonts", "--preload=amssymb']
commands = ['latexmlmath']
commands.extend(options)
commands.append('--')
commands.append('-')
TAG_RE = re.compile(r'<[^>]+>')
prefix_url = "http://35.245.156.13:8082/"

base_dir = "NTCIR-12_MathIR_Wikipedia_Corpus/MathTagArticles"
app = Flask(__name__)
fuzzy_formula = open("FUZZYINDEXFORMULA", "rb")
formula = open("INDEXFORMULA", "rb")
CORS(app, resources={r"/*": {"origins": "*"}})

fuzzy_dict = []
index_dict = []
fuzzy_index = pickle.load(fuzzy_formula)
exact_index = pickle.load(formula)

PATTEN = re.compile("\w+=\".*?\"")


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


def parse_query(math_formula_string, fuzzy):
    math_formula_string = re.sub(PATTEN, "", math_formula_string)
    math_formula_string = re.sub("<<", "&lt;<", math_formula_string)
    math_formula_string = re.sub(">><", ">&gt;<", math_formula_string)
    math_formula_string = re.sub("&<", "&amp;<", math_formula_string)
    math_formula_string = math_formula_string.replace(" ", "").replace("<math>", "")
    print(math_formula_string)
    if not fuzzy:
        parse_terms = xml_to_terms(math_formula_string)
        result_docouments = {}
        for term in parse_terms:
            sub_structure = term[0]
            level = term[1]
            if sub_structure in exact_index:
                matched_documetns = exact_index[sub_structure]
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
    else:
        parse_terms = xml_to_fuzzy_terms(math_formula_string)
        result_docouments = {}
        for term in parse_terms:
            sub_structure = term[0]
            level = term[1]
            if sub_structure in fuzzy_index:
                matched_documetns = fuzzy_index[sub_structure]
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


def check_query(math_formula_string, fuzzy):
    math_formula_string = re.sub(PATTEN, "", math_formula_string)
    math_formula_string = re.sub("<<", "&lt;<", math_formula_string)
    math_formula_string = re.sub(">><", ">&gt;<", math_formula_string)
    math_formula_string = re.sub("&<", "&amp;<", math_formula_string)
    parse_terms = xml_to_terms(math_formula_string)
    result_docouments = {}
    for term in parse_terms:
        sub_structure = term[0]
        level = term[1]
        for stored_dict in index_dict:
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


@app.route('/search/formula', methods=["POST"])
def index():
    content = request.get_json()
    fuzzy = content["isFuzzy"]
    query = content["key_formula"]
    popen = Popen(commands, stdin=PIPE, stdout=PIPE, stderr=PIPE)
    res = popen.communicate("".join(query).encode("utf8"))[0]
    res = res.decode("utf-8")
    res = res.strip("""<?xml version="1.0" encoding="UTF-8"?>""").replace("<mo>\u2062</mo>", "").replace("</math>", "").replace(
        "\n", "")
    print(res)
    results = parse_query(res, fuzzy)
    # sorted_result = dict(sorted(results.iteritems(), key=results.itemgetter(1), reverse=True)[:5])

    sorted_keys = sorted(results, key=results.get, reverse=True)[:20]
    return_douments = []
    already_exists_document = set()
    for key in sorted_keys:
        key = re.sub("html\d+", "html", key)
        if key not in already_exists_document:
            already_exists_document.add(key)
            key = re.sub("html\d+", "html", key)
            # Extract html content
            with open(os.path.join(base_dir, key)) as f:
                returned_instance = {}
                flag = False
                count = 0
                content = []
                for line in f:
                    line = line.strip()
                    if not line.startswith("<body>") and not flag:
                        continue
                    else:
                        flag = True
                    if flag:
                        line = re.sub(TAG_RE, "", line)
                        if line == "":
                            continue
                        content.append(line)
                        count += len(line)
                        if count > 200:
                            f.close()
                            break
                returned_instance["url"] = prefix_url + key
                returned_instance["title"] = key.split("/")[-1].replace(".html", "")
                returned_instance["keyword"] = " ".join(content) + "..."

                return_douments.append(returned_instance)

    response = jsonify(return_douments)
    response.headers.add('Access-Control-Allow-Origin', '*')
    return response


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=80)
    # f = open("try.xml", "r", encoding="utf-8")
    # query = []
    # for line in f:
    #     query.append(line.strip())
    # query = "".join(query)
    # results = parse_query(query, False)
    # # wpmath0000014/Mason–Stothers_theorem.html3
    # sorted_keys = sorted(results, key=results.get, reverse=True)[:100]
    # return_douments = []
    # for key in sorted_keys:
    #     for key in sorted_keys:
    #         key = re.sub("html\d+", "html", key)
    #         if key not in return_douments:
    #             return_douments.append(key)
    # print(return_douments)
