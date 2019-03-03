package com.search.engine.mathsearch.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class preProcessQuery {
    private static checkStopWord stopWord;

    static {
        try {
            stopWord = new checkStopWord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static wordNormalize wordNorm = new wordNormalize();

    public static ArrayList<String> ProcessQueryText(String termList) {
        String[] terms = termList.split("\\s+");
        ArrayList<String> stemWord = new ArrayList<>();
        for (String word : terms) {
            // Only non stop word will appear in result file.
            word = word.toLowerCase();
            if (!stopWord.isStopword(word)) {
                // Words are stemmed.
                stemWord.add(wordNorm.stem(word) + " ");
            }
        }
        return stemWord;
    }
}

