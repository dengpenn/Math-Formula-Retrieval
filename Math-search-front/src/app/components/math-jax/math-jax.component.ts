import {
  Component,
  OnInit,
  Input,
  OnChanges
} from '@angular/core';
import {
  SimpleChanges
} from '@angular/core';
import {
  ConfigService
} from '../../services/config.service'
@Component({
  selector: 'app-mathjax',
  templateUrl: './math-jax.component.html',
  styleUrls: ['./math-jax.component.css']
})
export class MathjaxComponent implements OnChanges, OnInit {
  @Input() content: string;

  constructor(public cs: ConfigService) {}
  mathJaxObject: any;

  ngOnChanges(changes: SimpleChanges) {
    // to render math equations again on content change
    if (changes['content']) {
      this.renderMath()
    }
  }
  ngOnInit() {
    this.loadMathConfig();
    this.renderMath();
  }

  updateMathObt() {
    this.mathJaxObject = this.cs.nativeGlobal()['MathJax'];
  }

  renderMath() {
    this.updateMathObt();
    let angObj = this;
    setTimeout(() => {
      angObj.mathJaxObject['Hub'].Queue(["Typeset", angObj.mathJaxObject.Hub], 'mathContent');
    }, 500)
  }

  loadMathConfig() {
    this.updateMathObt();
    this.mathJaxObject.Hub.Config({
      showMathMenu: true,
      extension: ["toMathML.js"],
      tex2jax: {
        inlineMath: [
          ["$", "$"]
        ],
        displayMath: [
          ["$$", "$$"]
        ]
      },
      menuSettings: {
        zoom: "Double-Click",
        zscale: "150%"
      },
      CommonHTML: {
        linebreaks: {
          automatic: true
        }
      },
      "HTML-CSS": {
        linebreaks: {
          automatic: true
        },
        styles: {
          '.MathJax_Display': {
            "margin": 0
          }
        }
      },
      SVG: {
        linebreaks: {
          automatic: true
        }
      }
    });
  }

  toMathML(jax, callback) {
    var mml;
    try {
      mml = jax.root.toMathML("");
    } catch (err) {
      if (!err.restart) {
        throw err
      } // an actual error
      return this.mathJaxObject.Callback.After([this.toMathML, jax, callback], err.restart);
    }
    this.mathJaxObject.Callback(callback)(mml);
  }

  getMathML() {
    this.updateMathObt();
    this.mathJaxObject.Hub.Queue(func => {
      var jax = this.mathJaxObject.Hub.getAllJax();
      for (var i = 0; i < jax.length; i++) {
        this.toMathML(jax[i], function (mml) {
          window.localStorage.setItem('mathml',mml);
        });
      }
    });
  }
}