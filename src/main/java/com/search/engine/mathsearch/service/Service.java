package com.search.engine.mathsearch.service;

import com.search.engine.mathsearch.IndexingLucene.MyIndexReader;
import com.search.engine.mathsearch.SearchLucene.QueryRetrievalModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.search.engine.mathsearch.Classes.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class Service {
    @GetMapping("/search/text")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Object> searchText(@Valid @RequestBody String query) {
//        @Valid @RequestBody String query
        try {
//            query="numbers algebra matrix";
            //token sterm query
            char[] content = query.toCharArray();
            WordTokenizer tokenizer = new WordTokenizer(content);
            WordNormalizer normalizer = new WordNormalizer();
            StopWordRemover stopwordRemover = new StopWordRemover();
            StringBuilder sb = new StringBuilder();
            // Initiate a word object, which can hold a word.
            char[] word = null;
            // Process the document word by word iteratively.
            while ((word = tokenizer.nextWord()) != null) {
                // Each word is transformed into lowercase.
                word = normalizer.lowercase(word);

                // Only non-stopword will appear in result file.
                if (!stopwordRemover.isStopword(word))
                    // Words are stemmed.
                    sb.append(normalizer.stem(word) + " ");

            }

            MyQuery aQuery = new MyQuery();
            MyIndexReader mir = new MyIndexReader("trecweb");
            aQuery.SetQueryContent(sb.toString());
            QueryRetrievalModel qrm = new QueryRetrievalModel(mir);
            List<String> result = qrm.retrieveQuery(aQuery, 20);

            return new ResponseEntity<Object>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String s = "fail query";
        return new ResponseEntity<Object>(s, HttpStatus.BAD_REQUEST);

    }
}
