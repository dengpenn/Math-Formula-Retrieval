package com.search.engine.mathsearch.service;

import com.search.engine.mathsearch.IndexingLucene.MyIndexReader;
import com.search.engine.mathsearch.SearchLucene.Article;
import com.search.engine.mathsearch.SearchLucene.QueryRetrievalModel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import com.search.engine.mathsearch.Classes.*;

import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Service {
    @PostMapping("/search/text")
    @CrossOrigin(origins = "https://xuesichao.github.io")
    public ResponseEntity<List> searchText(@Valid @RequestBody String query) {
//        @Valid @RequestBody String query
        try {
//            String query="numbers algebra matrix";
            //token sterm query
            query=query.split(":")[1];
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
            List<Article> res=qrm.retrieveQuery(aQuery, 20);

            Gson gson=new Gson();
            String s=gson.toJson(res);
            ResponseEntity response=new ResponseEntity(s, HttpStatus.OK);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity(new ArrayList(), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/get/document/")
    @GetMapping("/get/document/")
    public ResponseEntity<String> openHtml(@Valid @RequestBody String path) throws IOException {

        String q=path;
        File f=new File("data/output/MathTagArticles/"+q);
        char[] buffer=new char[(int)f.length()];
        new FileReader(f).read(buffer);
        String html=String.valueOf(buffer);
        ResponseEntity htmlFile=new ResponseEntity(html,HttpStatus.OK);
        return htmlFile;
    }

}

