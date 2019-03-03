package com.search.engine.mathsearch.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.search.engine.mathsearch.model.Query;
import com.search.engine.mathsearch.model.Article;

import javax.validation.Valid;

@RestController
public class Service {
    @PostMapping("/search/text")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Object> searchText(@Valid @RequestBody Query query) {
        System.out.println(query.getKey_text()+"-Text");
        System.out.println(query.getKey_formula()+"-Text");
        return new ResponseEntity<Object>(query, HttpStatus.OK);
    }

    @PostMapping("/search/formula")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Object> searchFormula(@Valid@RequestBody Query query) {
        System.out.println(query.getKey_text()+"-Formula2");
        System.out.println(query.getKey_formula()+"-Formula2");
        return new ResponseEntity<Object>(query, HttpStatus.OK);
    }
}
