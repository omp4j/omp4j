# omp4j [![CircleCI](https://circleci.com/gh/omp4j/omp4j/tree/master.svg?style=shield)](https://circleci.com/gh/omp4j/omp4j/tree/master)

Lightweight Java OMP-like preprocessor written in Scala and Java.
The input of the preprocessor are valid Java source files with proper directives (i.e. `// omp ...` comments).
One can use `omp4j` either as *blackbox* compiler (i.e. a preprocessor using `javac`) or as pure preprocessor.
In latter case the output is valid paralellized Java source files.

## Usage

The described method is for UNIX-like systems.
While Microsoft Windows are supported, the users are expected to figure the installation steps by themselves.

### Run Prerequisites
Only JDK is required in case only the preprocessor is used.
This is a common use-case.

The following JDKs are supported and tested.

- OracleJDK8
- OracleJDK7
- OpenJDK7
- OpenJDK6

### Fetching `omp4j`
Download `omp4j-*.jar` from the [releases page](https://github.com/omp4j/omp4j/releases).

### Using `omp4j`
Simply run

```
java -jar omp4j-*.jar <args>
```

Where `<args>` represent the desired program arguments as described in [tutorial](http://www.omp4j.org/tutorial)

We advise the users to create an `omp4j` alias which represents `java -jar omp4j-*.jar`.
By this trick, the previous preprocessor call might be replaced with `omp4j <args>`.

## Developement
In case of development, a slightly different environment and installation steps are recommended.

### Development Prerequisites
The developer is expected to work with an UNIX-like up-to-date operating system.
The officially supported operating systems are ArchLinux and Ubuntu 16.04 LTS.

The functional working environment is provided via docker image [omp4j/base](https://hub.docker.com/r/omp4j/base).
Its corresponding dockerfile is located in [dockerfiles/base.dockerfile](dockerfiles/base.dockerfile).

The following software must be installed in order to fetch, compile and test `omp4j`.

- `git`
- supported JDK (see above)
- `sbt` (tested version `0.13.13`)

The version of Scala used for running `sbt` is irrelevant as long as `sbt` can be launched in the specified version above.
`sbt` employs Scala `2.10.3`.

### Fetching code
Forking the project as pushing directly to the official repository is forbidden.
However, we provide the URL for this repository.
The URL should be modified accordingly.

```
$ git clone git@github.com:omp4j/omp4j.git --recursive
$ cd omp4j
```

### Compilation
Once the source code is fetched, it might be easily compiled via `sbt` API.

```
$ sbt compile
```

### Unit testing
When the code is compiled, the unit tests should be run.

```
$ sbt test
```

### Running `omp4j`

In development mode, `omp4j` might be invoked `sbt` interface.

```
$ sbt run <args>
```

This option is not suggested and it should be employed only for the development purposes since it perform significantly more poorly and the runtime library must be present in the classpath.
For this reason, we encourage the user to assembly the preprocessor and use the final `.jar`.
See *Using omp4j* above.

### Deployment
The preprocessor should be distributed as a dependency-less `.jar` file.
For this purpose, `sbt` is provided with the `assembly` command.

```
$ sbt assembly
```

This command creates `.jar` package in `target/scala-<version>/`.
This package is ready for deployment as has no further dependencies.
See *Using omp4j* above.

### API reference
`sbt` might be used to generate the API reference.

```
$ sbt doc
```

This command creates directory `target/scala-<version>/api/` including file `index.html`. One can simply open it in web browser in order to browse API reference.

## Authors
- Lead programmer - [Petr Bělohlávek](https://github.com/petrbel)
- Thesis supervisor - [Antonín Steinhauser](http://d3s.mff.cuni.cz/~steinhauser/)

See [project website](http://www.omp4j.org/authors) for contacts and details.

Licence
-------
Developed under BSD license, please refer to [LICENCE file](LICENSE).
