omp4j [![Build Status](https://travis-ci.org/omp4j/omp4j.svg?branch=master)](https://travis-ci.org/omp4j/omp4j)
=====
Lightweight Java OMP preprocessor written in Scala and Java. The input of preprocessor are valid Java source files with proper directives (`// omp ...` comments). One can use omp4j either as "blackbox" compiler (preprocessor using javac) or as pure preprocessor. In latter case the output is valid paralellized Java source files.

Warning
-------
This project is under development and for now **is not** meant to be used for any purposes. Please note that the project itself is going to be a part of bachelor thesis so any cooperation is prohibited. That is the only reason for ignoring forks, issues and pull request. Once the project is finished, it will be opened for public and this paragraph disappears.

Usage
-----
The described method is for UNIX-like systems. Microsoft Windows are supported though.

### Prerequisites
For Ubuntu users: `$ ./install-system-dependencies.sh`; then [download](http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb) and install sbt.

For other users, this is the list of required software:
- JRE (tested version `OpenJDK 1.7.0_55`)
- JDK (tested version `javac 1.7.0_55`)
- Scala (tested version `2.9.2`)
- sbt (tested version `0.13.5`)
- git

**Note:** Even though only `scala 2.9.2` is required to run `sbt` properly, using current ScalaTest determines `sbt` to use `scala 2.10.3`. If you don't have this version installed `sbt` will download it by itself. This scala version will be installed only in project directory and **will not affect** any other project or system itself.

**Note:** After the assemblation, **only proper JRE is required** to run `.jar` as all dependencies are packed into the package. One can distribute `.jar` to machines not having scala/ANTLR/ScalaTest installed at all.

### Fetching code
1. `$ git clone git@github.com:omp4j/omp4j.git`
2. `$ cd omp4j & ./install-dependencies.sh`
 
Step 2 alternative:

1. download [ANTLR runtime](http://www.antlr.org/download/antlr-runtime-4.2.2.jar) into `lib/` directory
2. donwload ANTLRv4 and set shell alias `antlr4`
2. compile grammar in `src/main/java/grammar` using both commands `$ antlr4 -visitor Java8.g4` and `$ antlr4 -visitor OMP.g4`. Please read [getting started](https://theantlrguy.atlassian.net/wiki/display/ANTLR4/Getting+Started+with+ANTLR+v4) in order to get familiar with setting up and using `antlr4` command.

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

### Unit testing
```
$ sbt test
```
Run ScalaTest unit testing. If ScalaTest isn't installed, it will be downloaded by `sbt` automatically.

Authors
-------
- Lead programmer - [Petr Bělohlávek](https://github.com/petrbel)
- Thesis supervisor - [Mgr. Antonín Steinhauser](http://d3s.mff.cuni.cz/~steinhauser/)

Licence
-------
Developed under GNU GPL v2, please see [LICENCE file](https://github.com/omp4j/omp4j/blob/master/LICENSE).



