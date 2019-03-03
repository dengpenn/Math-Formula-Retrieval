import java.io.FileWriter;
import java.util.Map;


public class FinalMain {


    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        FinalMain main = new FinalMain();
//		hm1.PreProcess("trectext");
        long endTime = System.currentTimeMillis();
//		System.out.println("text corpus running time: " + (endTime - startTime) / 60000.0 + " min");

        startTime = System.currentTimeMillis();
        main.PreProcess("trecweb");
        endTime = System.currentTimeMillis();
        System.out.println("web corpus running time: " + (endTime - startTime) / 60000.0 + " min");

        startTime = System.currentTimeMillis();
        main.WriteIndex("trecweb");
        endTime = System.currentTimeMillis();
        System.out.println("index web corpus running time: " + (endTime - startTime) / 60000.0 + " min");

        startTime = System.currentTimeMillis();
        main.ReadIndex("trecweb", "timbr");
        endTime = System.currentTimeMillis();
        System.out.println("load index & retrieve running time: " + (endTime - startTime) / 60000.0 + " min");
    }

    public void PreProcess(String dataType) throws Exception {
        // Initiate the DocumentCollection.
        DocumentCollection corpus;
        if (dataType.equals("trectext"))
            corpus = new TrectextCollection();
        else
            corpus = new TrecwebCollection();

        // Loading stopword, and initiate StopWordRemover.
        StopWordRemover stopwordRemover = new StopWordRemover();
        // Initiate WordNormalizer.
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

            // Initiate the WordTokenizer class.
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
            if (count % 30000 == 0)
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
        Map<String, String> doc = null;

        int count = 0;
        // build index of corpus document by document
        while ((doc = corpus.NextDocument()) != null) {
            // load document number and content of the document
            String docno = doc.get("DOCNO");
            String content = doc.get("CONTENT");

            // index this document
            output.IndexADocument(docno, content);

            count++;
            if (count % 30000 == 0)
                System.out.println("finish " + count + " docs");
        }
        System.out.println("totaly document count:  " + count);
        output.Close();
    }

    public void ReadIndex(String dataType, String token) throws Exception {
        // Initiate the index file reader
        MyIndexReader ixreader = new MyIndexReader(dataType);

        // conduct retrieval
        int df = ixreader.GetDocFreq(token);
        long ctf = ixreader.GetCollectionFreq(token);
        System.out.println(" >> the token \"" + token + "\" appeared in " + df + " documents and " + ctf + " times in total");
        if (df > 0) {
            int[][] posting = ixreader.GetPostingList(token);
            for (int ix = 0; ix < posting.length; ix++) {
                int docid = posting[ix][0];
                int freq = posting[ix][1];
                String docno = ixreader.GetDocno(docid);
                System.out.printf("    %20s     %6d    %6d\n", docno, docid, freq);
            }
        }
        ixreader.Close();
    }
}
