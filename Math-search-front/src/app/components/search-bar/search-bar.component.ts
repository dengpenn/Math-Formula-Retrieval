import { Component, OnInit } from '@angular/core';
import { HttpService } from '../../services/http.service'
import { Query } from '../../models/Query';
import { Article } from '../../models/Article';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {
  isText: boolean;
  isFormula: boolean;
  query: Query = {
    key_formula: '',
    key_text: ''
  }
  queries:Query[];
  articles: Article[];
  constructor(
    private httpService: HttpService
  ) { }

  ngOnInit() {
  }
  onSubmit_searchText({ value }: { value: Query }) {
    this.httpService.searchText(this.query).subscribe(articles => {
      this.articles = articles
    });

  }

  onSubmit_searchFormula({ value }: { value: Query }) {
    this.httpService.searchFormula(this.query).subscribe(articles => {
      this.articles = articles
    });
  }
}
