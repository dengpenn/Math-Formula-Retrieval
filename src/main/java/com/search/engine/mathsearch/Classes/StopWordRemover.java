package com.search.engine.mathsearch.Classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class StopWordRemover {
	// Essential private methods or variables can be added.
	Set<String> stopWords;
	FileReader stopWordStream;
	BufferedReader bufr;

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public StopWordRemover() throws FileNotFoundException {
		// Load and store the stop words from the fileinputstream with appropriate data
		// structure.
		// NT: address of stopword.txt is Classes.Path.StopwordDir
		stopWords = new HashSet<String>();
		stopWordStream = new FileReader(new File(Path.StopwordDir));
		bufr = new BufferedReader(stopWordStream);
		stopWords();

	}

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public boolean isStopword(char[] word) {
		// Return true if the input word is a stopword, or false if not.
		if (stopWords.contains(new String(word).trim())) {
			return true;
		} else {
			return false;
		}
	}

	private void stopWords() {
		try {
			String swd;

			while ((swd = bufr.readLine()) != null) {
				if (!swd.isEmpty()) {
					stopWords.add(swd.trim());
				}
			}
		} catch (Exception e) {
		}
	}
}
