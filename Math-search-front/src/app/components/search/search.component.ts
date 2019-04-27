import {
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import {
  HttpService
} from '../../services/http.service'
import {
  Query
} from '../../models/Query';
import {
  Article
} from '../../models/Article';
import {
  MathjaxComponent
} from '../math-jax/math-jax.component';
import {
  query
} from '@angular/core/src/render3';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  @ViewChild('mathjaxChild') mathjax;
  isText: boolean;
  isFormula: boolean;
  mathml: string;
  query: Query = {
    key_formula: '$$\\sqrt{2 + \\sqrt{2+ \\sqrt{2}}}$$',
    key_text: '',
    isFuzzy: false
  }

  article: Article = {
    title: '',
    url: '',
    keyword: ''
  }
  articles: Article[];

  paths: any;
  constructor(
    private httpService: HttpService,
  ) {}

  ngOnInit() {}
  onSubmit_searchText({
    value
  }: {
    value: Query
  }) {
    console.log(value);
    this.httpService.searchText(value).subscribe(articles => {
      this.articles = articles;
    });
    console.log(this.articles);
  }

  onSubmit_searchFormula({ value }: { value: Query }) 
  {
    this.articles=[];
    value.isFuzzy = this.query.isFuzzy;
    this.httpService.searchFormula(value).subscribe(articles => {
      this.articles=articles
      console.log(this.articles);
    });
  }

  auto_grow() {
    document.getElementById('textarea').style.height = "5px";
    document.getElementById('textarea').style.height = (document.getElementById('textarea').scrollHeight) + "px";
  }


}
