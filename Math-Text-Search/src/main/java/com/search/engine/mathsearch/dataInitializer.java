package com.search.engine.mathsearch;


import com.search.engine.mathsearch.Classes.*;
import com.search.engine.mathsearch.IndexingLucene.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


public class dataInitializer {


    public static void dataInitializer() throws Exception {
        long startTime = System.currentTimeMillis();
        dataInitializer main = new dataInitializer();
        long endTime = System.currentTimeMillis();

        //cleaning
        File[] fl=new File(Path.IndexWebDir).listFiles();
        if (fl!=null) for (File f:fl) {
            f.delete();
        }
        fl=null;
        fl=new File(Path.IndexTextDir).listFiles();
        if (fl!=null)for (File f:fl) {
            f.delete();
        }

        System.out.println("Initialization Started.");

        startTime = System.currentTimeMillis();
        main.PreProcess("trecweb");
        endTime = System.currentTimeMillis();
        System.out.println("web corpus running time: " + (endTime - startTime) / 60000.0 + " min");

        startTime = System.currentTimeMillis();
        main.WriteIndex("trecweb");
        endTime = System.currentTimeMillis();
        System.out.println("index web corpus running time: " + (endTime - startTime) / 60000.0 + " min");

        startTime = System.currentTimeMillis();
        main.ReadIndex("trecweb", "artin");
        endTime = System.currentTimeMillis();
        System.out.println("load index & retrieve running time: " + (endTime - startTime) / 60000.0 + " min");
        System.out.println("Initialization Finished.");
    }

    public void PreProcess(String dataType) throws Exception {
        // Initiate the Classes.DocumentCollection.
        TrecwebCollection corpus;
        if (dataType.equals("trectext"))
            corpus = new TrectextCollection();
        else
            corpus = new TrecwebCollection();

        // Loading stopword, and initiate Classes.StopWordRemover.
        StopWordRemover stopwordRemover = new StopWordRemover();
        // Initiate Classes.WordNormalizer.
        WordNormalizer normalizer = new WordNormalizer();

        // Initiate the BufferedWriter to output result.
        FileWriter wr = new FileWriter(Path.ResultHM1 + dataType);

        // <String, Object> can hold document number and content.
        Map<String, Object> doc = null;

        // Process the corpus, document by document, iteratively.
        int count = 0;
        while ((doc = corpus.nextDocument()) != null) {
            // Load document number of the document.
            String docno = doc.keySet().iterator().next();

            // Load document content.
            char[] content = (char[]) doc.get(docno);

            // Write docno into the result file.
            wr.append(docno + "\n");

            // Initiate the Classes.WordTokenizer class.
            WordTokenizer tokenizer = new WordTokenizer(content);

            // Initiate a word object, which can hold a word.
            char[] word = null;

            // Process the document word by word iteratively.
            while ((word = tokenizer.nextWord()) != null) {
                // Each word is transformed into lowercase.
                word = normalizer.lowercase(word);

                // Only non-stopword will appear in result file.
                if (!stopwordRemover.isStopword(word))
                    // Words are stemmed.
                    wr.append(normalizer.stem(word) + " ");

            }
            wr.append("\n");// Finish processing one document.
            count++;
            if (count % 5000 == 0)
                System.out.println("finish " + count + " docs");
        }
        System.out.println("totaly document count:  " + count);
        wr.close();
    }

    public void WriteIndex(String dataType) throws Exception {
        // Initiate pre-processed collection file reader
        PreProcessedCorpusReader corpus = new PreProcessedCorpusReader(dataType);

        // initiate the output object
        MyIndexWriter output = new MyIndexWriter(dataType);

        // initiate a doc object, which will hold document number and document content
        Map<String, char[]> doc = null;

        int count = 0;
        // build index of corpus document by document
        while ((doc = corpus.nextDocument()) != null) {
            // load document number and content of the document
            String docno = doc.keySet().iterator().next();
            char[] content = doc.get(docno);
            // index this document
            output.index(docno, content);
            count++;
            if (count % 30000 == 0)
                System.out.println("finish " + count + " docs");
        }
        System.out.println("totaly document count:  " + count);
        output.close();

    }



    public void ReadIndex(String dataType, String token) throws IOException {
        // Initiate the index file reader
        MyIndexReader ixreader = new MyIndexReader(dataType);
        // conduct retrieval
        int df = ixreader.getDocFreq(token);
        long ctf = ixreader.getCollectionFreq(token);
        System.out.println(" >> the token \"" + token + "\" appeared in " + df + " documents and " + ctf + " times in total");
        if (df > 0) {
            int[][] posting = ixreader.getPostingList(token);
            for (int ix = 0; ix < posting.length; ix++) {
                int docid = posting[ix][0];
                int freq = posting[ix][1];
                String docno = ixreader.getDocno(docid);
                System.out.printf("    %20s     %6d    %6d\n", docno, docid, freq);
            }
        }
        ixreader.close();
    }
}
