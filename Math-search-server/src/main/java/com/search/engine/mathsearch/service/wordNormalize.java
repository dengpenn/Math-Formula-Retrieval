package com.search.engine.mathsearch.service;

public class wordNormalize {
    // Essential private methods or variables can be added.

    // YOU MUST IMPLEMENT THIS METHOD.
    public char[] lowercase(char[] chars) {
        // Transform the word uppercase characters into lowercase.
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
//            Lower the character if it's upper case
            if (Character.isUpperCase(c)) {
                chars[i] = Character.toLowerCase(c);
            }
        }
        return chars;
    }

    // YOU MUST IMPLEMENT THIS METHOD.
    public String stem(String word) {
        // Return the stemmed word with Stemmer in Classes package.
//		Create stem object and add every character into this object
        Stemmer stem = new Stemmer();
        char[] chars = word.toCharArray();
        for (char c : chars) {
            stem.add(c);
        }
        stem.stem();
        return stem.toString();
    }
}