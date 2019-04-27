import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Article } from '../models/Article';
import { Observable } from 'rxjs';
import { Query } from '../models/Query';
const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}
@Injectable({
  providedIn: 'root'
})

export class HttpService {
  article: Observable<Article[]>
  query: Observable<Query[]>
  constructor(private http: HttpClient) { }

  // searchText(key_text: string) {
  //   let url = 'http://localhost:8080/search/text/' + key_text;
  //   return this.http.get<Article[]>(url);
  // }
  // searchFormula(key_formula: string) {
  //   let url = 'http://localhost:8080/search/formula/' + key_formula;
  //   return this.http.get<Article[]>(url);
  // }

  searchText(query): Observable<Article[]> {
    let url = 'http://35.238.247.88:8080/search/text';
    return this.http.post<Article[]>(url, query, httpOptions);
  }
  searchFormula(query): Observable<Article[]> {
    let url = 'http://35.245.156.13/search/formula';
    return this.http.post<Article[]>(url, query, httpOptions);
  }
}
