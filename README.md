# Math Formula Retrieval 

This repo contains front-end and backend of math formula retrieval system including text and formula index building and searching.

## Project Structure

* [Math-search-front](https://github.com/dengpenn/Math-Formula-Retrieval/tree/master/Math-search-front) includes front-end of our system using Angular JS.
* Lucene was used for building index and ranking documents(TF-IDF Model).
* Math formula(Latex format) was parsed as XML and the tree sub-structure was built on it, detailed information can be found in [formula-server](https://github.com/dengpenn/Math-Formula-Retrieval/tree/master/formula-server).

## Motivation

* Mathematical formulae are important means for dissemination and communication of scientific information.
* Non-alphabetical symbols that are not understood by current search systems.
* Terms are either meaningless or improperly read and processed by current systems; e.g. $$(sin(x + log x) == sin x + log x)$$
* Current search systems are not equipped to recognize those structure when searching.

## Method

* We treat every formula inside documents as single indexed file. 
* The ranking algorithms is $$score(Q,F) =\sum\limits_{s\in{F}}\sum\limits_{t\in{Q}}tf(f,F)\times iff(t)\times W_{level}(t,s)$$.$$W_{level}(t,s) = \frac{1}{1+level(t)+level(s)}$$. It indicated the matched level between the indexed formula and searched  formula.

## Contributor

[Deng Pan](<https://github.com/dengpenn>)

[Yuchen Deng](<https://github.com/lethelimited>)

[Sichao Xue](<https://github.com/xuesichao)

## Demo

### Searched Formula

![image-20190427214756275](./img/search_result.png)



### Matched Top3 Formula

#### Rank1

![image-20190427214432442](./img/match_result1.png)

#### Rank2



![image-20190427214619160](./img/match_result2.png)

#### Rank3

![image-20190427214644396](/Users/dengpan/Desktop/Information_Retrival/Math-Formula-Retrieval/Math-Formula-Retrieval/img/match_result3.png)

### Searched Text







## Reference 

Gao, Liangcai, et al. "The Math Retrieval System of ICST for NTCIR-12 MathIR Task." *NTCIR*. 2016.