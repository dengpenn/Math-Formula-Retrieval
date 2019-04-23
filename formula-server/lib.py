#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: lib.py
Author: huxuan
Email: i(at)huxuan.org
Description: Some common use functions
"""

import re
import zlib
import base64
from xml.dom import Node
from xml.dom.minidom import parseString
import os
import pickle
from collections import defaultdict

OPTIONS = ['--noparse', '--quiet']

IGNORE_LATEX_LIST = [r'\\qquad', r'\\quad', r'\\\\\[\w+\]', r'\\\\', r'\\!',
                     r'\\,', r'\\;', r'\\ ', r'{}', ]
IGNORE_SPACE_LIST = ['^\s+', '\s+$']
IGNORE_MULTI_SPACE = '\s+'

MATH_PATTERN = re.compile(r'<math>(.*?)</math>',
                          re.MULTILINE | re.IGNORECASE | re.DOTALL)

MO_ATTRIBUTE_PATTERN = re.compile(r"<mo[^>]*>")
MI_ATTRIBUTE_PATTERN = re.compile(r"<mi[^>]+>")
root_dir = "/Users/dengpan/Desktop/Information_Retrival/NTCIR-12_MathIR_Wikipedia_Corpus/MathTagArticles"
index_file = defaultdict(lambda: [])
fuzzy_index_file = defaultdict(lambda: [])


def normalize_content(content):
    """docstring for normalize_content"""
    content = re.sub(IGNORE_MULTI_SPACE, ' ', content)
    for ignore_item in IGNORE_SPACE_LIST:
        content = re.sub(ignore_item, '', content)
    return content


def normalize_latex(latex):
    """docstring for normalize_latex"""
    for ignore_item in IGNORE_LATEX_LIST:
        latex = re.sub(ignore_item, '', latex)
    # latex = re.sub(IGNORE_MULTI_SPACE, ' ', latex)
    # latex = re.sub('(\W) ', r'\1', latex)
    # latex = re.sub(' (\W)', r'\1', latex)
    for ignore_item in IGNORE_SPACE_LIST:
        latex = re.sub(ignore_item, '', latex)
    return latex


def term_compress(term):
    """docstring for term_compress"""
    return base64.b64encode(zlib.compress(term.encode('utf8')))


def term_decompress(term):
    """docstring for term_decompress"""
    return zlib.decompress(base64.b64decode(term)).decode('utf8')


import random


def xml2terms(root, document_name, count):
    """docstring for xml2terms"""
    try:
        root = parseString(root).documentElement
    except:
        print(root)
        print(document_name)
        return None
    # document_name = document_name + str(random.randint(1, 100))
    document_name += str(count)
    stack = [root, ]

    while stack:
        if stack[-1].firstChild and \
                stack[-1].firstChild.nodeType != Node.TEXT_NODE:
            term_raw = stack[-1].toxml()
            term_gen = re.sub('>[^<]+?<', '><', term_raw)
            # if (document_name, len(stack)) not in index_file[term_raw]:
            index_file[term_raw].append((document_name, len(stack)))
            # if (document_name, len(stack)) not in fuzzy_index_file[term_gen]:
            fuzzy_index_file[term_gen].append((document_name, len(stack)))
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


def check_variables(key):
    print(index_file[key])


def index2pickle(count):
    f = open("INDEXFORMULA_" + str(count), "wb")
    pickle.dump(dict(index_file), f)
    f.close()
    f = open("FUZZYINDEXFORMULA" + str(count), "wb")
    pickle.dump(dict(fuzzy_index_file), f)
    f.close()
    index_file.clear()
    fuzzy_index_file.clear()


def xmlclean(xml):
    """docstring for xmlclean"""
    # Filter tags' attributes
    xml = re.sub('(<[^>\s]+?)\ [^>]*?(\/?>)', r'\1\2', xml)
    # Filter whitespace characters
    xml = re.sub('\s+', '', xml)

    res = MATH_PATTERN.search(xml)
    if res:
        return res.group(1)
    else:
        return xml


if __name__ == '__main__':
    f = open("try.xml", "r")
    content = [line.strip() for line in open('try.xml', "r", encoding="utf-8")]
    print(re.sub(MO_ATTRIBUTE_PATTERN, "<mo>", "".join(content)))
    print(re.sub(MI_ATTRIBUTE_PATTERN, "<miu>", "".join(content)))
    root = parseString("".join(content)).documentElement
    xml2terms(root)
    for node in root.childNodes:
        if node.attributes:
            for key in list(node.attributes.keys()):
                node.removeAttribute(key)
                print(node.toxml())
    print(root.toxml())
    for table in root:
        print(table)
