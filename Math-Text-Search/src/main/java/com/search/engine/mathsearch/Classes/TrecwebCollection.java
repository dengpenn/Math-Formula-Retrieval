package com.search.engine.mathsearch.Classes;//this class is not used

import java.io.*;
import java.util.*;

public class TrecwebCollection implements DocumentCollection {
    // Essential private methods or variables can be added.
    LinkedList<File> fileList;
    Iterator<File> dIterator;

    public TrecwebCollection() {
        // 1. Open the file in Classes.Path.
        // 2. Make preparation for function nextDocument().
        // NT: you cannot load the whole corpus into memory!!
        try {

            BufferedReader lf = new BufferedReader(new FileReader(new File(Path.OutputDir + Path.FileCounts)));
            ArrayList<File> folderList = new ArrayList<>();
            String temp;
            while ((temp = lf.readLine()) != null) {
                temp = Path.OutputDir + temp.split(",", 3)[2].trim();
                File f = new File(temp);
                if (f.isDirectory()) {
                    folderList.add(f);
                }
            }
            dIterator = folderList.iterator();
            fileList = new LinkedList<>();

            while (dIterator.hasNext()) {
                File f = dIterator.next();

                File[] fl = f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {

                        return pathname.toString().contains("html");
                    }
                });

                for (File file : fl) {
                    fileList.add(file);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // YOU SHOULD IMPLEMENT THIS METHOD.
    public Map<String, Object> nextDocument() throws IOException {
        // 1. When called, this API processes one document from corpus, and returns its
        // doc number and content.
        // 2. When no document left, return null, and close the file.
        // 3. the HTML tags should be removed in document content.
        if (fileList.size() > 0) {
            Map<String, Object> r = new HashMap<>();
            File dataFile = fileList.pop();

            FileInputStream document = new FileInputStream(dataFile);
            byte[] context = new byte[(int) dataFile.length()];
            document.read(context);
            document.close();
            String text = new String(context, "UTF-8");
            char[] valueText = text.replaceAll("\\p{Cc}", "")
                    .replaceAll("<[^>]*>", " ").trim().toCharArray();
            r.put(dataFile.getPath(), valueText);
            return r;
        }

        return null;

    }


}

