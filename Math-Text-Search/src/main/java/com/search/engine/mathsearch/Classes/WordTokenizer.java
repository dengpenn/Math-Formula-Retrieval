package com.search.engine.mathsearch.Classes;
import java.security.PrivateKey;
import java.util.LinkedList;

/**
 * This is for INFSCI 2140 in 2019
 *
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	// Essential private methods or variables can be added.
	private LinkedList<char[]> tWords;
//	Iterator<char[]> iterator;

	// YOU MUST IMPLEMENT THIS METHOD.
	public WordTokenizer(char[] texts) {
		// Tokenize the input texts.
		tWords = new LinkedList<char[]>();
		StringBuilder oneWord = new StringBuilder();
		for (char Char : texts) {
			if (Character.isAlphabetic(Char) || Char == "-".charAt(0)) {
				if (Char=="-".charAt(0)&&oneWord.toString().length()==0){
					continue;
				}
				oneWord.append(Char);
			} else if (Char==" ".charAt(0)&&oneWord.length()>0) {
				if (oneWord.toString().charAt(oneWord.toString().length()-1)=="-".charAt(0)){
					tWords.add(oneWord.toString().substring(0,oneWord.toString().length()-1).toCharArray());
				}else{
					tWords.add(oneWord.toString().toCharArray());
				}
				oneWord = new StringBuilder();
			}
		}
		tWords.add(oneWord.toString().toCharArray());
//		iterator = tWords.iterator();

	}

	// YOU MUST IMPLEMENT THIS METHOD.
	public char[] nextWord() {
		// Return the next word in the document.
		// Return null, if it is the end of the document.

		if (tWords.size()>0) {
			return tWords.removeFirst();
		}
		return null;
	}

}
