omp4j [![Build Status](https://travis-ci.org/omp4j/omp4j.svg?branch=master)](https://travis-ci.org/omp4j/omp4j)
=====
Lightweight Java OMP-like preprocessor written in Scala and Java. The input of the preprocessor are valid Java source files with proper directives (i.e. `// omp ...` comments). One can use omp4j either as "blackbox" compiler (preprocessor using javac) or as pure preprocessor. In latter case the output is valid paralellized Java source files.

Warning
-------
This project is under development and for now **is not** meant to be used for any purposes. Please note that the project itself is going to be a part of bachelor thesis so any cooperation is prohibited. That is the only reason for ignoring forks, issues and pull request. Once the project is finished, it will be opened for public and this paragraph disappears.

Usage
-----
The described method is for UNIX-like systems. Microsoft Windows are supported though, nevertheless the users are expected to figure the installation steps by theirselves.

### Prerequisites
- supported JDK (see tested JDKs on [Travis-CI](https://github.com/omp4j/omp4j/blob/master/.travis.yml))
- Scala (tested version `2.9.2`)
- sbt (tested version `0.13.5`)
- git

**Note:** Even though only `scala 2.9.2` is required to run `sbt` properly, using current ScalaTest determines `sbt` to use `scala 2.10.3`. If you don't have this version installed `sbt` will download it by itself. This scala version will be installed only in the project directory and **will not affect** any other project or system itself.

**Note:** After the assemblation, **only proper JDK is required** to run `.jar` as all dependencies are packed into the package. One can distribute `.jar` to machines not having scala/ANTLR/ScalaTest installed at all.

### Fetching code
```
$ git clone git@github.com:omp4j/omp4j.git --recursive
$ cd omp4j
```

### Compilation
```
$ sbt compile
```

### Unit testing
Run ScalaTest unit testing. If ScalaTest isn't installed, it is downloaded by `sbt` automatically.
```
$ sbt test
```

### Deployment
```
$ sbt assembly
```
This command creates `.jar` package in `target/scala-<version>/`. One can simply run it on any regular JVM (even without installed scala!) using `java -jar <generated-package.jar> <params>`. This package is ready for deployment as has no further dependencies.

For the future references we assume that the user has created shell alias `omp4j` invoking `java -jar <generated-package.jar>` with all passed parameters. The alias creation depends on the employed shell.

### Run
```
$ omp4j <args>
```
Where `<args>` are passed arguments. See [tutorial](http://www.omp4j.org/tutorial) for supported options and arguments.

omp4j may be invoked also by using `$ sbt run <args>`, however this option is not suggested. It should be employed only for development purposes since it perform significantly more poorly and the runtime library must be present in the classpath.

### API reference
```
$ sbt doc
```
This command creates directory `target/scala-<version>/api/` including file `index.html`. One can simply open it in web browser in order to browse API reference.

### Directive Programming
The preprocessor accepts a serial Java source code which is decorated by directives. Refer to [project tutorial] in order to obtain directive-related information and usefull examples.

Authors
-------
- Lead programmer - [Petr Bělohlávek](https://github.com/petrbel)
- Thesis supervisor - [Mgr. Antonín Steinhauser](http://d3s.mff.cuni.cz/~steinhauser/)

See [project website](http://www.omp4j.org/authors) for contacts and details.

Licence
-------
Developed under BSD license, please refer to [LICENCE file](https://github.com/omp4j/omp4j/blob/master/LICENSE).



