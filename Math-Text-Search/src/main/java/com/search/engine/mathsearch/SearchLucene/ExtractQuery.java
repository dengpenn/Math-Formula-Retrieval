package com.search.engine.mathsearch.SearchLucene;

import com.search.engine.mathsearch.Classes.*;

import java.util.ArrayList;

public class ExtractQuery {

	ArrayList<MyQuery> queries;

	int idx = 0;

	public ExtractQuery() {
		// you should extract the 4 queries from the Path.TopicDir
		// NT: the query content of each topic should be 1) tokenized, 2) to
		// lowercase, 3) remove stop words, 4) stemming
		// NT: you can simply pick up title only for query, or you can also use
		// title + description + narrative for the query content.
		queries = new ArrayList<>();
		MyQuery aQuery = new MyQuery();
//		aQuery.SetTopicId("001");
		aQuery.SetQueryContent("number algebra matrix");
		queries.add(aQuery);

//		aQuery = new Query();
//		aQuery.SetTopicId("002");
//		aQuery.SetQueryContent("homosexu accept europ");
//		queries.add(aQuery);
//		aQuery = new Query();
//		aQuery.SetTopicId("003");
//		aQuery.SetQueryContent("star trek gener");
//		queries.add(aQuery);
//		aQuery = new Query();
//		aQuery.SetTopicId("004");
//		aQuery.SetQueryContent("progress dysphagia");
//		queries.add(aQuery);
	}

	public boolean hasNext() {
		if (idx == queries.size()) {
			return false;
		} else {
			return true;
		}
	}

	public MyQuery next() {
		return queries.get(idx++);
	}

}
