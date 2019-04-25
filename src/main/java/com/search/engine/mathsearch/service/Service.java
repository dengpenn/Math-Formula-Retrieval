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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Service {
    @PostMapping("/search/text")
    @CrossOrigin(origins = "*")
    public ResponseEntity searchText(@Valid @RequestBody String query) {
//        @Valid @RequestBody String query
        if (query.contains(":"))
            try {
//            String query="numbers algebra matrix";
                //token sterm query

                query = query.split(":")[1];
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

                System.out.println("Text:       " + sb.toString());
                MyQuery aQuery = new MyQuery();
                MyIndexReader mir = new MyIndexReader("trecweb");
                aQuery.SetQueryContent(sb.toString().trim());
                QueryRetrievalModel qrm = new QueryRetrievalModel(mir);
                List<Article> res = qrm.retrieveQuery(aQuery, 20);

                Gson gson = new Gson();
                String s = gson.toJson(res);
                ResponseEntity response = new ResponseEntity(s, HttpStatus.OK);

                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }

        return new ResponseEntity(new ArrayList(), HttpStatus.ACCEPTED);

    }

    @GetMapping("/get/document/{path}")
//    @PostMapping("/get/document")
    public ResponseEntity openHtml(@PathVariable String path) throws IOException {
        String html;
        if (path.contains(":")) {
            String q = path.replaceFirst(":", "/");
            System.out.println("Document=   " + q);
            File f = new File("data/output/MathTagArticles/" + q);
            char[] buffer = new char[(int) f.length()];
            new FileReader(f).read(buffer);
            html = String.valueOf(buffer);
        } else if (path.contains("wiki")) {
            html = downloadPage("https://en.wikipedia.org/" + path);
        } else {
            html = downloadPage("https://en.wikipedia.org/wiki/" + path);
        }
        ResponseEntity htmlFile = new ResponseEntity(html, HttpStatus.OK);
        return htmlFile;
    }

    @GetMapping("/wiki/{path}")
    public ResponseEntity jumpHtml(@PathVariable String path) throws IOException {
        String html;
        if(path!=null & path.length()>0){
            html = downloadPage("https://en.wikipedia.org/wiki/" + path);
        }else{
            return new ResponseEntity("", HttpStatus.NOT_FOUND);
        }
        ResponseEntity htmlFile = new ResponseEntity(html, HttpStatus.OK);
        return htmlFile;
    }


    public String downloadPage(String u) throws IOException {

        // Make a URL to the web page
        URL url = new URL(u);
        // Get the input stream through URL Connection
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        // read each line and write to System.out
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        if (sb != null) {
            return sb.toString();
        } else {
            return "Fail Downloading";
        }
    }

}

