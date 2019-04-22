package com.search.engine.mathsearch.SearchLucene;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.search.engine.mathsearch.Classes.*;
import com.search.engine.mathsearch.IndexingLucene.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class QueryRetrievalModel {

	protected MyIndexReader indexReader;

	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher indexSearcher;

	public QueryRetrievalModel(MyIndexReader ixreader) {
		try {
			directory = FSDirectory.open(Paths.get(Path.IndexWebDir));
			ireader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(ireader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> retrieveQuery(MyQuery aQuery, int TopN) throws Exception {
		List<Document> results = new ArrayList<Document>();
		Query theQ = new QueryParser("CONTENT", new WhitespaceAnalyzer()).parse(aQuery.GetQueryContent());
		ScoreDoc[] scoreDoc = indexSearcher.search(theQ, TopN).scoreDocs;
		for (ScoreDoc score : scoreDoc) {
			results.add(new Document(score.doc + "", ireader.document(score.doc).get("DOCNO"), score.score));
		}
//		results.sort(new Comparator<Document>() {
//			@Override
//			public int compare(Document o1, Document o2) {
//				return -Double.compare(o1.score(),o2.score());
//			}
//		});
		List<String> re = new ArrayList<>();

		for (Document d:results){
			FileInputStream f=new FileInputStream(d.docno());
			byte[] context = new byte[(int) new File(d.docno()).length()];
			f.read(context);
			f.close();
			String text = new String(context, "UTF-8");
			char[] valueText = text.replaceAll("\\p{Cc}", "")
					.replaceAll("<[^>]*>", " ").trim().toCharArray();
			WordTokenizer wt =new WordTokenizer(valueText);
			StringBuilder sb=new StringBuilder();
			for (int i = 0; i < 100 && wt.nextWord()!=null; i++) {
				sb.append(String.valueOf(wt.nextWord())+" ");
			}
//			String [] s=new String[2];
//			s[0]=d.docno().substring(28);
//			s[1]=;
			re.add(d.docno().substring(28)+"|"+sb.toString().trim());
		}
		return re;
	}

}