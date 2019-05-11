# Math Formula Retrieval 

This repo contains front-end and backend of math formula retrieval system including text and formula index building and searching.

## Project Structure

* [Math-search-front](https://github.com/dengpenn/Math-Formula-Retrieval/tree/master/Math-search-front) includes front-end of our system using Angular JS.
* Lucene was used for building index and ranking documents(TF-IDF Model).
* Math formula(Latex format) was parsed as XML and the tree sub-structure was built on it, detailed information can be found in [formula-server](https://github.com/dengpenn/Math-Formula-Retrieval/tree/master/formula-server).

## Motivation

* Mathematical formulae are important means for dissemination and communication of scientific information.
* Non-alphabetical symbols that are not understood by current search systems.
* Terms are either meaningless or improperly read and processed by current systems; e.g. <a href="https://www.codecogs.com/eqnedit.php?latex=(sin(x&space;&plus;&space;log&space;x)&space;==&space;sin&space;x&space;&plus;&space;log&space;x)" target="_blank"><img src="https://latex.codecogs.com/gif.latex?(sin(x&space;&plus;&space;log&space;x)&space;==&space;sin&space;x&space;&plus;&space;log&space;x)" title="(sin(x + log x) == sin x + log x)" /></a>
* Current search systems are not equipped to recognize those structure when searching.

## Method

* We treat every formula inside documents as single indexed file. 
* The ranking algorithms is <a href="https://www.codecogs.com/eqnedit.php?latex=score(Q,F)&space;=\sum\limits_{s\in{F}}\sum\limits_{t\in{Q}}tf(f,F)\times&space;iff(t)\times&space;W_{level}(t,s)" target="_blank"><img src="https://latex.codecogs.com/gif.latex?score(Q,F)&space;=\sum\limits_{s\in{F}}\sum\limits_{t\in{Q}}tf(f,F)\times&space;iff(t)\times&space;W_{level}(t,s)" title="score(Q,F) =\sum\limits_{s\in{F}}\sum\limits_{t\in{Q}}tf(f,F)\times iff(t)\times W_{level}(t,s)" /></a>. <a href="https://www.codecogs.com/eqnedit.php?latex=W_{level}(t,s)&space;=&space;\frac{1}{1&plus;level(t)&plus;level(s)}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?W_{level}(t,s)&space;=&space;\frac{1}{1&plus;level(t)&plus;level(s)}" title="W_{level}(t,s) = \frac{1}{1+level(t)+level(s)}" /></a>. It indicated the matched level between the indexed formula and searched  formula.

## Demo

### Searched Formula

![image-20190427214756275](./img/search_result.png)



### Matched Top3 Formula

#### Rank1

![image-20190427214432442](./img/match_result1.png)

#### Rank2



![image-20190427214619160](./img/match_result2.png)

#### Rank3

![image-20190427214644396](./img/match_result3.png)

## Contributor

[Deng Pan](<https://github.com/dengpenn>)

[Yuchen Deng](<https://github.com/lethelimited>)

[Sichao Xue](<https://github.com/xuesichao>)

## Reference 

Gao, Liangcai, et al. "The Math Retrieval System of ICST for NTCIR-12 MathIR Task." *NTCIR*. 2016.