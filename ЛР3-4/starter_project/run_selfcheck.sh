#!/bin/sh
set -eu
cd -- "$(dirname -- "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/ru/edu/pr01/*.java
java -Dfile.encoding=UTF-8 -cp out ru.edu.pr01.SelfCheck
java -Dfile.encoding=UTF-8 -cp out ru.edu.pr01.BoundaryCheck
