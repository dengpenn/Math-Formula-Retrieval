package com.search.engine.mathsearch.Classes;

import java.util.Locale;

/**
 * This is for INFSCI 2140 in 2018
 * 
 */
public class WordNormalizer {
	// Essential private methods or variables can be added.
	char[] lowercase;
	// YOU MUST IMPLEMENT THIS METHOD.
	public char[] lowercase(char[] chars) {
		// Transform the word uppercase characters into lowercase.
		lowercase=new String(chars).toLowerCase(Locale.ENGLISH).toCharArray();
		return lowercase;
	}

	// YOU MUST IMPLEMENT THIS METHOD.
	public String stem(char[] chars) {
		// Return the stemmed word with Stemmer in Classes package.
		Stemmer s = new Stemmer();
		s.add(chars, chars.length);
		s.stem();
		String str = s.toString();
		return str;
	}

}
