package com.search.engine.mathsearch.service;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class checkStopWord {
    private static final String file_path = Path.StopwordDir;
    private File file;
    private Set<String> s;

    public checkStopWord() throws Exception {
        // Load and store the stop words from the fileinputstream with appropriate data structure.
        // NT: address of stopword.txt is Path.StopwordDir
        Scanner scn = new Scanner(new File("./data/input/stopword.txt"));
        s = new HashSet<>();
//        Add each line into the hash set
        while (scn.hasNext()) {
            s.add(scn.next());
        }
        scn.close();
    }

    // YOU SHOULD IMPLEMENT THIS METHOD.
    public boolean isStopword(String word) {
        // Return true if the input word is a stopword, or false if not.
        return s.contains(word);
    }
}
