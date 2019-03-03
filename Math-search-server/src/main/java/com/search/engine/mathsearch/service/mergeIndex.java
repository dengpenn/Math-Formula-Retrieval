package com.search.engine.mathsearch.service;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class mergeIndex {
    void MergeIndex() {
    }

    private static String[] helpIndexMerge(ArrayList<String> docInv1, ArrayList<String> docInv2) {
        ArrayList<String> answer = new ArrayList<>();
        int docZeroLength = docInv1.size();
        int docOneLength = docInv2.size();
        int docZeroCurrentIndex = 0;
        int docOneCurrentIndex = 0;
        while (docZeroCurrentIndex < docZeroLength && docOneCurrentIndex < docOneLength) {
            if (docInv1.get(docZeroCurrentIndex).equals(docInv2.get(docOneCurrentIndex))) {
                answer.add(docInv1.get(docZeroCurrentIndex));
                docZeroCurrentIndex++;
                docOneCurrentIndex++;
            }
        }
        return null;
    }

    private static ArrayList<String> helpIndexNoOrder(ArrayList<String> docInv1, ArrayList<String> docInv2) {
        ArrayList<String> answer = new ArrayList<>();
        for (String docId : docInv1) {
            if (docInv2.contains(docId)) {
                answer.add(docId);
            }
        }
        return answer;
    }


    private static ArrayList<String> mergeIndex(String[] multipleIndexList) {
        ArrayList<String> answer;
        int totalTermLength = multipleIndexList.length;
        if (totalTermLength == 1) {
            return new ArrayList<>(Arrays.asList(multipleIndexList[0].split("\\|")));
        } else {
            answer = new ArrayList<>(Arrays.asList(multipleIndexList[0].split("\\|")));
            // Start from the first inverted index
            totalTermLength = 1;
            while (totalTermLength < multipleIndexList.length) {
                ArrayList<String> docIdList = new ArrayList<>(Arrays.asList(multipleIndexList[totalTermLength].split("\\|")));
                answer = helpIndexNoOrder(answer, docIdList);
                totalTermLength += 1;
            }
        }
        return answer;
    }

    private static String[] returnDir(ArrayList<String> al) throws IOException {
        File dictionary = new File(Path.IndexWebDir + Path.docId_docNo);
        ArrayList<String> s = null;

        if (dictionary.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(dictionary));
            s = new ArrayList<>();
            String temp;
            while ((temp = reader.readLine()) != null) {
                String[] r = temp.split(",", 2);
                for (Iterator<String> idIterator = al.iterator(); idIterator.hasNext(); ) {
                    if (idIterator.next().equals(r[0])) {
                        s.add(r[1]);
                        idIterator.remove();
                    }
                }
            }
        }
        String[] re = new String[s.size()];
        s.toArray(re);
        return re;
    }
}