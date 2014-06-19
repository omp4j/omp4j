omp4j
=====
Lightweight Java OMP preprocessor written in Scala and Java. The input of preprocessor are valid Java source files with proper directives (aka comments). The output are valid paralellized Java source files.

Warning
-------
This project is under development and for now **is not** meant to be used for any purposes. Please note that the project itself is going to be a part of bachelor thesis so any cooperation is prohibited. That is the only reason for ignoring forks, issues and pull request. Once the project is finished, it will be opened for public and this paragraph disappears.

Usage
-----
The described method is for UNIX-like systems. Microsoft Windows are supported though.

### Prerequisites
- JRE (tested version `OpenJDK 1.7.0_55`)
- JDK (tested version `javac 1.7.0_55`)
- Scala (tested version `2.9.2`)
- sbt (tested version `0.13.5`
- git

### Fetching code
1. `$ git clone git@github.com:omp4j/omp4j.git`
2. download [ANTLR runtime](http://www.antlr.org/download/antlr-runtime-4.2.2.jar) into `lib/` directory

### Compilation
```
$ sbt compile
```

### Run
```
$ sbt run <args>
```
Where `<args>` are passed arguments.

### Deployment
```
$ sbt assembly
```
This command creates `.jar` package in `target/scala-<version>/`. One can simply run it on any regular JVM (even without installed scala!) using `java -jar <generated-package.jar>`. This package is ready for deployment as has no further dependencies.

### API reference
```
$ sbt doc
```
This command creates directory `target/scala-<version>/api/` including file `index.html`. One can simply open it in web browser in order to browse API reference.

Authors
-------
- Lead programmer - [Petr Bělohlávek](https://github.com/petrbel)
- Thesis supervisor - [Mgr. Antonín Steinhauser](http://d3s.mff.cuni.cz/~steinhauser/)

Licence
-------
Developed under GNU GPL v2, please see [LICENCE file](https://github.com/omp4j/omp4j/blob/master/LICENSE).



