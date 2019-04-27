package com.search.engine.mathsearch.IndexingLucene;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import com.search.engine.mathsearch.Classes.*;

public class PreProcessedCorpusReader {
	

	private BufferedReader br;
	private FileInputStream instream_collection;
	private InputStreamReader is;
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor should open the file in Classes.Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// remember to close the file that you opened, when you do not use it any more
		instream_collection = new FileInputStream(Path.ResultHM1+type);
		is = new InputStreamReader(instream_collection);
        br = new BufferedReader(is);   
	}
	

	public Map<String, char[]> nextDocument() throws IOException {
		String docno=br.readLine();
		if(docno==null) {
			instream_collection.close();
			is.close();
			br.close();
			return null;
		}
		String content =br.readLine();
		Map<String, char[]> doc = new HashMap<>();
		doc.put(docno, content.toCharArray());
		return doc;
	}

}
