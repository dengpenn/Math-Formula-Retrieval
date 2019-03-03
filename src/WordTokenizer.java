import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is for INFSCI 2140 in 2019
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	// Essential private methods or variables can be added.
	ArrayList<char[]> tWords;
	Iterator<char[]> iterator;

	// YOU MUST IMPLEMENT THIS METHOD.
	public WordTokenizer(char[] texts) {
		// Tokenize the input texts.
		tWords = new ArrayList<char[]>();
		StringBuffer oneWord = new StringBuffer();
		for (char character : texts) {
			if (Character.isAlphabetic(character) || character=="-".charAt(0) || Character.isDigit(character)) {
				oneWord.append(character);
			} else if (oneWord.length() >= 1) {
				tWords.add(oneWord.toString().toCharArray());
				oneWord = new StringBuffer();
			}
		}
		iterator = tWords.iterator();

	}

	// YOU MUST IMPLEMENT THIS METHOD.
	public char[] nextWord() {
		// Return the next word in the document.
		// Return null, if it is the end of the document.
		if (iterator.hasNext()) {
			char[] nextWord = iterator.next();
			if (nextWord != null) {
				return nextWord;
			}
		}
		return null;
	}

}
