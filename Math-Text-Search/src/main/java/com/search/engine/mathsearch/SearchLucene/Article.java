package com.search.engine.mathsearch.SearchLucene;

import javax.persistence.Entity;

@Entity
public class Article{
    private String keyword;
    private String title;
    private String url;

    public Article(String k,String t,String u){
        keyword=k;
        title=t;
        url=u;
    }

    @Override
    public String toString(){
        return String.format("{title:%s,url:%s,keyword:%s}", title, url, keyword);
    }

}
