#!/bin/sh

# download ANTLR grammar-compiler dependency
cd src/main/java/grammar
curl -O http://www.antlr.org/download/antlr-4.4-complete.jar

java -jar antlr-4.4-complete.jar -visitor OMP.g4
java -jar antlr-4.4-complete.jar -visitor Java8.g4
