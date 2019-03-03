package com.search.engine.mathsearch.model;

import javax.persistence.Entity;

@Entity
public class Query {
    private String key_text;
    private String key_formula;

    public String getKey_text() {
        return key_text;
    }

    public void setKey_text(String key_text) {
        this.key_text = key_text;
    }

    public String getKey_formula() {
        return key_formula;
    }

    public void setKey_formula(String key_formula) {
        this.key_formula = key_formula;
    }
}
